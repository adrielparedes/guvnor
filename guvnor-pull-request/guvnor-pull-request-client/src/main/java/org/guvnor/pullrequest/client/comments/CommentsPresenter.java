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

package org.guvnor.pullrequest.client.comments;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.pullrequest.client.events.StatusChanged;
import org.guvnor.pullrequest.client.resources.i18n.Constants;
import org.guvnor.structure.repositories.Comment;
import org.guvnor.structure.repositories.PullRequest;
import org.guvnor.structure.repositories.PullRequestService;
import org.guvnor.structure.repositories.PullRequestStatus;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * Represents the list of comments for a Pull Request. It check if the PR is open or not to enable or
 * disable action buttons, and show all the comment that PR has.
 */
@Dependent
public class CommentsPresenter {

    private final Caller<PullRequestService> pullRequestService;
    private final ManagedInstance<CommentPresenter> commentPresenters;
    private final Event<NotificationEvent> notificationManager;
    private final Event<StatusChanged> mergeEvent;
    private final PlaceManager placeManager;
    private final TranslationService translationService;
    private final SessionInfo sessionInfo;
    private String repository;
    private long id;

    public interface View extends IsWidget {

        void init( CommentsPresenter presenter );

        void clear();

        void addComment( CommentPresenter.View view );

        void enableMergeButton();

        void disableMergeButton();

        void enableCloseButton();

        void disableCloseButton();
    }

    private View view;

    @Inject
    public CommentsPresenter( final CommentsPresenter.View view,
                              Caller<PullRequestService> pullRequestService,
                              TranslationService translationService,
                              ManagedInstance<CommentPresenter> commentPresenters,
                              Event<NotificationEvent> notificationManager,
                              Event<StatusChanged> mergeEvent,
                              PlaceManager placeManager,
                              SessionInfo sessionInfo ) {
        this.view = view;
        this.pullRequestService = pullRequestService;
        this.commentPresenters = commentPresenters;
        this.notificationManager = notificationManager;
        this.placeManager = placeManager;
        this.translationService = translationService;
        this.mergeEvent = mergeEvent;
        this.sessionInfo = sessionInfo;
    }

    @PostConstruct
    public void initialize() {
        this.view.init( this );
    }

    public void setPullRequest( PullRequest pullRequest ) {
        this.id = pullRequest.getId();
        this.repository = pullRequest.getTargetRepository();
        this.checkStatusAndDisableButtons( pullRequest.getStatus() );
        this.refresh();
    }

    public void comment( final String content ) {

        this.comment( content, o -> this.refresh() );
    }

    public void merge() {

        final String content = "Merged";
        this.comment( content, o -> {
            pullRequestService.call( ( PullRequest pr ) -> {
                this.pullRequestService.call( accepted -> {
                    this.refresh();
                    mergeEvent.fire( new StatusChanged( PullRequestStatus.MERGED ) );
                } ).acceptPullRequest( pr );
            } ).getPullRequestByRepositoryAndId( repository, id );
        } );

    }

    public void close() {
        final String content = "Closed";
        this.comment( content, o -> {
            pullRequestService.call( ( PullRequest pr ) -> {
                this.pullRequestService.call( accepted -> {
                    this.refresh();
                    mergeEvent.fire( new StatusChanged( PullRequestStatus.CLOSED ) );
                } ).closePullRequest( pr );
            } ).getPullRequestByRepositoryAndId( repository, id );
        } );
    }

    public void isMerged( @Observes StatusChanged event ) {
        checkStatusAndDisableButtons( event.getStatus() );
    }

    protected void checkStatusAndDisableButtons( final PullRequestStatus status ) {
        if ( status.equals( PullRequestStatus.OPEN ) ) {
            this.view.enableCloseButton();
            this.view.enableMergeButton();
        } else {
            this.view.disableMergeButton();
            this.view.disableCloseButton();
        }
    }

    public void comment( final String content,
                         final RemoteCallback<?> callback ) {

        String author = this.getUserName();

        if ( isEmpty( content ) ) {
            notificationManager.fire( new NotificationEvent( translationService.format( Constants.PULL_REQUEST_COMMENTS_EMPTY_CONTENT ),
                                                             NotificationEvent.NotificationType.ERROR ) );
        }

        if ( isEmpty( author ) ) {
            notificationManager.fire( new NotificationEvent( translationService.format( Constants.PULL_REQUEST_COMMENTS_EMPTY_AUTHOR ),
                                                             NotificationEvent.NotificationType.ERROR ) );
        }

        if ( !isEmpty( content ) && !isEmpty( author ) ) {

            this.pullRequestService.call( callback ).addComment( repository,
                                                                 id, author, content );
        }
    }

    private boolean isEmpty( final String text ) {
        return text == null || "".equals( text );
    }

    private void refresh() {
        this.view.clear();
        pullRequestService.call( ( List<Comment> comments ) -> {

            comments.forEach( comment -> {
                final CommentPresenter presenter = commentPresenters.get();
                presenter.initialize( comment );
                this.view.addComment( presenter.getView() );
            } );

        } ).getCommentsByPullRequetsId( repository, id );
    }

    public View getView() {
        return this.view;
    }

    protected String getUserName() {
        try {
            return this.sessionInfo.getIdentity().getIdentifier();
        } catch ( final Exception e ) {
            return "system";
        }
    }
}
