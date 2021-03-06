package org.bundlemaker.core.ui.app.perspective;

import org.bundlemaker.core.ui.view.dependencytable.DependencyTableView;
import org.bundlemaker.core.ui.view.dependencytree.DependencyTreeView;
import org.bundlemaker.core.ui.view.problemview.ProblemView;
import org.bundlemaker.core.ui.view.stage.view.StageView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class BundleMakerPerspectiveFactory implements IPerspectiveFactory {

  @Override
  public void createInitialLayout(IPageLayout layout) {

    // add wizard shortcut
    layout.addNewWizardShortcut("org.bundlemaker.core.ui.wizards.newwizard");

    //
    String editorArea = layout.getEditorArea();
    IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.26f, editorArea);
    left.addView(IPageLayout.ID_PROJECT_EXPLORER);
    left.addView(IPageLayout.ID_OUTLINE);
    
    IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.66f, "left");
    bottomLeft.addView(StageView.ID);

    IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.66f, editorArea);
    bottom.addView(DependencyTreeView.ID);
    bottom.addView(DependencyTableView.ID);
    bottom.addView(ProblemView.PROBLEM_VIEW_ID);
    bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
  }
}
