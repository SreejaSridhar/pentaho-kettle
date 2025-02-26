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


package org.pentaho.di.repository.kdr.delegates.metastore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.repository.kdr.delegates.KettleDatabaseRepositoryMetaStoreDelegate;
import org.pentaho.metastore.api.IMetaStoreAttribute;

public class KDBRMetaStoreAttribute implements IMetaStoreAttribute {

  protected KettleDatabaseRepositoryMetaStoreDelegate delegate;
  private LongObjectId objectId;

  private String id;
  private Object value;

  private List<IMetaStoreAttribute> children;

  public KDBRMetaStoreAttribute( KettleDatabaseRepositoryMetaStoreDelegate delegate ) {
    this.children = new ArrayList<IMetaStoreAttribute>();
    this.delegate = delegate;
  }

  public KDBRMetaStoreAttribute( KettleDatabaseRepositoryMetaStoreDelegate delegate, String id, Object value ) {
    this( delegate );
    this.id = id;
    this.value = value;
  }

  public void setObjectId( LongObjectId objectId ) {
    this.objectId = objectId;
  }

  public LongObjectId getObjectId() {
    return objectId;
  }

  public KettleDatabaseRepositoryMetaStoreDelegate getDelegate() {
    return delegate;
  }

  public void setDelegate( KettleDatabaseRepositoryMetaStoreDelegate delegate ) {
    this.delegate = delegate;
  }

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public Object getValue() {
    return value;
  }

  public void setValue( Object value ) {
    this.value = value;
  }

  public List<IMetaStoreAttribute> getChildren() {
    return children;
  }

  public void setChildren( List<IMetaStoreAttribute> children ) {
    this.children = children;
  }

  @Override
  public void addChild( IMetaStoreAttribute attribute ) {
    children.add( attribute );
  }

  @Override
  public void clearChildren() {
    children.clear();
  }

  @Override
  public void deleteChild( String attributeId ) {
    Iterator<IMetaStoreAttribute> iterator = children.iterator();
    while ( iterator.hasNext() ) {
      IMetaStoreAttribute attribute = iterator.next();
      if ( attribute.getId().equals( attributeId ) ) {
        iterator.remove();
        return;
      }
    }
  }

  @Override
  public IMetaStoreAttribute getChild( String id ) {
    Iterator<IMetaStoreAttribute> iterator = children.iterator();
    while ( iterator.hasNext() ) {
      IMetaStoreAttribute attribute = iterator.next();
      if ( attribute.getId().equals( id ) ) {
        return attribute;
      }
    }

    return null;
  }

}
