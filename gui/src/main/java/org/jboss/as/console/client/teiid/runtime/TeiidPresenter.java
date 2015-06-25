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

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.domain.model.SimpleCallback;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.state.ReloadEvent;
import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.teiid.model.EngineStatistics;
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

public class TeiidPresenter extends Presenter<TeiidPresenter.MyView, TeiidPresenter.MyProxy> 
	implements ReloadEvent.ReloadListener {

	private DispatchAsync dispatcher;
    private RevealStrategy revealStrategy;
	private EntityAdapter<EngineStatistics> runtimeAdaptor;
	
    @ProxyCodeSplit
    @NameToken("teiid-runtime")
    @RuntimeExtension(name="Teiid", key="teiid")
    /*
    @AccessControl(
        resources = {
                "/{selected.host}/{selected.server}/subsystem=teiid"
        } 
    ) 
    */   
    public interface MyProxy extends Proxy<TeiidPresenter>, Place {
    }

    public interface MyView extends View {
        void setPresenter(TeiidPresenter presenter);
        void setEngineStatistics(EngineStatistics stats);
    }	
    
	@Inject
	public TeiidPresenter(EventBus eventBus, MyView view, MyProxy proxy,
			DispatchAsync dispatcher, ApplicationMetaData metaData,
			RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);

        this.dispatcher = dispatcher;
        this.revealStrategy = revealStrategy;
        this.runtimeAdaptor = new EntityAdapter<EngineStatistics>(EngineStatistics.class, metaData);
    }
    
    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
        getEventBus().addHandler(ReloadEvent.TYPE, this);
    }	
    
    @Override
    public void onReload() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
            	getEngineStatistics();
            }
        });
	}

    @Override
    protected void onReset() {
        super.onReset();
        if(isVisible()) {
        	getEngineStatistics();
        }
    } 
    
	@Override
	protected void revealInParent() {
		revealStrategy.revealInRuntimeParent(this);
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
}
