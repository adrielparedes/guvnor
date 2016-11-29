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

/*
 * 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.pullrequest.client.item;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.guvnor.pullrequest.client.resources.i18n.Constants;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.common.client.logging.util.StringFormat;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 * Represents a Pull Request Item view. It shows the status of the pull request (blue if it is open, or red if it is not).
 * It also show the title and the days the PR it has been open.
 */
@Dependent
@Templated
public class PullRequestItemView extends Composite implements PullRequestItemPresenter.View {

    @Inject
    @DataField
    private Anchor title;

    @Inject
    @DataField("icon-circle")
    private Span icon;

    @Inject
    @DataField
    private Anchor subtitle;

    @Inject
    @DataField
    private Span from;

    @Inject
    @DataField
    private Span to;

    @Inject
    @DataField
    private Button review;

    @Inject
    private TranslationService translationService;

    private PullRequestItemPresenter presenter;
    private long id;
    private String repository;

    @Override
    public void init( PullRequestItemPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setId( final long id ) {
        this.id = id;
    }

    @Override
    public void setRepository( final String repository ) {
        this.repository = repository;
    }

    @Override
    public void setFrom( final String from ) {
        this.from.setText( from );
    }

    @Override
    public void setTo( final String to ) {
        this.to.setText( to );
    }

    @Override
    public void setTitle( String title ) {
        this.title.setText( title );
    }

    @Override
    public void setStatus( String status ) {
        if ( status.equals( "open" ) ) {
            this.icon.addStyleName( "primary" );
        } else {
            this.icon.addStyleName( "red" );
        }
    }

    @Override
    public void setSubtitle( long id,
                             long daysAgo,
                             String username ) {
        String subtitle = StringFormat.format( translationService.format( Constants.PULL_REQUEST_ITEM_VIEW_SUBTITLE ), id, daysAgo, username );
        this.subtitle.setText( subtitle );
    }

    @EventHandler("review")
    public void goToReviewView( final ClickEvent event ) {
        presenter.goToReview( id, repository );
    }

}
