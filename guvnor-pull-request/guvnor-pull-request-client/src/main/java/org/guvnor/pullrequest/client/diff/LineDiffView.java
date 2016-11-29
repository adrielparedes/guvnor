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

import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

/**
 * Represents a single diff line and its background is green if ADD, red if REMOVE, and transparent if NONE.
 */
@Dependent
@Templated
public class LineDiffView extends Composite implements LineDiffPresenter.View {

    @Inject
    @DataField("line-number-left")
    private Span lineNumberLeft;

    @Inject
    @DataField("line-number-right")
    private Span lineNumberRight;

    @Inject
    @DataField("line")
    private Span line;

    @Override
    public void setLine( final LineType lineType,
                         final String text,
                         final long lineNumberA,
                         final long lineNumberB ) {

        if ( LineType.ADD.equals( lineType ) ) {
            line.addStyleName( "add" );
            lineNumberRight.setText( String.valueOf( lineNumberB ) );
            lineNumberLeft.setText( "-" );
        }

        if ( LineType.REMOVE.equals( lineType ) ) {
            line.addStyleName( "remove" );
            lineNumberLeft.setText( String.valueOf( lineNumberA ) );
            lineNumberRight.setText( "-" );
        }

        if ( LineType.NONE.equals( lineType ) ) {
            lineNumberLeft.setText( String.valueOf( lineNumberA ) );
            lineNumberRight.setText( String.valueOf( lineNumberB ) );
        }

        line.setText( text );
    }

}
