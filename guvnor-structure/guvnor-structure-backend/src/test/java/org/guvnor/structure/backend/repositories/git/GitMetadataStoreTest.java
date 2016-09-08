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

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.GitMetadata;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.io.object.ObjectStorage;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GitMetadataStoreTest {

    private GitMetadataStore metadataStore;

    @Mock
    private Repository repository;

    @Mock
    private ObjectStorage storage;

    @Mock
    private OrganizationalUnit organizationalUnit;

    @Before
    public void setUp() throws Exception {
        metadataStore = new GitMetadataStore( storage );
    }

    @Test
    public void testStorageInitialization() {
        metadataStore.init();
        verify( storage ).init( eq( "default://system/metadata" ) );
    }

    @Test
    public void testCreatesRightPathToSave() {
        when( repository.getAlias() ).thenReturn( "theRepository" );
        when( organizationalUnit.getName() ).thenReturn( "aparedes" );
        metadataStore.write( repository, organizationalUnit );

        verify( storage ).write( eq( "/aparedes/theRepository" ), anyObject() );
    }

    @Test
    public void testCreatesRightPathToDelete() {
        when( repository.getAlias() ).thenReturn( "theRepository" );
        when( organizationalUnit.getName() ).thenReturn( "aparedes" );
        metadataStore.delete( repository, organizationalUnit );

        verify( storage ).delete( eq( "/aparedes/theRepository" ) );
    }

    @Test
    public void testMetadata() {

        final String repositoryName = "theRepository";
        GitMetadata metaResponse = mock( GitMetadata.class );
        when( metaResponse.getRepositoryName() ).thenReturn( repositoryName );
        when( repository.getAlias() ).thenReturn( repositoryName );
        when( organizationalUnit.getName() ).thenReturn( "aparedes" );
        when( storage.read( anyString() ) ).thenReturn( metaResponse );
        GitMetadata metadata = metadataStore.read( repository, organizationalUnit );

        verify( storage ).read( eq( "/aparedes/theRepository" ) );
        assertEquals( repositoryName, metadata.getRepositoryName() );

    }
}
