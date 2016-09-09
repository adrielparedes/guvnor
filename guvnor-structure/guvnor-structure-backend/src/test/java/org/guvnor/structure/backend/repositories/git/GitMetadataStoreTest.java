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

import java.util.HashMap;
import java.util.Map;

import org.guvnor.structure.repositories.GitMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.io.object.ObjectStorage;

import static org.jgroups.util.Util.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GitMetadataStoreTest {

    private GitMetadataStore metadataStore;

    @Mock
    private ObjectStorage storage;

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
        metadataStore.write( "test/repo", "" );
        verify( storage ).write( eq( "/test/repo.metadata" ), anyObject() );
    }

    @Test
    public void testFixRightPathToSave() {
        metadataStore.write( "/test/repo", "" );
        verify( storage ).write( eq( "/test/repo.metadata" ), anyObject() );
    }

    @Test
    public void testCreatesRightPathToDelete() {
        metadataStore.delete( "test/repo" );

        verify( storage ).delete( eq( "/test/repo.metadata" ) );
    }

    @Test
    public void testWriteNewMetadataWithoutOrigin() {

        Map<String, GitMetadata> metadatas = new HashMap<>();
        doAnswer( invocationOnMock -> {
            String key = invocationOnMock.getArgumentAt( 0, String.class );
            GitMetadata metadata = invocationOnMock.getArgumentAt( 1, GitMetadata.class );
            metadatas.put( key, metadata );
            return null;
        } ).when( storage ).write( anyString(), any() );

        metadataStore.write( "test/repo", null );
        assertEquals( "test/repo", metadatas.get( "/test/repo.metadata" ).getName() );

    }

    @Test
    public void testWriteNewMetadataWithOrigin() {

        Map<String, GitMetadata> metadatas = new HashMap<>();
        doAnswer( invocationOnMock -> {
            String key = invocationOnMock.getArgumentAt( 0, String.class );
            GitMetadata metadata = invocationOnMock.getArgumentAt( 1, GitMetadata.class );
            metadatas.put( key, metadata );
            return null;
        } ).when( storage ).write( anyString(), any() );

        metadataStore.write( "test/repo", "other/repo" );

        assertEquals( "test/repo", metadatas.get( "/test/repo.metadata" ).getName() );
        assertEquals( "other/repo", metadatas.get( "/other/repo.metadata" ).getName() );

    }

    @Test
    public void testWriteTwoForks() {

        Map<String, GitMetadata> metadatas = new HashMap<>();
        doAnswer( invocationOnMock -> {
            String key = invocationOnMock.getArgumentAt( 0, String.class );
            GitMetadata metadata = invocationOnMock.getArgumentAt( 1, GitMetadata.class );
            metadatas.put( key, metadata );
            return null;
        } ).when( storage ).write( anyString(), any() );

        doAnswer( invocationOnMock -> {
            String key = invocationOnMock.getArgumentAt( 0, String.class );
            return metadatas.get( key );
        } ).when( storage ).read( anyString() );

        metadataStore.write( "test/repo", "origin/repo" );
        metadataStore.write( "fork/repo", "origin/repo" );

        assertEquals( 3, metadatas.size() );
        assertEquals( "test/repo", metadatas.get( "/test/repo.metadata" ).getName() );
        assertEquals( "fork/repo", metadatas.get( "/fork/repo.metadata" ).getName() );
        assertEquals( "origin/repo", metadatas.get( "/origin/repo.metadata" ).getName() );

        assertEquals( 2, metadatas.get( "/origin/repo.metadata" ).getForks().size() );

    }
}
