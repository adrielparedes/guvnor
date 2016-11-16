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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.LinkedGroup;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class FileDiffView extends Composite implements FileDiffPresenter.View {

    @Inject
    @DataField("lines")
    private LinkedGroup lines;

    @Inject
    @DataField("local")
    private Anchor local;

    @Inject
    @DataField("remote")
    private Anchor remote;
    private FileDiffPresenter presenter;

    @Override
    public void initialize( FileDiffPresenter presenter ) {

        this.presenter = presenter;
        this.lines.clear();
        this.remote.setText( "src/main/local.java" );
        this.local.setText( "src/main/remote.java" );
    }

    @Override
    public void addLine( final LineDiffPresenter.View view ) {
        this.lines.add( view );
    }

    @EventHandler("local")
    public void handleLocalLinkClick( ClickEvent event ) {
        this.presenter.openFile( "master", "repo/readme.md" );
    }

    @EventHandler("remote")
    public void handleRemoteLinkClick( ClickEvent event ) {
        this.presenter.openFile( "master", "repo/readme.md" );
    }
}
