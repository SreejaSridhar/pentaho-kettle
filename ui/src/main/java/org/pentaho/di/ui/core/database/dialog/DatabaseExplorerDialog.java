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


package org.pentaho.di.ui.core.database.dialog;

import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.database.DatabaseMeta;

/**
 * This class has been adapted to use the XUL version of the DatabaseExplorerDialog instead. The old
 * DatabaseExplorerDialog has been renamed to DatabaseExplorerDialogLegacy
 */
public class DatabaseExplorerDialog extends XulDatabaseExplorerDialog {

  public DatabaseExplorerDialog( Shell parent, int style, DatabaseMeta conn, List<DatabaseMeta> databases ) {
    super( parent, conn, databases, false );
  }

  public DatabaseExplorerDialog( Shell parent, int style, DatabaseMeta conn, List<DatabaseMeta> databases,
    boolean aLook ) {
    super( parent, conn, databases, aLook );
  }
}
/*
 * public class DatabaseExplorerDialog extends DatabaseExplorerDialogLegacy {
 *
 * public DatabaseExplorerDialog(Shell parent, int style, DatabaseMeta conn, List<DatabaseMeta> databases) {
 * super(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN, conn, databases, false); }
 *
 * public DatabaseExplorerDialog(Shell parent, int style, DatabaseMeta conn, List<DatabaseMeta> databases, boolean
 * aLook) { super(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN, conn, databases,
 * aLook); } }
 */
