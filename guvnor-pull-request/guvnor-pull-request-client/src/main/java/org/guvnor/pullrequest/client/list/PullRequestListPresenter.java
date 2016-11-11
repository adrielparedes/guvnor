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

package org.guvnor.pullrequest.client.list;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.pullrequest.client.events.StatusChanged;
import org.guvnor.pullrequest.client.item.PullRequestItemPresenter;
import org.guvnor.pullrequest.client.resources.i18n.Constants;
import org.guvnor.structure.repositories.PullRequest;
import org.guvnor.structure.repositories.PullRequestService;
import org.guvnor.structure.repositories.PullRequestStatus;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@ApplicationScoped
@WorkbenchScreen(identifier = "PullRequestList")
public class PullRequestListPresenter {

    private final TranslationService translationService;
    private View view;
    final long pageSize = 10;

    private ManagedInstance<PullRequestItemPresenter> itemPresenters;
    private Caller<PullRequestService> pullRequestService;
    private boolean negated;
    private int openStatusPages;
    private String repository;

    public interface View extends IsWidget {

        void init( PullRequestListPresenter presenter );

        void addPullRequest( final PullRequestItemPresenter.View view );

        void setNumberOfOpenPullRequests( long number );

        void setNumberOfClosedPullRequests( long number );

        void clear();

        void setPaginator( long i );
    }

    @Inject
    public PullRequestListPresenter( final PullRequestListPresenter.View view,
                                     final ManagedInstance<PullRequestItemPresenter> itemPresenters,
                                     final TranslationService translationService,
                                     final Caller<PullRequestService> pullRequestService ) {
        this.view = view;
        this.itemPresenters = itemPresenters;
        this.pullRequestService = pullRequestService;
        this.translationService = translationService;
    }

    @PostConstruct
    public void initialize() {

        repository = "target";
        view.init( this );
        this.refresh();
    }

    public void refresh() {
        this.view.clear();
        refreshPullRequestsCount( repository, PullRequestStatus.OPEN, false, view::setNumberOfOpenPullRequests );
        refreshPullRequestsCount( repository, PullRequestStatus.OPEN, true, view::setNumberOfClosedPullRequests );
        refreshPullRequestsByStatus( PullRequestStatus.OPEN, repository, this.negated );
    }

    private void refreshPullRequestsCount( final String repository,
                                           PullRequestStatus status,
                                           boolean negated,
                                           RemoteCallback<Long> callback ) {
        pullRequestService.call( callback ).numberOfPullRequestsByStatus( repository, status, negated );
    }

    private void refreshPullRequestsByStatus( final PullRequestStatus status,
                                              final String repository,
                                              final boolean negated ) {
        pullRequestService
                .call( prs -> {
                    showPullRequests( (List<PullRequest>) prs );
                } )
                .getPullRequestsByStatus( 0, 0, repository, status, negated );
    }

    protected void showPullRequests( final List<PullRequest> prs ) {
        prs.forEach( ( pr ) -> {
            final PullRequestItemPresenter pullRequestItemPresenter = itemPresenters.get();
            pullRequestItemPresenter.setPullRequest( pr );
            pullRequestItemPresenter.initialize();
            view.addPullRequest( pullRequestItemPresenter.getView() );
        } );
    }

    public void createPullRequest() {
        pullRequestService.call( ( pr ) -> {
            refresh();
        } ).createPullRequest( "source", "a", "target", "b", "adrielparedes", "[GUVNOR-123] Pull Request Mock Title" );

    }

    public void showOpenPullRequests() {
        this.negated = false;
        this.refresh();
    }

    public void showClosedPullRequests() {
        this.negated = true;
        this.refresh();
    }

    public void calculatePaginatorSize() {
        pullRequestService.call( ( Long size ) -> {
            view.setPaginator( ( size + pageSize - 1 ) / pageSize );
        } ).numberOfPullRequestsByStatus( repository, PullRequestStatus.OPEN, negated );
    }

    public void onStatusChange( @Observes final StatusChanged statusChanged ) {
        this.refresh();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return translationService.format( Constants.PULL_REQUESTS_TITLE );
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

}
