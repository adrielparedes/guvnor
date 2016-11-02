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

package org.guvnor.pullrequest.client.diff;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.structure.repositories.PortableFileDiff;
import org.guvnor.structure.repositories.PullRequest;
import org.guvnor.structure.repositories.PullRequestService;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.api.ManagedInstance;

@Dependent
public class DiffsPresenter {

    private final Caller<PullRequestService> pullRequestService;

    public interface View extends IsWidget {

        void addFileDiff( FileDiffPresenter.View fileDiffView );

        void clear();
    }

    private final View view;

    private ManagedInstance<FileDiffPresenter> fileDiffPresenters;

    @Inject
    public DiffsPresenter( DiffsPresenter.View view,
                           Caller<PullRequestService> pullRequestService,
                           ManagedInstance<FileDiffPresenter> fileDiffsPresenter ) {
        this.view = view;
        this.pullRequestService = pullRequestService;
        this.fileDiffPresenters = fileDiffsPresenter;
    }

    public void setPullRequest( final PullRequest pullRequest ) {

        this.view.clear();

        pullRequestService.call( ( List<PortableFileDiff> diffs ) -> {

            diffs.forEach( ( diff ) -> {
                final FileDiffPresenter presenter = fileDiffPresenters.get();
                presenter.initialize( diff );
                this.view.addFileDiff( presenter.getView() );
            } );

        } ).diff( pullRequest );
    }

    public View getView() {
        return this.view;
    }

}