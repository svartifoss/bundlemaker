/*******************************************************************************
 * Copyright (c) 2011 Bundlemaker project team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Kai Lehmann - initial API and implementation
 *     Bundlemaker project team - integration with BundleMaker Analysis UI
 ******************************************************************************/
package org.bundlemaker.core.ui.dsmview;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.bundlemaker.analysis.model.IDependency;
import org.bundlemaker.core.analysis.IBundleMakerArtifact;
import org.bundlemaker.core.ui.selection.IArtifactSelectionChangedEvent;
import org.bundlemaker.core.ui.selection.Selection;
import org.bundlemaker.core.ui.selection.workbench.editor.AbstractArtifactSelectionAwareEditorPart;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchActionConstants;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class DSMArtifactModelEditor extends AbstractArtifactSelectionAwareEditorPart {

  /**
   * This is used as the DSMView's providerId for the xxxSelectionServices
   */
  public static String     DSM_EDITOR_ID = DSMArtifactModelEditor.class.getName();

  /** - */
  private DsmViewComposite _dsmViewWidget;

  /**
   * {@inheritDoc}
   */
  @Override
  public void onPartActivated() {
    System.out.println("onPartActivated: " + getCurrentArtifacts());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onPartDeactivated() {
    System.out.println("onPartDeactivated: " + getCurrentArtifacts());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onArtifactSelectionChanged(IArtifactSelectionChangedEvent event) {

    if (event.getSelection().getSelectedArtifacts().size() == 1) {
      IBundleMakerArtifact selectedArtifact = event.getSelection().getSelectedArtifacts().get(0);
      List<IBundleMakerArtifact> artifacts = new LinkedList<IBundleMakerArtifact>(selectedArtifact.getChildren());
      setCurrentArtifacts(artifacts);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(Composite parent) {
    //
    _dsmViewWidget = new DsmViewComposite(parent, new DsmViewModel());

    // create the context menu
    createContextMenu(_dsmViewWidget.getViewWidget());

    //
    setCurrentArtifacts(getCurrentArtifacts());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    setCurrentArtifacts(getCurrentArtifacts());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose() {
    super.dispose();

    clearDependencySelection();
  }

  private void clearDependencySelection() {
    List<IDependency> dependencies = Collections.emptyList();
    Selection.instance().getDependencySelectionService()
        .setSelection(Selection.MAIN_ARTIFACT_SELECTION_ID, DSM_EDITOR_ID, dependencies);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCurrentArtifacts(List<IBundleMakerArtifact> artifacts) {
    super.setCurrentArtifacts(artifacts);
    if (_dsmViewWidget != null) {
      _dsmViewWidget.setModel(new DsmViewModel(artifacts));
    }
    clearDependencySelection();
  }

  /**
   * <p>
   * </p>
   * 
   * @param dsmViewWidget
   */
  private void createContextMenu(DsmViewWidget dsmViewWidget) {

    MenuManager menuManager = new MenuManager("#PopupMenu");
    menuManager.setRemoveAllWhenShown(true);
    menuManager.addMenuListener(new IMenuListener() {

      private MenuItem _menuItem;

      @Override
      public void menuAboutToShow(IMenuManager manager) {
        System.out.println("menuAboutToShow");
        manager.add(new Separator("edit"));
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        manager.appendToGroup("edit", new ContributionItem("Test") {

          /**
           * {@inheritDoc}
           */
          @Override
          public void fill(Menu menu, int index) {
            _menuItem = new MenuItem(menu, SWT.PUSH);
            _menuItem.setText("Jupp");
            _menuItem.addSelectionListener(new SelectionListener() {

              @Override
              public void widgetSelected(SelectionEvent e) {
                System.out.println(e);
              }

              @Override
              public void widgetDefaultSelected(SelectionEvent e) {
                System.out.println(e);
              }
            });
          }
        });
      }
    });

    Menu menu = menuManager.createContextMenu(dsmViewWidget);
    dsmViewWidget.setMenu(menu);
  }
}