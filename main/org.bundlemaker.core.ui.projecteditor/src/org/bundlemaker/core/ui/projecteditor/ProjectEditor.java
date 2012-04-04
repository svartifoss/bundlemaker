/**
 * 
 */
package org.bundlemaker.core.ui.projecteditor;

import org.bundlemaker.core.BundleMakerCore;
import org.bundlemaker.core.IBundleMakerProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * <p>
 * Provides a graphical editor for BundleMaker's project artifacts
 * </p>
 * 
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public class ProjectEditor extends FormEditor {

  private IBundleMakerProject _bundleMakerProject;

  public ProjectEditor() {
    super();
  }

  @Override
  protected void addPages() {
    try {
      addPage(new ProjectEditorPage(this));
    } catch (Exception ex) {
      Activator.logError("Could not add page to editor", ex);
    }

  }

  @Override
  public void doSave(IProgressMonitor monitor) {
    // commit all editor pages
    commitPages(true);

    // save the project
    try {
      _bundleMakerProject.getModifiableProjectDescription().save();

      // propagate new dirty state
      editorDirtyStateChanged();
    } catch (Exception ex) {
      Activator.logError("Error while saving project: " + ex, ex);
    }

  }

  @Override
  public void doSaveAs() {
    // "Save As" is not allowed.
  }

  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  /**
   * Opens the {@link IBundleMakerProject}
   */
  @Override
  public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    super.init(site, input);

    IFileEditorInput adapter = (IFileEditorInput) input.getAdapter(IFileEditorInput.class);

    if (adapter == null) {
      Activator.logErrorMessage("Unsupported EditorInput '%s' cannot be adapted to '%s'", input,
          IFileEditorInput.class.getName());
      return;
    }

    // Open the BundleMaker project
    IProject project = adapter.getFile().getProject();

    setPartName(project.getName());
    try {
      // TODO use ProgressMonitor
      IBundleMakerProject bundleMakerProject = BundleMakerCore
          .getBundleMakerProject(project, new NullProgressMonitor());

      _bundleMakerProject = bundleMakerProject;

    } catch (Exception ex) {
      throw new PartInitException("Could not open BundleMaker project", ex);
    }

  }

  /**
   * Returns the {@link IBundleMakerProject} that this editor is working on.
   * <p>
   * </p>
   * 
   * @return The IBundleMakerProject instance. Never null.
   */
  public IBundleMakerProject getBundleMakerProject() {
    Assert.isNotNull(_bundleMakerProject);
    return this._bundleMakerProject;
  }

}