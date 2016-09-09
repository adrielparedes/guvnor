/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.structure.backend.repositories.git;

import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.guvnor.structure.repositories.GitMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.io.object.ObjectStorage;

public class GitMetadataStore {

    private Logger logger = LoggerFactory.getLogger( GitMetadataStore.class );
    public static final String SEPARATOR = "/";
    public static final String METADATA = "default://system/metadata";

    private ObjectStorage storage;

    @Inject
    public GitMetadataStore( ObjectStorage storage ) {
        this.storage = storage;
    }

    @PostConstruct
    public void init() {

        if ( logger.isDebugEnabled() ) {
            logger.debug( "Initializing GitMetadataStore {}", METADATA );
        }
        this.storage.init( METADATA );
    }

    public void write( String name,
                       String origin ) {

        GitMetadata repositoryMetadata = this.read( name ).orElse( new GitMetadata() );
        repositoryMetadata.setName( name );

        if ( origin != null ) {
            repositoryMetadata.setOrigin( origin );
        }

        if ( isStorableOrigin( origin ) ) {
            GitMetadata originMetadata = this.read( origin ).orElse( new GitMetadata() );
            originMetadata.setName( origin );
            originMetadata.addFork( name );
            this.storage.write( buildPath( origin ), originMetadata );
        }

        this.storage.write( buildPath( name ), repositoryMetadata );

    }

    public Optional<GitMetadata> read( String name ) {
        try {
            final GitMetadata metadata = this.storage.read( buildPath( name ) );
            if ( metadata == null ) {
                return Optional.empty();
            } else {
                return Optional.of( metadata );
            }

        } catch ( RuntimeException e ) {
            return Optional.empty();
        }
    }

    public void delete( String name ) {
        String path = buildPath( name );
        this.storage.delete( path );
    }

    private boolean isStorableOrigin( final String origin ) {
        return origin != null && origin.matches( "(^\\w+\\/\\w+$)" );
    }

    private String buildPath( String name ) {
        String path = SEPARATOR + name;
        if ( name.indexOf( SEPARATOR ) == 0 ) {
            path = name;
        }
        if ( path.lastIndexOf( SEPARATOR ) == path.length() - 1 ) {
            path = path.substring( 0, path.length() );
        }
        return path + ".metadata";
    }

}
