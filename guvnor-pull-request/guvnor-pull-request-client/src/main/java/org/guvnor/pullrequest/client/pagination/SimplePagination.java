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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.base.HasPaginationSize;
import org.gwtbootstrap3.client.ui.base.HasResponsiveness;
import org.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.PaginationSize;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.html.UnorderedList;

public class SimplePagination extends UnorderedList implements HasResponsiveness,
                                                               HasPaginationSize {

    private Consumer<Integer> handlePage;
    private long pageCount;
    private List<AnchorListItem> pages;
    private int selectedPage;

    public SimplePagination() {
        this.selectedPage = 1;
        this.pageCount = 1;
        setStyleName( Styles.PAGINATION );
        pages = new ArrayList<>();
    }

    public SimplePagination( final PaginationSize paginationSize ) {
        this();
        setPaginationSize( paginationSize );
    }

    public void initialize( long pageCount ) {
        this.selectedPage = 1;
        setStyleName( Styles.PAGINATION );
        pages = new ArrayList<>();
        this.setPageCount( pageCount );
    }

    public void setPageCount( long pageCount ) {
        this.pageCount = pageCount;
        this.rebuild();
    }

    @Override
    public void setPaginationSize( final PaginationSize paginationSize ) {
        StyleHelper.addUniqueEnumStyleName( this, PaginationSize.class, paginationSize );
    }

    @Override
    public PaginationSize getPaginationSize() {
        return PaginationSize.fromStyleName( getStyleName() );
    }

    public AnchorListItem addPreviousLink() {
        final AnchorListItem listItem = new AnchorListItem();
        listItem.setIcon( IconType.ANGLE_DOUBLE_LEFT );
        insert( listItem, 0 );
        return listItem;
    }

    public AnchorListItem addNextLink() {
        final AnchorListItem listItem = new AnchorListItem();
        listItem.setIcon( IconType.ANGLE_DOUBLE_RIGHT );
        add( listItem );
        return listItem;
    }

    public void setPageHandler( Consumer<Integer> handlePage ) {
        this.handlePage = handlePage;
    }

    public void rebuild() {
        clear();
        pages = new ArrayList<>();
        createPrevLink();
        createPageLinks();
        createNextLink();
    }

    private void createPrevLink() {
        final AnchorListItem prev = addPreviousLink();
        prev.addClickHandler( event -> {
            if ( selectedPage > 0 ) {
                final int prevId = selectedPage - 1;
                activate( prevId );
                handlePage.accept( prevId );
            }
        } );
    }

    private void createNextLink() {
        final AnchorListItem next = addNextLink();
        next.addClickHandler( event -> {
            if ( selectedPage < pageCount ) {
                final int nextId = selectedPage + 1;
                activate( nextId );
                handlePage.accept( nextId );
            }
        } );
    }

    private void createPageLinks() {

        for ( int i = 0; i < pageCount; i++ ) {
            final int display = i + 1;
            final AnchorListItem page = new AnchorListItem( String.valueOf( display ) );
            pages.add( page );
            page.addClickHandler( clickEvent -> {
                activate( display );
                handlePage.accept( display );
            } );
            add( page );
        }
    }

    public void activate( int id ) {
        pages.forEach( p -> p.setActive( false ) );
        this.selectedPage = id;
        final AnchorListItem page = pages.stream()
                .filter( elem -> elem.getText().equals( String.valueOf( id ) ) )
                .collect( Collectors.toList() ).get( 0 );
        page.setActive( true );
    }

}
