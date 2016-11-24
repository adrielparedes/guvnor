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
import java.util.stream.Collectors;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.base.HasPaginationSize;
import org.gwtbootstrap3.client.ui.base.HasResponsiveness;
import org.gwtbootstrap3.client.ui.base.helper.StyleHelper;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.PaginationSize;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.gwtbootstrap3.client.ui.html.UnorderedList;

public class SimplePaginationView extends UnorderedList implements HasResponsiveness,
                                                                   HasPaginationSize,
                                                                   SimplePaginationPresenter.View {

    private SimplePaginationPresenter presenter;
    private List<AnchorListItem> pages;

    public SimplePaginationView() {
        setStyleName( Styles.PAGINATION );
    }

    public void initialize( SimplePaginationPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setPageCount( final long pageCount ) {
    }

    public SimplePaginationView( final PaginationSize paginationSize ) {
        this();
        setPaginationSize( paginationSize );
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

    public void rebuild() {
        clear();
        createPrevLink();
        pages = createPageLinks( this.presenter.getPageCount() );
        createNextLink();
        activate( presenter.getSelectedPage() );
    }

    protected AnchorListItem createPrevLink() {
        final AnchorListItem prev = addPreviousLink();
        prev.addClickHandler( event -> this.presenter.prevPage() );
        return prev;
    }

    protected AnchorListItem createNextLink() {
        final AnchorListItem next = addNextLink();
        next.addClickHandler( event -> this.presenter.nextPage() );
        return next;
    }

    protected List<AnchorListItem> createPageLinks( final long pageCount ) {
        List<AnchorListItem> items = new ArrayList<>();
        for ( int i = 0; i < pageCount; i++ ) {
            final int display = i + 1;
            final AnchorListItem page = new AnchorListItem( String.valueOf( display ) );
            page.addClickHandler( clickEvent -> {
                presenter.selectPage( display );
            } );
            items.add( page );
            add( page );
        }
        return items;
    }

    public void activate( long id ) {

        pages.forEach( p -> p.setActive( false ) );
        final List<AnchorListItem> found = pages.stream()
                .filter( elem -> elem.getText().equals( String.valueOf( id ) ) )
                .collect( Collectors.toList() );

        if ( found.size() > 0 ) {
            final AnchorListItem page = found.get( 0 );
            page.setActive( true );
        }
    }

}
