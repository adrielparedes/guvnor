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

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.client.resources.i18n.CommonConstants;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.errors.ErrorPopup;
import org.uberfire.ext.widgets.core.client.resources.i18n.CoreConstants;

@Dependent
public class ForkRepositoryViewImpl extends BaseModal implements ForkRepositoryView,
                                                                 HasCloseHandlers<ForkRepositoryViewImpl> {

    interface ForkRepositoryFormBinder
            extends
            UiBinder<Widget, ForkRepositoryViewImpl> {

    }

    private Presenter presenter;

    private static ForkRepositoryFormBinder uiBinder = GWT.create( ForkRepositoryFormBinder.class );

    @UiField
    Button fork;

    @UiField
    Button cancel;

    @UiField
    FormGroup sourceOrganizationalUnitGroup;

    @UiField
    TextBox sourceOrganizationalUnitTextBox;

    @UiField
    FormGroup targetOrganizationalUnitGroup;

    @UiField
    TextBox targetOrganizationalUnitTextBox;

    @UiField
    FormGroup repositoryGroup;

    @UiField
    TextBox repositoryTextBox;

    @UiField
    FormLabel sourceOrganizationalUnitLabel;

    @UiField
    FormLabel repositoryLabel;

    @UiField
    FormLabel targetOrganizationalUnitLabel;

    @UiField
    BaseModal popup;

    @UiField
    FormGroup managedReposiotryGroup;

    @UiField
    CheckBox managedRepository;

    @UiHandler("fork")
    public void onForkClick( final ClickEvent e ) {
        presenter.handleForkClick();
    }

    @UiHandler("cancel")
    public void onCancelClick( final ClickEvent e ) {
        presenter.handleCancelClick();
    }

    @Override
    public String getSelectedRepository() {
        return this.repositoryTextBox.getValue();
    }

    @Override
    public void setSelectedRepository( final String repository ) {

    }

    @Override
    public void init( final Presenter presenter,
                      final boolean isOuMandatory ) {
        this.presenter = presenter;
        setBody( uiBinder.createAndBindUi( this ) );

        setTitle( CoreConstants.INSTANCE.ForkRepository() );

        sourceOrganizationalUnitTextBox.addValueChangeHandler( e -> {
            sourceOrganizationalUnitGroup.setValidationState( ValidationState.NONE );
        } );

        repositoryTextBox.addValueChangeHandler( e -> {
            repositoryGroup.setValidationState( ValidationState.NONE );
        } );

        targetOrganizationalUnitTextBox.addValueChangeHandler( e -> {
            targetOrganizationalUnitGroup.setValidationState( ValidationState.NONE );
        } );

    }

    @Override
    public String getSourceSelectedOrganizationalUnit() {
        return sourceOrganizationalUnitTextBox.getValue();
    }

    @Override
    public void setSourceOrganizationalUnitGroupType( final ValidationState state ) {
        sourceOrganizationalUnitGroup.setValidationState( state );
    }

    @Override
    public void setTargetOrganizationalUnitGroupType( final ValidationState state ) {
        targetOrganizationalUnitGroup.setValidationState( state );
    }

    @Override
    public void setOrganizationalUnitEnabled( final boolean enabled ) {
        sourceOrganizationalUnitTextBox.setEnabled( enabled );
//        sourceOrganizationalUnitTextBox.refresh();
    }

    @Override
    public void setForkEnabled( final boolean enabled ) {
        fork.setEnabled( enabled );
    }

    @Override
    public void setCancelEnabled( final boolean enabled ) {
        cancel.setEnabled( enabled );
    }

    @Override
    public void setPopupCloseVisible( final boolean closeVisible ) {
        setClosable( closeVisible );
    }

    @Override
    public void showBusyPopupMessage() {
        BusyPopup.showMessage( CoreConstants.INSTANCE.Cloning() );
    }

    @Override
    public void closeBusyPopup() {
        BusyPopup.close();
    }

    @Override
    public boolean showAgreeNormalizeNameWindow( final String normalizedName ) {
        return Window.confirm( CoreConstants.INSTANCE.RepositoryNameInvalid() + " \"" + normalizedName + "\". " + CoreConstants.INSTANCE.DoYouAgree() );
    }

    @Override
    public void alertRepositoryForked() {
        Window.alert( CoreConstants.INSTANCE.RepoCloneSuccess() + "\n\n" + CommonConstants.INSTANCE.IndexClonedRepositoryWarning() );
    }

    @Override
    public void errorRepositoryAlreadyExist() {
        ErrorPopup.showMessage( CoreConstants.INSTANCE.RepoAlreadyExists() );
    }

    @Override
    public void errorForkRepositoryFail( final Throwable cause ) {
        ErrorPopup.showMessage( CommonConstants.INSTANCE.RepoCloneFail() + " \n" + cause.getMessage() );
    }

    @Override
    public void errorLoadOrganizationalUnitsFail( final Throwable cause ) {
        ErrorPopup.showMessage( CoreConstants.INSTANCE.CantLoadOrganizationalUnits() + " \n" + cause.getMessage() );
    }

    @Override
    public HandlerRegistration addCloseHandler( final CloseHandler<ForkRepositoryViewImpl> handler ) {
        return addHandler( handler,
                           CloseEvent.getType() );
    }

    @Override
    public void reset() {
        sourceOrganizationalUnitTextBox.setValue( "" );
        sourceOrganizationalUnitGroup.setValidationState( ValidationState.NONE );

        targetOrganizationalUnitTextBox.setValue( "" );
        targetOrganizationalUnitGroup.setValidationState( ValidationState.NONE );

        managedRepository.setValue( Boolean.FALSE );
    }

    @Override
    public boolean isManagedRepository() {
        return managedRepository.getValue();
    }

    @Override
    public void enableManagedRepoCreation( boolean enabled ) {
        managedReposiotryGroup.setVisible( enabled );
    }

    @Override
    public void show() {
        popup.show();
    }

    @Override
    public String getTargetOrganizationalUnitSelected() {
        return this.targetOrganizationalUnitTextBox.getText();
    }

    @Override
    public void hide() {
        popup.hide();
        CloseEvent.fire( this, this );
    }
}
