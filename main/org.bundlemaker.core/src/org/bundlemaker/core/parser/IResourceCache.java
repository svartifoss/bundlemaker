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
package org.bundlemaker.core.parser;

import org.bundlemaker.core.resource.IResourceKey;
import org.bundlemaker.core.resource.modifiable.IModifiableResource;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public interface IResourceCache {

  /**
   * <p>
   * </p>
   * 
   * @param resourceKey
   * @return
   */
  IModifiableResource getOrCreateResource(IResourceKey resourceKey);
}