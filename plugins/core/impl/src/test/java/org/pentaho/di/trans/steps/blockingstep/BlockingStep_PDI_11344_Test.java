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


package org.pentaho.di.trans.steps.blockingstep;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBigNumber;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.row.value.ValueMetaNumber;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.steps.mock.StepMockHelper;

public class BlockingStep_PDI_11344_Test {
  private StepMockHelper<BlockingStepMeta, BlockingStepData> mockHelper;

  @Before
  public void setUp() {
    mockHelper =
        new StepMockHelper<BlockingStepMeta, BlockingStepData>( "BlockingStep", BlockingStepMeta.class,
            BlockingStepData.class );
    when( mockHelper.logChannelInterfaceFactory.create( any(), any( LoggingObjectInterface.class ) ) )
        .thenReturn( mockHelper.logChannelInterface );
    when( mockHelper.trans.isRunning() ).thenReturn( true );
  }

  @After
  public void cleanUp() {
    mockHelper.cleanUp();
  }

  private static RowMetaInterface createRowMetaInterface() {
    RowMetaInterface rm = new RowMeta();

    ValueMetaInterface[] valuesMeta = {
      new ValueMetaString( "field1" ), new ValueMetaInteger( "field2" ), new ValueMetaNumber( "field3" ),
      new ValueMetaDate( "field4" ), new ValueMetaBoolean( "field5" ), new ValueMetaBigNumber( "field6" ),
      new ValueMetaBigNumber( "field7" ) };

    for ( ValueMetaInterface aValuesMeta : valuesMeta ) {
      rm.addValueMeta( aValuesMeta );
    }

    return rm;
  }

  @Test
  public void outputRowMetaIsCreateOnce() throws Exception {
    BlockingStep step =
        new BlockingStep( mockHelper.stepMeta, mockHelper.stepDataInterface, 0, mockHelper.transMeta,
            mockHelper.trans );
    step = spy( step );

    BlockingStepData data = new BlockingStepData();
    step.init( mockHelper.processRowsStepMetaInterface, data );
    step.setInputRowMeta( createRowMetaInterface() );

    doReturn( new Object[0] ).when( step ).getRow();
    step.processRow( mockHelper.processRowsStepMetaInterface, data );

    RowMetaInterface outputRowMeta = data.outputRowMeta;
    assertNotNull( outputRowMeta );

    doReturn( new Object[0] ).when( step ).getRow();
    step.processRow( mockHelper.processRowsStepMetaInterface, data );
    assertTrue( data.outputRowMeta == outputRowMeta );
  }
}
