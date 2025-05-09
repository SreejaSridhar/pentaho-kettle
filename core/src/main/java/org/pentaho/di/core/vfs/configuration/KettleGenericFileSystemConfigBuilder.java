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


package org.pentaho.di.core.vfs.configuration;

import java.io.IOException;

import org.apache.commons.vfs2.FileSystemConfigBuilder;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.util.DelegatingFileSystemOptionsBuilder;
import org.pentaho.di.core.bowl.Bowl;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.vfs.KettleVFS;

/**
 * A generic FileSystemConfigBuilder that inserts parameters and values as literally specified.
 *
 * Note: ALL parameters are case sensitive! Please see individual FileSystemConfigBuilder for list of available
 * parameters. Please also see Kettle FileSystemConfigBuilder overrides for additional parameters.
 *
 * @see KettleSftpFileSystemConfigBuilder
 *
 * @author cboyden
 */
public class KettleGenericFileSystemConfigBuilder extends FileSystemConfigBuilder implements
  IKettleFileSystemConfigBuilder {

  private static final String BOWL_KEY = "kettle.bowl";
  private static final String VAR_SPACE_KEY = "kettle.VariableSpace";

  private static final KettleGenericFileSystemConfigBuilder builder = new KettleGenericFileSystemConfigBuilder();
  private static final LogChannelInterface log = new LogChannel( "cfgbuilder" );

  @Override
  public String parseParameterName( String parameter, String scheme ) {
    String result = null;

    // Frame the parameter name
    int begin = parameter.indexOf( ".", parameter.indexOf( "." ) + 1 ) + 1; // Get the index of the second "."
                                                                            // (vfs.scheme.parameter)
    int end = -1;

    end = parameter.indexOf( '.', begin );

    if ( end < 0 ) {
      end = parameter.length();
    }

    if ( end > begin ) {
      result = parameter.substring( begin, end );
    }

    return result;
  }

  public static KettleGenericFileSystemConfigBuilder getInstance() {
    return builder;
  }

  /**
   * Extract the scheme from a Kettle VFS configuration paramter (vfs.scheme.parameter)
   *
   * @param fullParameterName
   *          A VFS configuration parameter in the form of 'vfs.scheme.parameter'
   */
  public static String extractScheme( String fullParameterName ) throws IllegalArgumentException {
    String result = null;

    // Verify that this is a Kettle VFS configuration parameter
    if ( ( fullParameterName != null )
      && ( fullParameterName.length() > 4 ) && ( fullParameterName.startsWith( "vfs." ) ) ) {
      int schemeEnd = fullParameterName.indexOf( ".", 4 );
      if ( schemeEnd > 4 ) {
        result = fullParameterName.substring( 4, schemeEnd );
      } else {
        throw new IllegalArgumentException( "The configuration parameter does not match a valid scheme: "
          + fullParameterName );
      }
    } else {
      throw new IllegalArgumentException( "The configuration parameter does not match a valid scheme: "
        + fullParameterName );
    }

    return result;
  }

  protected KettleGenericFileSystemConfigBuilder() {
    super();
  }

  @Override
  protected Class<? extends FileSystem> getConfigClass() {
    return FileSystem.class;
  }

  @Override
  public void setParameter( FileSystemOptions opts, String name, String value, String fullParameterName,
    String vfsUrl ) throws IOException {
    // Use the DelgatingFileSystemOptionsBuilder to insert generic parameters
    // This must be done to assure the correct VFS FileSystem drivers will process the parameters
    String scheme = extractScheme( fullParameterName );
    try {
      DelegatingFileSystemOptionsBuilder delegateFSOptionsBuilder =
        new DelegatingFileSystemOptionsBuilder( KettleVFS.getInstance().getFileSystemManager() );
      if ( scheme != null ) {
        delegateFSOptionsBuilder.setConfigString( opts, scheme, name, value );
      } else {
        log.logMinimal( "Warning: Cannot process VFS parameters if no scheme is specified: " + vfsUrl );
      }
    } catch ( FileSystemException e ) {
      if ( ( e.getCode() != null ) && ( e.getCode().equalsIgnoreCase( "vfs.provider/config-key-invalid.error" ) ) ) {
        // This key is not supported by the default scheme config builder. This may be a custom key of another config
        // builder
        log.logMinimal( "Warning: The configuration parameter ["
          + name + "] is not supported by the default configuration builder for scheme: " + scheme );
      } else {
        // An unexpected error has occurred loading in parameters
        throw new IOException( e.getLocalizedMessage(), e );
      }
    }
  }

  @Override
  public void setParameter( FileSystemOptions opts, String name, VariableSpace value, String vfsUrl ) {
    super.setParam( opts, VAR_SPACE_KEY, value );
  }

  @Override
  public Object getVariableSpace( FileSystemOptions fileSystemOptions ) {
    return getParam( fileSystemOptions, VAR_SPACE_KEY );
  }

  @Override
  public void setBowl( FileSystemOptions opts, Bowl bowl ) {
    super.setParam( opts, BOWL_KEY, bowl );
  }

  @Override
  public Bowl getBowl( FileSystemOptions opts ) {
    return super.getParam( opts, BOWL_KEY );
  }
}
