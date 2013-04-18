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

package org.bundlemaker.core.ui.editor.xref3;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bundlemaker.core.analysis.IBundleMakerArtifact;
import org.bundlemaker.core.analysis.IResourceArtifact;
import org.bundlemaker.core.ui.operations.MoveArtifactsOperation;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public class MoveReferencedArtifactsAction extends Action {

  private final Set<IBundleMakerArtifact> _referencedArtifacts;

  public MoveReferencedArtifactsAction(Set<IBundleMakerArtifact> set) {
    super("Move referenced Artifacts");
    _referencedArtifacts = set;
    setEnabled(_referencedArtifacts != null && !_referencedArtifacts.isEmpty());
  }

  @Override
  public void run() {
    List<IBundleMakerArtifact> resources = new LinkedList<IBundleMakerArtifact>();
    for (IBundleMakerArtifact artifact : _referencedArtifacts) {
      if (artifact.isInstanceOf(IResourceArtifact.class)) {
        resources.add(artifact);
      }
    }

    MoveArtifactsOperation moveArtifactsOperation = new MoveArtifactsOperation(Display.getCurrent().getActiveShell(),
        resources);
    moveArtifactsOperation.run();

  }
}
