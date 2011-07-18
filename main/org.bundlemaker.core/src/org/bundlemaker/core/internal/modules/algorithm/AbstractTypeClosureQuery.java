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
package org.bundlemaker.core.internal.modules.algorithm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bundlemaker.core.internal.modules.modularizedsystem.ModularizedSystem;
import org.bundlemaker.core.resource.IType;
import org.eclipse.core.runtime.Assert;

/**
 * <p>
 * </p>
 */
public abstract class AbstractTypeClosureQuery {

  /** - */
  private Map<String, IType> _typesMap;

  /** - */
  private ModularizedSystem  _modularizedSystem;

  /**
   * <p>
   * </p>
   */
  public AbstractTypeClosureQuery(ModularizedSystem modularizedSystem) {

    Assert.isNotNull(modularizedSystem);

    _modularizedSystem = modularizedSystem;

    _typesMap = new HashMap<String, IType>();
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  protected ModularizedSystem getModularizedSystem() {
    return _modularizedSystem;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  protected Map<String, IType> getTypesMap() {
    return _typesMap;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public Collection<IType> getTypes() {
    return _typesMap.values();
  }
}