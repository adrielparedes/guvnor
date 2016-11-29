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

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Presenter of a single diff line. If the line starts with a + it creates a ADD Line View.
 * If it stars with - it reates a REMOVE Line View, otherwise it creates a NONE Line View.
 */
@Dependent
public class LineDiffPresenter {

    public interface View extends IsWidget {

        void setLine( LineType lineType,
                      String line,
                      long lineNumberA,
                      long lineNumberB );
    }

    private final View view;

    @Inject
    public LineDiffPresenter( View view ) {
        this.view = view;
    }

    public void setLine( String line,
                         long lineNumberA,
                         long lineNumberB ) {
        LineType type = LineType.NONE;
        if ( line.trim().startsWith( "+" ) ) {
            type = LineType.ADD;
        }
        if ( line.trim().startsWith( "-" ) ) {
            type = LineType.REMOVE;
        }
        this.view.setLine( type, line, lineNumberA, lineNumberB );
    }

    public View getView() {
        return this.view;
    }
}
