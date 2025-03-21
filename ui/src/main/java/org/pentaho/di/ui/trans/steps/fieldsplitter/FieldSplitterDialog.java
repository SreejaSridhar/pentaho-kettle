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


package org.pentaho.di.ui.trans.steps.fieldsplitter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.steps.fieldsplitter.FieldSplitterMeta;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class FieldSplitterDialog extends BaseStepDialog implements StepDialogInterface {
  private static Class<?> PKG = FieldSplitterMeta.class; // for i18n purposes, needed by Translator2!!

  private Label wlSplitfield;
  private CCombo wSplitfield;
  private FormData fdlSplitfield, fdSplitfield;

  private Label wlDelimiter;
  private TextVar wDelimiter;
  private FormData fdlDelimiter, fdDelimiter;

  private TextVar wEnclosure;

  private Label wlFields;
  private TableView wFields;
  private FormData fdlFields, fdFields;

  private FieldSplitterMeta input;

  private boolean gotPreviousFields = false;

  public FieldSplitterDialog( Shell parent, Object in, TransMeta transMeta, String sname ) {
    super( parent, (BaseStepMeta) in, transMeta, sname );
    input = (FieldSplitterMeta) in;
  }

  @Override
  public String open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN );
    props.setLook( shell );
    setShellImage( shell, input );

    ModifyListener lsMod = new ModifyListener() {
      @Override
      public void modifyText( ModifyEvent e ) {
        input.setChanged();
      }
    };
    changed = input.hasChanged();

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "FieldSplitterDialog.Shell.Title" ) );

    int middle = props.getMiddlePct();
    int margin = Const.MARGIN;

    // Stepname line
    wlStepname = new Label( shell, SWT.RIGHT );
    wlStepname.setText( BaseMessages.getString( PKG, "FieldSplitterDialog.Stepname.Label" ) );
    props.setLook( wlStepname );
    fdlStepname = new FormData();
    fdlStepname.left = new FormAttachment( 0, 0 );
    fdlStepname.right = new FormAttachment( middle, -margin );
    fdlStepname.top = new FormAttachment( 0, margin );
    wlStepname.setLayoutData( fdlStepname );
    wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wStepname.setText( stepname );
    props.setLook( wStepname );
    wStepname.addModifyListener( lsMod );
    fdStepname = new FormData();
    fdStepname.left = new FormAttachment( middle, 0 );
    fdStepname.top = new FormAttachment( 0, margin );
    fdStepname.right = new FormAttachment( 100, 0 );
    wStepname.setLayoutData( fdStepname );

    // Typefield line
    wlSplitfield = new Label( shell, SWT.RIGHT );
    wlSplitfield.setText( BaseMessages.getString( PKG, "FieldSplitterDialog.SplitField.Label" ) );
    props.setLook( wlSplitfield );
    fdlSplitfield = new FormData();
    fdlSplitfield.left = new FormAttachment( 0, 0 );
    fdlSplitfield.right = new FormAttachment( middle, -margin );
    fdlSplitfield.top = new FormAttachment( wStepname, margin );
    wlSplitfield.setLayoutData( fdlSplitfield );
    wSplitfield = new CCombo( shell, SWT.BORDER | SWT.READ_ONLY );
    wSplitfield.setText( "" );
    props.setLook( wSplitfield );
    wSplitfield.addModifyListener( lsMod );
    fdSplitfield = new FormData();
    fdSplitfield.left = new FormAttachment( middle, 0 );
    fdSplitfield.top = new FormAttachment( wStepname, margin );
    fdSplitfield.right = new FormAttachment( 100, 0 );
    wSplitfield.setLayoutData( fdSplitfield );
    wSplitfield.addFocusListener( new FocusListener() {
      @Override
      public void focusLost( org.eclipse.swt.events.FocusEvent e ) {
      }

      @Override
      public void focusGained( org.eclipse.swt.events.FocusEvent e ) {
        Cursor busy = new Cursor( shell.getDisplay(), SWT.CURSOR_WAIT );
        shell.setCursor( busy );
        getFields();
        shell.setCursor( null );
        busy.dispose();
      }
    } );

    // Typefield line
    wlDelimiter = new Label( shell, SWT.RIGHT );
    wlDelimiter.setText( BaseMessages.getString( PKG, "FieldSplitterDialog.Delimiter.Label" ) );
    props.setLook( wlDelimiter );
    fdlDelimiter = new FormData();
    fdlDelimiter.left = new FormAttachment( 0, 0 );
    fdlDelimiter.right = new FormAttachment( middle, -margin );
    fdlDelimiter.top = new FormAttachment( wSplitfield, margin );
    wlDelimiter.setLayoutData( fdlDelimiter );
    wDelimiter = new TextVar( transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wDelimiter.setToolTipText( BaseMessages.getString( PKG, "FieldSplitterDialog.Delimiter.Tooltip" ) );
    wDelimiter.setText( "" );
    props.setLook( wDelimiter );
    wDelimiter.addModifyListener( lsMod );
    fdDelimiter = new FormData();
    fdDelimiter.left = new FormAttachment( middle, 0 );
    fdDelimiter.top = new FormAttachment( wSplitfield, margin );
    fdDelimiter.right = new FormAttachment( 100, 0 );
    wDelimiter.setLayoutData( fdDelimiter );

    // enclosure
    Label wlEnclosure = new Label( shell, SWT.RIGHT );
    wlEnclosure.setText( BaseMessages.getString( PKG, "FieldSplitterDialog.Enclosure.Label" ) );
    props.setLook( wlEnclosure );
    FormData fdlEnclosure = new FormData();
    fdlEnclosure.top = new FormAttachment( wDelimiter, margin );
    fdlEnclosure.left = new FormAttachment( 0, 0 );
    fdlEnclosure.right = new FormAttachment( middle, -margin );
    wlEnclosure.setLayoutData( fdlEnclosure );
    wEnclosure = new TextVar( transMeta, shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wEnclosure.setToolTipText( BaseMessages.getString( PKG, "FieldSplitterDialog.Enclosure.Tooltip" ) );
    props.setLook( wEnclosure );
    wEnclosure.addModifyListener( lsMod );
    FormData fdEnclosure = new FormData();
    fdEnclosure.top = new FormAttachment( wDelimiter, margin );
    fdEnclosure.left = new FormAttachment( middle, 0 );
    fdEnclosure.right = new FormAttachment( 100, 0 );
    wEnclosure.setLayoutData( fdEnclosure );

    wlFields = new Label( shell, SWT.RIGHT );
    wlFields.setText( BaseMessages.getString( PKG, "FieldSplitterDialog.Fields.Label" ) );
    props.setLook( wlFields );
    fdlFields = new FormData();
    fdlFields.left = new FormAttachment( 0, 0 );
    fdlFields.top = new FormAttachment( wEnclosure, margin );
    wlFields.setLayoutData( fdlFields );

    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

    setButtonPositions( new Button[] { wOK, wCancel }, margin, null );

    final int fieldsRows = input.getFieldName().length;

    final ColumnInfo[] colinf =
      new ColumnInfo[] {
        new ColumnInfo(
          BaseMessages.getString( PKG, "FieldSplitterDialog.ColumnInfo.NewField" ),
          ColumnInfo.COLUMN_TYPE_TEXT, false ),
        new ColumnInfo(
          BaseMessages.getString( PKG, "FieldSplitterDialog.ColumnInfo.ID" ), ColumnInfo.COLUMN_TYPE_TEXT,
          false ),
        new ColumnInfo(
          BaseMessages.getString( PKG, "FieldSplitterDialog.ColumnInfo.RemoveID" ), ColumnInfo.COLUMN_TYPE_CCOMBO,
          new String[] { "Y", "N" } ),
        new ColumnInfo(
          BaseMessages.getString( PKG, "FieldSplitterDialog.ColumnInfo.Type" ),
          ColumnInfo.COLUMN_TYPE_CCOMBO, ValueMetaFactory.getValueMetaNames() ),
        new ColumnInfo(
          BaseMessages.getString( PKG, "FieldSplitterDialog.ColumnInfo.Length" ),
          ColumnInfo.COLUMN_TYPE_TEXT, false ),
        new ColumnInfo(
          BaseMessages.getString( PKG, "FieldSplitterDialog.ColumnInfo.Precision" ),
          ColumnInfo.COLUMN_TYPE_TEXT, false ),
        new ColumnInfo(
          BaseMessages.getString( PKG, "FieldSplitterDialog.ColumnInfo.Format" ),
          ColumnInfo.COLUMN_TYPE_TEXT, false ),
        new ColumnInfo(
          BaseMessages.getString( PKG, "FieldSplitterDialog.ColumnInfo.Group" ),
          ColumnInfo.COLUMN_TYPE_TEXT, false ),
        new ColumnInfo(
          BaseMessages.getString( PKG, "FieldSplitterDialog.ColumnInfo.Decimal" ),
          ColumnInfo.COLUMN_TYPE_TEXT, false ),
        new ColumnInfo(
          BaseMessages.getString( PKG, "FieldSplitterDialog.ColumnInfo.Currency" ),
          ColumnInfo.COLUMN_TYPE_TEXT, false ),
        new ColumnInfo(
          BaseMessages.getString( PKG, "FieldSplitterDialog.ColumnInfo.Nullif" ),
          ColumnInfo.COLUMN_TYPE_TEXT, false ),
        new ColumnInfo(
          BaseMessages.getString( PKG, "FieldSplitterDialog.ColumnInfo.IfNull" ),
          ColumnInfo.COLUMN_TYPE_TEXT, false ),
        new ColumnInfo(
          BaseMessages.getString( PKG, "FieldSplitterDialog.ColumnInfo.TrimType" ),
          ColumnInfo.COLUMN_TYPE_CCOMBO, ValueMetaString.trimTypeDesc, true ), };
    wFields =
      new TableView(
        transMeta, shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI, colinf, fieldsRows, lsMod, props );

    fdFields = new FormData();
    fdFields.left = new FormAttachment( 0, 0 );
    fdFields.top = new FormAttachment( wlFields, margin );
    fdFields.right = new FormAttachment( 100, 0 );
    fdFields.bottom = new FormAttachment( wOK, -2 * margin );
    wFields.setLayoutData( fdFields );

    // Add listeners
    lsOK = new Listener() {
      @Override
      public void handleEvent( Event e ) {
        ok();
      }
    };
    lsCancel = new Listener() {
      @Override
      public void handleEvent( Event e ) {
        cancel();
      }
    };

    wOK.addListener( SWT.Selection, lsOK );
    wCancel.addListener( SWT.Selection, lsCancel );

    lsDef = new SelectionAdapter() {
      @Override
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };

    wStepname.addSelectionListener( lsDef );

    // Detect X or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      @Override
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    // Set the shell size, based upon previous time...
    setSize();

    getData();
    input.setChanged( changed );

    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return stepname;
  }

  private void getFields() {
    if ( !gotPreviousFields ) {
      try {
        String field = wSplitfield.getText();
        RowMetaInterface r = transMeta.getPrevStepFields( stepname );
        if ( r != null ) {
          wSplitfield.setItems( r.getFieldNames() );
        }
        if ( field != null ) {
          wSplitfield.setText( field );
        }
      } catch ( KettleException ke ) {
        new ErrorDialog(
          shell, BaseMessages.getString( PKG, "FieldSplitterDialog.FailedToGetFields.DialogTitle" ),
          BaseMessages.getString( PKG, "FieldSplitterDialog.FailedToGetFields.DialogMessage" ), ke );
      }
      gotPreviousFields = true;
    }
  }

  public void getData() {
    if ( input.getSplitField() != null ) {
      wSplitfield.setText( input.getSplitField() );
    }
    if ( input.getDelimiter() != null ) {
      wDelimiter.setText( input.getDelimiter() );
    }
    if ( input.getEnclosure() != null ) {
      wEnclosure.setText( input.getEnclosure() );
    }

    for ( int i = 0; i < input.getFieldName().length; i++ ) {
      final TableItem ti = wFields.table.getItem( i );
      if ( input.getFieldName()[i] != null ) {
        ti.setText( 1, input.getFieldName()[i] );
      }
      if ( input.getFieldID()[i] != null ) {
        ti.setText( 2, input.getFieldID()[i] );
      }
      ti.setText( 3, input.getFieldRemoveID()[i] ? "Y" : "N" );
      ti.setText( 4, ValueMetaFactory.getValueMetaName( input.getFieldType()[i] ) );
      if ( input.getFieldLength()[i] >= 0 ) {
        ti.setText( 5, "" + input.getFieldLength()[i] );
      }
      if ( input.getFieldPrecision()[i] >= 0 ) {
        ti.setText( 6, "" + input.getFieldPrecision()[i] );
      }
      if ( input.getFieldFormat()[i] != null ) {
        ti.setText( 7, input.getFieldFormat()[i] );
      }
      if ( input.getFieldGroup()[i] != null ) {
        ti.setText( 8, input.getFieldGroup()[i] );
      }
      if ( input.getFieldDecimal()[i] != null ) {
        ti.setText( 9, input.getFieldDecimal()[i] );
      }
      if ( input.getFieldCurrency()[i] != null ) {
        ti.setText( 10, input.getFieldCurrency()[i] );
      }
      if ( input.getFieldNullIf()[i] != null ) {
        ti.setText( 11, input.getFieldNullIf()[i] );
      }
      if ( input.getFieldIfNull()[i] != null ) {
        ti.setText( 12, input.getFieldIfNull()[i] );
      }
      ti.setText( 13, ValueMetaString.getTrimTypeDesc( input.getFieldTrimType()[i] ) );
    }
    wFields.setRowNums();
    wFields.optWidth( true );

    wStepname.selectAll();
    wStepname.setFocus();
  }

  private void cancel() {
    stepname = null;
    input.setChanged( changed );
    dispose();
  }

  private void ok() {
    if ( Utils.isEmpty( wStepname.getText() ) ) {
      return;
    }

    stepname = wStepname.getText(); // return value

    input.setSplitField( wSplitfield.getText() );
    input.setDelimiter( wDelimiter.getText() );
    input.setEnclosure( wEnclosure.getText() );

    // Table table = wFields.table;
    int nrfields = wFields.nrNonEmpty();

    input.allocate( nrfields );

    //CHECKSTYLE:Indentation:OFF
    for ( int i = 0; i < input.getFieldName().length; i++ ) {
      final TableItem ti = wFields.getNonEmpty( i );
      input.getFieldName()[i] = ti.getText( 1 );
      input.getFieldID()[i] = ti.getText( 2 );
      input.getFieldRemoveID()[i] = "Y".equalsIgnoreCase( ti.getText( 3 ) );
      input.getFieldType()[i] = ValueMetaFactory.getIdForValueMeta( ti.getText( 4 ) );
      input.getFieldLength()[i] = Const.toInt( ti.getText( 5 ), -1 );
      input.getFieldPrecision()[i] = Const.toInt( ti.getText( 6 ), -1 );
      input.getFieldFormat()[i] = ti.getText( 7 );
      input.getFieldGroup()[i] = ti.getText( 8 );
      input.getFieldDecimal()[i] = ti.getText( 9 );
      input.getFieldCurrency()[i] = ti.getText( 10 );
      input.getFieldNullIf()[i] = ti.getText( 11 );
      input.getFieldIfNull()[i] = ti.getText( 12 );
      input.getFieldTrimType()[i] = ValueMetaString.getTrimTypeByDesc( ti.getText( 13 ) );
    }

    dispose();
  }
}
