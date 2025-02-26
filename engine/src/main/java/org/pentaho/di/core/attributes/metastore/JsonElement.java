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


package org.pentaho.di.core.attributes.metastore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import org.pentaho.metastore.api.IMetaStoreAttribute;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.api.IMetaStoreElementType;
import org.pentaho.metastore.api.security.IMetaStoreElementOwner;
import org.pentaho.metastore.stores.memory.MemoryMetaStoreAttribute;
import org.pentaho.metastore.stores.memory.MemoryMetaStoreElement;
import org.pentaho.metastore.stores.memory.MemoryMetaStoreElementOwner;

import java.io.IOException;

import com.google.common.base.Preconditions;

/**
 * @author nhudak
 */
@JsonIgnoreProperties( { "elementType" } )
public class JsonElement extends MemoryMetaStoreElement implements AttributesInterfaceEntry {
  private final ObjectMapper objectMapper = new ObjectMapper();

  {
    SimpleModule module = new SimpleModule( "MetaStore Elements", Version.unknownVersion() );
    module.addAbstractTypeMapping( IMetaStoreAttribute.class, MemoryMetaStoreAttribute.class );
    module.addAbstractTypeMapping( IMetaStoreElementOwner.class, EmptyOwner.class );

    objectMapper.registerModule( module );
  }

  public JsonElement() {
  }

  public JsonElement( IMetaStoreElement element ) {
    super( element );
  }

  static JsonElement from( IMetaStoreElement element ) {
    return element instanceof JsonElement ? ( (JsonElement) element ) : new JsonElement( element );
  }

  @Override public String getId() {
    if ( Strings.isNullOrEmpty( super.getId() ) ) {
      setId( getName() );
    }
    return Strings.emptyToNull( super.getId() );
  }

  public static String groupName( IMetaStoreElementType elementType ) {
    ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
    objectNode.put( "_", "Embedded MetaStore Elements" );
    objectNode.put( "namespace", Preconditions.checkNotNull( elementType.getNamespace() ) );
    objectNode.put( "type", Preconditions.checkNotNull( elementType.getId() ) );
    return objectNode.toString();
  }

  @Override public String groupName() {
    return groupName( Preconditions.checkNotNull( getElementType() ) );
  }

  @Override public String key() {
    return Preconditions.checkNotNull( getId() );
  }

  @Override public String jsonValue() throws IOException {
    return objectMapper.writeValueAsString( this );
  }

  public JsonElement load( String jsonData ) throws IOException {
    return objectMapper.readerForUpdating( this ).readValue( jsonData );
  }

  private static class EmptyOwner extends MemoryMetaStoreElementOwner {
    public EmptyOwner() {
      super( null, null );
    }
  }
}
