package org.bundlemaker.core.internal.projectdescription;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bundlemaker.core.IBundleMakerProject;
import org.bundlemaker.core.internal.resource.ResourceStandin;
import org.bundlemaker.core.projectdescription.IFileBasedContent;
import org.bundlemaker.core.projectdescription.IResourceContent;
import org.bundlemaker.core.util.FileUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class FileBasedContent implements IFileBasedContent {

	/** - */
	private boolean _isInitialized;

	/** - */
	private String _id;

	/** - */
	private String _name;

	/** - */
	private String _version;

	/** - */
	private Set<IPath> _binaryPaths;

	/** - */
	private ResourceContent _resourceContent;

	/**
	 * <p>
	 * Creates a new instance of type {@link FileBasedContent}.
	 * </p>
	 */
	public FileBasedContent() {

		//
		_isInitialized = false;

		//
		_binaryPaths = new HashSet<IPath>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return _id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return _name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getVersion() {
		return _version;
	}

	@Override
	public Set<IPath> getBinaryPaths() {
		return Collections.unmodifiableSet(_binaryPaths);
	}

	@Override
	public boolean isResourceContent() {
		return _resourceContent != null;
	}

	@Override
	public IResourceContent getResourceContent() {
		return _resourceContent;
	}

	public ResourceContent getModifiableResourceContent() {
		return _resourceContent;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public Set<IPath> getModifiableBinaryPaths() {
		return _binaryPaths;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param id
	 */
	public void setId(String id) {
		_id = id;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setVersion(String version) {
		_version = version;
	}

	public void setResourceContent(ResourceContent resourceContent) {
		_resourceContent = resourceContent;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param fileBasedContent
	 * @param bundleMakerProject
	 * @throws CoreException
	 */
	public void initialize(IBundleMakerProject bundleMakerProject) {

		// return if content already is initialized
		if (_isInitialized) {
			return;
		}

		if (isResourceContent()) {

			// add the binary resources
			for (IPath root : _binaryPaths) {

				// get the root
				String rootPath = root.toFile().getAbsolutePath();

				try {

					for (String child : FileUtils.getAllChildren(root.toFile())) {

						// create the resource standin
						ResourceStandin resourceStandin = new ResourceStandin(
								_id, rootPath, child);

						// add the resource
						_resourceContent.getModifiableBinaryResources().add(
								resourceStandin);
					}

				} catch (CoreException e) {

					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// add the source resources
			for (IPath root : _resourceContent.getSourcePaths()) {

				// get the root
				String rootPath = root.toFile().getAbsolutePath();

				try {

					for (String child : FileUtils.getAllChildren(root.toFile())) {

						// create the resource standin
						ResourceStandin resourceStandin = new ResourceStandin(
								_id, rootPath, child);

						// add the resource
						_resourceContent.getModifiableSourceResources().add(
								resourceStandin);
					}

				} catch (CoreException e) {

					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// set initialized
		_isInitialized = true;
	}
}
