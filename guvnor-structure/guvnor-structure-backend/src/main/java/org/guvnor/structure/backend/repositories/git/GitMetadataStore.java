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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.GitMetadata;
import org.guvnor.structure.repositories.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.io.object.ObjectStorage;

public class GitMetadataStore {

    private Logger logger = LoggerFactory.getLogger( GitMetadataStore.class );

    public static final String METADATA = "default://metadata";

    private ObjectStorage storage;

    @Inject
    public GitMetadataStore( ObjectStorage storage ) {
        this.storage = storage;
    }

    @PostConstruct
    public void init() {

        logger.info( "Initializing GitMetadataStore {}", this.storage );
        this.storage.init( METADATA );
    }

    public void write( Repository repository,
                       OrganizationalUnit organizationalUnit ) {

        String owner = getOwner( organizationalUnit );
        String repositoryName = repository.getAlias();
        String path = buildPath( owner, repositoryName );

        final GitMetadata metadata = new GitMetadata();

        metadata.setRepositoryName( repositoryName );
        metadata.setOwner( owner );

        this.storage.write( path, metadata );

    }

    public GitMetadata read( String gitRepository,
                             OrganizationalUnit organizationalUnit ) {
        String owner = getOwner( organizationalUnit );
        String repositoryName = "";
        String path = buildPath( owner, repositoryName );
        return this.storage.read( path );
    }

    public void move( String source,
                      String target ) {
        this.storage.move( source, target );
    }

    public void delete( Repository repository,
                        OrganizationalUnit organizationalUnit ) {
        String owner = getOwner( organizationalUnit );
        String repositoryName = repository.getAlias();
        String path = buildPath( owner, repositoryName );
        this.storage.delete( path );
    }

    private String getOwner( final OrganizationalUnit organizationalUnit ) {
        String owner = organizationalUnit.getName();
        return owner;
    }

    private String buildPath( final String owner,
                              final String repositoryName ) {
        return owner + "/" + repositoryName;
    }

}
