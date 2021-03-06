package org.bundlemaker.core.itest.simple_artifact_model.analysis;

import static org.bundlemaker.core.itestframework.simple_artifact_model.ArtifactAssert.assertResourceModuleCount;
import static org.bundlemaker.core.itestframework.simple_artifact_model.ArtifactAssert.assertResourceModuleCountInModularizedSystem;

import org.bundlemaker.core.analysis.IModuleArtifact;
import org.bundlemaker.core.itestframework.simple_artifact_model.AbstractSimpleArtifactModelTest;
import org.bundlemaker.core.itestframework.simple_artifact_model.NoModificationAssertion;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class Undo_ModuleGetExisting_Test extends AbstractSimpleArtifactModelTest {

  @Test
  public void createNewModuleWithGroupBelowExistingGroup() throws Exception {

    //
    NoModificationAssertion.assertNoModification(this, new NoModificationAssertion.Action() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void prePostCondition() {
        Assert.assertEquals(2, getModularizedSystem().getGroups().size());
        assertResourceModuleCountInModularizedSystem(getModularizedSystem(), 2);
        assertResourceModuleCount(getBinModel(), 2);
        assertResourceModuleCount(getSrcModel(), 2);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void execute() {
        
        IModuleArtifact moduleArtifact = getBinModel().getRootArtifact().getOrCreateModule(
            "group1/group2/SimpleArtifactModelTest", "1.0.0");
        
        Assert.assertEquals("group1/group2/SimpleArtifactModelTest_1.0.0", moduleArtifact.getQualifiedName());
      }
    });
  }
}
