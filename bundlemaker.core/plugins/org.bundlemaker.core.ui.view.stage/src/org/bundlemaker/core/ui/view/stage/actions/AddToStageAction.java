/*******************************************************************************
 * Copyright (c) 2013 Bundlemaker project team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Bundlemaker project team - initial API and implementation
 ******************************************************************************/

package org.bundlemaker.core.ui.view.stage.actions;

import org.bundlemaker.core.selection.IArtifactSelection;
import org.bundlemaker.core.ui.view.ArtifactStageActionHelper;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public class AddToStageAction extends AbstractStageAction {

  public AddToStageAction() {
    super("Selected Artifact");
  }

  @Override
  public void setArtifactSelection(IArtifactSelection artifactSelection) {
    super.setArtifactSelection(artifactSelection);

    setEnabled(artifactSelection.hasSelectedArtifacts());

    if (artifactSelection.hasSelectedArtifacts()) {
      setText("Add " + artifactSelection.getSelectedArtifacts().size() + " Artifacts to Stage");
    } else {
      setText("Selected Artifacts");
    }

  }

  @Override
  public void run() {
    if (!ArtifactStageActionHelper.switchToManualAddModeIfRequired()) {
      return;
    }

    addToStage(getArtifactSelection().getSelectedArtifacts());

  }

}
