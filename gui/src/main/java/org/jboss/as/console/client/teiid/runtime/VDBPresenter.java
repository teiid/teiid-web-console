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

import java.util.ArrayList;
import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.BeanFactory;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.teiid.model.CacheStatistics;
import org.jboss.as.console.client.teiid.model.DataModelFactory;
import org.jboss.as.console.client.teiid.model.EngineStatistics;
import org.jboss.as.console.client.teiid.model.MaterializedView;
import org.jboss.as.console.client.teiid.model.Model;
import org.jboss.as.console.client.teiid.model.Request;
import org.jboss.as.console.client.teiid.model.Session;
import org.jboss.as.console.client.teiid.model.VDB;
import org.jboss.as.console.client.teiid.model.ValidityError;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.spi.RequiredResources;
import org.jboss.as.console.spi.RuntimeExtension;
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
public class VDBPresenter extends
		Presenter<VDBPresenter.MyView, VDBPresenter.MyProxy> {
   
	private DispatchAsync dispatcher;
    private RevealStrategy revealStrategy;
    private DataModelFactory factory;
    private EntityAdapter<VDB> vdbAdaptor;
    private EntityAdapter<Request> requestAdaptor;
    private EntityAdapter<Session> sessionAdaptor;
    private EntityAdapter<MaterializedView> matViewAdaptor;
    private EntityAdapter<CacheStatistics> cacheAdaptor;
    private EntityAdapter<EngineStatistics> runtimeAdaptor;
    public 	List<ModelNode> list;
    
	
    @ProxyCodeSplit
    @NameToken("vdb-runtime")
    @RuntimeExtension(name="Teiid", key="teiid")
    @RequiredResources(resources = {"{selected.profile}/subsystem=teiid"})
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
		void setEngineStatistics(EngineStatistics stats);
		void setSQLResult(List<ModelNode> list);
		void setVDBList(List<VDB> list);
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
        this.runtimeAdaptor = new EntityAdapter<EngineStatistics>(EngineStatistics.class, metaData); 
    }
    
    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getView().setDataModelFactory(this.factory);
    }
	
        
    @Override
    protected void onReset() {
        super.onReset();
        getEngineStatistics();
        if(isVisible()) refresh(true);
    }    
    
	@Override
	protected void onReveal() {
		super.onReveal();
	}
	
	@Override
	protected void revealInParent() {
		revealStrategy.revealInRuntimeParent(this);
	}
	
    public void refresh(final boolean paging) {
        
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
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to get list of current VDBs deployed in the system",
                        caught.getMessage());
            }                         
        });        
    }	
    
    
