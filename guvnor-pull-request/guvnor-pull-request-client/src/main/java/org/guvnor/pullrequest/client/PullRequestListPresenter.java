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

package org.guvnor.pullrequest.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;

@ApplicationScoped
@WorkbenchScreen(identifier = "PullRequestList")
public class PullRequestListPresenter {

    private View view;

    private ManagedInstance<PullRequestItemPresenter> itemPresenters;
    private Caller<> pullRequestService;

    public interface View extends IsWidget {

        void addPullRequest( final PullRequestItemPresenter.View view );
    }

    @Inject
    public PullRequestListPresenter( final View view,
                                     final ManagedInstance<PullRequestItemPresenter> itemPresenters ) {
        this.view = view;
        this.itemPresenters = itemPresenters;
    }

    @PostConstruct
    public void initialize() {

        final PullRequestItemPresenter itemPresenter = itemPresenters.get();

        view.addPullRequest( itemPresenter.getView() );

    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Pull Requests";
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

}
