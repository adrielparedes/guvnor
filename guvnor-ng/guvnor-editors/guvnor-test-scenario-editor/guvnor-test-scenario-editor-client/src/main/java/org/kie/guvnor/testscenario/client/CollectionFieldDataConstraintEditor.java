/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.testscenario.client;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.CollectionFieldData;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.Fact;
import org.drools.ide.common.client.modeldriven.testing.Scenario;
import org.kie.guvnor.datamodel.oracle.DataModelOracle;
import org.kie.guvnor.testscenario.model.CollectionFieldData;
import org.kie.guvnor.testscenario.model.ExecutionTrace;
import org.kie.guvnor.testscenario.model.Fact;
import org.kie.guvnor.testscenario.model.Scenario;
import org.uberfire.client.common.DirtyableComposite;

/**
 * Constraint editor for the FieldData in the Given Section
 */
public class CollectionFieldDataConstraintEditor
        extends DirtyableComposite
        implements
        ScenarioParentWidget {

    private CollectionFieldData field;
    private final Panel panel = new SimplePanel();
    private final FieldConstraintHelper helper;

    public CollectionFieldDataConstraintEditor(String factType,
                                               CollectionFieldData field,
                                               Fact givenFact,
                                               DataModelOracle dmo,
                                               Scenario scenario,
                                               ExecutionTrace executionTrace) {
        this.field = field;
        this.helper = new FieldConstraintHelper(scenario,
                executionTrace,
                dmo,
                factType,
                field,
                givenFact);
        renderEditor();
        initWidget(panel);
    }

    @Override
    public void renderEditor() {
        panel.clear();

        panel.add(new ListEditor(field, helper, this));
    }

}
