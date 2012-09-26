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

import org.jboss.as.console.client.shared.subsys.RevealStrategy;
import org.jboss.as.console.client.shared.viewframework.FrameworkView;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.Place;
import com.gwtplatform.mvp.client.proxy.Proxy;
import org.jboss.as.console.spi.SubsystemExtension;

public class SubsystemPresenter extends Presenter<SubsystemPresenter.MyView, SubsystemPresenter.MyProxy>{
    private final RevealStrategy revealStrategy;

    @ProxyCodeSplit
    @NameToken("teiid")
    @SubsystemExtension(name="Query Engine", group = "Teiid", key="teiid")
    public interface MyProxy extends Proxy<SubsystemPresenter>, Place {
    }

    public interface MyView extends View, FrameworkView {
    }

    @Inject
    public SubsystemPresenter(EventBus eventBus, MyView view, MyProxy proxy, RevealStrategy revealStrategy) {
        super(eventBus, view, proxy);
        this.revealStrategy = revealStrategy;
    }

    @Override
    protected void revealInParent() {
        revealStrategy.revealInParent(this);
    }
    
    @Override
    protected void onReset() {
        super.onReset();
        getView().initialLoad();
    }
}
