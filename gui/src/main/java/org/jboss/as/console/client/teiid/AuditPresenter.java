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

import static org.jboss.dmr.client.ModelDescriptionConstants.ADDRESS;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;

import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.teiid.model.TeiidLogger;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
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

@SuppressWarnings("nls")
public class AuditPresenter extends Presenter<AuditPresenter.MyView, AuditPresenter.MyProxy> {

    public static final String CTX_COMMANDLOGGING = "org.teiid.COMMAND_LOG"; //$NON-NLS-1$
    public static final String CTX_AUDITLOGGING = "org.teiid.AUDIT_LOG"; //$NON-NLS-1$
    public static final String CTX_TRACELOGGING = "org.teiid"; //$NON-NLS-1$
    
	private DispatchAsync dispatch;
    private RevealStrategy revealStrategy;
    private EntityAdapter<TeiidLogger> loggerAdaptor;
    
    
    @ProxyCodeSplit
    @NameToken("teiid-audit")
    @SubsystemExtension(name="Audit Log", group = "Teiid", key="teiid")
    /*
    @AccessControl(resources = {
            "{selected.profile}/subsystem=logging"
    })
    */    
    public interface MyProxy extends Proxy<AuditPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(AuditPresenter presenter);  
        void initialLoad();
        void addLogger(String context);
        void deleteLogger(String context);
        void loggingStatus(String context, TeiidLogger logger);
        void logHandlerAdded(String handlerName, boolean added);
        void logHandlerRemoved(String handlerName, boolean removed);
        void loggerAdded(String context, boolean added);
        void loggerRemoved(String context, boolean removed);
        void handlerExists(String context, String name, boolean dbAppender, boolean exists);
    }	
    
	@Inject
	public AuditPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			DispatchAsync asyncDispatcher, ApplicationMetaData metaData,
			RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);
        this.dispatch = asyncDispatcher;
        this.revealStrategy = revealStrategy;
        this.loggerAdaptor = new EntityAdapter<TeiidLogger>(TeiidLogger.class, metaData);
    }
	
    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }	
    
    @Override
    protected void onReset() {
        super.onReset();
        getView().initialLoad();
    } 
    
    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }  
	
	public void getLogger(final String context) {
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "logging");
        address.add("logger", context);
        ModelNode operation = new ModelNode();
        operation.get(OP).set("read-resource");
        operation.get(ADDRESS).set(address);
        
        this.dispatch.execute(new DMRAction(operation),new SimpleCallback<DMRResponse>() {
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
                getView().loggingStatus(context, null);
            }
        });
	}
	
    public void checkHandler(final String context, final String name, final boolean dbAppender) {
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
        
        this.dispatch.execute(new DMRAction(operation),new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = result.get();
                if (isSucess(response)) {
                    getView().handlerExists(context, name, dbAppender, true);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                getView().handlerExists(context, name, dbAppender, false);
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
        
        this.dispatch.execute(new DMRAction(operation),new SimpleCallback<DMRResponse>() {
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
        
        this.dispatch.execute(new DMRAction(operation),new SimpleCallback<DMRResponse>() {
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
                
        this.dispatch.execute(new DMRAction(operation),new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                getView().logHandlerRemoved(name, isSucess(response));
            }   
            @Override
            public void onFailure(Throwable caught) {
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
        
        this.dispatch.execute(new DMRAction(operation),new SimpleCallback<DMRResponse>() {
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
        this.dispatch.execute(new DMRAction(operation),new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                getView().loggerRemoved(context, isSucess(response));
            }
            @Override
            public void onFailure(Throwable caught) {
            }
        });               
    }
}
