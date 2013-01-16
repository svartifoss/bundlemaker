package org.bundlemaker.core.ui.mvn;

import org.bundlemaker.core.mvn.BmMvnCoreConstants;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class MvnRepositoriesPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

  public MvnRepositoriesPreferencePage() {
    super(GRID);
  }

  public void createFieldEditors() {
    addField(new StringFieldEditor(BmMvnCoreConstants.PREF_MVN_LOCAL_REPO, "Local Repo:",
        getFieldEditorParent()));
    addField(new StringFieldEditor(BmMvnCoreConstants.PREF_MVN_REMOTE_REPO, "Remote Repo:",
        getFieldEditorParent()));
  }

  @Override
  public void init(IWorkbench workbench) {
    setPreferenceStore(Activator.getDefault().getPreferenceStore());
    setDescription("Set Maven Repositories");
  }

  @Override
  public boolean performOk() {

    //
    boolean result = super.performOk();
    //
    // //
    // Activator.getDefault().resetMvnRepositories();

    //
    return result;
  }
}