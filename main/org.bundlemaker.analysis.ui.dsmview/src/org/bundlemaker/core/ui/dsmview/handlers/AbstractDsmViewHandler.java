package org.bundlemaker.core.ui.dsmview.handlers;

import java.util.List;

import org.bundlemaker.analysis.model.IArtifact;
import org.bundlemaker.analysis.ui.handlers.AbstractArtifactBasedHandler;
import org.bundlemaker.core.ui.dsmview.DSMArtifactModelEditor;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.part.NullEditorInput;

public abstract class AbstractDsmViewHandler extends AbstractArtifactBasedHandler {

  private IEditorInput nullInputEditor = new NullEditorInput();

  @Override
  protected void execute(ExecutionEvent event, List<IArtifact> selectedArtifacts) throws Exception {

    // get the artifacts that should be displayed in DSM View
    List<IArtifact> artifactsForDsmView = getArtifactsForDsmView(selectedArtifacts);

    // make sure the editor and views are visible
    openEditorAndViews(artifactsForDsmView);
  }

  /**
   * Return the IArtifacts from the list of selected artifacts that should be added to the DSM view.
   * 
   * @param selectedArtifacts
   * @return
   */
  protected abstract List<IArtifact> getArtifactsForDsmView(List<IArtifact> selectedArtifacts);

  private void openEditorAndViews(List<IArtifact> selectedArtifacts) {
    IWorkbenchPage page = getActiveWorkbenchPage();
    if (page != null) {
      try {
        IEditorPart editorPart = page.openEditor(nullInputEditor, DSMArtifactModelEditor.ID);
        if (!(editorPart instanceof DSMArtifactModelEditor)) {
          System.err.println("EditorPart " + editorPart + " is not a DSMViewNeu?");
          return;
        }

        DSMArtifactModelEditor genericEditor = (DSMArtifactModelEditor) editorPart;
        genericEditor.useArtifacts(selectedArtifacts);

      } catch (PartInitException e) {
        e.printStackTrace();
      }
    }
  }

  private IWorkbenchPage getActiveWorkbenchPage() {
    IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
    if (workbenchWindow != null) {
      IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
      if (workbenchPage != null) {
        return workbenchPage;
      }
    }
    return null;
  }
}