public void getVDBS( ) {
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
	                getView().setVDBList(vdbs);
                }
            }
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to get list of current VDBs deployed in the system",
                        caught.getMessage());
            }                         
        });    
    }	
    
    
    public void removeRoleName(final String vdbName, final int version, final String dataRole, final String mappedRole) {
        
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
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to remove '"+mappedRole+"' role from the VDB = " +vdbName+"."+version,
                        caught.getMessage());
            }
        });    	
    }
    
    public void addRoleName(final String vdbName, final int version, final String dataRole, final String mappedRole) {
        
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
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to add role", caught.getMessage());
            }            
        });    	
    }    
    
    public void getRequests(final String vdbName, final int version, final boolean includeSourceQueries) {
        
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
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to get in process requests for VDB "+ vdbName +"."+ version, 
                        caught.getMessage());
            }                                                                                    
        });    	
    }
    
    public void getQueryPlan(Request request) {
        
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
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to get query plan", caught.getMessage());
            }                                                                        
        });    	
    }
    
    public void cancelRequest(final Request request) {
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
	                Console.info("Query Cancel Submitted. Session Id:"+request.getSessionId()
	                        +", Execution Id:"+request.getExecutionId());
                }
                else {
                	getView().cancelSubmitted(null);
                }
            }
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Filed to submit Query Cancel. Session Id:"+request.getSessionId()
                        +", Execution Id:"+request.getExecutionId(), caught.getMessage());
            }                        
        });    	
    }

    public void getSchema(final String vdbName, final int version, final String modelName) {
        
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
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to get schema of the model = "+modelName 
                        + " in vdb = " + vdbName 
                        + " with version = " + version, 
                        caught.getMessage());
            }                                                                                                
        });     	
    }

	public void getSessions(final String vdbName, final int version) {

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
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to get current sessions for vdb = " + vdbName +"."+ version, 
                        caught.getMessage());
            }             
        }); 
	}

	public void terminateSession(final Session session) {
        
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
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to terminate seession '"+session, caught.getMessage());
            }            
        });    	
	}
	
	public <T> void executeQuery(final String vdbName, final int version, final String sql, final String clazz) {
        
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");
        ModelNode operation = new ModelNode();
        operation.get(OP).set("execute-query");
        operation.get(ADDRESS).set(address);
        operation.get("vdb-name").set(new ModelNode().set(vdbName));
        operation.get("vdb-version").set(new ModelNode().set(version));
        operation.get("sql-query").set(new ModelNode().set(sql));
        operation.get("timeout-in-milli").set(new ModelNode().set(-1));
        
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
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to execute query, or timeout occured", caught.getMessage());
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
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to clear Cache " + cacheType + " on VDB = " + vdbName + "." + version, 
                        caught.getMessage());
            }                                    
        }); 		
	}

	public void changeConnectionType(final String vdbName, final int version, final String connType) {
        
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
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to change Connection type to "+connType+" on VDB = "+vdbName+"."+version, 
                        caught.getMessage());
            }                        
            
        }); 
	}

	public void assignDataSource(final String vdbName, final int version,
			final String modelName, final String sourceName,
			final String translatorName, final String dataSourceName) {

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
                Console.info("Changing the JNDI name of the data source on VDB ="
                                + vdbName + "." + version + " on Model="
                                + modelName + " to " + dataSourceName);
            }
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Changing the JNDI name of the data source on VDB ="
                              + vdbName + "." + version+ " on Model=" + modelName + " to "
                              + dataSourceName, caught.getMessage());
            }                        
        }); 
		
	}

	public void reloadVDB(final String vdbName, final int version) {
        
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
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to reload the VDB = " +vdbName+"."+version,
                        caught.getMessage());
            }                         
        }); 		
	}

	public void getCacheStatistics() {
        
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
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to get cache statistics", caught.getMessage());
            }                                                            
        });		
	}

	public void getSourceRequests(final Request selection) {
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
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to get data source requests for session = "+selection.getSessionId(),
                        caught.getMessage());
            }                         
        });    	
	}

    public void getEngineStatistics() {
        ModelNode address = RuntimeBaseAddress.get();
        address.add("subsystem", "teiid");  //$NON-NLS-1$  //$NON-NLS-2$
        ModelNode operation = new ModelNode();
        operation.get(OP).set("engine-statistics");  //$NON-NLS-1$ 
        operation.get(ADDRESS).set(address);
        
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override
            public void onSuccess(DMRResponse result) {             
                ModelNode response  = result.get();
                if (response.get(RESULT).isDefined()) {
                    EngineStatistics stats = runtimeAdaptor.fromDMR(response.get(RESULT));
                    getView().setEngineStatistics(stats);    
                }
                else {
                    getView().setEngineStatistics(null);
                }
            }
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to retrieve query engine statistics for Teiid", caught.getMessage());
            }             
        });     
    }	
    
    public void getExecuteSQL(String vdbName, int version, String sql) {
    	
    	ModelNode address = RuntimeBaseAddress.get();
		address.add("subsystem", "teiid");
		ModelNode operation = new ModelNode();
		operation.get(OP).set("execute-query");
        operation.get(ADDRESS).set(address);
        operation.get("vdb-name").set(new ModelNode().set(vdbName));
        operation.get("vdb-version").set(new ModelNode().set(version));
        operation.get("sql-query").set(new ModelNode().set(sql));
        operation.get("timeout-in-milli").set(new ModelNode().set(-1));
        dispatcher.execute(new DMRAction(operation), new SimpleCallback<DMRResponse>() {
            @Override 
            public void onSuccess(DMRResponse result) {             
                ModelNode response  = result.get();
                if (response.get(RESULT).isDefined()) {
                	list = response.get(RESULT).asList();
                	 getView().setSQLResult(list);
                	Console.info("VDB "+vdbName+"."+version+" has been execute SQL query. "+sql);
//                	getView().vdbReloaded(vdbName, version);
                }else {
//                	 getView().setSQLResult(sqlResult);
                }
                
            }
            @Override
            public void onFailure(Throwable caught) {
                Console.error("Failed to execute SQL query", caught.getMessage());
            }             
        }); 
    }
    
}
