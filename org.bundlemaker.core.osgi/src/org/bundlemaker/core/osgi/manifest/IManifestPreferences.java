package org.bundlemaker.core.osgi.manifest;

import java.util.Set;

public interface IManifestPreferences {

  /**
   * <p>
   * E.g. boot delegated packages.
   * </p>
   * 
   * @return
   */
  @Deprecated
  Set<String> importsToIgnore();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  DependencyStyle getDependencyStyle();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public boolean isSourceManifest();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public boolean noUses();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public boolean noExportPackageVersion();
}
