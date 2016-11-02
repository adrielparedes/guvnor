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

package org.guvnor.structure.repositories;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Portable
public class PortableFileDiff {

    private List<String> lines;
    private String changeType;
    private String nameA;
    private String nameB;
    private int startA;
    private int endA;
    private int startB;
    private int endB;

    public PortableFileDiff() {

    }

    public PortableFileDiff( @MapsTo("nameA") final String nameA,
                             @MapsTo("nameB") final String nameB,
                             @MapsTo("startA") final int startA,
                             @MapsTo("endA") final int endA,
                             @MapsTo("startB") final int startB,
                             @MapsTo("endB") final int endB,
                             @MapsTo("changeType") final String changeType,
                             @MapsTo("lines") final List<String> lines ) {

        this.nameA = checkNotEmpty( "nameA", nameA );
        this.nameB = checkNotEmpty( "nameB", nameB );
        this.startA = startA;
        this.endA = endA;
        this.startB = startB;
        this.endB = endB;
        this.changeType = checkNotEmpty( "nameA", changeType );
        this.lines = checkNotNull( "lines", lines );

    }

    public List<String> getLines() {
        return lines;
    }

    public String getChangeType() {
        return changeType;
    }

    public String getNameA() {
        return nameA;
    }

    public String getNameB() {
        return nameB;
    }

    public int getStartA() {
        return startA;
    }

    public int getEndA() {
        return endA;
    }

    public int getStartB() {
        return startB;
    }

    public int getEndB() {
        return endB;
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode( startA );
        result = ~~result;
        result = 31 * result + ( Integer.hashCode( endA ) );
        result = ~~result;
        result = 31 * result + ( Integer.hashCode( startB ) );
        result = ~~result;
        result = 31 * result + ( Integer.hashCode( endB ) );
        result = ~~result;
        result = 31 * result + ( nameA.hashCode() );
        result = ~~result;
        result = 31 * result + ( nameB.hashCode() );
        result = ~~result;
        result = 31 * result + ( changeType.hashCode() );
        result = ~~result;
        result = 31 * result + ( lines.hashCode() );
        result = ~~result;
        return result;
    }

    @Override
    public boolean equals( final Object obj ) {
        if ( obj instanceof PortableFileDiff ) {
            PortableFileDiff external = (PortableFileDiff) obj;
            return this.startA == external.startA &&
                    this.endA == external.endA &&
                    this.startB == external.startB &&
                    this.endB == external.endB &&
                    this.changeType.equals( external.changeType ) &&
                    this.nameA.equals( external.nameA ) &&
                    this.nameB.equals( external.nameB ) &&
                    this.lines.equals( external.lines );

        } else {
            return super.equals( obj );
        }
    }

}
