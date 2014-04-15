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
package org.jboss.as.console.client.teiid.runtime;

import static org.jboss.dmr.client.ModelDescriptionConstants.ADDRESS;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;

import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.state.GlobalServerSelection;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.teiid.model.*;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.spi.RuntimeExtension;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.dispatch.DispatchAsync;
import org.jboss.dmr.client.dispatch.impl.DMRAction;
import org.jboss.dmr.client.dispatch.impl.DMRResponse;

import com.google.gwt.core.client.Scheduler;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;

@SuppressWarnings("nls")
public class VDBPresenter extends Presenter<VDBPresenter.MyView, VDBPresenter.MyProxy> implements GlobalServerSelection.ServerSelectionListener {
   
	private DispatchAsync dispatcher;
    private RevealStrategy revealStrategy;
    private ServerInstance serverSelection;
    private DataModelFactory factory;
    private EntityAdapter<VDB> vdbAdaptor;
    private EntityAdapter<Request> requestAdaptor;
    private EntityAdapter<Session> sessionAdaptor;
    private EntityAdapter<MaterializedView> matViewAdaptor;
    private EntityAdapter<CacheStatistics> cacheAdaptor;
	
    @ProxyCodeSplit
    @NameToken("vdb-runtime")
    @RuntimeExtension(name="Virtual Databases", key="teiid")
    public interface MyProxy extends Proxy<VDBPresenter>, Place {
    }

    public interface MyView extends View {
        void setDeployedVDBs(List<VDB> vdbs);
        void setPresenter(VDBPresenter presenter);
        void setVDBRequests(List<Request> requests);
        void setQueryPlan(String plan);
        void cancelSubmitted(Request request);
        void setDataModelFactory(DataModelFactory factory);
        void setModelSchema(String ddl);
        void terminateSessionSubmitted(Session session);
        void setVDBSessions(String vdbName, int version, List<Session> sessions);
        <T> void setQueryResults(List<T> results, String clazz);
        void connectionTypeChanged(String vdbName, int version);
		void vdbReloaded(String vdbName, int version);
		void setCacheStatistics(CacheStatistics cache);
		void setSourceRequests(Request selection, List<Request> requests);
    }
    
