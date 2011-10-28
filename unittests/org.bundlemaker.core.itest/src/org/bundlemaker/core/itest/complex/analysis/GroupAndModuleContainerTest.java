package org.bundlemaker.core.itest.complex.analysis;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.bundlemaker.analysis.model.IArtifact;
import org.bundlemaker.core.analysis.ArtifactUtils;
import org.bundlemaker.core.analysis.IGroupArtifact;
import org.bundlemaker.core.analysis.IModuleArtifact;
import org.bundlemaker.core.analysis.IRootArtifact;
import org.bundlemaker.core.modules.IModule;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

/**
 * <p>
 * Example: group1/group2/jedit_1.0.0 velocity_1.5 jdk16_jdk16 << Missing Types >>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class GroupAndModuleContainerTest extends AbstractJeditArtifactTest {

  /**
   * <p>
   * Creates a new group 'testGroup' (below root) and adds the 'Jedit' module to it without removing it from its parent.
   * It must be automatically removed from its former parent.
   * </p>
   */
  @Test
  public void testAddModuleWithoutRemove() {

    //
    IRootArtifact rootArtifact = (IRootArtifact) getRootArtifact();

    //
    IModuleArtifact moduleArtifact = rootArtifact.getOrCreateModule("groupTest1/groupTest2/MyModule", "1.0.0");
    assertNotNull(moduleArtifact);
    assertEquals("groupTest1/groupTest2/MyModule_1.0.0", moduleArtifact.getQualifiedName());
    
    IModuleArtifact module2Artifact = rootArtifact.getOrCreateModule("groupTest1/groupTest2/MyModule", "1.0.0");
    assertSame(module2Artifact, moduleArtifact);
    
    IGroupArtifact groupTest2Artifact = (IGroupArtifact) moduleArtifact.getParent();
    module2Artifact = groupTest2Artifact.getOrCreateModule("/groupTest1/groupTest2/MyModule", "1.0.0");
    assertSame(module2Artifact, moduleArtifact);
  }
}