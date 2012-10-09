package org.bundlemaker.core.analysis;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bundlemaker.analysis.model.IDependency;
import org.bundlemaker.core.modules.IModularizedSystem;
import org.eclipse.core.runtime.IPath;

/**
 * <p>
 * Common interface for all nodes in a BundleMaker artifact tree.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public interface IBundleMakerArtifact extends Comparable<IBundleMakerArtifact> {

  /**
   * <p>
   * Returns the simple name of the artifact. For a detailed description of the semantic of <code>getName()</code> and
   * <code>getQualifiedName()</code> see {@link IBundleMakerArtifact#getQualifiedName()}.
   * </p>
   * 
   * @return the simple name of the artifact.
   */
  public String getName();

  /**
   * <p>
   * Returns the fully qualified name of the artifact:
   * <table border="1">
   * <tr>
   * <th>Type</th>
   * <th>getName()</th>
   * <th>getQualifiedName()</th>
   * </tr>
   * <tr>
   * <td>Root</td>
   * <td colspan="2">Both the the qualified name and the (simple) name return the name of the root artifact that was
   * specified when the root was created.<br/>
   * <b>Example:</b> <code>TransformedSystem</code>, <code>MySystem</code></td>
   * </tr>
   * <tr>
   * <td>Group</td>
   * <td>The name is the name of the current artifact element.<br/>
   * <b>Example:</b> <code>group2</code>.</td>
   * <td>The qualified name is the full group path delimited by '|'.<br/>
   * <b>Example:</b> <code>group1|group2</code></td>
   * </tr>
   * <tr>
   * <td>Module</td>
   * <td colspan="2">Both the the qualified name and the (simple) name return the name of the module.<br/>
   * <b>Example:</b> <code>MyModule</code>, <code>MyModule_1.0.0</code></td>
   * <tr>
   * <td>Package</td>
   * <td>The name is the last segment of the current package.<br/>
   * <b>Example:</b> <code>core</code></td>
   * <td>The qualified name is the package delimited by '.'.<br/>
   * <b>Example:</b> <code>org.bundlemaker.core</code></td>
   * </tr>
   * <tr>
   * <td>Resource</td>
   * <td colspan="2">Both the the qualified name and the (simple) name return the name of the Resource without the path<br/>
   * <b>Example:</b> <code>MyType.class</code>, <code>MyImage.png</code>
   * </tr>
   * <tr>
   * <td>Type</td>
   * <td>The name is the simple type name.<br/>
   * <b>Example:</b> <code>MyType</code></td>
   * <td>The qualified name is the fully qualified type name delimited by '.'.<br/>
   * <b>Example:</b> <code>org.bundlemaker.core.MyType</code></td>
   * </tr>
   * </table>
   * </p>
   * 
   * @return the fully qualified name of the artifact.
   */
  public String getQualifiedName();

  /**
   * <p>
   * Returns {@code true}, if this object is an instance of the specified class.
   * </p>
   * 
   * @param clazz
   *          the class
   * @return {@code true}, if this object is an instance of the specified class.
   */
  public boolean isInstanceOf(Class<? extends IBundleMakerArtifact> clazz);

  /**
   * <p>
   * Returns this object as the specified type.
   * </p>
   * 
   * @param clazz
   *          type
   * @return this object as the specified type.
   */
  public <T extends IBundleMakerArtifact> T castTo(Class<? extends IBundleMakerArtifact> clazz);

  /**
   * <p>
   * Returns the root artifact for this artifact node.
   * </p>
   * 
   * @return the root artifact.
   */
  IRootArtifact getRoot();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  IArtifactModelConfiguration getConfiguration();

  /**
   * <p>
   * Returns the {@link IModularizedSystem} this artifact belongs to.
   * </p>
   * 
   * @return the modularized system, never null
   */
  IModularizedSystem getModularizedSystem();

  /**
   * <p>
   * </p>
   * 
   * @param artifact
   * @return
   */
  boolean isParent(IBundleMakerArtifact artifact);

  /**
   * <p>
   * Returns the parent artifact of this artifact or <code>null</code> if this artifact is the root artifact.
   * </p>
   * 
   * @return the parent artifact of this artifact or <code>null</code> if this artifact is the root artifact.
   */
  IBundleMakerArtifact getParent();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  boolean hasParent();

  /**
   * <p>
   * </p>
   * 
   * @param type
   * @return
   */
  <T extends IBundleMakerArtifact> T getParent(Class<T> type);

  /**
   * <p>
   * Sets a property with the given key and value.
   * </p>
   * 
   * @param key
   *          the key
   * @param value
   *          the value
   */
  public void setProperty(Object key, Object value);

  /**
   * <p>
   * Returns the value of the property with the specified key.
   * </p>
   * 
   * @param key
   *          the key of the property
   * @return the value of the property with the specified key.
   */
  public String getProperty(Object key);

  /**
   * <p>
   * Returns the property with the given key and the specified type.
   * </p>
   * 
   * @param <T>
   * @param key
   * @param t
   * @return the property with the given key and the specified type.
   */
  public <T> T getProperty(Object key, Class<T> t);

  /**
   * <p>
   * </p>
   * 
   * @param artifact
   * @return
   */
  boolean canAdd(IBundleMakerArtifact artifact);

  /**
   * <p>
   * Adds the given artifact to this artifact.
   * </p>
   * 
   * @param artifact
   *          the artifact to add.
   */
  public void addArtifact(IBundleMakerArtifact artifact);

  /**
   * <p>
   * Adds the given artifacts to this artifact.
   * </p>
   * 
   * @param artifacts
   *          the artifacts to add.
   */
  public void addArtifacts(List<? extends IBundleMakerArtifact> artifact);

  /**
   * <p>
   * Adds all artifacts selected by the specified {@link IArtifactSelector} to this {@link IBundleMakerArtifact}.
   * </p>
   * 
   * @param artifactSelector
   *          the {@link IArtifactSelector}.
   */
  public void addArtifacts(IArtifactSelector artifactSelector);

  /**
   * <p>
   * </p>
   * 
   * @param artifact
   * @return
   */
  boolean canRemove(IBundleMakerArtifact artifact);

  /**
   * <p>
   * Removes the given artifact from this artifact.
   * </p>
   * 
   * @param artifact
   *          the {@link IBundleMakerArtifact} to remove
   * @return
   */
  public boolean removeArtifact(IBundleMakerArtifact artifact);

  /**
   * <p>
   * Removes the specified {@link IBundleMakerArtifact IBundleMakerArtifacts} from this {@link IBundleMakerArtifact}.
   * </p>
   * 
   * @param artifacts
   *          the artifacts to remove
   */
  public void removeArtifacts(List<? extends IBundleMakerArtifact> artifacts);

  /**
   * <p>
   * Removes all artifacts selected by the specified {@link IArtifactSelector} from this {@link IBundleMakerArtifact}.
   * </p>
   * 
   * @param artifactSelector
   *          the {@link IArtifactSelector}
   */
  public void removeArtifacts(IArtifactSelector artifactSelector);

  /**
   * <p>
   * Returns an unmodifiable {@link Collection} with all (direct) children of this {@link IArtifact}.
   * </p>
   * 
   * @return an unmodifiable {@link Collection} with all (direct) children of this {@link IArtifact}.
   */
  Collection<IBundleMakerArtifact> getChildren();

  /**
   * <p>
   * Returns an unmodifiable {@link Collection} with all (direct) children of this {@link IArtifact} of the given type.
   * </p>
   * 
   * @return an unmodifiable {@link Collection} with all (direct) children of this {@link IArtifact} of the given type.
   */
  <T extends IBundleMakerArtifact> Collection<T> getChildren(Class<T> clazz);

  /**
   * <p>
   * Returns all the dependencies that this artifact contains.
   * </p>
   * 
   * @return
   */
  public Collection<IDependency> getDependencies();

  /**
   * <p>
   * Returns the dependency to the given Artifact.
   * </p>
   * 
   * @param artifact
   *          the artifact
   * 
   * @return the dependency to the given Artifact.
   */
  public IDependency getDependency(IBundleMakerArtifact artifact);

  /**
   * <p>
   * Returns all the dependencies to the given collection of artifacts.
   * </p>
   * 
   * @param artifacts
   *          the {@link IBundleMakerArtifact IBundleMakerArtifacts}
   * @return all the dependencies to the given collection of artifacts.
   */
  public Collection<? extends IDependency> getDependencies(Collection<? extends IBundleMakerArtifact> artifacts);

  /**
   * {@inheritDoc}
   */
  public Collection<? extends IDependency> getDependencies(IBundleMakerArtifact... artifacts);

  /**
   * <p>
   * </p>
   * 
   * @param visitor
   */
  void accept(IArtifactTreeVisitor visitor);

  /**
   * <p>
   * </p>
   * 
   * @param visitors
   */
  void accept(IArtifactTreeVisitor... visitors);

  /***********************/
  /** REMOVE **/
  /***********************/

  /**
   * <p>
   * Returns all leafs of this artifact.
   * </p>
   * 
   * @return
   */
  // replace with getChildren(ArtifactType)
  @Deprecated
  public Collection<IBundleMakerArtifact> getLeafs();

  /**
   * <p>
   * Returns the path identifier for this artifact.
   * </p>
   * 
   * @return
   */
  @Deprecated
  String getUniquePathIdentifier();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  @Deprecated
  IPath getFullPath();

  @Deprecated
  IBundleMakerArtifact getChild(String path);

  /**
   * <p>
   * </p>
   * 
   * @param clazz
   * @param path
   * 
   * @param <T>
   * @return
   */
  @Deprecated
  <T extends IBundleMakerArtifact> T getChildByPath(Class<T> clazz, IPath path);

  /**
   * <p>
   * Returns <code>true</code>, if this artifact node is <i>virtual</i>. A virtual artifact node has no associated
   * resource element.
   * </p>
   * 
   * @return
   */
  @Deprecated
  boolean isVirtual();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  @Deprecated
  boolean isMovable();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  @Deprecated
  boolean containsTypesOrResources();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  @Deprecated
  boolean containsTypes();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  @Deprecated
  boolean containsResources();

  /**
   * <p>
   * </p>
   * 
   */
  List<IBundleMakerArtifact> invalidateDependencyCache();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  Map<IBundleMakerArtifact, IDependency> getCachedDependencies();

  /**
   * <p>
   * Returns <code>true</code> if this artifact contains the specified artifact, <code>false</code> otherwise.
   * </p>
   * 
   * @param the
   *          artifact
   * @return
   */
  @Deprecated
  // must not check for leafs
  // replace with visitor
  public boolean contains(IBundleMakerArtifact artifact);
}
