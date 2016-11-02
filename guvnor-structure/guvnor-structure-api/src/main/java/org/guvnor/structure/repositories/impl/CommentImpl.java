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

package org.guvnor.structure.repositories.impl;

import java.util.Date;
import java.util.UUID;

import org.guvnor.structure.repositories.Comment;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Portable
public class CommentImpl implements Comment {

    private String id;
    private String author;
    private Date date;
    private String content;

    public CommentImpl() {
    }

    public CommentImpl( @MapsTo("id") String id,
                        @MapsTo("author") String author,
                        @MapsTo("date") Date date,
                        @MapsTo("content") String content ) {

        this.id = checkNotNull( "id", id );
        this.date = checkNotNull( "date", date );
        this.author = checkNotEmpty( "author", author );
        this.content = checkNotEmpty( "content", content );

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getContent() {
        return content;
    }
}
