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
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.VFSService;
import org.uberfire.client.mvp.PlaceManager;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class FileDiffPresenter {

    private final ManagedInstance<LineDiffPresenter> lineDiffPresenters;
    private final Caller<VFSService> vfsServiceCaller;
    private final PlaceManager placeManager;

    public interface View extends IsWidget {

        void initialize( FileDiffPresenter presenter );

        void addLine( LineDiffPresenter.View view );
    }

    private final View view;

    @Inject
    public FileDiffPresenter( FileDiffPresenter.View view,
                              Caller<VFSService> vfsService,
                              PlaceManager placeManager,
                              ManagedInstance<LineDiffPresenter> lineDiffPresenters ) {
        this.view = view;
        this.lineDiffPresenters = lineDiffPresenters;
        this.vfsServiceCaller = vfsService;
        this.placeManager = placeManager;
    }

    public void initialize( final PortableFileDiff diff ) {

        this.view.initialize( this );

        final List<String> lines = diff.getLines();
        lines.forEach( ( line ) -> {
            final LineDiffPresenter presenter = lineDiffPresenters.get();
            presenter.setLine( line, 1l );
            this.getView().addLine( presenter.getView() );
        } );

    }

    public void openFile( String branch,
                          String uri ) {

        checkNotEmpty( "branch", branch );
        checkNotEmpty( "uri", uri );

        vfsServiceCaller.call( new RemoteCallback<Path>() {
            @Override
            public void callback( Path path ) {
                placeManager.goTo( path );
            }
        } ).get( "default://" + branch + "@" + uri );
    }

    public View getView() {
        return this.view;
    }

}
