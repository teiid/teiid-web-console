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

import static org.jboss.dmr.client.ModelDescriptionConstants.ADD;
import static org.jboss.dmr.client.ModelDescriptionConstants.ADDRESS;
import static org.jboss.dmr.client.ModelDescriptionConstants.CHILD_TYPE;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.OUTCOME;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_CHILDREN_RESOURCES_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.RECURSIVE;
import static org.jboss.dmr.client.ModelDescriptionConstants.REMOVE;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;
import static org.jboss.dmr.client.ModelDescriptionConstants.SUCCESS;
import static org.jboss.dmr.client.ModelDescriptionConstants.WRITE_ATTRIBUTE_OPERATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.teiid.TranslatorPresenter.MyProxy;
import org.jboss.as.console.client.teiid.TranslatorPresenter.MyView;
import org.jboss.as.console.client.teiid.model.DataModelFactory;
import org.jboss.as.console.client.teiid.model.Translator;
import org.jboss.as.console.client.teiid.model.Transport;
import org.jboss.as.console.client.teiid.model.Transport;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.spi.SearchIndex;
import org.jboss.as.console.spi.SubsystemExtension;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelNodeUtil;
import org.jboss.dmr.client.Property;
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
import com.gwtplatform.mvp.shared.proxy.PlaceRequest;

public class TransportPresenter extends
        Presenter<TransportPresenter.MyView, TransportPresenter.MyProxy>
        implements Persistable<Transport> {
    
    private DispatchAsync dispatcher;
    private RevealStrategy revealStrategy;
    private BeanMetaData metadata;
    private DataModelFactory factory;
    private EntityAdapter<Transport> adapter;
    private String selected;
    private DefaultWindow window;

    @ProxyCodeSplit
    @NameToken("teiid-transports")
    @SubsystemExtension(name="Transports", group = "Teiid", key="teiid")
    @SearchIndex(keywords = {"teiid", "transport"})
    public interface MyProxy extends Proxy<TransportPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(TransportPresenter presenter);
        void setTransports(List<Transport> transports);        
    }

    @Inject
    public TransportPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            RevealStrategy revealStrategy, DispatchAsync dispatcher,
            DataModelFactory factory, ApplicationMetaData metadata) {
        super(eventBus, view, proxy);
        
        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.factory = factory;

        this.metadata = metadata.getBeanMetaData(Transport.class);
        this.adapter = new EntityAdapter<>(Transport.class, metadata);
    }

    @Override
    public void prepareFromRequest(PlaceRequest request) {
        this.selected = request.getParameter("name", null);
    }
    
    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }
    
    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }   
    
    @Override
    protected void onReset() {
        super.onReset();
        loadModel();
    }
    
    private void loadModel() {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "teiid");
        operation.get(CHILD_TYPE).set("transport");
        operation.get(RECURSIVE).set(true);        
        
        this.dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();
                
                List<Property> children = response.get(RESULT).asPropertyList();
                List<Transport> transports = new ArrayList<Transport>(children.size());

                for (Property child : children) {
                    ModelNode model = child.getValue();

                    Transport transport = adapter.fromDMR(model);
                    transport.setName(child.getName());
                    transports.add(transport);
                }                
                getView().setTransports(transports);
            }
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to retrieve configuration for Teiid transports", caught.getMessage());
            }             
        });        
    }
    
    @Override
    public void save(final Transport transport, Map<String, Object> changeset) {
        
        AddressBinding address = this.metadata.getAddress();
        ModelNode addressModel = address.asResource(Baseadress.get(), transport.getName());
        addressModel.get(OP).set(WRITE_ATTRIBUTE_OPERATION);

        ModelNode operation = this.adapter.fromChangeset(changeset, addressModel);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                boolean success = response.get(OUTCOME).asString().equals(SUCCESS);

                if (success) {
                    Console.info(Console.MESSAGES.saved("Teiid Transport " + transport.getName()));
                } else {
                    Console.error(Console.MESSAGES.saveFailed("Teiid Transport " + transport.getName()),
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
    public void delete(final Transport transport) {
        AddressBinding address = this.metadata.getAddress();
        ModelNode operation = address.asResource(Baseadress.get(), transport.getName());
        operation.get(OP).set(REMOVE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();
                if (ModelNodeUtil.indicatesSuccess(result)) {
                    Console.info(Console.MESSAGES.deleted("Teiid Transport " + transport.getName()));
                } else {
                    Console.error(Console.MESSAGES.deletionFailed("Teiid Transport " + transport.getName()),
                            result.toString());
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
    
//    public void launchTranslatorWizard() {
//        this.window = new DefaultWindow(Console.MESSAGES.createTitle("Transport"));
//        this.window.setWidth(480);
//        this.window.setHeight(360);
//
//        this.window.trapWidget(new TranslatorWizard(this, factory.getTransportModel().as()).asWidget());
//
//        this.window.setGlassEnabled(true);
//        this.window.center();
//    }

    public void closeDialoge() {
        window.hide();
    }    
    
    @Override
    public void create(final Transport transport) {
        closeDialoge();
        
        ModelNode addressModel = this.metadata.getAddress()
                .asResource(Baseadress.get(), transport.getName());

        ModelNode operation = this.adapter.fromEntity(transport);
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).set(addressModel.get(ADDRESS).asObject());

        operation.remove("name"); // work around

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();
                if (ModelNodeUtil.indicatesSuccess(result)) {
                    Console.info(Console.MESSAGES.added("Teiid Transport " + transport.getName()));
                } else {
                    Console.error(Console.MESSAGES.addingFailed("Teiid Transport " + transport.getName()), 
                            result.toString());
                }
                loadModel();
            }
            @Override
            public void onFailure(Throwable caught) {
                Console.error(Console.MESSAGES.addingFailed("Teiid Transport "+ transport.getName()), 
                        caught.getMessage());
                loadModel();
            }            
        });
    }      
}
