/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.teiid;

import static org.jboss.dmr.client.ModelDescriptionConstants.INCLUDE_RUNTIME;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.OUTCOME;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_RESOURCE_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;
import static org.jboss.dmr.client.ModelDescriptionConstants.SUCCESS;
import static org.jboss.dmr.client.ModelDescriptionConstants.WRITE_ATTRIBUTE_OPERATION;

import java.util.Map;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.teiid.model.SubsystemConfiguration;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.spi.RequiredResources;
import org.jboss.as.console.spi.SearchIndex;
import org.jboss.as.console.spi.SubsystemExtension;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;


public class SubsystemPresenter extends
        Presenter<SubsystemPresenter.MyView, SubsystemPresenter.MyProxy>
        implements Persistable<SubsystemConfiguration> {
    
    private DispatchAsync dispatcher;
    private RevealStrategy revealStrategy;
    private BeanMetaData beanMetadata;
    private EntityAdapter<SubsystemConfiguration> entityAdapter;
    
    @ProxyCodeSplit
    @NameToken("teiid")
    @SubsystemExtension(name="Query Engine", group = "Teiid", key="teiid")
    @RequiredResources(resources = {"{selected.profile}/subsystem=teiid"})
    @SearchIndex(keywords = {"teiid", "vdb"})  
    public interface MyProxy extends Proxy<SubsystemPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(SubsystemPresenter presenter);
        void setBean(SubsystemConfiguration bean);
    }

    @Inject
    public SubsystemPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher, RevealStrategy revealStrategy,
            ApplicationMetaData metaData) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;

        this.beanMetadata = metaData.getBeanMetaData(SubsystemConfiguration.class);
        this.entityAdapter = new EntityAdapter<>(SubsystemConfiguration.class, metaData);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }
    
    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }
    
    @Override
    protected void onReset() {
        super.onReset();
        loadModel();
    }
    
    private void loadModel() {
        ModelNode operation = beanMetadata.getAddress().asResource(Baseadress.get());
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(INCLUDE_RUNTIME).set(true);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();
                SubsystemConfiguration bean = entityAdapter.fromDMR(response.get(RESULT));
                getView().setBean(bean);
            }
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to retrieve configuration for Teiid subsystem", caught.getMessage());
            }             
        });        
    }

    @Override
    public void save(SubsystemConfiguration t, Map<String, Object> changeset) {
        ModelNode address = beanMetadata.getAddress().asResource(Baseadress.get());
        address.get(OP).set(WRITE_ATTRIBUTE_OPERATION);

        ModelNode operation = this.entityAdapter.fromChangeset(changeset, address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                boolean success = response.get(OUTCOME).asString().equals(SUCCESS);

                if (success) {
                    Console.info(Console.MESSAGES.saved("Teiid configuration modified"));
                } else {
                    Console.error(Console.MESSAGES.saveFailed("Teiid configuration modification failed"),
                            response.getFailureDescription());
                }
                loadModel();
            }
            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadModel();
            }            
        }); 
    }

    @Override
    public void delete(SubsystemConfiguration t) {
        // no-op
    }

    @Override
    public void create(SubsystemConfiguration t) {
        //no-op
    }
}
