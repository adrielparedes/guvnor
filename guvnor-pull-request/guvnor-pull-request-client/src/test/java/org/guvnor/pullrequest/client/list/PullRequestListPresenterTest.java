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

package org.guvnor.pullrequest.client.list;

import org.guvnor.pullrequest.client.item.PullRequestItemPresenter;
import org.guvnor.structure.backend.repositories.PullRequestServiceMock;
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
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PullRequestListPresenterTest {

    @Mock
    private PullRequestListPresenter.View view;

    @Mock
    private ManagedInstance<PullRequestItemPresenter> itemPresenters;

    @Mock
    private TranslationService translationService;

    private Caller<PullRequestService> caller;
    private PullRequestServiceMock serviceMock;
    private PullRequestListPresenter presenter;

    @Before
    public void setUp() {
        serviceMock = new PullRequestServiceMock();
        serviceMock.initialize();
        caller = new CallerMock<>( serviceMock );
        presenter = new PullRequestListPresenter( view, itemPresenters, translationService, caller );
        presenter.initialize();

        when( itemPresenters.get() ).thenReturn( mock( PullRequestItemPresenter.class ) );

        final PullRequest pr1 = serviceMock.createPullRequest( "source", "a", "target", "b", "kie", "PR-1" );
        final PullRequest pr2 = serviceMock.createPullRequest( "source", "a", "target", "b", "kie", "PR-2" );
        final PullRequest pr3 = serviceMock.createPullRequest( "source", "a", "target", "b", "kie", "PR-3" );
        final PullRequest pr4 = serviceMock.createPullRequest( "source", "a", "target", "b", "kie", "PR-4" );

        serviceMock.acceptPullRequest( pr3 );

    }

    @Test
    public void testShowOpenPullRequests() {
        presenter.showOpenPullRequests();
        verify( view, times( 2 ) ).clear();
        verify( view, times( 3 ) ).addPullRequest( any() );
    }

    @Test
    public void testShowClosedPullRequests() {
        presenter.showClosedPullRequests();
        assertEquals( 1, serviceMock.getPullRequestsByStatus( 0, 0, "target", PullRequestStatus.OPEN, true ).size() );
        verify( view, times( 2 ) ).clear();
        verify( view, times( 1 ) ).addPullRequest( any() );
    }

}