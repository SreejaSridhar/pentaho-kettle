/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.di.ui.repository.pur.repositoryexplorer.abs.controller;

import java.util.List;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.ui.repository.pur.services.IAbsSecurityProvider;
import org.pentaho.di.ui.repository.repositoryexplorer.ControllerInitializationException;
import org.pentaho.di.ui.repository.repositoryexplorer.IUISupportController;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.binding.DefaultBindingFactory;
import org.pentaho.ui.xul.impl.AbstractXulEventHandler;

public class AbsContextMenuController extends AbstractXulEventHandler implements IUISupportController,
    java.io.Serializable {

  private static final long serialVersionUID = 8878231461011554114L; /* EESOURCE: UPDATE SERIALVERUID */

  private IAbsSecurityProvider service;
  private boolean isAllowed = false;
  private BindingFactory bf;

  public void init( Repository repository ) throws ControllerInitializationException {
    try {
      if ( repository.hasService( IAbsSecurityProvider.class ) ) {
        service = (IAbsSecurityProvider) repository.getService( IAbsSecurityProvider.class );
        bf = new DefaultBindingFactory();
        bf.setDocument( this.getXulDomContainer().getDocumentRoot() );

        if ( bf != null ) {
          createBindings();
        }
        setAllowed( allowedActionsContains( service, IAbsSecurityProvider.CREATE_CONTENT_ACTION ) );
      }
    } catch ( KettleException e ) {
      throw new ControllerInitializationException( e );
    }
  }

  private void createBindings() {
    bf.createBinding( this, "allowed", "file-context-rename", "!disabled" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    bf.createBinding( this, "allowed", "file-context-delete", "!disabled" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    bf.createBinding( this, "allowed", "folder-context-create", "!disabled" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    bf.createBinding( this, "allowed", "folder-context-rename", "!disabled" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    bf.createBinding( this, "allowed", "folder-context-delete", "!disabled" ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  public String getName() {
    return "contextMenuController"; //$NON-NLS-1$
  }

  public boolean isAllowed() {
    return isAllowed;
  }

  public void setAllowed( boolean isAllowed ) {
    this.isAllowed = isAllowed;
    this.firePropertyChange( "allowed", null, isAllowed ); //$NON-NLS-1$
  }

  private boolean allowedActionsContains( IAbsSecurityProvider service, String action ) throws KettleException {
    List<String> allowedActions = service.getAllowedActions( IAbsSecurityProvider.NAMESPACE );
    for ( String actionName : allowedActions ) {
      if ( action != null && action.equals( actionName ) ) {
        return true;
      }
    }
    return false;
  }
}
