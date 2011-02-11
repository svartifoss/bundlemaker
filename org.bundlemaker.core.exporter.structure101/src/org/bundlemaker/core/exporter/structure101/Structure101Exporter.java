package org.bundlemaker.core.exporter.structure101;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bundlemaker.core.exporter.IModularizedSystemExporter;
import org.bundlemaker.core.exporter.IModuleExporterContext;
import org.bundlemaker.core.exporter.structure101.xml.DataType;
import org.bundlemaker.core.exporter.structure101.xml.DependenciesType;
import org.bundlemaker.core.exporter.structure101.xml.DependencyType;
import org.bundlemaker.core.exporter.structure101.xml.ModuleType;
import org.bundlemaker.core.exporter.structure101.xml.ModulesType;
import org.bundlemaker.core.modules.AmbiguousDependencyException;
import org.bundlemaker.core.modules.IModularizedSystem;
import org.bundlemaker.core.modules.IReferencedModulesQueryResult;
import org.bundlemaker.core.modules.IResourceModule;
import org.bundlemaker.core.modules.IModule;
import org.bundlemaker.core.resource.IReference;
import org.bundlemaker.core.resource.IType;
import org.bundlemaker.core.util.StopWatch;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class Structure101Exporter implements IModularizedSystemExporter,
		Structure101ExporterConstants {

	/** - */
	private DataType _result;

	/** - */
	private IdentifierMap _identifierMap;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void export(IModularizedSystem modularizedSystem,
			IModuleExporterContext context) throws Exception {

		// set up the DataType element
		_result = new DataType();
		_result.setFlavor(STRUCTURE_101_FLAVOR);
		_result.setModules(new ModulesType());
		_result.setDependencies(new DependenciesType());

		//
		_identifierMap = new IdentifierMap();

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		//
		for (IModule typeModule : modularizedSystem.getAllModules()) {

			// create the entries
			createEntries(typeModule, modularizedSystem);
		}

		stopWatch.stop();
		System.out.println("Elapsed Time : " + stopWatch.getElapsedTime());

		//
		FileOutputStream outputStream = new FileOutputStream(new File(
				context.getDestinationDirectory(), "structure101.xml"));

		//
		Structure101ExporterUtils.marshal(_result, outputStream);
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param typeModule
	 * @param packageList
	 */
	private void createEntries(IModule typeModule,
			IModularizedSystem modularizedSystem) {

		// step 1: create and add the ModuleType
		createModule(typeModule);

		// step 3: add the dependencies
		createDependencies(typeModule, modularizedSystem);
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param typeModule
	 * @param modularizedSystem
	 */
	private void createDependencies(IModule typeModule,
			IModularizedSystem modularizedSystem) {

		// only handle resource modules
		if (typeModule instanceof IResourceModule) {

			//
			IResourceModule resourceModule = (IResourceModule) typeModule;

			// IReferencedModulesQueryResult queryResult = modularizedSystem
			// .getReferencedModules(resourceModule, false, true);

			Set<TypeToTypeDependency> dependencies = new HashSet<TypeToTypeDependency>();

			for (IType type : resourceModule.getContainedTypes()) {

				for (IReference reference : type.getReferences()) {

					IModule referencedModule = null;

					try {

						//
						referencedModule = modularizedSystem
								.getContainingModule(reference
										.getFullyQualifiedName());

					} catch (AmbiguousDependencyException e) {

						//
						System.out.println("AMBIGIOUS "
								+ reference.getFullyQualifiedName());
						continue;
					}

					if (referencedModule == null) {

						//
						System.out.println("MISSING TYPE "
								+ reference.getFullyQualifiedName());

					} else {

						// from
						String from = _identifierMap.getClassId(resourceModule,
								type.getFullyQualifiedName());
						// to
						String to = _identifierMap.getClassId(referencedModule,
								reference.getFullyQualifiedName());

						// dependency
						TypeToTypeDependency dependency = new TypeToTypeDependency(
								from, to, reference.isImplements(),
								reference.isExtends());
						//
						dependencies.add(dependency);
					}
				}
			}

			//
			for (TypeToTypeDependency typeToTypeDependency : dependencies) {

				DependencyType dependency = new DependencyType();
				dependency.setType(TYPE_REQUIRES);
				dependency.setFrom(typeToTypeDependency.getFrom());
				dependency.setTo(typeToTypeDependency.getTo());

				_result.getDependencies().getDependency().add(dependency);
			}
		}
	}

	/**
	 * <p>
	 * Creates a {@link ModuleType} for the given {@link IModule}
	 * </p>
	 * 
	 * @param typeModule
	 * @param packageList
	 * @return
	 */
	private void createModule(IModule typeModule) {

		// step 1: create the result
		ModuleType module = new ModuleType();
		module.setType(TYPE_OSGIBUNDLE);
		module.setName(getTypeModuleName(typeModule));
		module.setId(_identifierMap.getModuleId(typeModule));

		// step 2: get all packages
		Map<String, List<String>> packageList = extractPackageList(typeModule);

		for (Entry<String, List<String>> entry : packageList.entrySet()) {

			// add an entry for each package
			ModuleType packageSubmodule = new ModuleType();
			packageSubmodule.setType(TYPE_PACKAGE);
			packageSubmodule.setName(entry.getKey());
			packageSubmodule.setId(_identifierMap.getPackageId(typeModule,
					entry.getKey()));

			// add all class names to to package
			for (String fullyQualifiedTypeName : entry.getValue()) {

				// create class sub module
				ModuleType classSubmodule = new ModuleType();

				// set the type
				classSubmodule.setType(TYPE_CLASS);

				// set the class name
				String classname = fullyQualifiedTypeName.indexOf('.') != -1 ? fullyQualifiedTypeName
						.substring(fullyQualifiedTypeName.lastIndexOf('.') + 1)
						: fullyQualifiedTypeName;
				classSubmodule.setName(classname);

				// set the id
				classSubmodule.setId(_identifierMap.getClassId(typeModule,
						fullyQualifiedTypeName));

				// add the submodule
				packageSubmodule.getSubmodule().add(classSubmodule);
			}

			// add the package modules
			module.getSubmodule().add(packageSubmodule);
		}

		// add the module to the modules
		_result.getModules().getModule().add(module);
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param typeModule
	 * @return
	 */
	private Map<String, List<String>> extractPackageList(IModule typeModule) {

		// create the package list
		Map<String, List<String>> packageList = new HashMap<String, List<String>>();

		// get the contained types
		for (String fullyQualifiedType : typeModule.getContainedTypeNames()) {

			// get the package
			String packageName = Structure101ExporterUtils
					.getPackageName(fullyQualifiedType);

			// create the package list
			if (!packageList.containsKey(packageName)) {
				packageList.put(packageName, new LinkedList<String>());
			}

			// add to package
			packageList.get(packageName).add(fullyQualifiedType);
		}

		// return the package list
		return packageList;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @param typeModule
	 * @return
	 */
	protected String getTypeModuleName(IModule typeModule) {

		//
		return typeModule.getClassification() != null ? typeModule
				.getClassification().toString()
				+ "/"
				+ typeModule.getModuleIdentifier().toString() : typeModule
				.getModuleIdentifier().toString();
	}
}
