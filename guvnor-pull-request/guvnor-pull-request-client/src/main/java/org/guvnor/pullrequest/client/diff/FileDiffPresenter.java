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
import org.uberfire.commons.data.Pair;

import static org.uberfire.commons.validation.PortablePreconditions.*;

/**
 * This Presenter take the {@link PortableFileDiff}, analyze every line and create a line representation
 * for the view. This way it can show if it is an addition, removal, or normal line into the view. It also
 * removes dispensable information.
 */
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
        showLines( diff );
    }

    protected void showLines( final PortableFileDiff diff ) {
        final List<String> lines = diff.getLines();
        Pair<Integer, Integer> lineNumber = new Pair<>( diff.getStartA(), diff.getStartB() );
        for ( String line : lines ) {
            if ( isVisibleLine( line ) ) {
                final LineDiffPresenter presenter = lineDiffPresenters.get();
                presenter.setLine( line, lineNumber.getK1(), lineNumber.getK2() );
                lineNumber = incrementLineNumber( lineNumber, line );
                this.getView().addLine( presenter.getView() );
            }
        }
    }

    protected Pair<Integer, Integer> incrementLineNumber( Pair<Integer, Integer> lineNumber,
                                                          final String line ) {
        if ( line.startsWith( "+" ) ) {
            lineNumber = incrementB( lineNumber );
        } else if ( line.startsWith( "-" ) ) {
            lineNumber = incrementA( lineNumber );
        } else {
            lineNumber = incrementB( incrementA( lineNumber ) );
        }
        return lineNumber;
    }

    private Pair<Integer, Integer> incrementA( final Pair<Integer, Integer> lineNumber ) {
        return new Pair<Integer, Integer>( lineNumber.getK1() + 1, lineNumber.getK2() );
    }

    private Pair<Integer, Integer> incrementB( final Pair<Integer, Integer> lineNumber ) {
        return new Pair<Integer, Integer>( lineNumber.getK1(), lineNumber.getK2() + 1 );
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

    protected boolean isVisibleLine( final String line ) {
        return !line.trim().startsWith( "+++" ) &&
                !line.trim().startsWith( "---" ) &&
                !line.trim().startsWith( "index" ) &&
                !line.trim().matches( "@@.*@@" ) &&
                !line.trim().matches( "diff --git.*" );
    }

}
