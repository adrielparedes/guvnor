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

import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.structure.repositories.PullRequest;
import org.guvnor.structure.repositories.PullRequestService;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

@Dependent
public class PullRequestItemPresenter {

    private final Caller<PullRequestService> pullRequestService;

    public interface View extends IsWidget {

        void init( PullRequestItemPresenter presenter );

        void setId( long id );

        void setRepository( String repository );

        void setFrom( String from );

        void setTo( String to );

        void setTitle( String title );

        void setSubtitle( String subtitle );
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
                                     PlaceManager placeManager,
                                     Caller<PullRequestService> pullRequestService ) {
        this.view = view;
        this.placeManager = placeManager;
        this.pullRequestService = pullRequestService;

    }

    public void initialize() {

        this.view.init( this );
        this.view.setRepository( this.getPullRequest().getTargetRepository() );
        this.view.setTitle( "Pull Request featuressss" );
        this.view.setSubtitle( "#" + this.getPullRequest().getId() + " opened 4 days ago by aparedes" );
        this.view.setId( this.getPullRequest().getId() );
        this.view.setFrom( this.getPullRequest().getSourceRepository() + "/" + this.getPullRequest().getSourceBranch() );
        this.view.setTo( this.getPullRequest().getTargetRepository() + "/" + this.getPullRequest().getTargetBranch() );
    }

    public void goToReview( final long id,
                            final String repository ) {
        final Map<String, String> params = new HashMap<String, String>() {
            {
                put( "id", String.valueOf( id ) );
                put( "repository", String.valueOf( repository ) );
            }
        };
        PlaceRequest request = new DefaultPlaceRequest( "PullRequestDescription", params );
        placeManager.goTo( request );
    }

    public View getView() {
        return view;
    }

}
