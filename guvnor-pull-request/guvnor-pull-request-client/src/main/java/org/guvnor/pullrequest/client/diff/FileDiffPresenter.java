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
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.slf4j.Logger;

@Dependent
public class FileDiffPresenter {

    private final ManagedInstance<LineDiffPresenter> lineDiffPresenters;
    private final Logger logger;

    public interface View extends IsWidget {

        void addLine( LineDiffPresenter.View view );
    }

    private final View view;

    @Inject
    public FileDiffPresenter( FileDiffPresenter.View view,
                              Logger logger,
                              ManagedInstance<LineDiffPresenter> lineDiffPresenters ) {
        this.view = view;
        this.logger = logger;
        this.lineDiffPresenters = lineDiffPresenters;
    }

    public void initialize( final PortableFileDiff diff ) {

        final List<String> lines = diff.getLines();
        logger.info( "{}", lines );
        lines.forEach( ( line ) -> {
            final LineDiffPresenter presenter = lineDiffPresenters.get();
            presenter.setLine( line, 1l );
            this.getView().addLine( presenter.getView() );
        } );

    }

    public View getView() {
        return this.view;
    }

}
