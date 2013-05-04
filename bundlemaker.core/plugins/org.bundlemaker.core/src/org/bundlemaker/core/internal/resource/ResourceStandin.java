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
package org.bundlemaker.core.internal.resource;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bundlemaker.core._type.IReference;
import org.bundlemaker.core._type.IType;
import org.bundlemaker.core.internal.modules.modularizedsystem.ModularizedSystem;
import org.bundlemaker.core.internal.projectdescription.IResourceStandin;
import org.bundlemaker.core.modules.IModularizedSystem;
import org.bundlemaker.core.modules.IModule;
import org.bundlemaker.core.resource.IMovableUnit;
import org.bundlemaker.core.resource.IResource;
import org.bundlemaker.core.resource.ResourceKey;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class ResourceStandin extends ResourceKey implements IResourceStandin {

  /** - */
  private Resource       _resource;

  /** - */
  private Set<IResource> _stickyResourceStandins;

  /**
   * <p>
   * Creates a new instance of type {@link ResourceStandin}.
   * </p>
   * 
   * @param resource
   */
  public ResourceStandin(Resource resource) {

    this(nullCheck(resource).getProjectContentEntryId(), nullCheck(resource).getRoot(), nullCheck(resource).getPath());

    resource.setResourceStandin(this);
    setResource(resource);
  }

  /**
   * <p>
   * Creates a new instance of type {@link ResourceStandin}.
   * </p>
   * 
   * @param contentId
   * @param root
   * @param path
   * @param archiveFileCache
   */
  public ResourceStandin(String contentId, String root, String path) {
    super(contentId, root, path);
  }

  @Override
  public IModule getModule(IModularizedSystem modularizedSystem) {

    //
    return ((ModularizedSystem) modularizedSystem).getAssociatedResourceModule(this);
  }

  public IResource getResource() {
    return _resource;
  }

  /**
   * <p>
   * </p>
   * 
   * @param resource
   */
  public void setResource(Resource resource) {
    _resource = resource;
  }

  @Override
  public int compareTo(IResource other) {

    if (!getProjectContentEntryId().equals(other.getProjectContentEntryId())) {
      return getProjectContentEntryId().compareTo(other.getProjectContentEntryId());
    }
    if (!getRoot().equals(other.getRoot())) {
      return getRoot().compareTo(other.getRoot());
    }
    if (!getPath().equals(other.getPath())) {
      return getPath().compareTo(other.getPath());
    }

    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IMovableUnit getMovableUnit(IModularizedSystem modularizedSystem) {

    //
    if (_resource == null) {
      // TODO
      throw new RuntimeException();
    }

    return _resource.getMovableUnit(modularizedSystem);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Set<IReference> getReferences() {

    //
    if (_resource == null) {
      // TODO
      throw new RuntimeException();
    }

    return _resource.getReferences();
  }

  @Override
  public Set<IType> getContainedTypes() {

    //
    if (_resource == null) {
      // TODO
      throw new RuntimeException();
    }

    return _resource.getContainedTypes();
  }

  @Override
  public IType getContainedType() throws CoreException {

    //
    if (_resource == null) {
      // TODO
      throw new RuntimeException();
    }

    return _resource.getContainedType();
  }

  @Override
  public boolean containsTypes() {

    //
    if (_resource == null) {
      // TODO
      throw new RuntimeException();
    }

    return _resource.containsTypes();
  }

  @Override
  public IType getPrimaryType() {
    //
    if (_resource == null) {
      // TODO
      throw new RuntimeException();
    }

    return _resource.getPrimaryType();
  }

  @Override
  public boolean isPrimaryType(IType type) {
    //
    if (_resource == null) {
      // TODO
      throw new RuntimeException();
    }

    return _resource.isPrimaryType(type);
  }

  public boolean hasPrimaryType() {
    return _resource.hasPrimaryType();
  }

  @Override
  public Set<IResource> getStickyResources() {

    //
    if (_resource == null) {
      throw new RuntimeException();
    }

    //
    if (_resource.getStickyResources().isEmpty()) {
      return Collections.emptySet();
    }

    // lazy init
    if (_stickyResourceStandins == null) {

      // create new set
      _stickyResourceStandins = new HashSet<IResource>();

      // add resource standins
      for (IResource resource : _resource.getStickyResources()) {
        _stickyResourceStandins.add(((Resource) resource).getResourceStandin());
      }
    }

    return _stickyResourceStandins;
  }

  /**
   * <p>
   * </p>
   * 
   * @param resource
   * @return
   */
  private static Resource nullCheck(Resource resource) {
    Assert.isNotNull(resource, "Parameter resource must not be null.");
    return resource;
  }
}
