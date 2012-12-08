/*******************************************************************************
 * Copyright (c) 2012 Bundlemaker project team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Bundlemaker project team - initial API and implementation
 ******************************************************************************/
package org.bundlemaker.core.ui.transformations.runner;

import org.bundlemaker.core.transformations.script.runner.AbstractTransformationScriptLogger;
import org.bundlemaker.core.ui.transformations.console.TransformationScriptConsole;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public class TransformationScriptConsoleLogger extends AbstractTransformationScriptLogger {

  /*
   * (non-Javadoc)
   * 
   * @see org.bundlemaker.core.transformations.script.AbstractTransformationScriptLogger#doLog(java.lang.String)
   */
  @Override
  protected void doLog(String msg) {
    TransformationScriptConsole.instance().append(msg);

  }

}
