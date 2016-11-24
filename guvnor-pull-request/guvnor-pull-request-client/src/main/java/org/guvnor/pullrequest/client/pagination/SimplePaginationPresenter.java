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
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
public class SimplePaginationPresenter {

    public interface View extends IsWidget {

        void initialize( SimplePaginationPresenter presenter );

        void setPageCount( long pageCount );

        void rebuild();

        void activate( long page );

    }

    private long pageSize;

    private long itemsCount;

    private View view;

    private long selectedPage;

    private Consumer<Long> clickHandler;

    @Inject
    public SimplePaginationPresenter( SimplePaginationPresenter.View view ) {
        this.itemsCount = 0;
        this.pageSize = 10;
        this.view = view;
    }

    @PostConstruct
    public void initialize() {
        this.setPageSize( 10 );
        this.view.initialize( this );
        this.view.setPageCount( 1 );
        this.view.rebuild();
    }

    public long getSelectedPage() {
        return selectedPage;
    }

    public void setClickHandler( final Consumer<Long> clickHandler ) {
        this.clickHandler = clickHandler;
    }

    protected boolean isValidRange( final long page ) {
        return page >= 1 && page <= getPageCount();
    }

    public long nextPage() {
        if ( this.getSelectedPage() < getPageCount() ) {
            this.selectPage( this.getSelectedPage() + 1 );
        }
        return this.getSelectedPage();
    }

    public void selectPage( final long page ) {
        this.setSelectedPage( page );
        this.view.activate( page );
        if ( clickHandler != null ) {
            this.clickHandler.accept( page );
        }
    }

    public long getPageCount() {
        if ( itemsCount == 0 || itemsCount == 0 ) {
            return 1;
        }
        return (int) ( itemsCount + pageSize - 1 ) / pageSize;
    }

    public long prevPage() {
        if ( this.getSelectedPage() > 1 ) {
            this.selectPage( this.getSelectedPage() - 1 );
        }
        return this.getSelectedPage();
    }

    public void setSelectedPage( final long page ) {
        if ( this.isValidRange( page ) ) {
            this.selectedPage = page;
        } else {
            throw new IllegalArgumentException( "Page <<" + page + ">>is out of range. Must be between 1 and " + this.getPageCount() );
        }
    }

    public void setPageSize( final long pageSize ) {
        this.pageSize = pageSize;
    }

    public void setItemsCount( final long itemsCount ) {
        this.itemsCount = itemsCount;
    }

    public void refresh() {
        this.view.rebuild();
    }

    public View getView() {
        return this.view;
    }

}
