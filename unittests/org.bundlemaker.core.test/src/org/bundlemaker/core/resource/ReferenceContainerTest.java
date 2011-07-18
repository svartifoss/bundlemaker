package org.bundlemaker.core.resource;

import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.bundlemaker.core.internal.resource.FlyWeightCache;
import org.bundlemaker.core.internal.resource.Reference;
import org.bundlemaker.core.internal.resource.ReferenceContainer;
import org.bundlemaker.core.resource.ReferenceType;
import org.bundlemaker.core.resource.modifiable.ReferenceAttributes;
import org.junit.Before;
import org.junit.Test;

public class ReferenceContainerTest {

  private ReferenceContainer _referenceContainer;

  private Set<Reference>     _references;

  /**
   * <p>
   * </p>
   */
  @Before
  public void pre() {
    // set up
    FlyWeightCache flyWeightCache = new FlyWeightCache();
    _references = new HashSet<Reference>();
    _referenceContainer = new ReferenceContainer(flyWeightCache) {

      @Override
      protected Set<Reference> createReferencesSet() {
        return _references;
      }
    };
  }

  @Test
  public void test() {

    // test: assert same reference
    for (int i = 0; i < 10; i++) {
      _referenceContainer.recordReference("a.b.c", new ReferenceAttributes(ReferenceType.TYPE_REFERENCE, true, true,
          false, false, false, false, false));
    }

    // assert
    Assert.assertEquals(1, _references.size());

    // test: assert same reference with different attributes
    _referenceContainer.recordReference("a.b.c", new ReferenceAttributes(ReferenceType.TYPE_REFERENCE, true, true,
        true, false, false, false, false));
    _referenceContainer.recordReference("a.b.c", new ReferenceAttributes(ReferenceType.TYPE_REFERENCE, true, false,
        true, true, false, false, false));

    // assert
    Assert.assertEquals(1, _references.size());

    // test: assert same reference with different attributes
    _referenceContainer.recordReference("a.b.c.d", new ReferenceAttributes(ReferenceType.TYPE_REFERENCE, true, true,
        true, false, false, false, false));
    _referenceContainer.recordReference("a.b.c.f", new ReferenceAttributes(ReferenceType.TYPE_REFERENCE, true, false,
        true, true, false, false, false));

    // assert
    Assert.assertEquals(3, _references.size());
    assertReference("a.b.c", ReferenceType.TYPE_REFERENCE, true, true, true, true, false);
    assertReference("a.b.c.d", ReferenceType.TYPE_REFERENCE, true, true, true, false, false);
    assertReference("a.b.c.f", ReferenceType.TYPE_REFERENCE, true, false, true, true, false);
  }

  /**
   * <p>
   * </p>
   * 
   */
  @Test
  public void referenceUpdate() {

    _referenceContainer.recordReference("a.b.c", new ReferenceAttributes(ReferenceType.TYPE_REFERENCE, true, true,
        false, false, true, false, false));

    _referenceContainer.recordReference("a.b.c", new ReferenceAttributes(ReferenceType.TYPE_REFERENCE, true, true,
        true, false, false, false, false));

    _referenceContainer.recordReference("a.b.c", new ReferenceAttributes(ReferenceType.TYPE_REFERENCE, true, false,
        false, false, false, false, false));

    Assert.assertEquals(1, _references.size());

    assertReference("a.b.c", ReferenceType.TYPE_REFERENCE, true, true, true, false, true);
  }

  /**
   * <p>
   * </p>
   * 
   * @param fullyQualifiedName
   * @param referenceType
   * @param isExtends
   * @param isImplements
   * @param isCompiletime
   * @param isRuntime
   */
  private void assertReference(String fullyQualifiedName, ReferenceType referenceType, boolean isExtends,
      boolean isImplements, boolean isClassAnnotation, boolean isCompiletime, boolean isRuntime) {

    // assert
    for (Reference reference : _references) {

      if (reference.getFullyQualifiedName().equals(fullyQualifiedName)) {

        Assert.assertEquals(referenceType, reference.getReferenceType());
        Assert.assertEquals(reference.isExtends(), isExtends);
        Assert.assertEquals(reference.isImplements(), isImplements);
        Assert.assertEquals(reference.isClassAnnotation(), isClassAnnotation);
        Assert.assertEquals(reference.isSourceReference(), isCompiletime);
        Assert.assertEquals(reference.isBinaryReference(), isRuntime);
      }
    }
  }
}