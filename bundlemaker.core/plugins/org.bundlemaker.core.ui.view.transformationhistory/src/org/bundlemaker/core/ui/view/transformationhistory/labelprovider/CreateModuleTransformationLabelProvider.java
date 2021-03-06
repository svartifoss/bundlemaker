/*******************************************************************************
 * Copyright (c) 2012 BundleMaker Project Team
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Nils Hartmann - initial API and implementation
 ******************************************************************************/
package org.bundlemaker.core.ui.view.transformationhistory.labelprovider;

import org.bundlemaker.core.resource.ITransformationCreateModule;
import org.eclipse.swt.graphics.Image;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public class CreateModuleTransformationLabelProvider extends
    AbstractTransformationLabelProvider<ITransformationCreateModule> {

  /**
   * @param supportedType
   */
  public CreateModuleTransformationLabelProvider() {
    super(ITransformationCreateModule.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bundlemaker.core.ui.transformations.history.labelprovider.AbstractTransformationLabelProvider#getTitle(org.
   * bundlemaker.core.transformation.ITransformation)
   */
  @Override
  protected String getTitle(ITransformationCreateModule transformation) {
    return "Create Module";
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bundlemaker.core.ui.transformations.history.labelprovider.AbstractTransformationLabelProvider#getTitleImage
   * (org.bundlemaker.core.transformation.ITransformation)
   */
  @Override
  protected Image getTitleImage(ITransformationCreateModule transformation) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bundlemaker.core.ui.transformations.history.labelprovider.AbstractTransformationLabelProvider#getDetails(org
   * .bundlemaker.core.transformation.ITransformation)
   */
  @Override
  protected String getDetails(ITransformationCreateModule transformation) {
    return transformation.getModuleArtifact().getAssociatedModule().getModuleIdentifier().toString();
  }

}
