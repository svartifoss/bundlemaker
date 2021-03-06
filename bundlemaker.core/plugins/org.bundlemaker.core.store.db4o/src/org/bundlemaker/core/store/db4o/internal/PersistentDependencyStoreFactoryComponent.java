/*******************************************************************************
 * Copyright (c) 2011 Gerd Wuetherich (gerd@gerd-wuetherich.de).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Wuetherich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package org.bundlemaker.core.store.db4o.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bundlemaker.core.BundleMakerCore;
import org.bundlemaker.core.common.utils.FileUtils;
import org.bundlemaker.core.project.IProjectDescriptionAwareBundleMakerProject;
import org.bundlemaker.core.spi.store.IPersistentDependencyStore;
import org.bundlemaker.core.spi.store.IPersistentDependencyStoreFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
@Component
public class PersistentDependencyStoreFactoryComponent implements IPersistentDependencyStoreFactory {

  /** PREFIX_BUNDLEMAKER_DB4O_STORE */
  private static final String                                                            PREFIX_BUNDLEMAKER_DB4O_STORE                         = "db4o.store";

  /** the cache */
  private Map<IProjectDescriptionAwareBundleMakerProject, PersistentDependencyStoreImpl> _cache;

  /** - */
  private String                                                                         _fileName;

  /** - */
  private static final boolean                                                           DELETE_DEPENDENCYSTORE_IF_CORE_BUNDLE_VERSION_CHANGED = true;

  /** - */
  private BundleContext                                                                  _bundleContext;

  /**
   * <p>
   * Creates a new instance of type {@link PersistentDependencyStoreFactoryComponent}.
   * </p>
   */
  public PersistentDependencyStoreFactoryComponent() {

    // create the cache
    _cache = new HashMap<IProjectDescriptionAwareBundleMakerProject, PersistentDependencyStoreImpl>();
  }

  /**
   * @param bundleContext
   */
  @Activate
  public void activate(BundleContext bundleContext) {
    _bundleContext = bundleContext;
  }

  /**
   * @see org.bundlemaker.core.spi.store.IPersistentDependencyStoreFactory#resetPersistentDependencyStore(org.bundlemaker.core.project.IProjectDescriptionAwareBundleMakerProject)
   */
  public void resetPersistentDependencyStore(IProjectDescriptionAwareBundleMakerProject project) throws CoreException {

    // step 1: dispose the cache if necessary
    if (_cache.containsKey(project)) {

      // get the store
      PersistentDependencyStoreImpl infoStore = _cache.get(project);

      // dispose the store if necessary
      if (infoStore.isInitialized()) {
        infoStore.dispose();
      }
    }

    // step 2: delete the existing '.bundlemaker/db4o.store' file
    IFile file = project.getProject().getFile(
        new Path(BundleMakerCore.BUNDLEMAKER_DIRECTORY_NAME).append(getFileName()));
    if (!file.getRawLocation().toFile().delete()) {
      System.out.println();
      System.out.println(String.format("Could not delete file '%s'", file.getRawLocation().toFile().getAbsolutePath()));
      System.out.println();
    }

    // step 3: re-init the dependency store
    if (_cache.containsKey(project)) {

      // get the store
      PersistentDependencyStoreImpl dependencyStore = _cache.get(project);

      // initialize the dependency store
      dependencyStore.init();
    }
  }

  public void releasePersistentDependencyStore(IProjectDescriptionAwareBundleMakerProject project) {
    // step 1: dispose the cache if necessary
    if (_cache.containsKey(project)) {

      // get the store
      PersistentDependencyStoreImpl storeImpl = _cache.get(project);

      // dispose the store if necessary
      if (storeImpl.isInitialized()) {
        storeImpl.dispose();
      }

      //
      _cache.remove(project);
    }

  }

  /**
   * @see org.bundlemaker.core.spi.store.IPersistentDependencyStoreFactory#getPersistentDependencyStore(org.bundlemaker.core.project.IProjectDescriptionAwareBundleMakerProject)
   */
  public IPersistentDependencyStore getPersistentDependencyStore(IProjectDescriptionAwareBundleMakerProject project) {

    // step 1: return the cached version if one exists
    if (_cache.containsKey(project)) {

      //
      IPersistentDependencyStore dependencyStore = _cache.get(project);

      //
      if (!dependencyStore.isInitialized()) {
        dependencyStore.init();
      }

      //
      return dependencyStore;
    }

    // delete the old store
    if (DELETE_DEPENDENCYSTORE_IF_CORE_BUNDLE_VERSION_CHANGED) {
      try {
        IFile parent = project.getProject().getFile(new Path(BundleMakerCore.BUNDLEMAKER_DIRECTORY_NAME));
        File parentFile = parent.getRawLocation().toFile();
        for (String child : FileUtils.getAllChildren(parentFile)) {
          if (!child.endsWith(getFileName())) {
            boolean done = new File(parentFile, child).delete();
          }
        }
      } catch (CoreException e) {
        e.printStackTrace();
      }
    }

    // step 2: create a new store
    IFile file = project.getProject().getFile(
        new Path(BundleMakerCore.BUNDLEMAKER_DIRECTORY_NAME).append(getFileName()));
    PersistentDependencyStoreImpl store = new PersistentDependencyStoreImpl(file.getRawLocation().toOSString(),
        _bundleContext);

    // step 3: initialize the store
    store.init();

    // step 4: cache it
    _cache.put(project, store);

    // return the store
    return store;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public String getFileName() {

    //
    if (_fileName == null) {
      _fileName = DELETE_DEPENDENCYSTORE_IF_CORE_BUNDLE_VERSION_CHANGED ? String.format("%s_%s",
          PREFIX_BUNDLEMAKER_DB4O_STORE, BundleMakerCore.getVersion()) : PREFIX_BUNDLEMAKER_DB4O_STORE;
    }

    //
    return _fileName;
  }
}
