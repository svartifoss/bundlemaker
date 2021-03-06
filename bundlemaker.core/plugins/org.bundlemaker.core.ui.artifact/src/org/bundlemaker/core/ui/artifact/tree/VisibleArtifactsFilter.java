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
package org.bundlemaker.core.ui.artifact.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bundlemaker.core.analysis.IBundleMakerArtifact;
import org.bundlemaker.core.analysis.IRootArtifact;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * <p>
 * Filter for IArtifact trees. This filter gets a white list of IArtifacts given in the constructor. Only these
 * artifacts and their parent nodes are displayed in the tree, everything else is hidden.
 * </p>
 * 
 * @author Frank Schlueter
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class VisibleArtifactsFilter extends ViewerFilter {

  /** - */
  private Set<IBundleMakerArtifact> _artifacts = new HashSet<IBundleMakerArtifact>();

  /**
   * <p>
   * Creates a new instance of type {@link VisibleArtifactsFilter}.
   * </p>
   * 
   * @param visibleArtifacts
   */
  public VisibleArtifactsFilter(Collection<IBundleMakerArtifact> visibleArtifacts) {

    // add all visible artifacts
    this._artifacts.addAll(visibleArtifacts);

    // add the parent nodes as well
    for (IBundleMakerArtifact dependency : visibleArtifacts) {
      IBundleMakerArtifact parent = dependency.getParent();
      while (parent != null) {
        this._artifacts.add((IBundleMakerArtifact) parent);
        parent = parent.getParent();
      }
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public Set<IBundleMakerArtifact> getArtifacts() {
    return Collections.unmodifiableSet(_artifacts);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean select(Viewer viewer, Object parentElement, Object element) {
    if (element instanceof IRootArtifact) {
      return true;
    } else if (_artifacts.contains(element)) {
      return true;
    } else {
      return false;
    }
  }

}
