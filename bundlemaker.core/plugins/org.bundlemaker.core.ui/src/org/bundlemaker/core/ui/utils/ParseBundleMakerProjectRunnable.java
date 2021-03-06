/*******************************************************************************
 * Copyright (c) 2011 Bundlemaker project team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Bundlemaker project team - initial API and implementation
 ******************************************************************************/
package org.bundlemaker.core.ui.utils;

import java.lang.reflect.InvocationTargetException;

import org.bundlemaker.core.analysis.AnalysisCore;
import org.bundlemaker.core.analysis.IBundleMakerArtifact;
import org.bundlemaker.core.parser.IParserAwareBundleMakerProject;
import org.bundlemaker.core.project.BundleMakerProjectState;
import org.bundlemaker.core.resource.IModularizedSystem;
import org.bundlemaker.core.ui.ErrorDialogUtil;
import org.bundlemaker.core.ui.artifact.configuration.IArtifactModelConfigurationProvider;
import org.bundlemaker.core.ui.internal.Activator;
import org.bundlemaker.core.ui.internal.BundleMakerUiUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

/**
 * A {@link IRunnableWithProgress} that initializes and (re-parses) a bundlemaker project
 * 
 * @see BundleMakerProjectOpener for a convienence class that not only parses the project but also runs all required
 *      actions for the UI handling
 * 
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public class ParseBundleMakerProjectRunnable implements IRunnableWithProgress {

  private final IParserAwareBundleMakerProject _bundleMakerProject;

  private IBundleMakerArtifact                 _defaultModularizedSystem = null;

  /**
   * @param bundleMakerProject
   */
  public ParseBundleMakerProjectRunnable(IParserAwareBundleMakerProject bundleMakerProject) {
    _bundleMakerProject = bundleMakerProject;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
   */
  @Override
  public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
    try {
      _bundleMakerProject.initialize(monitor);

      // Check if initializing was successful
      BundleMakerProjectState state = _bundleMakerProject.getState();
      if (state == BundleMakerProjectState.INITIALIZED | state == BundleMakerProjectState.READY) {
        // parse the project
        _bundleMakerProject.parseAndOpen(monitor);
      } else {
        return;
      }

      _defaultModularizedSystem = getDefaultModularizedSystemArtifact(_bundleMakerProject, monitor);

    } catch (Exception ex) {
      // Forward exception
      throw new InvocationTargetException(ex);
    }
  }

  /**
   * Initialize and parse the specified project
   * 
   * <p>
   * Errors happening during the operation are reported via Error Log and an Error Dialog
   * 
   * @param bundleMakerProject
   * @return true if the project has been successfully opened
   */
  public static IBundleMakerArtifact parseProject(IParserAwareBundleMakerProject bundleMakerProject) {
    // Create runnable
    ParseBundleMakerProjectRunnable runnable = new ParseBundleMakerProjectRunnable(bundleMakerProject);

    // Execute runnable via IProgressService
    try {
      PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnable);
    } catch (InvocationTargetException ex) {
      // Report Error to error log
      Throwable cause = ex.getCause();
      BundleMakerUiUtils.logError("Error while parsing project: " + cause, cause);

      // Report error to user
      Throwable throwable = ErrorDialogUtil.getNestedNonCoreThrowable(cause);
      ErrorDialogUtil.errorDialogWithStackTrace("Error while parsing project", throwable.getMessage(),
          Activator.PLUGIN_ID,
          throwable);

      // MessageDialog.openError(Display.getCurrent().getActiveShell(), ,
      // String.format(:%n%s%nSee Error Log for details", cause));

    } catch (InterruptedException ex) {
      // ignore. User has canceled the operation
    }

    // Refresh navigator tree
    BundleMakerUiUtils.refreshProjectExplorer();

    return runnable._defaultModularizedSystem;
  }

  /**
   * <p>
   * </p>
   * 
   * @param bundleMakerProject
   * @param monitor
   * @return
   * @throws CoreException
   */
  protected static IBundleMakerArtifact getDefaultModularizedSystemArtifact(
      IParserAwareBundleMakerProject bundleMakerProject,
      IProgressMonitor monitor)
      throws CoreException {

    if (monitor != null) {
      monitor.subTask("Creating default Analysis Model");
    }

    IArtifactModelConfigurationProvider artifactModelConfigurationProvider = Activator.getDefault()
        .getArtifactModelConfigurationProvider();

    IModularizedSystem modularizedSystem = bundleMakerProject.getModularizedSystemWorkingCopy();

    //
    return AnalysisCore.getAnalysisModel(modularizedSystem, artifactModelConfigurationProvider
        .getArtifactModelConfiguration(), monitor);
  }

}
