package org.bundlemaker.core.test.inspector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bundlemaker.core.BundleMakerCore;
import org.bundlemaker.core.IBundleMakerProject;
import org.bundlemaker.core.internal.Activator;
import org.bundlemaker.core.internal.resource.ArchiveFileCache;
import org.bundlemaker.core.internal.resource.FlyWeightString;
import org.bundlemaker.core.internal.resource.Reference;
import org.bundlemaker.core.internal.resource.Resource;
import org.bundlemaker.core.internal.resource.Type;
import org.bundlemaker.core.resource.modifiable.ReferenceAttributes;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.osgi.util.tracker.ServiceTracker;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;
import com.db4o.osgi.Db4oService;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class DbInspector {

	/**
	 * <p>
	 * </p>
	 * 
	 * @throws CoreException
	 */
	public void testDatabase(IProject project) throws CoreException {

		// create a new configuration
		Configuration configuration = Db4o.newConfiguration();

		// set the activation depth
		configuration.activationDepth(10);

		// open file
		IFile file = project.getFile(".bundlemaker/db4o.store");

		ServiceTracker serviceTracker = new ServiceTracker(
				Activator.getContext(), Db4oService.class.getName(), null);

		serviceTracker.open();

		Db4oService db4oService = (Db4oService) serviceTracker.getService();

		ObjectContainer _database = db4oService.openFile(configuration, file
				.getRawLocation().toOSString());

		//
		System.out
				.println("Resource " + _database.query(Resource.class).size());
		System.out.println("Reference "
				+ _database.query(Reference.class).size());
		System.out.println("ReferenceAttributes "
				+ _database.query(ReferenceAttributes.class).size());
		System.out.println("Type " + _database.query(Type.class).size());
		System.out.println("FlyWeightString "
				+ _database.query(FlyWeightString.class).size());
		System.out.println("ArchiveFileCache "
				+ _database.query(ArchiveFileCache.class).size());

		Map<String, Integer> classNames = new HashMap<String, Integer>();

		for (Object object : _database.query().execute()) {
			if (classNames.containsKey(object.getClass().getName())) {
				classNames.put(object.getClass().getName(),
						classNames.get(object.getClass().getName()) + 1);
			} else {
				classNames.put(object.getClass().getName(), 0);
			}
		}

		for (Entry<String, Integer> entry : classNames.entrySet()) {
			System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}
}
