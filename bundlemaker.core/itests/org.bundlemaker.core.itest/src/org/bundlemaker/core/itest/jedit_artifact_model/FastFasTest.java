package org.bundlemaker.core.itest.jedit_artifact_model;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.bundlemaker.core.analysis.AnalysisModelQueries;
import org.bundlemaker.core.analysis.IPackageArtifact;
import org.bundlemaker.core.analysis.algorithms.AdjacencyMatrix;
import org.bundlemaker.core.analysis.algorithms.FastFAS;
import org.bundlemaker.core.itestframework.jedit_model.AbstractJeditAnalysisModelTest;
import org.junit.Test;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class FastFasTest extends AbstractJeditAnalysisModelTest {

  /**
   * <p>
   * </p>
   * 
   * @throws Exception
   */
  @Test
  public void fastFasTest() throws Exception {

    IPackageArtifact packageArtifact = AnalysisModelQueries.findPackageArtifactByQualifiedName(
        getBinHierarchicalModel().getJeditModuleArtifact(), "org.gjt.sp.jedit");

    Assert.assertNotNull(packageArtifact);

    //
    List<IPackageArtifact> packages = new LinkedList<IPackageArtifact>(
        packageArtifact.getChildren(IPackageArtifact.class));
    Assert.assertEquals(22, packages.size());

    //
    FastFAS fastFAS = new FastFAS(AdjacencyMatrix.computeAdjacencyMatrix(null, packages));

    //
    int[] ordered = fastFAS.getOrderedSequence();
    for (int i = 1; i <= ordered.length; i++) {
      System.out.println(packages.get(ordered[ordered.length - i]));
    }
  }
}
