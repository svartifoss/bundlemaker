package org.bundlemaker.core.internal.parser;

import java.util.List;

import org.bundlemaker.core.internal.resource.Resource;
import org.bundlemaker.core.internal.resource.ResourceStandin;
import org.bundlemaker.core.internal.store.IPersistentDependencyStore;
import org.bundlemaker.core.resource.IResourceKey;
import org.bundlemaker.core.resource.ResourceKey;
import org.bundlemaker.core.resource.modifiable.IModifiableResource;
import org.bundlemaker.core.util.StopWatch;
import org.eclipse.core.runtime.CoreException;
import org.junit.Test;

public class ResourceCacheTest {

  private int count = 0;

  @Test
  public void testResourceCache() throws CoreException {

    ResourceCache cache = new ResourceCache(new Dummy());

    KeyCreator keyCreator = new KeyCreator() {
      @Override
      public IResourceKey createResourceKey(String contentId, String root, String path) {
        return new ResourceKey(contentId, root, path);
      }
    };

    long time_1 = create(cache, keyCreator, "a", 500000);
    long time_2 = create(cache, keyCreator, "b", 500000);
    long time_3 = create(cache, keyCreator, "a", 500000);

    System.out.println(time_1 + " : " + time_2 + " : " + time_3);

  }

  @Test
  public void testResourceCache_ResourceStandin() throws CoreException {

    ResourceCache cache = new ResourceCache(new Dummy());

    KeyCreator keyCreator = new KeyCreator() {
      @Override
      public IResourceKey createResourceKey(String contentId, String root, String path) {
        return new ResourceStandin(contentId, root, path);
      }
    };

    long time_1 = create(cache, keyCreator, "a", 500000);
    long time_2 = create(cache, keyCreator, "b", 500000);
    long time_3 = create(cache, keyCreator, "a", 500000);

    System.out.println(time_1 + " : " + time_2 + " : " + time_3);
  }

  @Test
  public void testResourceCache_Mixed() throws CoreException {

    ResourceCache cache = new ResourceCache(new Dummy());

    KeyCreator keyCreator = new KeyCreator() {
      @Override
      public IResourceKey createResourceKey(String contentId, String root, String path) {
        return new ResourceStandin(contentId, root, path);
      }
    };

    long time_1 = create(cache, keyCreator, "a", 500000);

    keyCreator = new KeyCreator() {
      @Override
      public IResourceKey createResourceKey(String contentId, String root, String path) {
        return new ResourceKey(contentId, root, path);
      }
    };

    long time_2 = create(cache, keyCreator, "b", 500000);
    long time_3 = create(cache, keyCreator, "a", 500000);

    System.out.println(time_1 + " : " + time_2 + " : " + time_3);
  }

  /**
   * <p>
   * </p>
   * 
   * @param cache
   * @return
   * @throws CoreException
   */
  private long create(ResourceCache cache, KeyCreator keyCreator, String prefix, int aCount) throws CoreException {

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    for (int i = 0; i < aCount; i++) {

      IResourceKey key = keyCreator.createResourceKey(prefix + i, prefix + i, prefix + i);

      cache.getOrCreateResource(key);
    }

    stopWatch.stop();

    return stopWatch.getElapsedTime();
  }

  // TODO: EasyMock
  private class Dummy implements IPersistentDependencyStore {

    @Override
    public void delete(Resource resource) {
      // TODO Auto-generated method stub
    }

    @Override
    public List<Resource> getResources() {
      return null;
    }

    @Override
    public void updateResource(IModifiableResource resource) {
      ResourceCacheTest.this.count++;
    }

    @Override
    public void commit() {
    }

    @Override
    public void init() {
      // TODO Auto-generated method stub

    }

    @Override
    public boolean isInitialized() {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public void dispose() {
      // TODO Auto-generated method stub

    }

  }

  /**
   * <p>
   * </p>
   * 
   * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
   */
  private interface KeyCreator {

    public IResourceKey createResourceKey(String contentId, String root, String path);
  }
}