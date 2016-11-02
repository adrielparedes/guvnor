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

import javax.enterprise.event.Event;

import org.guvnor.structure.repositories.PullRequestService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.uberfire.workbench.events.NotificationEvent;

public class CommentsPresenterTest {

    @Mock
    private CommentsPresenter.View view;

    @Mock
    private Caller<PullRequestService> service;

    @Mock
    private ManagedInstance<CommentPresenter> commentPresenters;

    @Mock
    private Event<NotificationEvent> notificationManager;

    private CommentsPresenter presenter;

    @Before
    public void setUp() {
        presenter = new CommentsPresenter( view, service, commentPresenters, notificationManager );
    }

    @Test
    public void comment() throws Exception {

        presenter.comment( "", "content" );
    }

}