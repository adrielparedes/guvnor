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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
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

    @EventHandler("comment-button")
    public void handleCommentClick( final ClickEvent click ) {
        String author = "adrielparedes";
        String content = this.content.getText();
        this.presenter.comment( author, content );
        this.content.setText( "" );
    }

}
