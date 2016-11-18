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

package org.guvnor.pullrequest.client.diff;

import java.util.Arrays;
import java.util.List;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileDiffPresenterTest {

    @Mock
    private FileDiffPresenter.View view;

    @Mock
    private VFSService service;

    @Mock
    private Caller<VFSService> vfsService;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private ManagedInstance<LineDiffPresenter> linesPresenters;

    private FileDiffPresenter presenter;

    @Before
    public void setUp() {
        vfsService = new CallerMock<>( service );
        when( linesPresenters.get() ).thenReturn( mock( LineDiffPresenter.class ) );
        this.presenter = spy( new FileDiffPresenter( view, vfsService, placeManager, linesPresenters ) );
    }

    @Test
    public void testOpenFile() throws Exception {

        this.presenter.openFile( "master", "repo" );
        verify( service ).get( eq( "default://master@repo" ) );

    }

    @Test(expected = IllegalArgumentException.class)
    public void testOpenFileEmptyBranch() throws Exception {
        this.presenter.openFile( "", "repo" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOpenFileEmptyUri() throws Exception {
        this.presenter.openFile( "master", "" );
    }

    @Test
    public void testLineIsNotVisible() {

        boolean isVisible = this.presenter.isVisibleLine( "+++ hello" );
        assertFalse( isVisible );

        isVisible = this.presenter.isVisibleLine( "--- hello" );
        assertFalse( isVisible );

        isVisible = this.presenter.isVisibleLine( "@@ hello @@" );
        assertFalse( isVisible );

        isVisible = this.presenter.isVisibleLine( "diff --git something" );
        assertFalse( isVisible );

        isVisible = this.presenter.isVisibleLine( "index and something else" );
        assertFalse( isVisible );
    }

    @Test
    public void testLineIsVisible() {
        boolean isVisible = this.presenter.isVisibleLine( "+hello" );
        assertTrue( isVisible );
    }

    @Test
    public void testIncrementLineNumber() {

        final List<String> lines = Arrays.asList( "+line", "-line", "the same", "+line" );
        Pair<Integer, Integer> lineNumber = this.presenter.incrementLineNumber( new Pair<>( 1, 1 ), lines.get( 0 ) );
        assertEquals( new Pair<>( 1, 2 ), lineNumber );

        lineNumber = this.presenter.incrementLineNumber( lineNumber, lines.get( 1 ) );
        assertEquals( new Pair<>( 2, 2 ), lineNumber );

        lineNumber = this.presenter.incrementLineNumber( lineNumber, lines.get( 2 ) );
        assertEquals( new Pair<>( 3, 3 ), lineNumber );

        lineNumber = this.presenter.incrementLineNumber( lineNumber, lines.get( 3 ) );
        assertEquals( new Pair<>( 3, 4 ), lineNumber );

    }

}