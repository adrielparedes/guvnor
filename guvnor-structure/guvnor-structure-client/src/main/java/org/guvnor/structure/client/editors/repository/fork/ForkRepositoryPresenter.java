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

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.structure.client.editors.repository.RepositoryPreferences;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryAlreadyExistsException;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import static org.guvnor.structure.security.RepositoryFeatures.*;

@Dependent
public class ForkRepositoryPresenter implements ForkRepositoryView.Presenter {

    private RepositoryPreferences repositoryPreferences;

    private ForkRepositoryView view;

    private Caller<RepositoryService> repositoryService;

    private Caller<OrganizationalUnitService> organizationalUnitService;

    private PlaceManager placeManager;

    private AuthorizationManager authorizationManager;

    private Map<String, OrganizationalUnit> availableOrganizationalUnits = new HashMap<String, OrganizationalUnit>();

    private SessionInfo sessionInfo;
    private boolean assetsManagementIsGranted = false;

    @Inject
    public ForkRepositoryPresenter( final RepositoryPreferences repositoryPreferences,
                                    final ForkRepositoryView view,
                                    final Caller<RepositoryService> repositoryService,
                                    final Caller<OrganizationalUnitService> organizationalUnitService,
                                    final PlaceManager placeManager,
                                    final AuthorizationManager authorizationManager,
                                    final SessionInfo sessionInfo ) {
        this.repositoryPreferences = repositoryPreferences;
        this.view = view;
        this.repositoryService = repositoryService;
        this.organizationalUnitService = organizationalUnitService;
        this.placeManager = placeManager;
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
    }

    @PostConstruct
    public void init() {
        view.init( this,
                   isOuMandatory() );
        setAssetsManagementGrant();
    }

//    @AfterInitialization
//    public void load() {
//        populateOrganizationalUnits();
//    }

    @Override
    public void handleCancelClick() {
        view.hide();
    }

    @Override
    public void handleForkClick() {
        repositoryService.call( getNormalizeRepositoryNameCallback() )
                .normalizeRepositoryName( view.getSelectedRepository() );
    }

    private RemoteCallback<String> getNormalizeRepositoryNameCallback() {
        return new RemoteCallback<String>() {
            @Override
            public void callback( final String normalizedName ) {
                if ( !view.getSelectedRepository().equals( normalizedName ) ) {
                    if ( !view.showAgreeNormalizeNameWindow( normalizedName ) ) {
                        return;
                    }
                    view.setSelectedRepository( normalizedName );
                }

                lockScreen();

                final String scheme = "git";
                final String alias = view.getSelectedRepository();

                final OrganizationalUnit targetOrganizationalUnit = organizationalUnitService.call().getOrganizationalUnit( view.getTargetOrganizationalUnitSelected() );

                repositoryService.call( getCreateRepositoryCallback(),
                                        getErrorCallback() ).createRepository( targetOrganizationalUnit,
                                                                               scheme,
                                                                               alias,
                                                                               getRepositoryConfiguration() );

            }
        };
    }

    private RepositoryEnvironmentConfigurations getRepositoryConfiguration() {
        final RepositoryEnvironmentConfigurations configuration = new RepositoryEnvironmentConfigurations();
        configuration.setOrigin( view.getSourceSelectedOrganizationalUnit() + "/" + view.getSelectedRepository() );
        configuration.setManaged( view.isManagedRepository() );
        return configuration;
    }

    private RemoteCallback<Repository> getCreateRepositoryCallback() {
        return new RemoteCallback<Repository>() {
            @Override
            public void callback( final Repository o ) {
                view.alertRepositoryForked();
                unlockScreen();
                view.hide();
                placeManager.goTo( new DefaultPlaceRequest( "RepositoryEditor" ).addParameter( "alias",
                                                                                               o.getAlias() ) );
            }
        };
    }

    private ErrorCallback<Message> getErrorCallback() {
        return new ErrorCallback<Message>() {
            @Override
            public boolean error( final Message message,
                                  final Throwable throwable ) {
                try {
                    throw throwable;
                } catch ( RepositoryAlreadyExistsException ex ) {
                    view.errorRepositoryAlreadyExist();
                } catch ( Throwable ex ) {
                    view.errorForkRepositoryFail( ex );
                }
                unlockScreen();
                return true;
            }
        };
    }

    public void showForm() {
        view.reset();
        view.show();
    }

    private void lockScreen() {
        view.showBusyPopupMessage();
        view.setPopupCloseVisible( false );
        view.setForkEnabled( false );
        view.setCancelEnabled( false );
        view.setOrganizationalUnitEnabled( false );
    }

    private void unlockScreen() {
        view.closeBusyPopup();
        view.setPopupCloseVisible( true );
        view.setForkEnabled( true );
        view.setCancelEnabled( true );
        view.setOrganizationalUnitEnabled( true );
    }

    private boolean isOuMandatory() {
        return repositoryPreferences == null || repositoryPreferences.isOUMandatory();
    }

    private void setAssetsManagementGrant() {
        assetsManagementIsGranted = authorizationManager.authorize( CONFIGURE_REPOSITORY, sessionInfo.getIdentity() );
        view.enableManagedRepoCreation( assetsManagementIsGranted );
    }
}