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
import javax.enterprise.event.Event;

import org.guvnor.pullrequest.client.events.StatusChanged;
import org.guvnor.pullrequest.client.resources.i18n.Constants;
import org.guvnor.structure.backend.repositories.PullRequestServiceMock;
import org.guvnor.structure.repositories.Comment;
import org.guvnor.structure.repositories.PullRequest;
import org.guvnor.structure.repositories.PullRequestService;
import org.guvnor.structure.repositories.PullRequestStatus;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mocks.CallerMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommentsPresenterTest {

    @Mock
    private TranslationService translationService;

    @Mock
    private PlaceManager placeManager;

    @Mock
    private CommentsPresenter.View view;

    @Mock
    private ManagedInstance<CommentPresenter> commentPresenters;

    @Mock
    private Event<NotificationEvent> notificationManager;

    @Mock
    private Event<StatusChanged> mergeEvent;

    private CommentsPresenter presenter;
    private PullRequest pullRequest;
    private PullRequestServiceMock service;
    private Caller<PullRequestService> caller;

    @Before
    public void setUp() {

        service = new PullRequestServiceMock();
        service.initialize();
        pullRequest = service.createPullRequest( "source", "a", "target", "a", "kie", "title" );

        when( translationService.format( Constants.PULL_REQUEST_COMMENTS_EMPTY_CONTENT ) ).thenReturn( "empty content" );
        when( translationService.format( Constants.PULL_REQUEST_COMMENTS_EMPTY_AUTHOR ) ).thenReturn( "empty author" );

        caller = new CallerMock<>( service );

        when( commentPresenters.get() ).thenReturn( mock( CommentPresenter.class ) );

        presenter = new CommentsPresenter( view, caller, translationService, commentPresenters, notificationManager, mergeEvent, placeManager );
        presenter.initialize();
        presenter.setPullRequest( pullRequest );
    }

    @Test
    public void testCommentWithoutAuthor() throws Exception {
        presenter.comment( "", "content" );
        verify( notificationManager ).fire( eq( new NotificationEvent( "empty author", NotificationEvent.NotificationType.ERROR ) ) );
    }

    @Test
    public void testCommentWithoutContent() throws Exception {
        presenter.comment( "kie", "" );
        verify( notificationManager ).fire( eq( new NotificationEvent( "empty content", NotificationEvent.NotificationType.ERROR ) ) );
    }

    @Test
    public void testSuccessfulContent() throws Exception {
        presenter.comment( "kie", "content" );
        final List<Comment> response = service.getCommentsByPullRequetsId( pullRequest.getTargetRepository(), pullRequest.getId() );
        assertEquals( 1, response.size() );
    }

    @Test
    public void testAddSeveralContents() throws Exception {
        presenter.comment( "kie", "content1" );
        presenter.comment( "kie", "content2" );
        presenter.comment( "kie", "content3" );
        presenter.comment( "kie", "content4" );
        presenter.comment( "kie", "content5" );
        final List<Comment> response = service.getCommentsByPullRequetsId( pullRequest.getTargetRepository(), pullRequest.getId() );
        assertEquals( 5, response.size() );
    }

    @Test
    public void testMerge() throws Exception {
        presenter.merge( "kie" );
        final List<Comment> response = service.getCommentsByPullRequetsId( pullRequest.getTargetRepository(), pullRequest.getId() );
        final PullRequest pr = service.getPullRequestByRepositoryAndId( pullRequest.getTargetRepository(), pullRequest.getId() );

        assertEquals( 1, response.size() );
        assertEquals( PullRequestStatus.MERGED, pr.getStatus() );
    }

    @Test
    public void testClose() throws Exception {
        presenter.close( "kie" );
        final List<Comment> response = service.getCommentsByPullRequetsId( pullRequest.getTargetRepository(), pullRequest.getId() );
        final PullRequest pr = service.getPullRequestByRepositoryAndId( pullRequest.getTargetRepository(), pullRequest.getId() );

        assertEquals( 1, response.size() );
        assertEquals( PullRequestStatus.CLOSED, pr.getStatus() );
    }

    @Test
    public void testCheckStatusAndMDoNotDisableButtons() {
        presenter.checkStatusAndDisableButtons( PullRequestStatus.OPEN );
        verify( view, times( 0 ) ).disableMergeButton();
        verify( view, times( 0 ) ).disableCloseButton();
    }

    @Test
    public void testCheckStatusAndDisableButtons() {
        presenter.checkStatusAndDisableButtons( PullRequestStatus.MERGED );
        verify( view, times( 1 ) ).disableMergeButton();
        verify( view, times( 1 ) ).disableCloseButton();
    }

    @Test
    public void testButtonsEnabled() throws Exception {
        presenter.comment( "kie", "content" );
        final PullRequest pr = service.getPullRequestByRepositoryAndId( pullRequest.getTargetRepository(), pullRequest.getId() );
        this.presenter.setPullRequest( pr );
        verify( view, times( 0 ) ).disableCloseButton();
        verify( view, times( 0 ) ).disableMergeButton();
    }

}