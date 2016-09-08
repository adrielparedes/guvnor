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

package org.guvnor.structure.client.editors.repository.fork;

import org.gwtbootstrap3.client.ui.constants.ValidationState;

public interface ForkRepositoryView {

    String getSelectedRepository();

    void setSelectedRepository( String repository );

    interface Presenter {

        void handleCancelClick();

        void handleForkClick();

    }
    void init( Presenter presenter,
               boolean isOuMandatory );

    void hide();

    void show();

    String getTargetOrganizationalUnitSelected();

    String getSourceSelectedOrganizationalUnit();

    void setSourceOrganizationalUnitGroupType( ValidationState state );

    void setTargetOrganizationalUnitGroupType( ValidationState state );

    void setOrganizationalUnitEnabled( boolean enabled );

    void setForkEnabled( boolean enabled );

    void setCancelEnabled( boolean enabled );

    void setPopupCloseVisible( boolean closeVisible );

    void showBusyPopupMessage();

    void closeBusyPopup();

    boolean showAgreeNormalizeNameWindow( String normalizedName );

    void alertRepositoryForked();

    void errorRepositoryAlreadyExist();

    void errorForkRepositoryFail( Throwable cause );

    void errorLoadOrganizationalUnitsFail( Throwable cause );

    void reset();

    boolean isManagedRepository();

    void enableManagedRepoCreation( boolean assetsManagementIsGranted );

}
