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

package org.guvnor.pullrequest.client.pagination;

import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SimplePaginationPresenterTest {

    @Mock
    private SimplePaginationPresenter presenter;

    @Mock
    private SimplePaginationPresenter.View view;

    @Before
    public void setUp() {
        presenter = new SimplePaginationPresenter( view );
        presenter.initialize();
        presenter.setPageSize( 10 );
        presenter.setItemsCount( 400 );
        presenter.setSelectedPage( 1 );
    }

    @Test
    public void testInitialize() {
        verify( view ).initialize( eq( presenter ) );
        verify( view ).setPageCount( 1 );
    }

    @Test
    public void testNextPage() {
        final long page = presenter.nextPage();
        assertEquals( 2, page );
        assertEquals( 2, presenter.getSelectedPage() );
    }

    @Test
    public void testPrevPage() {
        presenter.setSelectedPage( 4 );
        final long page = presenter.prevPage();
        assertEquals( 3, page );
        assertEquals( 3, presenter.getSelectedPage() );
    }

    @Test
    public void testPrevPageEmpty() {
        presenter.setSelectedPage( 1 );
        final long page = presenter.prevPage();
        assertEquals( 1, page );
        assertEquals( 1, presenter.getSelectedPage() );
    }

    @Test
    public void testPageCount() {
        assertEquals( 40, presenter.getPageCount() );
    }

    @Test
    public void testPageCountZeroItems() {
        presenter.setPageSize( 10 );
        presenter.setItemsCount( 0 );
        assertEquals( 1, presenter.getPageCount() );
    }

    @Test
    public void testPageCountLessThanTenItems() {
        presenter.setPageSize( 10 );
        presenter.setItemsCount( 1 );
        assertEquals( 1, presenter.getPageCount() );
    }

    @Test
    public void testClickHandler() {

        presenter.setPageSize( 10 );
        presenter.setItemsCount( 400 );

        final Consumer<Long> consumerMock = mock( Consumer.class );
        presenter.setClickHandler( consumerMock );
        presenter.selectPage( 3 );

        verify( consumerMock, only() ).accept( eq( 3l ) );
    }

    @Test
    public void testInvalidaPageSelection() {

        presenter.setPageSize( 1 );
        final Consumer<Long> consumerMock = mock( Consumer.class );
        presenter.setClickHandler( consumerMock );

        try {
            presenter.selectPage( -1 );
            fail();
        } catch ( IllegalArgumentException e ) {
            verify( consumerMock, never() ).accept( eq( 3l ) );
            assertEquals( 1, presenter.getSelectedPage() );

        }

    }

    @Test
    public void testValidRange() {
        assertTrue( this.presenter.isValidRange( 3 ) );
        assertFalse( this.presenter.isValidRange( 0 ) );
    }

}