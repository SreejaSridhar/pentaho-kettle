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


package org.pentaho.di.core.metrics;

import java.util.Date;

import org.pentaho.di.core.logging.MetricsInterface;

public interface MetricsSnapshotInterface {

  /**
   * @return The metric that is being recorded, includes type, code and description
   */
  public MetricsInterface getMetric();

  public Date getDate();

  public void setDate( Date date );

  public String getSubject();

  public String getLogChannelId();

  public Long getValue();

  public void setValue( Long value );

  /**
   * Calculate the key for this snapshot, usually a combination of description and subject.
   *
   * @return the key for this snapshot
   */
  public String getKey();

}
