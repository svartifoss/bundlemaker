package org.bundlemaker.core.itest.complex.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.bundlemaker.analysis.model.ArtifactType;
import org.bundlemaker.analysis.model.IArtifact;
import org.bundlemaker.core.analysis.ArtifactModelConfiguration;
import org.bundlemaker.core.analysis.ArtifactUtils;
import org.bundlemaker.core.analysis.IBundleMakerArtifact;
import org.bundlemaker.core.analysis.IModuleArtifact;
import org.bundlemaker.core.analysis.IRootArtifact;
import org.bundlemaker.core.analysis.ITypeArtifact;
import org.bundlemaker.core.analysis.ModelTransformer;
import org.bundlemaker.core.itest.AbstractModularizedSystemTest;
import org.eclipse.core.runtime.CoreException;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class ComplexArtifactTreeTest extends AbstractModularizedSystemTest {

  /**
   * <p>
   * </p>
   * 
   */
  @Test
  public void testModule_GetOrCreateModule() {

    // Step 1: transform the model
    IRootArtifact rootArtifact = getModularizedSystem().getArtifactModel(
        ArtifactModelConfiguration.AGGREGATE_INNER_TYPES_NO_RESOURCES_CONFIGURATION);
    Assert.assertNotNull(rootArtifact);

    IModuleArtifact moduleArtifact = rootArtifact.findChild(IModuleArtifact.class, "jedit_1.0.0");
    List<ITypeArtifact> types = moduleArtifact.findChildren(ITypeArtifact.class);

    IModuleArtifact newModule = null;
    for (ITypeArtifact typeArtifact : types) {
      newModule = rootArtifact.getOrCreateModule("DEV/FRAMEWORK/de.test", "1.23");
      newModule.addArtifact(typeArtifact);
    }
    ArtifactUtils.dumpArtifact(newModule);
  }

  @Test
  public void testModule_GetOrCreateModule_DeprecatedAPI() {

    // Step 1: transform the model
    IRootArtifact rootArtifact = getModularizedSystem().getArtifactModel(
        ArtifactModelConfiguration.AGGREGATE_INNER_TYPES_NO_RESOURCES_CONFIGURATION);
    Assert.assertNotNull(rootArtifact);

    //
    ArtifactUtils.dumpArtifact(rootArtifact);
    
    Collection<IArtifact> leafs = rootArtifact.getLeafs();
    IModuleArtifact newModule = null;
    for (IArtifact typeArtifact : leafs) {
      IArtifact moduleArtifact = typeArtifact.getParent(ArtifactType.Module);
      
      if (((IModuleArtifact)moduleArtifact).isResourceModule()) {
        newModule = rootArtifact.getOrCreateModule("DEV/FRAMEWORK/de.test_1.2.3", "1.2.3");
        newModule.addArtifact(typeArtifact);
      }
    }

    ArtifactUtils.dumpArtifact(newModule);
  }

  /**
   * <p>
   * </p>
   * 
   * @throws CoreException
   * @throws IOException
   */
  @Test
  public void testSourceNonHierarchical() throws CoreException, IOException {

    // Step 1: transform the model
    IBundleMakerArtifact rootArtifact = (IBundleMakerArtifact) ModelTransformer.getDependencyModel(
        getModularizedSystem(), ArtifactModelConfiguration.SOURCE_RESOURCES_CONFIGURATION).getRoot();
    Assert.assertNotNull(rootArtifact);

    // get the 'jedit' artifact...
    IBundleMakerArtifact artifact = rootArtifact.getChild("group1|group2|jedit_1.0.0");
    Assert.assertNotNull(artifact);

    // assert the result
    InputStream inputstream = getClass().getResourceAsStream(
        "results/ComplexArtifactTreeTest_SourceNonHierarchical.txt");
    assertResult(ArtifactUtils.artifactToString(artifact), inputstream,
        "ComplexArtifactTreeTest_SourceNonHierarchical_" + getCurrentTimeStamp());
  }

  /**
   * <p>
   * </p>
   * 
   * @throws CoreException
   * @throws IOException
   */
  @Test
  public void testBinaryNonHierarchical() throws CoreException, IOException {

    // transform the model...
    IBundleMakerArtifact rootArtifact = (IBundleMakerArtifact) ModelTransformer.getDependencyModel(
        getModularizedSystem(), ArtifactModelConfiguration.BINARY_RESOURCES_CONFIGURATION).getRoot();
    Assert.assertNotNull(rootArtifact);

    // get the 'jedit' artifact...
    IBundleMakerArtifact artifact = rootArtifact.getChild("group1|group2|jedit_1.0.0");
    Assert.assertNotNull(artifact);

    // assert the result
    InputStream inputstream = getClass().getResourceAsStream(
        "results/ComplexArtifactTreeTest_BinaryNonHierarchical.txt");
    assertResult(ArtifactUtils.artifactToString(artifact), inputstream,
        "ComplexArtifactTreeTest_BinaryNonHierarchical_" + getCurrentTimeStamp());
  }

  @Test
  public void testMoveModule() throws CoreException, IOException {

    // Step 1: transform the model
    IBundleMakerArtifact rootArtifact = (IBundleMakerArtifact) ModelTransformer.getDependencyModel(
        getModularizedSystem(), ArtifactModelConfiguration.SOURCE_RESOURCES_CONFIGURATION).getRoot();
    Assert.assertNotNull(rootArtifact);

    // get the 'jedit' artifact...
    IBundleMakerArtifact moduleArtifact = rootArtifact.getChild("group1|group2|jedit_1.0.0");
    Assert.assertNotNull(moduleArtifact);

    // get the 'group1Artifact' artifact...
    IArtifact group1Artifact = rootArtifact.getChild("group1");
    Assert.assertNotNull(moduleArtifact);

    moduleArtifact.getParent(ArtifactType.Group).removeArtifact(moduleArtifact);
    group1Artifact.addArtifact(moduleArtifact);

    // assert the result
    InputStream inputstream = getClass().getResourceAsStream("results/ComplexArtifactTreeTest_MoveModule.txt");
    assertResult(ArtifactUtils.artifactToString(moduleArtifact), inputstream, "ComplexArtifactTreeTest_MoveModule_"
        + getCurrentTimeStamp());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String computeTestProjectName() {
    return "jedit";
  }
}
