package org.bundlemaker.core.internal.analysis.transformer.caches;

import org.bundlemaker.analysis.model.IArtifact;
import org.bundlemaker.analysis.model.impl.AbstractArtifactContainer;
import org.bundlemaker.core.analysis.ArtifactModelConfiguration.ResourcePresentation;
import org.bundlemaker.core.internal.analysis.AdapterType2IArtifact;
import org.bundlemaker.core.internal.analysis.transformer.DefaultArtifactCache;
import org.bundlemaker.core.internal.analysis.transformer.ModulePackageKey;
import org.bundlemaker.core.internal.analysis.transformer.ModuleResourceKey;
import org.bundlemaker.core.modules.IModule;
import org.bundlemaker.core.modules.IResourceModule;
import org.bundlemaker.core.projectdescription.ContentType;
import org.bundlemaker.core.resource.IResource;
import org.bundlemaker.core.resource.IType;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class TypeCache extends AbstractArtifactCacheAwareGenericCache<IType, IArtifact> {

  /**
   * <p>
   * Creates a new instance of type {@link TypeCache}.
   * </p>
   * 
   * @param artifactCache
   */
  public TypeCache(DefaultArtifactCache artifactCache) {
    super(artifactCache);
  }

  @Override
  protected IArtifact create(IType type) {

    // get the associated resources
    IResource resource = null;
    if (getArtifactCache().getConfiguration().getResourcePresentation().equals(ResourcePresentation.ALL_RESOURCES)) {

      resource = getArtifactCache().getConfiguration().getContentType().equals(ContentType.SOURCE)
          && type.hasSourceResource() ? type.getSourceResource() : type.getBinaryResource();

    } else {
      resource = type.getBinaryResource();
    }

    // get the associated module
    IModule module = resource != null ? resource.getAssociatedResourceModule(getArtifactCache().getModularizedSystem())
        : type.getModule(getArtifactCache().getModularizedSystem());

    // get the parent
    AbstractArtifactContainer parent = null;

    if (module instanceof IResourceModule
        && getArtifactCache().getConfiguration().getResourcePresentation().equals(ResourcePresentation.ALL_RESOURCES)) {

      // get the module package
      ModuleResourceKey resourceKey = new ModuleResourceKey((IResourceModule) module, resource);

      //
      parent = getArtifactCache().getResourceCache().getOrCreate(resourceKey);

    } else {

      // get the module package
      ModulePackageKey modulePackageKey = new ModulePackageKey(module, type.getPackageName());

      // get the parent
      parent = getArtifactCache().getPackageCache().getOrCreate(modulePackageKey);
    }

    //
    IArtifact artifact = new AdapterType2IArtifact(type, getArtifactCache(), parent);

    //
    return artifact;
  }
}