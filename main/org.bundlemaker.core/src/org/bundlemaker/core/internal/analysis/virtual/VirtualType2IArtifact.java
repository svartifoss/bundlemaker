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
package org.bundlemaker.core.internal.analysis.virtual;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bundlemaker.analysis.model.ArtifactType;
import org.bundlemaker.analysis.model.IArtifact;
import org.bundlemaker.analysis.model.IDependency;
import org.bundlemaker.analysis.model.IDependencyModel;
import org.bundlemaker.analysis.model.impl.AbstractArtifact;
import org.bundlemaker.core.analysis.IArtifactTreeVisitor;
import org.bundlemaker.core.analysis.IRootArtifact;
import org.bundlemaker.core.analysis.ITypeArtifact;
import org.bundlemaker.core.internal.analysis.AbstractAdvancedContainer;
import org.bundlemaker.core.internal.analysis.AdapterUtils;
import org.bundlemaker.core.modules.IModularizedSystem;
import org.bundlemaker.core.modules.modifiable.IMovableUnit;
import org.bundlemaker.core.resource.IResource;
import org.bundlemaker.core.resource.IType;
import org.eclipse.core.runtime.Assert;

/**
 * 
 */
public class VirtualType2IArtifact extends AbstractArtifact implements IMovableUnit, ITypeArtifact {

  /** - */
  private String _fullyQualifiedName;

  /**
   * <p>
   * </p>
   * 
   * @param type
   * @param classification
   */
  public VirtualType2IArtifact(String name, String fullyQualifiedName, IArtifact parent) {

    super(ArtifactType.Type, name);

    Assert.isNotNull(parent);

    // set parent/children dependency
    setParent(parent);
    ((AbstractAdvancedContainer) parent).getModifiableChildren().add(this);

    _fullyQualifiedName = fullyQualifiedName;
  }

  @Override
  public List<IArtifact> invalidateDependencyCache() {
    return Arrays.asList(new IArtifact[] { this });
  }

  @Override
  public Map<IArtifact, IDependency> getCachedDependencies() {
    return Collections.emptyMap();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<IResource> getAssociatedBinaryResources() {
    return Collections.emptyList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IResource getAssociatedSourceResource() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<IType> getAssociatedTypes() {
    return Collections.emptyList();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getQualifiedName() {
    return _fullyQualifiedName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IRootArtifact getRoot() {
    return (IRootArtifact) getParent(ArtifactType.Root);
  }

  @Override
  public boolean removeArtifact(IArtifact artifact) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addArtifact(IArtifact artifact) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<IArtifact> getChildren() {
    return Collections.emptySet();
  }

  @Override
  public boolean contains(IArtifact artifact) {
    return this.equals(artifact);
  }

  @Override
  public IDependency getDependency(IArtifact artifact) {
    return null;
  }

  @Override
  public Collection<IDependency> getDependencies() {
    return Collections.emptyList();
  }

  @Override
  public Collection<IArtifact> getLeafs() {
    // simply return null
    return null;
  }

  @Override
  public IModularizedSystem getModularizedSystem() {
    return AdapterUtils.getModularizedSystem(this);
  }

  @Override
  public IDependencyModel getDependencyModel() {
    return ((AbstractAdvancedContainer) getParent(ArtifactType.Root)).getDependencyModel();
  }

  @Override
  public boolean canAdd(IArtifact artifact) {
    return false;
  }

  @Override
  public IArtifact getChild(String path) {
    return null;
  }

  @Override
  public IType getAssociatedType() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(IArtifactTreeVisitor visitor) {
    //
    visitor.visit(this);
  }

  @Override
  public boolean isVirtual() {
    return true;
  }
}