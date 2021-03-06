package org.bundlemaker.core.itest.misc_models;

import java.util.Set;

import org.bundlemaker.core.jtype.IType;
import org.bundlemaker.core.jtype.ITypeSelector;
import org.bundlemaker.core.project.IProjectContentEntry;
import org.bundlemaker.core.project.IProjectDescription;
import org.bundlemaker.core.resource.IModule;
import org.eclipse.core.runtime.Assert;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class AmbiguousTypesTest_TestTypeSelector implements ITypeSelector {

  /** - */
  public static final String  BUNDLEMAKER_INTERNAL_JDK_MODULE_IDENTIFIER = "#####BUNDLEMAKER_INTERNAL_JDK_MODULE_IDENTIFIER#####";

  /** - */
  private IProjectDescription _bundleMakerProjectDescription;

  /** - */
  private boolean             _preferJdkTypes;

  /**
   * <p>
   * Creates a new instance of type {@link AmbiguousTypesTest_TestTypeSelector}.
   * </p>
   * 
   * @param bundleMakerProjectDescription
   */
  public AmbiguousTypesTest_TestTypeSelector(IProjectDescription bundleMakerProjectDescription) {

    Assert.isNotNull(bundleMakerProjectDescription);

    _bundleMakerProjectDescription = bundleMakerProjectDescription;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public final boolean isPreferJdkTypes() {
    return _preferJdkTypes;
  }

  /**
   * <p>
   * </p>
   * 
   * @param preferJdkTypes
   */
  public final void setPreferJdkTypes(boolean preferJdkTypes) {
    _preferJdkTypes = preferJdkTypes;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IType selectType(IModule module, String fullyQualifiedName, Set<IType> types) {

    //
    int currentIndex = Integer.MAX_VALUE;
    IType currentType = null;
    IType jdkType = null;

    for (IType iType : types) {

      //
      String identifier = iType.getProjectContentEntryId().toString();

      // get the content identifier
      if (!identifier.equals(AmbiguousTypesTest_TestTypeSelector.BUNDLEMAKER_INTERNAL_JDK_MODULE_IDENTIFIER)) {

        // get the file based content
        IProjectContentEntry fileBasedContent = _bundleMakerProjectDescription.getProjectContentEntry(identifier);

        // get the index
        int index = _bundleMakerProjectDescription.getContent().indexOf(fileBasedContent);

        //
        if (index < currentIndex) {
          currentIndex = index;
          currentType = iType;
        }
      } else {
        jdkType = iType;
      }
    }

    if (_preferJdkTypes) {
      return jdkType != null ? jdkType : currentType;
    } else {
      return currentType != null ? currentType : jdkType;
    }
  }
}
