package org.bundlemaker.core.itestframework.jedit_model;

import org.bundlemaker.core.analysis.IBundleMakerArtifact;
import org.bundlemaker.core.analysis.IDependency;
import org.bundlemaker.core.modules.IModularizedSystem;
import org.junit.Assert;

public class TOOLS {

  /**
   * <p>
   * </p>
   * 
   * @param artifact
   * @param count
   */
  public void assertArtifactChildrenCount(IBundleMakerArtifact artifact, int count) {
    Assert.assertEquals(artifact.getChildren().toString(), count, artifact.getChildren().size());
  }

  /**
   * <p>
   * </p>
   * 
   * @param from
   * @param to
   * @param weight
   */
  public void assertDependencyWeight(IBundleMakerArtifact from, IBundleMakerArtifact to, int weight) {
    IDependency dependency = from.getDependencyTo(to);

    if (weight == 0) {
      Assert.assertNull(dependency);
    } else {
      Assert.assertEquals(weight, dependency.getWeight());
    }
  }

  public void assertArtifactHasParent(IBundleMakerArtifact child, IBundleMakerArtifact parent) {
    Assert.assertEquals(parent, child.getParent());
  }

  /**
   * <p>
   * </p>
   * 
   * @param typeCountWithoutJdkTypes
   */
  public static void assertTypeCount(IModularizedSystem modularizedSystem, int typeCountWithoutJdkTypes) {

    // assert the specified number of types
    Assert.assertEquals(
        "Expected: "
            + typeCountWithoutJdkTypes
            + ", actual: "
            + (modularizedSystem.getTypes().size() - modularizedSystem.getExecutionEnvironment().getContainedTypes()
                .size()), modularizedSystem.getExecutionEnvironment().getContainedTypes().size()
            + typeCountWithoutJdkTypes, modularizedSystem.getTypes().size());
  }

  /**
   * <p>
   * </p>
   * 
   * @param count
   */
  public static void assertModificationCount(AbstractJeditAnalysisModelTest test, int count) {
    Assert.assertEquals(count, test.getSrcHierarchicalModel().getModifiedNotificationCount());
    Assert.assertEquals(count, test.getSrcFlatModel().getModifiedNotificationCount());
    Assert.assertEquals(count, test.getBinHierarchicalModel().getModifiedNotificationCount());
    Assert.assertEquals(count, test.getBinFlatModel().getModifiedNotificationCount());
  }
}