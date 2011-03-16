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
package org.bundlemaker.core.modules.query;

public class StringQueryFilters {

  /** TRUE_QUERY_FILTER */
  public static IQueryFilter<String> TRUE_QUERY_FILTER = new IQueryFilter<String>() {

                                                         @Override
                                                         public boolean matches(String content) {
                                                           return true;
                                                         }
                                                       };
}
