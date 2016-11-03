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

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.pullrequest.client.comments.CommentsPresenter;
import org.guvnor.pullrequest.client.diff.DiffsPresenter;
import org.guvnor.structure.repositories.PullRequest;
import org.guvnor.structure.repositories.PullRequestService;
import org.jboss.errai.common.client.api.Caller;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.PlaceRequest;

@ApplicationScoped
@WorkbenchScreen(identifier = "PullRequestDescription")
public class PullRequestDescriptionPresenter {

    public static final String PULL_REQUESTS_DESCRIPTION_TITLE = "Pull Requests Description";
    private final Caller<PullRequestService> pullRequestService;
    private PlaceRequest place;

    public interface View extends IsWidget {

        void initialize( PullRequestDescriptionPresenter presenter,
                         CommentsPresenter commentsPresenter,
                         DiffsPresenter diffsPresenter );

        void setId( final long l );

        void setTitle( final String title );

        void setAuthor( final String author );

        void setSource( final String source );

        void setTarget( final String target );

        void setStatus( String status );
    }

    private final View view;
    private final CommentsPresenter commentsPresenter;
    private final DiffsPresenter diffsPresenter;

    @Inject
    public PullRequestDescriptionPresenter( final PullRequestDescriptionPresenter.View view,
                                            final Caller<PullRequestService> pullRequestService,
                                            final CommentsPresenter commentsPresenter,
                                            final DiffsPresenter diffsPresenter
    ) {
        this.view = view;
        this.commentsPresenter = commentsPresenter;
        this.diffsPresenter = diffsPresenter;
        this.pullRequestService = pullRequestService;
    }

    @PostConstruct
    public void initialize() {
        this.view.initialize( this, commentsPresenter, diffsPresenter );
    }

    @OnStartup
    public void onStartup( final PlaceRequest place ) {
        this.place = place;
        setup( place.getParameters() );
    }

    public void setup( final Map<String, String> parameters ) {

        final long id = Long.parseLong( parameters.get( "id" ) );
        final String repository = parameters.get( "repository" );

        pullRequestService.call( ( PullRequest pr ) -> {
            this.view.setId( pr.getId() );
            this.view.setTitle( pr.getTitle() );
            this.view.setAuthor( pr.getAuthor() );
            this.view.setSource( buildRepoName( pr.getSourceRepository(), pr.getSourceBranch() ) );
            this.view.setTarget( buildRepoName( pr.getTargetRepository(), pr.getTargetBranch() ) );
            this.view.setStatus( pr.getStatus().toString() );
            commentsPresenter.setPullRequest( pr );
            diffsPresenter.setPullRequest( pr );
        } ).getPullRequestByRepositoryAndId( repository, id );
    }

    private String buildRepoName( String repository,
                                  String branch ) {
        return repository + "/" + branch;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return PULL_REQUESTS_DESCRIPTION_TITLE;
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

}
