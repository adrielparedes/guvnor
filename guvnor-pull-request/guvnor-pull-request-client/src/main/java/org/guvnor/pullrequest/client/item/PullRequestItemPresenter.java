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

package org.guvnor.pullrequest.client.item;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.pullrequest.client.utils.Places;
import org.guvnor.structure.repositories.PullRequest;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

/**
 * Represents the behaviour of Pull Request Item that goes into {@link org.guvnor.pullrequest.client.list.PullRequestListPresenter}.
 * It create the item, calculate the days ago it was created, and the link to the
 * {@link org.guvnor.pullrequest.client.description.PullRequestDescriptionPresenter}
 */
@Dependent
public class PullRequestItemPresenter {

    public interface View extends IsWidget {

        void init( PullRequestItemPresenter presenter );

        void setId( long id );

        void setRepository( String repository );

        void setFrom( String from );

        void setTo( String to );

        void setTitle( String title );

        void setSubtitle( long id,
                          long daysAgo,
                          String username );

        void setStatus( String s );
    }

    private PlaceManager placeManager;

    public void setPullRequest( final PullRequest pullRequest ) {
        this.pullRequest = pullRequest;
    }

    public PullRequest getPullRequest() {
        return this.pullRequest;
    }

    private View view;
    private PullRequest pullRequest;

    @Inject
    public PullRequestItemPresenter( final View view,
                                     PlaceManager placeManager ) {
        this.view = view;
        this.placeManager = placeManager;

    }

    public void initialize() {

        this.view.init( this );
        this.view.setRepository( this.getPullRequest().getTargetRepository() );
        this.view.setTitle( this.getPullRequest().getTitle() );
        this.view.setSubtitle( this.getPullRequest().getId(), this.calculateDaysAgo( this.getPullRequest().getDate() ), this.getPullRequest().getAuthor() );
        this.view.setId( this.getPullRequest().getId() );
        this.view.setFrom( generatePath( this.getPullRequest().getSourceRepository(), this.getPullRequest().getSourceBranch() ) );
        this.view.setTo( this.generatePath( this.getPullRequest().getTargetRepository(), this.getPullRequest().getTargetBranch() ) );
        this.view.setStatus( this.getPullRequest().getStatus().toString().toLowerCase() );
    }

    protected String generatePath( String repository,
                                   String branch ) {
        return repository + "/" + branch;
    }

    protected long calculateDaysAgo( final Date date ) {
        long diff = new Date().getTime() - date.getTime();
        return diff / ( 1000 * 60 * 60 * 24 );
    }

    public void goToReview( final long id,
                            final String repository ) {
        final Map<String, String> params = new HashMap<String, String>() {
            {
                put( "id", String.valueOf( id ) );
                put( "repository", String.valueOf( repository ) );
            }
        };
        PlaceRequest request = new DefaultPlaceRequest( Places.PULL_REQUEST_DESCRIPTION, params );
        placeManager.goTo( request );
    }

    public View getView() {
        return view;
    }

}
