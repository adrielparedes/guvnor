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
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.structure.repositories.Comment;

@Dependent
public class CommentPresenter {

    public interface View extends IsWidget {

        void setAuthor( String author );

        void setDate( String date );

        void setContent( String content );
    }

    private View view;

    @Inject
    public CommentPresenter( final CommentPresenter.View view ) {
        this.view = view;
    }

    public void initialize( final Comment comment ) {

        view.setAuthor( comment.getAuthor() );
        view.setDate( getDate( comment ) );
        view.setContent( comment.getContent() );
    }

    private String getDate( final Comment comment ) {
        final DateTimeFormat format = DateTimeFormat.getFormat( "MM/dd/yyyy HH:mm:ss" );
        final Date date = comment.getDate();
        return format.format( date );

    }

    public View getView() {
        return view;
    }
}