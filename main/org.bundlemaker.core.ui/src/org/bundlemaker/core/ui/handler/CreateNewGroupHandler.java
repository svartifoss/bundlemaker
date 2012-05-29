package org.bundlemaker.core.ui.handler;

import org.bundlemaker.core.analysis.ArtifactType;
import org.bundlemaker.core.analysis.IBundleMakerArtifact;
import org.bundlemaker.core.analysis.IGroupAndModuleContainer;
import org.bundlemaker.core.analysis.IGroupArtifact;
import org.bundlemaker.core.analysis.IRootArtifact;
import org.bundlemaker.core.ui.artifact.CommonNavigatorUtils;
import org.bundlemaker.core.ui.validators.NonEmptyStringValidator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public class CreateNewGroupHandler extends AbstractBundleMakerHandler {

  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute(ExecutionEvent event, ISelection selection) throws Exception {
    IStructuredSelection structuredSelection = (IStructuredSelection) selection;
    IBundleMakerArtifact artifact = (IBundleMakerArtifact) structuredSelection.getFirstElement();

    if (artifact instanceof IRootArtifact || artifact instanceof IGroupArtifact) {

      // JFace Input Dialog
      InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), "", "Enter group name", "GROUP",
          NonEmptyStringValidator.instance());

      if (dlg.open() == Window.OK) {
        System.out.println(dlg.getValue());
      }

      // we have to use "getOrCreateGroup" to prevent duplicate groups
      IGroupAndModuleContainer advancedArtifact = ((IGroupAndModuleContainer) artifact);
      advancedArtifact.getOrCreateGroup(new Path(dlg.getValue()));

      // update navigator
      // TODO
      CommonNavigatorUtils.refresh("org.eclipse.ui.navigator.ProjectExplorer",
          artifact.getType().equals(ArtifactType.Root) ? artifact : artifact.getParent(ArtifactType.Root));
    }
  }
}