package org.bundlemaker.core.internal.transformation.add;

import org.bundlemaker.core.internal.modules.modifiable.IModifiableModularizedSystem;
import org.bundlemaker.core.modules.IModularizedSystem;
import org.eclipse.core.runtime.Assert;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 * 
 */
public class ModelNotificationSuppressor {

  /**
   * <p>
   * </p>
   * 
   * @param modularizedSystem
   * @param runnable
   */
  public static void performWithoutNotification(IModularizedSystem modularizedSystem, Runnable runnable) {
    Assert.isNotNull(modularizedSystem);
    Assert.isNotNull(runnable);

    try {

      // we have to set the model modification handling to 'false'
      ((IModifiableModularizedSystem) modularizedSystem).setHandleModelModification(false);

      //
      runnable.run();

    } finally {

      // we have to set the model modification handling to 'true'
      ((IModifiableModularizedSystem) modularizedSystem).setHandleModelModification(true);

    }
  }
}