	@Inject
	public VDBPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			DispatchAsync dispatcher, ApplicationMetaData metaData,
			RevealStrategy revealStrategy, BeanFactory factory) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.factory = (DataModelFactory)factory;
        this.vdbAdaptor = new EntityAdapter<VDB>(VDB.class, metaData);
        this.requestAdaptor = new EntityAdapter<Request>(Request.class, metaData);
        this.sessionAdaptor = new EntityAdapter<Session>(Session.class, metaData);
        this.matViewAdaptor = new EntityAdapter<MaterializedView>(MaterializedView.class, metaData);
        this.cacheAdaptor = new EntityAdapter<CacheStatistics>(CacheStatistics.class, metaData);
    }
    
    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getView().setDataModelFactory(this.factory);
        getEventBus().addHandler(GlobalServerSelection.TYPE, this);
    }
	
    @Override
    protected void onReset() {
        super.onReset();
        if(isVisible()) refresh(true);
    }    
    
	@Override
	protected void onReveal() {
		super.onReveal();
	}
	
    @Override
    public void onServerSelection(final ServerInstance server) {
        this.serverSelection = server;
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				refresh(true);
			}
		});
	}

	@Override
	protected void revealInParent() {
		revealStrategy.revealInRuntimeParent(this);
	}
	
    public void refresh(final boolean paging) {
        if(!isServerActive()) {
            Console.warning(Console.CONSTANTS.common_err_server_not_active());
            getView().setDeployedVDBs(null);
            return;
        }
        
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("list-vdbs");
        operation.get(ADDRESS).set(address);

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                if (response.get(RESULT).isDefined()) {
	                List<VDB> vdbs = vdbAdaptor.fromDMRList(response.get(RESULT).asList());
	                for (VDB vdb:vdbs) {
	                	boolean valid = true;
	                	for (Model m:vdb.getModels()) {
	                		if (!m.getValidityErrors().isEmpty()) {
	                			for (ValidityError ve: m.getValidityErrors()) {
	                				if (ve.getSeverity().equals("ERROR")) {
	                					valid = false;
	                				}
	                			}
	                		}
	                	}
	                	vdb.setValid(valid);
	                }
	                getView().setDeployedVDBs(vdbs);
                }
            }
        });        
    }	
    
    public void removeRoleName(final String vdbName, final int version, final String dataRole, final String mappedRole) {
        if(!isServerActive()) {
            Console.warning(Console.CONSTANTS.common_err_server_not_active());
            return;
        }
        
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("remove-data-role");
        operation.get(ADDRESS).set(address);
        operation.get("vdb-name").set(new ModelNode().set(vdbName));
        operation.get("vdb-version").set(new ModelNode().set(version));
        operation.get("data-role").set(new ModelNode().set(dataRole));
        operation.get("mapped-role").set(new ModelNode().set(mappedRole));

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                Console.info(mappedRole+ " role removed from VDB "+vdbName+"."+version);
            }
        });    	
    }
    
    public void addRoleName(final String vdbName, final int version, final String dataRole, final String mappedRole) {
        if(!isServerActive()) {
            Console.warning(Console.CONSTANTS.common_err_server_not_active());
            return;
        }
        
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("add-data-role");
        operation.get(ADDRESS).set(address);
        operation.get("vdb-name").set(new ModelNode().set(vdbName));
        operation.get("vdb-version").set(new ModelNode().set(version));
        operation.get("data-role").set(new ModelNode().set(dataRole));
        operation.get("mapped-role").set(new ModelNode().set(mappedRole));

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                Console.info(mappedRole+ " role added to VDB "+vdbName+"."+version);
            }
        });    	
    }    
    
    public void getRequests(String vdbName, int version, boolean includeSourceQueries) {
        if(!isServerActive()) {
        	getView().setVDBRequests(null);
        }
        
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("list-requests-per-vdb");
        operation.get(ADDRESS).set(address);
        operation.get("vdb-name").set(new ModelNode().set(vdbName));
        operation.get("vdb-version").set(new ModelNode().set(version));
        operation.get("include-source").set(new ModelNode().set(includeSourceQueries));

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                if (response.get(RESULT).isDefined()) {
	                List<Request> requests = requestAdaptor.fromDMRList(response.get(RESULT).asList());
	                getView().setVDBRequests(requests);    
                }
                else {
                	getView().setVDBRequests(null);
                }
            }
        });    	
    }
    
    public void getQueryPlan(Request request) {
        if(!isServerActive()) {
        	getView().setQueryPlan("No Server Found");
        }
        
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("get-query-plan");
        operation.get(ADDRESS).set(address);
        operation.get("session").set(new ModelNode().set(request.getSessionId()));
        operation.get("execution-id").set(new ModelNode().set(request.getExecutionId()));

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                String plan =  null;
                if (response.get(RESULT).isDefined()) {
                	plan = response.get(RESULT).asString();
                }
                if (plan != null && !plan.trim().isEmpty()) {
                	getView().setQueryPlan(plan);
                }
                else {
                	getView().setQueryPlan("<node name=\"query\"><property name=\"noplan\">No Plan found, query might have finished executing!</property></node>");
                }
            }
        });    	
    }
    
    public void cancelRequest(final Request request) {
        if(!isServerActive()) {
        	getView().cancelSubmitted(request);
        }
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("cancel-request");
        operation.get(ADDRESS).set(address);
        operation.get("session").set(new ModelNode().set(request.getSessionId()));
        operation.get("execution-id").set(new ModelNode().set(request.getExecutionId()));

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                if (response.get(RESULT).isDefined()) {
	                getView().cancelSubmitted(request);   
	                Console.info("Query Cancel Submitted. Session Id:"+request.getSessionId()+", Execution Id:"+request.getExecutionId());
                }
                else {
                	getView().cancelSubmitted(null);
                }
            }
        });    	
    }

    public void getSchema(String vdbName, int version, String modelName) {
        if(!isServerActive()) {
        	getView().setModelSchema("No Active Server Found");
        }
        
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("get-schema");
        operation.get(ADDRESS).set(address);
        operation.get("vdb-name").set(new ModelNode().set(vdbName));
        operation.get("vdb-version").set(new ModelNode().set(version));
        operation.get("model-name").set(new ModelNode().set(modelName));

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                if (response.get(RESULT).isDefined()) {
	                getView().setModelSchema(response.get(RESULT).asString());    
                }
                else {
                	getView().setModelSchema(null);
                }
            }
        });     	
    }

	public void getSessions(final String vdbName, final int version) {
        if(!isServerActive()) {
        	getView().setVDBRequests(null);
        }

        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("list-sessions");
        operation.get(ADDRESS).set(address);
        operation.get("vdb-name").set(new ModelNode().set(vdbName));
        operation.get("vdb-version").set(new ModelNode().set(version));

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                if (response.get(RESULT).isDefined()) {
	                List<Session> sessions = sessionAdaptor.fromDMRList(response.get(RESULT).asList());
	                getView().setVDBSessions(vdbName, version, sessions);    
                }
                else {
                	getView().setVDBRequests(null);
                }
            }
        }); 
	}

	public void terminateSession(final Session session) {
        if(!isServerActive()) {
        	getView().terminateSessionSubmitted(session);
        }
        
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("terminate-session");
        operation.get(ADDRESS).set(address);
        operation.get("session").set(new ModelNode().set(session.getSessionId()));

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                if (response.get(RESULT).isDefined()) {
	                getView().terminateSessionSubmitted(session);   
	                Console.info("Terminate Session Submitted. Session Id:"+session.getSessionId());
                }
                else {
                	getView().terminateSessionSubmitted(null);
                }
            }
        });    	
	}
	
	public <T> void executeQuery(final String vdbName, final int version, final String sql, final String clazz) {
        if(!isServerActive()) {
        	getView().setQueryResults(null, clazz);
        }
        
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("execute-query");
        operation.get(ADDRESS).set(address);
        operation.get("vdb-name").set(new ModelNode().set(vdbName));
        operation.get("vdb-version").set(new ModelNode().set(version));
        operation.get("sql-query").set(new ModelNode().set(sql));
        operation.get("timeout-in-milli").set(new ModelNode().set(10000));
        
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                if (response.get(RESULT).isDefined()) {
                		if (getEntityAdapter(clazz) != null) {
                			getView().setQueryResults(matViewAdaptor.fromDMRList(response.get(RESULT).asList()), clazz);
                		}
                		else {
                			getView().setQueryResults(null, clazz);
                		}
                }
                else {
                	getView().setQueryResults(null, clazz);
                }
            }
        });    	
	}	
	
	private <T> EntityAdapter<T> getEntityAdapter(String clazz){
		if (clazz.equals(MaterializedView.class.getName())) {
			return (EntityAdapter<T>) this.matViewAdaptor;
		}
		return null;
	}

	public void clearCache(final String vdbName, final int version, final String cacheType) {
        if(!isServerActive()) {
        	return;
        }
        
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("clear-cache");
        operation.get(ADDRESS).set(address);
        operation.get("cache-type").set(new ModelNode().set(cacheType));
        operation.get("vdb-name").set(new ModelNode().set(vdbName));
        operation.get("vdb-version").set(new ModelNode().set(version));
        
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
            	Console.info("Cache "+cacheType+" on VDB = "+vdbName+"."+version+" has been cleared");
            }
        }); 		
	}

	public void changeConnectionType(final String vdbName, final int version, final String connType) {
        if(!isServerActive()) {
        	return;
        }
        
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("change-vdb-connection-type");
        operation.get(ADDRESS).set(address);
        operation.get("connection-type").set(new ModelNode().set(connType));
        operation.get("vdb-name").set(new ModelNode().set(vdbName));
        operation.get("vdb-version").set(new ModelNode().set(version));
        
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
            	getView().connectionTypeChanged(vdbName, version);
            	Console.info("Changing Connection type to "+connType+" on VDB = "+vdbName+"."+version+" has been cleared");
            }
        }); 
	}

	public void assignDataSource(final String vdbName, final int version,
			final String modelName, final String sourceName,
			final String translatorName, final String dataSourceName) {

		if(!isServerActive()) {
        	return;
        }

		ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("assign-datasource");
        operation.get(ADDRESS).set(address);
        operation.get("vdb-name").set(new ModelNode().set(vdbName));
        operation.get("vdb-version").set(new ModelNode().set(version));
        operation.get("model-name").set(new ModelNode().set(modelName));
        operation.get("source-name").set(new ModelNode().set(sourceName));
        operation.get("translator-name").set(new ModelNode().set(translatorName));
        operation.get("ds-name").set(new ModelNode().set(dataSourceName));
        
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {            	
            	Console.info("Changing the JNDI name of the data source on VDB ="+vdbName+"."+version+" on Model="+modelName+" to "+dataSourceName);
            }
        }); 
		
	}

	public void reloadVDB(final String vdbName, final int version) {
		if(!isServerActive()) {
        	return;
        }
        
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("restart-vdb");
        operation.get(ADDRESS).set(address);
        operation.get("vdb-name").set(new ModelNode().set(vdbName));
        operation.get("vdb-version").set(new ModelNode().set(version));
        
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {            	
            	Console.info("VDB "+vdbName+"."+version+" has been submitted for reload");
            	getView().vdbReloaded(vdbName, version);
            }
        }); 		
	}

	public void getCacheStatistics() {
		if(!isServerActive()) {
        	return;
        }
        
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("cache-statistics");
        operation.get(ADDRESS).set(address);
        operation.get("cache-type").set(new ModelNode().set("QUERY_SERVICE_RESULT_SET_CACHE"));
        
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {            	
                ModelNode response  = result.get();
                if (response.get(RESULT).isDefined()) {
	                CacheStatistics cache = cacheAdaptor.fromDMR(response.get(RESULT));
	                getView().setCacheStatistics(cache);    
                }
                else {
                	getView().setCacheStatistics(null);
                }
            }
        });		
	}

	public void getSourceRequests(final Request selection) {
        if(!isServerActive()) {
        	getView().setSourceRequests(selection, null);
        }
        
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("list-requests-per-session");
        operation.get(ADDRESS).set(address);
        operation.get("session").set(new ModelNode().set(selection.getSessionId()));
        operation.get("include-source").set(new ModelNode().set(true));

        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response  = result.get();
                if (response.get(RESULT).isDefined()) {
	                List<Request> requests = requestAdaptor.fromDMRList(response.get(RESULT).asList());
	                getView().setSourceRequests(selection, requests);    
                }
                else {
                	getView().setSourceRequests(selection, null);
                }
            }
        });    	
	}

    private boolean isServerActive() {
        return ((serverSelection== null) || (serverSelection != null && serverSelection.isRunning()));
    }
}
