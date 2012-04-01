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
package org.bundlemaker.core.ui.editor.xref;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bundlemaker.core.analysis.IBundleMakerArtifact;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Filter for IArtifact trees. This filter gets a whitelist of IArtifacts given in the constructor. Only these artifacts
 * and their parent nodes are displayed in the tree, everything else is hidden.
 * 
 * @author Frank Schlueter
 */
public class DependentArtifactsFilter extends ViewerFilter {

  private Set<IBundleMakerArtifact> dependencies = new HashSet<IBundleMakerArtifact>();

  public DependentArtifactsFilter(List<IBundleMakerArtifact> dependencies) {
    this.dependencies.addAll(dependencies);
    for (IBundleMakerArtifact dependency : dependencies) {
      IBundleMakerArtifact parent = dependency.getParent();
      while (parent != null) {
        this.dependencies.add(parent);
        parent = parent.getParent();
      }
    }
  }

  @Override
  public boolean select(Viewer viewer, Object parentElement, Object element) {
    if (dependencies.contains(element)) {
      return true;
    } else {
      return false;
    }
  }

}