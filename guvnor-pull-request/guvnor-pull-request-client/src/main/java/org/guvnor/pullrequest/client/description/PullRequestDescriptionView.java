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

package org.guvnor.pullrequest.client.description;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Composite;
import org.guvnor.pullrequest.client.comments.CommentsPresenter;
import org.guvnor.pullrequest.client.diff.DiffsPresenter;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class PullRequestDescriptionView extends Composite implements PullRequestDescriptionPresenter.View {

    @Inject
    @DataField
    private NavTabs tabs;

    @Inject
    @DataField
    private TabContent content;

    @Inject
    @DataField("id")
    private Span id;

    @Inject
    @DataField("title")
    private Span title;

    @Inject
    @DataField("author")
    private Span author;

    @Inject
    @DataField("source")
    private Span source;

    @Inject
    @DataField("target")
    private Span target;

    @Inject
    @DataField("status")
    private Span status;

    private PullRequestDescriptionPresenter presenter;

    @Inject
    public PullRequestDescriptionView() {
    }

    @Override
    public void initialize( PullRequestDescriptionPresenter presenter,
                            CommentsPresenter commentsPresenter,
                            DiffsPresenter diffsPresenter ) {

        this.presenter = presenter;

        final TabPane commentsPane = new TabPane() {{
            add( commentsPresenter.getView() );
        }};

        final TabPane diffsPane = new TabPane() {{
            add( diffsPresenter.getView() );
        }};

        content.add( commentsPane );
        content.add( diffsPane );

        tabs.add( new TabListItem( "Comments" ) {{
            addStyleName( "uf-dropdown-tab-list-item" );
            setDataTargetWidget( commentsPane );
            setActive( true );
        }} );

        tabs.add( new TabListItem( "Diffs" ) {{
            addStyleName( "uf-dropdown-tab-list-item" );
            setDataTargetWidget( diffsPane );
        }} );

        commentsPane.setActive( true );

    }

    @Override
    public void setId( final long l ) {
        this.id.setText( String.valueOf( l ) );
    }

    @Override
    public void setTitle( final String title ) {
        this.title.setText( title );
    }

    @Override
    public void setAuthor( final String author ) {
        this.author.setText( author );
    }

    @Override
    public void setSource( final String source ) {
        this.source.setText( source );
    }

    @Override
    public void setTarget( final String target ) {
        this.target.setText( target );
    }

    @Override
    public void setStatus( final String status ) {
        this.status.setText( status );
    }
}
