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


package org.pentaho.di.core.row;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains a list of data rows as well as the RowMetaInterface to describe it.
 *
 * @author matt
 *
 */
public class RowBuffer {
  private RowMetaInterface rowMeta;
  private List<Object[]> buffer;

  /**
   * @param rowMeta
   * @param buffer
   */
  public RowBuffer( RowMetaInterface rowMeta, List<Object[]> buffer ) {
    this.rowMeta = rowMeta;
    this.buffer = buffer;
  }

  /**
   * @param rowMeta
   */
  public RowBuffer( RowMetaInterface rowMeta ) {
    this( rowMeta, new ArrayList<Object[]>() );
  }

  /**
   * @return the rowMeta
   */
  public RowMetaInterface getRowMeta() {
    return rowMeta;
  }

  /**
   * @param rowMeta
   *          the rowMeta to set
   */
  public void setRowMeta( RowMetaInterface rowMeta ) {
    this.rowMeta = rowMeta;
  }

  /**
   * @return the buffer
   */
  public List<Object[]> getBuffer() {
    return buffer;
  }

  /**
   * @param buffer
   *          the buffer to set
   */
  public void setBuffer( List<Object[]> buffer ) {
    this.buffer = buffer;
  }
}
