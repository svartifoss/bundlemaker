package org.bundlemaker.core.osgi.internal.manifest;

import org.bundlemaker.core.modules.IModularizedSystem;
import org.bundlemaker.core.modules.IResourceModule;
import org.bundlemaker.core.osgi.manifest.IBundleManifestCreator;
import org.bundlemaker.core.osgi.manifest.IManifestPreferences;
import org.bundlemaker.core.osgi.utils.ManifestUtils;
import org.bundlemaker.core.util.StopWatch;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.ResourceType;
import org.drools.runtime.StatefulKnowledgeSession;

import com.springsource.util.osgi.manifest.BundleManifest;
import com.springsource.util.osgi.manifest.BundleManifestFactory;
import com.springsource.util.parser.manifest.ManifestContents;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class DroolsBasedBundleManifestCreator implements IBundleManifestCreator {

  /** - */
  private DroolsServiceAccessor _serviceAccessor;

  /** - */
  private KnowledgeBase         _knowledgeBase;

  /**
   * <p>
   * Creates a new instance of type {@link DroolsBasedBundleManifestCreator}.
   * </p>
   * 
   * @param serviceAccessor
   */
  public DroolsBasedBundleManifestCreator() {

    _serviceAccessor = new DroolsServiceAccessor();

    // init
    KnowledgeBaseConfiguration kbaseConfig = _serviceAccessor.getKnowledgeBaseFactoryService()
        .newKnowledgeBaseConfiguration(null, this.getClass().getClassLoader());

    _knowledgeBase = _serviceAccessor.getKnowledgeBaseFactoryService().newKnowledgeBase(kbaseConfig);

    KnowledgeBuilder kbuilder = _serviceAccessor.getKnowledgeBuilderFactoryService()
        .newKnowledgeBuilder(_knowledgeBase);

    //
    kbuilder.add(
        _serviceAccessor.getResourceFactoryService().newClassPathResource(
            "org/bundlemaker/core/osgi/manifest/rules/rules-exportPackage.drl", this.getClass().getClassLoader()),
        ResourceType.DRL);
    if (kbuilder.hasErrors()) {
      System.err.println(kbuilder.getErrors().toString());
    }

    //
    kbuilder.add(
        _serviceAccessor.getResourceFactoryService().newClassPathResource(
            "org/bundlemaker/core/osgi/manifest/rules/rules-importPackage.drl", this.getClass().getClassLoader()),
        ResourceType.DRL);
    if (kbuilder.hasErrors()) {
      System.err.println(kbuilder.getErrors().toString());
    }

    //
    kbuilder.add(
        _serviceAccessor.getResourceFactoryService().newClassPathResource(
            "org/bundlemaker/core/osgi/manifest/rules/rules-general.drl", this.getClass().getClassLoader()),
        ResourceType.DRL);
    if (kbuilder.hasErrors()) {
      System.err.println(kbuilder.getErrors().toString());
    }

    //
    _knowledgeBase.addKnowledgePackages(kbuilder.getKnowledgePackages());
  }

  public ManifestContents createManifest(IModularizedSystem modularizedSystem, IResourceModule resourceModule,
      BundleManifest manifestTemplate, BundleManifest originalManifest, IManifestPreferences manifestPreferences) {

    System.out.println("createManifest - start");
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    // create a new bundle manifest
    BundleManifest result = BundleManifestFactory.createBundleManifest();

    // execute the knowledge base
    StatefulKnowledgeSession ksession = _knowledgeBase.newStatefulKnowledgeSession();
    ksession.insert(new DelegatingModularizedSystem(modularizedSystem));
    ksession.insert(new DelegatingResourceModule(resourceModule));
    ksession.insert(new IdentifiableBundleManifest(result, IdentifiableBundleManifest.BUNDLE_MANIFEST));
    ksession.insert(new IdentifiableBundleManifest(manifestTemplate, IdentifiableBundleManifest.MANIFEST_TEMPLATE));
    ksession.insert(new IdentifiableBundleManifest(originalManifest, IdentifiableBundleManifest.ORIGINAL_MANIFEST));
    ksession.insert(manifestPreferences);
    ksession.insert(new ReferencesCache(modularizedSystem, resourceModule, manifestPreferences.isSourceManifest(),
        manifestPreferences.isSourceManifest()));

    ksession.fireAllRules();

    stopWatch.stop();
    System.out.println("createManifest - done");
    System.out.println(stopWatch.getElapsedTime());

    // get the new manifest contents
    return ManifestUtils.toManifestContents(result);
  }
}
