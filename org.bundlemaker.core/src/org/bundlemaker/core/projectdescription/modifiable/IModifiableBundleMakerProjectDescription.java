/*******************************************************************************
 * Copyright (c) 2011 Gerd Wuetherich (gerd@gerd-wuetherich.de).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Wuetherich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package org.bundlemaker.core.projectdescription.modifiable;

import java.util.List;

import org.bundlemaker.core.internal.projectdescription.BundleMakerProjectDescription;
import org.bundlemaker.core.projectdescription.IBundleMakerProjectDescription;
import org.bundlemaker.core.projectdescription.IFileBasedContent;
import org.eclipse.core.runtime.CoreException;

/**
 * <p>
 * Defines the interface of a bundle maker project description.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IModifiableBundleMakerProjectDescription extends IBundleMakerProjectDescription {

  /**
   * <p>
   * Sets the JRE:
   * </p>
   * 
   * @param jre
   */
  void setJre(String jre);

  /**
   * <p>
   * Returns a (modifiable) list with all the defined {@link IModifiableFileBasedContent}.
   * </p>
   * 
   * @return a (modifiable) list with all the defined {@link IModifiableFileBasedContent}.
   */
  List<? extends IModifiableFileBasedContent> getModifiableFileBasedContent();

  /**
   * <p>
   * Returns the {@link IModifiableFileBasedContent} with the specified identifier.
   * </p>
   * 
   * @param id
   *          the identifier
   * @return the {@link IModifiableFileBasedContent} with the specified identifier.
   */
  IModifiableFileBasedContent getModifiableFileBasedContent(String id);

  /**
   * <p>
   * Adds a new type content entry with the specified binary root. The name and the version of the content entry are
   * automatically extracted from the binary root.
   * </p>
   * 
   * @param binaryRoot
   *          the binary root.
   */
  IModifiableFileBasedContent addTypeContent(String binaryRoot);

  /**
   * <p>
   * Adds a new type content entry with the specified name, version and binary root.
   * </p>
   * 
   * @param name
   *          the name of the content entry
   * @param version
   *          the version of the content entry
   * @param binaryRoot
   *          the binary root
   */
  IModifiableFileBasedContent addTypeContent(String name, String version, String binaryRoot);

  /**
   * <p>
   * Adds a new type content entry with the specified name, version and binary roots.
   * </p>
   * 
   * @param name
   *          the name of the content entry
   * @param version
   *          the version of the content entry
   * @param binaryRoots
   *          the binary roots
   */
  IModifiableFileBasedContent addTypeContent(String name, String version, List<String> binaryRoots);

  /**
   * <p>
   * Adds a new resource content entry with the specified binary. The name and the version of the content entry are
   * automatically extracted from the binary root.
   * </p>
   * <p>
   * This method is a convenience method an fully equivalent to <code><pre>
   * addResourceContent(binaryRoot, null);
   * </pre></code>
   * </p>
   * 
   * @param binaryRoot
   */
  IModifiableFileBasedContent addResourceContent(String binaryRoot);

  /**
   * <p>
   * Adds a new resource content entry with the specified binary and source root. The name and the version of the
   * content entry are automatically extracted from the binary root.
   * </p>
   * 
   * @param binaryRoot
   * @param sourceRoot
   */
  IModifiableFileBasedContent addResourceContent(String binaryRoot, String sourceRoot);

  IModifiableFileBasedContent addResourceContent(String binaryRoot, String sourceRoot, boolean analyzeSource);

  /**
   * <p>
   * Adds a new resource content entry with the specified name, version, binary root.
   * </p>
   * 
   * @param name
   *          the name of the content entry
   * @param version
   *          the version of the content entry
   * @param binaryRoot
   */
  IModifiableFileBasedContent addResourceContent(String name, String version, String binaryRoot);

  /**
   * <p>
   * Adds a new resource content entry with the specified name, version, binary and source root.
   * </p>
   * 
   * @param name
   * @param version
   * @param binaryRoot
   * @param sourceRoot
   */
  IModifiableFileBasedContent addResourceContent(String name, String version, String binaryRoot, String sourceRoot);

  IModifiableFileBasedContent addResourceContent(String name, String version, String binaryRoot, String sourceRoot,
      boolean analyzeSource);

  /**
   * <p>
   * Adds a new resource content entry with the specified name, version, binary and source roots.
   * </p>
   * 
   * @param name
   * @param version
   * @param binaryRoots
   * @param sourceRoots
   */
  IModifiableFileBasedContent addResourceContent(String name, String version, List<String> binaryRoots,
      List<String> sourceRoots);

  IModifiableFileBasedContent addResourceContent(String name, String version, List<String> binaryRoots,
      List<String> sourceRoots, boolean analyzeSource);

  /**
   * <p>
   * Removes the content entry with the specified identifier.
   * </p>
   * 
   * @param identifier
   *          the identifier
   */
  void removeContent(String identifier);

  /**
   * <p>
   * Clears the list of content entries. After calling this method the list of content entries is empty.
   * </p>
   */
  void clear();

  /**
   * <p>
   * Saves the {@link BundleMakerProjectDescription}.
   * </p>
   * <p>
   * The project description is saved internally in the xml file
   * <code>"&lt;project-directory&gt;/.bundlemaker/projectdescription.xml"</code> . Note that it's not intended to
   * directly modify this file.
   * </p>
   * 
   * @throws CoreException
   * 
   * @precondition none
   */
  void save() throws CoreException;
}