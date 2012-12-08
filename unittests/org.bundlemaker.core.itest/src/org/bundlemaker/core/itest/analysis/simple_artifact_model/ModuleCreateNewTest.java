package org.bundlemaker.core.itest.analysis.simple_artifact_model;

import org.bundlemaker.core.analysis.IModuleArtifact;
import org.bundlemaker.core.itest.analysis.framework.ArtifactVisitorUtils;
import org.bundlemaker.core.itest.analysis.simple_artifact_model.framework.AbstractSimpleArtifactModelTest;
import org.bundlemaker.core.modules.ModuleIdentifier;
import org.bundlemaker.core.modules.modifiable.IModifiableResourceModule;
import org.eclipse.core.runtime.Path;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class ModuleCreateNewTest extends AbstractSimpleArtifactModelTest {

  /**
   * <p>
   * Tests if the artifact models are updated correct if a resource module is added in the resource model.
   * </p>
   * 
   * @throws Exception
   */
  @Test
  public void createNewModuleInResourceModel() throws Exception {

    //
    assertResourceModuleCountInModularizedSystem(1);
    assertResourceModuleCount(_binModel, 1);
    assertResourceModuleCount(_srcModel, 1);

    //
    IModifiableResourceModule resourceModule = getModularizedSystem().createResourceModule(
        new ModuleIdentifier("test", "1.2.3"));
    Assert.assertNull(resourceModule.getClassification());

    // assert that we have three groups
    Assert.assertEquals(2, getModularizedSystem().getGroups().size());
    assertResourceModuleCount(_binModel, 2);
    assertResourceModuleCount(_srcModel, 2);
  }

  /**
   * <p>
   * </p>
   * 
   * @throws Exception
   */
  @Test
  public void createNewModuleBelowExistingGroup() throws Exception {

    //
    assertResourceModuleCountInModularizedSystem(1);
    assertResourceModuleCount(_binModel, 1);
    assertResourceModuleCount(_srcModel, 1);

    // create a new group
    IModuleArtifact newModuleArtifact = _binModel.getGroup2Artifact().getOrCreateModule("NewModule", "1.0.0");
    Assert.assertEquals("group1/group2/NewModule_1.0.0", newModuleArtifact.getQualifiedName());

    // assert that we have three groups
    Assert.assertEquals(2, getModularizedSystem().getGroups().size());
    assertResourceModuleCount(_binModel, 2);
    assertResourceModuleCount(_srcModel, 2);

    //
    IModuleArtifact srcModule = ArtifactVisitorUtils.findModuleArtifact(_binModel.getRootArtifact(),
        new ModuleIdentifier("NewModule", "1.0.0"));
    IModuleArtifact binModule = ArtifactVisitorUtils.findModuleArtifact(_srcModel.getRootArtifact(),
        new ModuleIdentifier("NewModule", "1.0.0"));

    //
    Assert.assertNotNull(srcModule);
    Assert.assertNotNull(binModule);
    
    //
    Assert.assertNotNull(srcModule.getParent());
    Assert.assertNotNull(binModule.getParent());
    Assert.assertEquals(srcModule.getParent().getName(), "group2");
    Assert.assertEquals(binModule.getParent().getName(), "group2");
  }

  /**
   * <p>
   * </p>
   * 
   * @throws Exception
   */
  @Test
  public void createNewModuleBelowRoot() throws Exception {

    //
    assertResourceModuleCountInModularizedSystem(1);
    assertResourceModuleCount(_binModel, 1);
    assertResourceModuleCount(_srcModel, 1);

    // create a new group
    IModuleArtifact newModuleArtifact = _binModel.getRootArtifact().getOrCreateModule("NewModule", "1.0.0");
    Assert.assertEquals("NewModule_1.0.0", newModuleArtifact.getQualifiedName());

    // assert that we have three groups
    Assert.assertEquals(2, getModularizedSystem().getGroups().size());
    assertResourceModuleCount(_binModel, 2);
    assertResourceModuleCount(_srcModel, 2);
  }
}