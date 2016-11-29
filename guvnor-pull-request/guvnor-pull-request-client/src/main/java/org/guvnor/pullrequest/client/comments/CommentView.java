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

import java.util.Date;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import org.guvnor.pullrequest.client.resources.PullRequestResources;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class CommentView extends Composite implements CommentPresenter.View {

    @Inject
    @DataField("title")
    private Span title;

    @Inject
    @DataField("content")
    private Span content;

    @Inject
    @DataField("date")
    private Span date;

    @Inject
    @DataField("avatar")
    private Image avatar;
    private DateTimeFormat timeFormat;

    @PostConstruct
    public void initialize() {
        avatar.setResource( PullRequestResources.INSTANCE.images().emptyUser() );
        avatar.addStyleName( "avatar media-object" );
        avatar.setWidth( "70px" );
        avatar.setHeight( "70px" );
        timeFormat = DateTimeFormat.getFormat( "MM/dd/yyyy HH:mm:ss" );
    }

    @Override
    public void setAuthor( final String author ) {
        title.setText( author );
    }

    @Override
    public void setDate( final Date date ) {
        this.date.setText( timeFormat.format( date ) );
    }

    @Override
    public void setContent( final String content ) {
        this.content.setText( content );
    }

}
