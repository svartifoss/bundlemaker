package org.bundlemaker.core.internal.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.FutureTask;

import org.bundlemaker.core.IBundleMakerProject;
import org.bundlemaker.core.common.ZipFileCache;
import org.bundlemaker.core.common.collections.GenericCache;
import org.bundlemaker.core.common.utils.StopWatch;
import org.bundlemaker.core.internal.BundleMakerProject;
import org.bundlemaker.core.internal.modelext.ModelExtFactory;
import org.bundlemaker.core.internal.resource.Resource;
import org.bundlemaker.core.parser.IProblem;
import org.bundlemaker.core.project.AnalyzeMode;
import org.bundlemaker.core.project.IProjectContentEntry;
import org.bundlemaker.core.project.IProjectContentResource;
import org.bundlemaker.core.project.internal.IResourceStandinNEW;
import org.bundlemaker.core.project.internal.ProjectContentEntry;
import org.bundlemaker.core.resource.IModuleResource;
import org.bundlemaker.core.spi.parser.IParsableResource;
import org.bundlemaker.core.spi.parser.IParser;
import org.bundlemaker.core.spi.parser.IParserFactory;
import org.bundlemaker.core.spi.store.IDependencyStore;
import org.bundlemaker.core.spi.store.IPersistentDependencyStore;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class ModelSetup {

  public static final boolean LOG          = true;

  /** THREAD_COUNT */
  private static final int    THREAD_COUNT = Runtime.getRuntime().availableProcessors();

  /** the bundle maker project */
  private IBundleMakerProject _bundleMakerProject;

  /**  */
  private List<IParser[]>     _parsers4threads;

  /**
   * <p>
   * Creates a new instance of type {@link ProjectParser}.
   * </p>
   * 
   * @param bundleMakerProject
   *          the bundle maker project
   */
  public ModelSetup(BundleMakerProject bundleMakerProject) {
    Assert.isNotNull(bundleMakerProject);

    // set the project
    _bundleMakerProject = bundleMakerProject;
  }

  /**
   * <p>
   * </p>
   * 
   * @param modifiableFileBasedContent
   * @param dependencyStore
   */
  public List<IProblem> setup(final List<IProjectContentEntry> projectContents,
      final IPersistentDependencyStore dependencyStore, IProgressMonitor mainMonitor)
      throws OperationCanceledException, CoreException {

    Assert.isNotNull(projectContents);
    Assert.isNotNull(dependencyStore);

    final List[] result = new List[1];

    // create new null monitor if necessary
    if (mainMonitor == null) {
      mainMonitor = new NullProgressMonitor();
    }

    // create the sub-monitor
    final SubMonitor progressMonitor = SubMonitor.convert(mainMonitor, 100);

    //
    setupParsers();

    //
    notifyParseStart();

    try {

      // ***********************************************************************************************
      // STEP 1: Read all the resources from the underlying dependency store and put it in a map
      // ***********************************************************************************************
      mainMonitor.subTask("Reading from datastore...");

      // execute as loggable action...
      final Map<IProjectContentResource, Resource> storedResourcesMap = StaticLog.log(LOG, "Reading from datastore",
          new LoggableAction<Map<IProjectContentResource, Resource>>() {
            @Override
            public Map<IProjectContentResource, Resource> execute() {
              return readFromDependencyStore(dependencyStore, progressMonitor.newChild(10));
            }
          });

      // ***********************************************************************************************
      // STEP 2: Perform up-to-date check and parse new or modified resources
      // ***********************************************************************************************
      // create the resource cache that holds all resources that must be stored
      mainMonitor.subTask("Reparsing...");
      final ResourceCache resourceCache = new ResourceCache(dependencyStore);

      // execute as loggable action...
      StaticLog.log(LOG, "Compare and update...", new LoggableAction<Void>() {
        @Override
        public Void execute() {
          result[0] = compareAndUpdate(projectContents, storedResourcesMap, resourceCache, progressMonitor.newChild(60));
          return null;
        }
      });

      // ***********************************************************************************************
      // STEP 3: Update dependency store
      // ***********************************************************************************************

      mainMonitor.subTask("Writing to disc...");
      // execute as loggable action...
      StaticLog.log(LOG, "Writing to disc...", new LoggableAction<Void>() {
        @Override
        public Void execute() throws CoreException {
          resourceCache.commit(progressMonitor.newChild(25));
          deleteResourcesFromDependencyStore(storedResourcesMap.values(), dependencyStore, progressMonitor.newChild(5));
          return null;
        }
      });

      //
      for (IProjectContentEntry contentEntry : projectContents) {
        ModelExtFactory.getModelExtensionFactory().resourceModelSetupCompleted(contentEntry,
            (Collection<IModuleResource>) contentEntry.getBinaryResources(),
            (Collection<IModuleResource>) contentEntry.getSourceResources());
      }

      progressMonitor.worked(1);

    } finally {
      progressMonitor.done();
    }

    //
    notifyParseStop();

    //
    return result[0];
  }

  /**
   * <p>
   * </p>
   * 
   * @param projectContents
   * @param storedResourcesMap
   * @param resourceCache
   * @param mainMonitor
   */
  private List<IProblem> compareAndUpdate(List<IProjectContentEntry> projectContents,
      Map<IProjectContentResource, Resource> storedResourcesMap, ResourceCache resourceCache,
      IProgressMonitor mainMonitor) {

    //
    List<IProblem> result = Collections.emptyList();

    //
    StopWatch stopWatch = null;

    //
    int contentCount = projectContents.size();
    SubMonitor subMonitor = SubMonitor.convert(mainMonitor, contentCount);

    try {

      // activate the zip cache. We need this here to keep the
      // zip files open while parsing the content
      ZipFileCache.instance().activateCache();

      // ITERATE OVER ALL THE CONTENT ENTRIES
      for (IProjectContentEntry projectContent : projectContents) {

        SubMonitor contentMonitor = subMonitor.newChild(1);

        //
        if (LOG) {
          stopWatch = new StopWatch();
          stopWatch.start();
        }

        SubMonitor resourceContentMonitor = SubMonitor.convert(contentMonitor, (projectContent.getBinaryResources()
            .size() + projectContent.getSourceResources().size()));

        // step 4.1: compute new and modified resources
        Set<IResourceStandinNEW> newAndModifiedBinaryResources = FunctionalHelper.computeNewAndModifiedResources(
            ((ProjectContentEntry) projectContent).getBinaryResourceStandins(), storedResourcesMap, resourceCache,
            new NullProgressMonitor());

        //
        Set<IResourceStandinNEW> newAndModifiedSourceResources = Collections.emptySet();

        //
        if (AnalyzeMode.BINARIES_AND_SOURCES.equals(projectContent.getAnalyzeMode())) {
          newAndModifiedSourceResources = FunctionalHelper.computeNewAndModifiedResources(
              ((ProjectContentEntry) projectContent).getSourceResourceStandins(), storedResourcesMap, resourceCache,
              new NullProgressMonitor());
        }

        //
        if (LOG) {
          StaticLog.log(String.format(" - compare and update '%s_%s' - computeNewAndModifiedResources [%s ms]",
              projectContent.getName(), projectContent.getVersion(), stopWatch.getElapsedTime()));

          StaticLog
              .log(String.format("   - new/modified binary resources: %s", newAndModifiedBinaryResources.size()));
          StaticLog
              .log(String.format("   - new/modified source resources: %s", newAndModifiedSourceResources.size()));
        }

        // TODO: setup model
        ModelExtFactory.getModelExtensionFactory().prepareStoredResourceModel(projectContent, storedResourcesMap);

        // adjust work remaining
        int remaining = newAndModifiedSourceResources.size() + newAndModifiedBinaryResources.size();
        resourceContentMonitor.setWorkRemaining(remaining);

        ModelExtFactory.getModelExtensionFactory().beforeParseResourceModel(projectContent,
            newAndModifiedBinaryResources,
            newAndModifiedSourceResources);

        result = multiThreadedReparse(storedResourcesMap, newAndModifiedSourceResources,
            newAndModifiedBinaryResources, resourceCache, projectContent, resourceContentMonitor.newChild(remaining));

        ModelExtFactory.getModelExtensionFactory().afterParseResourceModel(projectContent,
            newAndModifiedBinaryResources,
            newAndModifiedSourceResources);

      }

      // adjust monitor in case that fileBasedContent is NOT resource content
      subMonitor.setWorkRemaining(contentCount--);
      // }
    } finally {

      // deactivate the zip cache.
      ZipFileCache.instance().deactivateCache();

      subMonitor.done();
    }

    return result;
  }

  private List<IProblem> multiThreadedReparse(Map<IProjectContentResource, Resource> storedResourcesMap,
      Collection<IResourceStandinNEW> sourceResources, Collection<IResourceStandinNEW> binaryResources,
      ResourceCache resourceCache, IProjectContentEntry fileBasedContent, IProgressMonitor monitor) {

    List<IProblem> result = new LinkedList<IProblem>();

    //
    monitor.beginTask("PARSE ", sourceResources.size() + binaryResources.size());

    try {

      //
      GenericCache<String, Directory> directories = new GenericCache<String, ModelSetup.Directory>() {
        @Override
        protected Directory create(String key) {
          return new Directory();
        }
      };

      //
      for (IResourceStandinNEW resourceStandin : binaryResources) {
        directories.getOrCreate(resourceStandin.getDirectory()).addBinaryResource(resourceStandin);
      }
      for (IResourceStandinNEW resourceStandin : sourceResources) {
        directories.getOrCreate(resourceStandin.getDirectory()).addSourceResource(resourceStandin);
      }

      // compute the part size
      float partSizeAsFloat = directories.size() / (float) THREAD_COUNT;
      int partSize = (int) Math.ceil(partSizeAsFloat);

      // split the package list in n sublist (one for each thread)
      List<Directory> dirs = new ArrayList<ModelSetup.Directory>(directories.values());
      List<Directory>[] packageFragmentsParts = new List[THREAD_COUNT];
      for (int i = 0; i < THREAD_COUNT; i++) {
        if ((i + 1) * partSize <= directories.size()) {
          packageFragmentsParts[i] = dirs.subList(i * partSize, (i + 1) * partSize);
        } else if ((i) * partSize <= dirs.size()) {
          packageFragmentsParts[i] = dirs.subList(i * partSize, dirs.size());
        } else {
          packageFragmentsParts[i] = Collections.EMPTY_LIST;
        }
      }

      // set up the callables
      CallableReparse[] callables = new CallableReparse[THREAD_COUNT];
      for (int i = 0; i < callables.length; i++) {
        callables[i] = new CallableReparse(fileBasedContent, packageFragmentsParts[i], _parsers4threads.get(i),
            resourceCache, monitor);
      }

      // create the future tasks
      FutureTask<List<IProblem>>[] futureTasks = new FutureTask[THREAD_COUNT];
      for (int i = 0; i < futureTasks.length; i++) {
        futureTasks[i] = new FutureTask<List<IProblem>>(callables[i]);
        new Thread(futureTasks[i]).start();
      }

      // collect the result
      for (int i = 0; i < futureTasks.length; i++) {
        try {
          result.addAll(futureTasks[i].get());
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

    } finally {
      monitor.done();
    }

    return result;
  }

  /**
   * <p>
   * </p>
   * 
   * @param values
   */
  private void deleteResourcesFromDependencyStore(Collection<Resource> values,
      IPersistentDependencyStore dependencyStore, IProgressMonitor progressMonitor) {

    //
    if (progressMonitor != null) {
      progressMonitor.beginTask("Clean up database...", values.size());
    }

    //
    for (Resource resource : values) {
      dependencyStore.delete(resource);

      //
      if (progressMonitor != null) {
        progressMonitor.worked(1);
      }
    }

    // commit the changes
    dependencyStore.commit();

    //
    if (progressMonitor != null) {
      progressMonitor.done();
    }
  }

  /**
   * <p>
   * Reads all resources from the underlying dependency store.
   * </p>
   * 
   * @param dependencyStore
   * @param monitor
   * @return
   */
  private static Map<IProjectContentResource, Resource> readFromDependencyStore(IDependencyStore dependencyStore,
      IProgressMonitor monitor) {

    Assert.isNotNull(dependencyStore);
    Assert.isNotNull(monitor);

    Map<IProjectContentResource, Resource> map = new HashMap<IProjectContentResource, Resource>();

    if (dependencyStore != null) {

      List<IParsableResource> resources = dependencyStore.getResources();

      monitor.beginTask("Opening database ", resources.size());

      for (IParsableResource resource : resources) {

        // check if canceled
        // checkIfCanceled(monitor);

        // put in the map
        map.put(resource, (Resource) resource);

        // set monitor
        monitor.worked(1);
      }
    }

    // work is done
    monitor.done();

    // return the map
    return map;
  }

  /**
   * <p>
   * </p>
   * 
   * @throws CoreException
   */
  private void setupParsers() throws CoreException {

    // get the registered parser factories
    List<IParserFactory> parserFactories = XYZService.instance().getParserFactoryRegistry().getParserFactories();

    // no parsers defined
    if (parserFactories.isEmpty()) {
      throw new RuntimeException("No parserFactories defined...");
    }

    // create one parser for each thread...
    List<IParser[]> parsers4threads = new LinkedList<IParser[]>();
    for (int i = 0; i < THREAD_COUNT; i++) {
      parsers4threads.add(new IParser[parserFactories.size()]);
    }

    // ... setup
    for (IParser[] parsers : parsers4threads) {
      for (int i = 0; i < parsers.length; i++) {
        parsers[i] = parserFactories.get(i).createParser();
      }
    }

    // sort
    for (int i = 0; i < parsers4threads.size(); i++) {
      Arrays.sort(parsers4threads.get(i), new Comparator<IParser>() {
        @Override
        public int compare(IParser o1, IParser o2) {
          return o2.getParserType().compareTo(o1.getParserType());
        }
      });
    }

    _parsers4threads = parsers4threads;
  }

  /**
   * <p>
   * </p>
   * 
   * @throws CoreException
   */
  private void notifyParseStart() throws CoreException {

    //
    for (IParser[] parsers : _parsers4threads) {
      for (IParser parser : parsers) {

        // notify 'start'
        try {
          parser.batchParseStart(_bundleMakerProject);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @throws CoreException
   */
  private void notifyParseStop() throws CoreException {

    //
    for (IParser[] parsers : _parsers4threads) {
      for (IParser parser : parsers) {

        // notify 'stop'
        try {
          parser.batchParseStop(_bundleMakerProject);
        } catch (Exception e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
   */
  public static class Directory {

    /** - */
    private List<IResourceStandinNEW> _binaryResources;

    /** - */
    private List<IResourceStandinNEW> _sourceResources;

    /** - */
    private int                       _count = 0;

    /**
     * <p>
     * Creates a new instance of type {@link Directory}.
     * </p>
     */
    public Directory() {
      _binaryResources = new LinkedList<IResourceStandinNEW>();
      _sourceResources = new LinkedList<IResourceStandinNEW>();
    }

    /**
     * <p>
     * </p>
     * 
     * @param resourceStandin
     */
    public void addBinaryResource(IResourceStandinNEW resourceStandin) {
      _binaryResources.add(resourceStandin);
      _count++;
    }

    /**
     * <p>
     * </p>
     * 
     * @param resourceStandin
     */
    public void addSourceResource(IResourceStandinNEW resourceStandin) {
      _sourceResources.add(resourceStandin);
      _count++;
    }

    /**
     * <p>
     * </p>
     * 
     * @return
     */
    public List<IResourceStandinNEW> getBinaryResources() {
      return _binaryResources;
    }

    /**
     * <p>
     * </p>
     * 
     * @return
     */
    public List<IResourceStandinNEW> getSourceResources() {
      return _sourceResources;
    }

    /**
     * <p>
     * </p>
     * 
     * @return
     */
    public int getCount() {
      return _count;
    }
  }
}
