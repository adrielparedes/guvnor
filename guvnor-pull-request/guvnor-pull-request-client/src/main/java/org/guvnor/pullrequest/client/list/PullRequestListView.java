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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.guvnor.pullrequest.client.item.PullRequestItemPresenter;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.LinkedGroup;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class PullRequestListView extends Composite implements PullRequestListPresenter.View {

    @Inject
    private TranslationService translationService;

    @Inject
    @DataField("pull-requests-list")
    private LinkedGroup pullRequests;

    @Inject
    @DataField("open-link")
    private Anchor openLink;

    @Inject
    @DataField("closed-link")
    private Anchor closedLink;

    @Inject
    @DataField("create-pull-request")
    private Button createMockPR;

    @Inject
    @DataField("open-pull-requests-size")
    private Span openPullRequestsSize;

    @Inject
    @DataField("closed-pull-requests-size")
    private Span closedPullRequestsSize;

    private PullRequestListPresenter presenter;

    @Override
    public void init( PullRequestListPresenter presenter ) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void initialize() {
        setOpenLinkString();
        pullRequests.clear();
    }

    @Override
    public void addPullRequest( final PullRequestItemPresenter.View view ) {
        this.pullRequests.add( view );
    }

    @Override
    public void setNumberOfOpenPullRequests( final long number ) {
        this.openPullRequestsSize.setText( String.valueOf( number ) );
    }

    @Override
    public void setNumberOfClosedPullRequests( final long number ) {
        this.closedPullRequestsSize.setText( String.valueOf( number ) );
    }

    @Override
    public void clear() {
        this.pullRequests.clear();
    }

    @Override
    public void setPaginator( final long i ) {
        
    }

    @EventHandler("open-link")
    public void showOpenPullRequests( final ClickEvent event ) {
        setOpenLinkString();
        presenter.showOpenPullRequests();
    }

    @EventHandler("closed-link")
    public void showClosedPullRequests( final ClickEvent event ) {
        setClosedLinkStrong();
        presenter.showClosedPullRequests();
    }

    private void setClosedLinkStrong() {
        final String strong = "strong";
        this.closedLink.addStyleName( strong );
        this.openLink.removeStyleName( strong );
    }

    private void setOpenLinkString() {
        final String strong = "strong";
        this.openLink.addStyleName( strong );
        this.closedLink.removeStyleName( strong );
    }

    @EventHandler("create-pull-request")
    public void createPullRequest( final ClickEvent event ) {
        presenter.createPullRequest();
    }
}
