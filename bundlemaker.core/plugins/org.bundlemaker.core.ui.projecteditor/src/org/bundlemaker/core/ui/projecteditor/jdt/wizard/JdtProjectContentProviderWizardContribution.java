package org.bundlemaker.core.ui.projecteditor.jdt.wizard;

import org.bundlemaker.core.jdt.content.JdtProjectContentProvider;
import org.bundlemaker.core.project.IModifiableProjectDescription;
import org.bundlemaker.core.project.IProjectDescriptionAwareBundleMakerProject;
import org.bundlemaker.core.ui.BundleMakerImages;
import org.bundlemaker.core.ui.projecteditor.provider.INewProjectContentProviderWizardContribution;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.Image;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public class JdtProjectContentProviderWizardContribution implements INewProjectContentProviderWizardContribution {

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bundlemaker.core.ui.projecteditor.provider.NewProjectContentProviderWizardContribution#getLabel(org.bundlemaker
   * .core.IBundleMakerProject)
   */
  @Override
  public String getLabel(IProjectDescriptionAwareBundleMakerProject bundleMakerProject) {
    return "JDT Projects (experimental)";
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bundlemaker.core.ui.projecteditor.provider.NewProjectContentProviderWizardContribution#getImage(org.bundlemaker
   * .core.IBundleMakerProject)
   */
  @Override
  public Image getImage(IProjectDescriptionAwareBundleMakerProject bundleMakerProject) {
    return BundleMakerImages.JDT_PROJECT_CONTENT_PROVIDER.getImage();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.bundlemaker.core.ui.projecteditor.provider.NewProjectContentProviderWizardContribution#getDescription(org.
   * bundlemaker.core.IBundleMakerProject)
   */
  @Override
  public String getDescription(IProjectDescriptionAwareBundleMakerProject bundleMakerProject) {
    return "Add JDT projects from your workspace";
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.bundlemaker.core.ui.projecteditor.provider.NewProjectContentProviderWizardContribution#createWizard(org.bundlemaker
   * .core.IBundleMakerProject)
   */
  @Override
  public IWizard createWizard(final IProjectDescriptionAwareBundleMakerProject bundleMakerProject) {
    final IModifiableProjectDescription modifiableProjectDescription = bundleMakerProject
        .getModifiableProjectDescription();
    return new Wizard() {
      private final EditJdtContentProviderPage _page = new EditJdtContentProviderPage(modifiableProjectDescription);

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.jface.wizard.Wizard#addPages()
       */
      @Override
      public void addPages() {
        addPage(_page);
      }

      @Override
      public boolean performFinish() {
        IProject[] selectedProjects = _page.getSelectedProjects();
        JdtProjectContentProvider provider = new JdtProjectContentProvider();
        
        provider.setName(_page.getName());
        
        for (IProject iProject : selectedProjects) {

          IJavaProject javaProject = JavaCore.create(iProject);
          provider.addJavaProject(javaProject);
        }
        
        
        
        modifiableProjectDescription.addContentProvider(provider);
        return true;
      }
    };
  }
}
