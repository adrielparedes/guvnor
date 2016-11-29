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

package org.guvnor.pullrequest.client.item;

import java.util.Calendar;
import java.util.Date;

import org.jboss.errai.common.client.logging.util.StringFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.PlaceManager;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PullRequestItemPresenterTest {

    @Mock
    private PullRequestItemPresenter.View view;

    @Mock
    private PlaceManager placeManager;

    private PullRequestItemPresenter presenter;

    @Before
    public void setUp() {
        presenter = new PullRequestItemPresenter( view, placeManager );
    }

    @Test
    public void testDaysAgoGenerationToday() {
        final long daysAgo = presenter.calculateDaysAgo( new Date() );
        assertEquals( 0, daysAgo );
    }

    @Test
    public void testDaysAgoGeneration4DaysAgo() {
        Calendar calendar = Calendar.getInstance();
        long ago = calendar.getTimeInMillis() - 345600000;
        calendar.setTimeInMillis( ago );
        final long daysAgo = presenter.calculateDaysAgo( calendar.getTime() );
        assertEquals( 4, daysAgo );
    }

    @Test
    public void testGeneratePath() {
        String path = presenter.generatePath( "a", "b" );
        assertEquals( "a/b", path );
    }

    @Test
    public void testFormatText() {
        final String formatted = StringFormat.format( "#%s %s %s", 500l, 500l, "kie" );
        assertEquals( "#500 500 kie", formatted );
    }
}