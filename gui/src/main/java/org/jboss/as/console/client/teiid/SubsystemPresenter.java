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
import static org.jboss.dmr.client.ModelDescriptionConstants.INCLUDE_RUNTIME;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.OUTCOME;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_CHILDREN_RESOURCES_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_RESOURCE_OPERATION;
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
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.teiid.model.Authentication;
import org.jboss.as.console.client.teiid.model.SubsystemConfiguration;
import org.jboss.as.console.client.teiid.model.TeiidLogger;
import org.jboss.as.console.client.teiid.model.Translator;
import org.jboss.as.console.client.teiid.model.Transport;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.spi.RequiredResources;
import org.jboss.as.console.spi.SearchIndex;
import org.jboss.as.console.spi.SubsystemExtension;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.ModelNodeUtil;
import org.jboss.dmr.client.Property;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;

import com.allen_sauer.gwt.log.client.GWTLogger;
import com.google.gwt.core.client.GWT;
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
    private EntityAdapter<SubsystemConfiguration> configurationEntityAdapter;
    private EntityAdapter<TeiidLogger> loggerAdaptor;
    private EntityAdapter<Transport> transportEntityAdapter;
    private EntityAdapter<Translator> translatorAdapter;
    private EntityAdapter<Authentication> authenticationAdapter;
    private ApplicationMetaData metadata;
    
    @ProxyCodeSplit
    @NameToken("teiid")
    @SubsystemExtension(name="Teiid", group = "Teiid", key="teiid")
    @RequiredResources(resources = {"{selected.profile}/subsystem=teiid"})
    @SearchIndex(keywords = {"teiid", "vdb"})  
    public interface MyProxy extends Proxy<SubsystemPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(SubsystemPresenter presenter);
        void setConfigurationBean(SubsystemConfiguration bean);
        
        // for logging
        void addLogger(String context);
        void deleteLogger(String context);
        void loggingStatus(String context, TeiidLogger logger);
        void logHandlerAdded(String handlerName, boolean added);
        void logHandlerRemoved(String handlerName, boolean removed);
        void loggerAdded(String context, boolean added);
        void loggerRemoved(String context, boolean removed);
        void setLogHandlerStatus(String context, String name, boolean dbAppender, boolean exists);     
        
        //transport
        void setTransports(List<Transport> transports);
        void setTranslators(List<Translator> translators);
        
        void setAuthentication(Authentication authentication);
    }

    @Inject
    public SubsystemPresenter(EventBus eventBus, MyView view, MyProxy proxy,
            DispatchAsync dispatcher, RevealStrategy revealStrategy,
            ApplicationMetaData metadata) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;

        this.configurationEntityAdapter = new EntityAdapter<>(SubsystemConfiguration.class, metadata);
        this.loggerAdaptor = new EntityAdapter<TeiidLogger>(TeiidLogger.class, metadata);
        this.transportEntityAdapter = new EntityAdapter<Transport>(Transport.class, metadata);
        this.translatorAdapter = new EntityAdapter<>(Translator.class, metadata);
        this.authenticationAdapter = new EntityAdapter<>(Authentication.class,
				metadata);
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
        loadConfigurationModel();
        loadLoggingConfiguration();
        loadTransports();
        loadTranslators();
        loadAuthenticationl();
    }
    
    public void loadLoggingConfiguration() {
        getLogHandlerStatus(AuditEditor.CTX_AUDITLOGGING);
        getLogHandlerStatus(AuditEditor.CTX_COMMANDLOGGING);
        getLogHandlerStatus(AuditEditor.CTX_TRACELOGGING);
    }    
    
    private void loadConfigurationModel() {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "teiid");
        operation.get(INCLUDE_RUNTIME).set(true);
        
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();
                SubsystemConfiguration bean = configurationEntityAdapter.fromDMR(response.get(RESULT));
                getView().setConfigurationBean(bean);
            }
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to retrieve configuration for Teiid subsystem", caught.getMessage());
            }             
        });
    }

    @Override
    public void save(SubsystemConfiguration t, Map<String, Object> changeset) {
        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "teiid");
        address.get(OP).set(WRITE_ATTRIBUTE_OPERATION);

        ModelNode operation = this.configurationEntityAdapter.fromChangeset(changeset, address);

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
                loadConfigurationModel();
            }
            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadConfigurationModel();
            }            
        }); 
    }
    
    public void getLogHandlerStatus(final String context) {
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "logging");
        address.add("logger", context);
        ModelNode operation = new ModelNode();
        operation.get(OP).set("read-resource");
        operation.get(ADDRESS).set(address);
        
        
        
        this.dispatcher.execute(new DMRAction(operation),new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if (isSucess(response)) {
                    if (response.get(RESULT).isDefined()) {
                        TeiidLogger logger = loggerAdaptor.fromDMR(response.get(RESULT));
                        getView().loggingStatus(context, logger); 
                    }
                }
                else {
                    getView().loggingStatus(context, null);
                }
            }
            @Override
            public void onFailure(Throwable caught) {
                GWT.log("on failure log");
                getView().loggingStatus(context, null);
            }
        });
    }
    
    public void checkLogHandlerStatus(final String context, final String name, final boolean dbAppender) {
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "logging");
        if (dbAppender) {
            address.add("async-handler", name);
        }
        else {
            address.add("periodic-rotating-file-handler", name);
        }
        ModelNode operation = new ModelNode();
        operation.get(OP).set("read-resource");
        operation.get(ADDRESS).set(address);
        
        this.dispatcher.execute(new DMRAction(operation),new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if (isSucess(response)) {
                    getView().setLogHandlerStatus(context, name, dbAppender, true);
                }
            }
            @Override
            public void onFailure(Throwable caught) {
                getView().setLogHandlerStatus(context, name, dbAppender, false);
            }
        });
    }   
    
    public void addOrRemoveLogger(final String context, final boolean addOperation) {
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "logging");
        address.add("logger", context);
        ModelNode operation = new ModelNode();
        operation.get(OP).set("read-resource");
        operation.get(ADDRESS).set(address);
        
        this.dispatcher.execute(new DMRAction(operation),new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if (isSucess(response)) {
                    if (response.get(RESULT).isDefined()) {
                        if (!addOperation) {
                            getView().deleteLogger(context);    
                        }
                    }
                }
                else {
                    if (addOperation) {
                        getView().addLogger(context);    
                    }
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                if (addOperation) {
                    getView().addLogger(context);    
                }                
            }
        });
    }   
    
    public void addFileHandler(final String name, String fileName) {
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "logging");
        address.add("periodic-rotating-file-handler", name);
        ModelNode operation = new ModelNode();
        operation.get(OP).set("add");
        operation.get(ADDRESS).set(address);
        operation.get("append").set(new ModelNode().set("true"));
        operation.get("autoflush").set(new ModelNode().set("true"));
        operation.get("level").set(new ModelNode().set("DEBUG"));
        operation.get("suffix").set(new ModelNode().set(".yyyy-MM-dd"));
        operation.get("formatter").set(new ModelNode().set("%d{HH:mm:ss,SSS} %-5p [%c] (%t) %X{teiid-session} %s%E%n"));
        
        ModelNode file = new ModelNode();
        file.get("path").set(new ModelNode().set(fileName));
        file.get("relative-to").set(new ModelNode().set("jboss.server.log.dir"));
        operation.get("file").set(file);
        
        this.dispatcher.execute(new DMRAction(operation),new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                getView().logHandlerAdded(name, isSucess(response));
            }  
            
            @Override
            public void onFailure(Throwable caught) {
                getView().logHandlerAdded(name, false);
            }
        });
    }
    
    private boolean isSucess(ModelNode response) {
        return response.hasDefined("outcome") && response.get("outcome").asString().equals("success");
    }
    
    
    public void removeFileHandler(final String name) {
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "logging");
        address.add("periodic-rotating-file-handler", name);
        ModelNode operation = new ModelNode();
        operation.get(OP).set("remove");
        operation.get(ADDRESS).set(address);
                
        this.dispatcher.execute(new DMRAction(operation),new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                getView().logHandlerRemoved(name, isSucess(response));
            }   
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to remove log handler "+name, caught.getMessage());
            } 
        });     
    }

    public void addLogger(final String context, String level, String handler) {
        //subsystem=logging/logger=com.your.package.name1:add(level=DEBUG, handlers=[foo])
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "logging");
        address.add("logger", context);
        ModelNode operation = new ModelNode();
        operation.get(OP).set("add");
        operation.get(ADDRESS).set(address);
        operation.get("level").set(new ModelNode().set(level));
        if (handler != null) {
            operation.get("handlers").add(new ModelNode().set(handler));
        }
        
        this.dispatcher.execute(new DMRAction(operation),new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                getView().loggerAdded(context, isSucess(response));
            }   
            
            @Override
            public void onFailure(Throwable caught) {
                getView().logHandlerAdded(context, false);
            }
            
        });               
    }
    
    public void removeLogger(final String context) {
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "logging");
        address.add("logger", context);
        ModelNode operation = new ModelNode();
        operation.get(OP).set("remove");
        operation.get(ADDRESS).set(address);
        this.dispatcher.execute(new DMRAction(operation),new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                getView().loggerRemoved(context, isSucess(response));
            }
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to remove logger "+context, caught.getMessage());
            } 
        });               
    }    
    
    private void loadTransports() {
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

                    Transport transport = transportEntityAdapter.fromDMR(model);
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
    
    public void createTransport(final Transport transport) {
        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "teiid");
        address.get(ADDRESS).add("transport", transport.getName());
        
        ModelNode operation = this.transportEntityAdapter.fromEntity(transport);
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).set(address.get(ADDRESS).asObject());
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
                loadTransports();
            }
            @Override
            public void onFailure(Throwable caught) {
                Console.error(Console.MESSAGES.addingFailed("Teiid Transport "+ transport.getName()), 
                        caught.getMessage());
                loadTransports();
            }            
        });
    }
    
    public void saveTransport(final Transport transport, Map<String, Object> changeset) {
        ModelNode address = new ModelNode();
        address.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "teiid");
        address.get(ADDRESS).add("transport", transport.getName());

        ModelNode operation = this.transportEntityAdapter.fromChangeset(changeset, address);

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
                loadTransports();
            }
            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadTransports();
            }            
        });        
    }
    
    public void deleteTransport(final Transport transport) {
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "teiid");
        operation.get(ADDRESS).add("transport", transport.getName());
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
                loadTransports();
            }
            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadTransports();
            }            
        });
    }    
    
    // translator
    
    private void loadTranslators() {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "teiid");
        operation.get(CHILD_TYPE).set("translator");
        operation.get(RECURSIVE).set(true);        
        
        this.dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode response = dmrResponse.get();
                List<Property> children = response.get(RESULT).asPropertyList();
                List<Translator> translators = new ArrayList<Translator>(children.size());

                for (Property child : children) {
                    ModelNode model = child.getValue();

                    Translator translator = translatorAdapter.fromDMR(model);
                    translator.setName(child.getName());
                    translators.add(translator);
                }                
                getView().setTranslators(translators);
            }
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to retrieve configuration for Teiid translators", caught.getMessage());
            }             
        });        
    }
    
    public void saveTranslator(final Translator translator, Map<String, Object> changeset) {
        
        ModelNode address = new ModelNode();
        address.get(OP).set(WRITE_ATTRIBUTE_OPERATION);
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "teiid");
        address.get(ADDRESS).add("translator", translator.getName());
        
        ModelNode operation = this.translatorAdapter.fromChangeset(changeset, address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                boolean success = response.get(OUTCOME).asString().equals(SUCCESS);

                if (success) {
                    Console.info(Console.MESSAGES.saved("Teiid Translator " + translator.getName()));
                } else {
                    Console.error(Console.MESSAGES.saveFailed("Teiid Translator " + translator.getName()),
                            response.getFailureDescription());
                }
                loadTranslators();
            }
            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadTranslators();
            }            
        });        
    } 
    
    public void deleteTranslator(final Translator translator) {
        ModelNode operation = new ModelNode();
        operation.get(ADDRESS).set(Baseadress.get());
        operation.get(ADDRESS).add("subsystem", "teiid");
        operation.get(ADDRESS).add("translator", translator.getName());
        operation.get(OP).set(REMOVE);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();
                if (ModelNodeUtil.indicatesSuccess(result)) {
                    Console.info(Console.MESSAGES.deleted("Teiid Translator " + translator.getName()));
                } else {
                    Console.error(Console.MESSAGES.deletionFailed("Teiid Translator " + translator.getName()),
                            result.toString());
                }
                loadTranslators();
            }
            @Override
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                loadTranslators();
            }            
        });
    }
    
    public void createTranslator(final Translator translator) {
        ModelNode address = new ModelNode();
        address.get(ADDRESS).set(Baseadress.get());
        address.get(ADDRESS).add("subsystem", "teiid");
        address.get(ADDRESS).add("translator", translator.getName());
        
        ModelNode operation = this.translatorAdapter.fromEntity(translator);
        operation.get(OP).set(ADD);
        operation.get(ADDRESS).set(address.get(ADDRESS).asObject());
        operation.remove("name"); // work around

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse dmrResponse) {
                ModelNode result = dmrResponse.get();
                if (ModelNodeUtil.indicatesSuccess(result)) {
                    Console.info(Console.MESSAGES.added("Teiid Translator " + translator.getName()));
                } else {
                    Console.error(Console.MESSAGES.addingFailed("Teiid Translator " + translator.getName()), 
                            result.toString());
                }
                loadTranslators();
            }
            @Override
            public void onFailure(Throwable caught) {
                Console.error(Console.MESSAGES.addingFailed("Teiid Translator "+ translator.getName()), 
                        caught.getMessage());
                loadTranslators();
            }            
        });
    } 
    

	// Authentication
	public void saveAuthentication(Authentication authentication,
			Map<String, Object> changeset) {

		ModelNode address = new ModelNode();
		address.get(ADDRESS).set(Baseadress.get());
		address.get(ADDRESS).add("subsystem", "teiid");
		address.get(OP).set(WRITE_ATTRIBUTE_OPERATION);

		ModelNode operation = this.authenticationAdapter.fromChangeset(
				changeset, address);

		dispatcher.execute(new DMRAction(operation),
				new SimpleCallback<DMRResponse>() {
					@Override
					public void onSuccess(DMRResponse result) {
						ModelNode response = result.get();
						boolean success = response.get(OUTCOME).asString()
								.equals(SUCCESS);

						if (success) {
							Console.info(Console.MESSAGES
									.saved("Teiid configuration modified"));
						} else {
							Console.error(
									Console.MESSAGES
											.saveFailed("Teiid configuration modification failed"),
									response.getFailureDescription());
						}
						loadAuthenticationl();
					}

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						loadAuthenticationl();
					}
				});
	}

	private void loadAuthenticationl() {
		ModelNode operation = new ModelNode();
		operation.get(OP).set(READ_RESOURCE_OPERATION);
		operation.get(ADDRESS).set(Baseadress.get());
		operation.get(ADDRESS).add("subsystem", "teiid");

		dispatcher.execute(new DMRAction(operation),
				new SimpleCallback<DMRResponse>() {
					@Override
					public void onSuccess(DMRResponse dmrResponse) {
						ModelNode response = dmrResponse.get();
						Authentication bean = authenticationAdapter
								.fromDMR(response.get(RESULT));
						getView().setAuthentication(bean);
					}

					@Override
					public void onFailure(Throwable caught) {
						Console.error(
								"Failed to retrieve configuration for Teiid subsystem",
								caught.getMessage());
					}
				});
	}
    
}
