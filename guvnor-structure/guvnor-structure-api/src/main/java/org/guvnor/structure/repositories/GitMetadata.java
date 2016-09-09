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

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class GitMetadata {

    private String origin;
    private List<String> forks;
    private String name;
    private String owner;

    public GitMetadata() {
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOwner( final String owner ) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public List<String> getForks() {
        if ( this.forks == null ) {
            this.forks = new ArrayList<>();
        }
        return forks;
    }

    public void setForks( final List<String> forks ) {
        this.forks = forks;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin( final String origin ) {
        this.origin = origin;
    }

    public void addFork( final String name ) {
        this.getForks().add( name );
    }
}
