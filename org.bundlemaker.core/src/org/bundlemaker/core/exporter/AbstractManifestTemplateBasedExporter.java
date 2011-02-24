package org.bundlemaker.core.exporter;

import java.io.File;

import org.bundlemaker.core.exporter.manifest.ManifestUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;

import com.springsource.bundlor.util.SimpleManifestContents;
import com.springsource.util.parser.manifest.ManifestContents;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
public abstract class AbstractManifestTemplateBasedExporter extends
		AbstractManifestAwareExporter {

	/** - */
	private ManifestContents _manifestTemplateContents;

	/** - */
	private File _templateRootDirectory;

	/** - */
	private File _projectTemplateDirectory;

	/**
	 * <p>
	 * </p>
	 * 
	 * @param templateRootDirectory
	 */
	public final void setTemplateRootDirectory(File templateRootDirectory) {
		Assert.isNotNull(templateRootDirectory);
		Assert.isTrue(templateRootDirectory.isDirectory());

		_templateRootDirectory = templateRootDirectory;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public final File getCurrentTemplateRootDirectory() {
		return _templateRootDirectory;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public final File getCurrentProjectTemplateDirectory() {
		return _projectTemplateDirectory;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	public final ManifestContents getCurrentManifestTemplate() {
		return _manifestTemplateContents;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void preExportModule() throws CoreException {

		// 'create' the project template directory
		_projectTemplateDirectory = createProjectTemplateDirectory();

		// get the template manifest
		_manifestTemplateContents = createManifestTemplate();

		//
		super.preExportModule();

	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	protected ManifestContents createManifestTemplate() {

		//
		if (_templateRootDirectory == null) {
			return createDefaultManifestTemplate();
		}

		File templateFile = getManifestTemplateFile();

		ManifestContents templateManifestContents = ManifestUtils
				.readManifestContents(templateFile);

		if (templateManifestContents != null) {
			return templateManifestContents;
		}

		// return the default manifest contents
		else {
			return createDefaultManifestTemplate();
		}
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	protected File getManifestTemplateFile() {

		// get the template file
		File templateFile = new File(_templateRootDirectory, getCurrentModule()
				.getModuleIdentifier().toString() + ".template");

		if (!templateFile.exists()) {
			templateFile = new File(_templateRootDirectory, getCurrentModule()
					.getModuleIdentifier().getName() + ".template");
		}

		//
		return templateFile;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	protected ManifestContents createDefaultManifestTemplate() {
		return new SimpleManifestContents();
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return
	 */
	protected File createProjectTemplateDirectory() {
		return _projectTemplateDirectory;
	}
}
