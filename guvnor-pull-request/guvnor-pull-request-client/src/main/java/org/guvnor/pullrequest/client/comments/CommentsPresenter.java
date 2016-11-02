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
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.structure.repositories.Comment;
import org.guvnor.structure.repositories.PullRequest;
import org.guvnor.structure.repositories.PullRequestService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * Represents the list of comments for a Pull Request.
 */
@Dependent
public class CommentsPresenter {

    private final Caller<PullRequestService> pullRequestService;
    private final ManagedInstance<CommentPresenter> commentPresenters;
    private final Event<NotificationEvent> notificationManager;
    private String repository;
    private long id;

    public interface View extends IsWidget {

        void init( CommentsPresenter presenter );

        void clear();

        void addComment( CommentPresenter.View view );

    }

    private View view;

    @Inject
    public CommentsPresenter( final CommentsPresenter.View view,
                              Caller<PullRequestService> pullRequestService,
                              ManagedInstance<CommentPresenter> commentPresenters,
                              Event<NotificationEvent> notificationManager ) {
        this.view = view;
        this.pullRequestService = pullRequestService;
        this.commentPresenters = commentPresenters;
        this.notificationManager = notificationManager;
    }

    @PostConstruct
    public void initialize() {
        this.view.init( this );

    }

    public void setPullRequest( PullRequest pullRequest ) {
        this.id = pullRequest.getId();
        this.repository = pullRequest.getTargetRepository();
        this.refresh();
    }

    public void comment( final String author,
                         final String content ) {

        if ( isEmpty( content ) ) {
            notificationManager.fire( new NotificationEvent( "Content should not be empty",
                                                             NotificationEvent.NotificationType.ERROR ) );
        }

        if ( isEmpty( author ) ) {
            notificationManager.fire( new NotificationEvent( "Author should not be empty",
                                                             NotificationEvent.NotificationType.ERROR ) );
        }

        if ( !isEmpty( content ) && !isEmpty( author ) ) {
            this.pullRequestService.call( o -> this.refresh() ).addComment( repository,
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
}
