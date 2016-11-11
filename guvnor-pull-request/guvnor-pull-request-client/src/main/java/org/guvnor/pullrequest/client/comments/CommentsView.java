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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import org.guvnor.pullrequest.client.resources.PullRequestResources;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.LinkedGroup;
import org.gwtbootstrap3.client.ui.TextArea;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 * Represents the list of comments for a Pull Request.
 */
@Dependent
@Templated
public class CommentsView extends Composite implements CommentsPresenter.View {

    private CommentsPresenter presenter;

    @Inject
    @DataField("comments")
    private LinkedGroup comments;

    @Inject
    @DataField("content")
    private TextArea content;

    @Inject
    @DataField("comment-button")
    private Button commentButton;

    @Inject
    @DataField("merge-button")
    private Button mergeButton;

    @Inject
    @DataField("close-button")
    private Button closeButton;

    @Inject
    @DataField("avatar")
    private Image avatar;

    @PostConstruct
    public void initialize() {
        avatar.setResource( PullRequestResources.INSTANCE.images().emptyUser() );
        avatar.addStyleName( "avatar media-object" );
        avatar.setWidth( "70px" );
        avatar.setHeight( "70px" );
    }

    @Override
    public void init( CommentsPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        this.comments.clear();
    }

    @Override
    public void addComment( final CommentPresenter.View view ) {
        this.comments.add( view );
    }

    @Override
    public void enableMergeButton() {
        this.mergeButton.setEnabled( true );
    }

    @Override
    public void disableMergeButton() {
        this.mergeButton.setEnabled( false );
    }

    @Override
    public void enableCloseButton() {
        this.closeButton.setEnabled( true );
    }

    @Override
    public void disableCloseButton() {
        this.closeButton.setEnabled( false );
    }

    @EventHandler("comment-button")
    public void handleCommentClick( final ClickEvent click ) {
        String author = "adrielparedes";
        String content = this.content.getText();
        this.presenter.comment( author, content );
        this.content.setText( "" );
    }

    @EventHandler("merge-button")
    public void handleMergeClick( final ClickEvent click ) {
        String author = "adrielparedes";
        this.presenter.merge( author );
    }

    @EventHandler("close-button")
    public void handleCloseClick( final ClickEvent click ) {
        String author = "adrielparedes";
        this.presenter.close( author );
    }

}
