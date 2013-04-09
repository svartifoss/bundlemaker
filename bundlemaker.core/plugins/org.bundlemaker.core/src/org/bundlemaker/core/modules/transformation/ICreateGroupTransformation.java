package org.bundlemaker.core.modules.transformation;

import org.eclipse.core.runtime.IPath;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public interface ICreateGroupTransformation extends ITransformation {

  IPath getParentGroupPath();

  IPath getGroupPath();
}