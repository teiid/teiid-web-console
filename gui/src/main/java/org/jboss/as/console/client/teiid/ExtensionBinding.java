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

import org.jboss.as.console.client.teiid.runtime.TeiidPresenter;
import org.jboss.as.console.client.teiid.runtime.TeiidView;
import org.jboss.as.console.client.teiid.runtime.VDBPresenter;
import org.jboss.as.console.client.teiid.runtime.VDBView;
import org.jboss.as.console.spi.GinExtensionBinding;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;


@GinExtensionBinding
public class ExtensionBinding extends AbstractPresenterModule {

    @Override
    protected void configure() {
        bindPresenter(SubsystemPresenter.class,
        		SubsystemPresenter.MyView.class,
                SubsystemView.class,
                SubsystemPresenter.MyProxy.class);
        bindPresenter(TranslatorPresenter.class,
        		TranslatorPresenter.MyView.class,
                TranslatorView.class,
                TranslatorPresenter.MyProxy.class); 
        
        bindPresenter(TransportPresenter.class,
        		TransportPresenter.MyView.class,
                TransportView.class,
                TransportPresenter.MyProxy.class);    
        
        bindPresenter(VDBPresenter.class,
        		VDBPresenter.MyView.class,
                VDBView.class,
                VDBPresenter.MyProxy.class);
        
        bindPresenter(TeiidPresenter.class,
        		TeiidPresenter.MyView.class,
                TeiidView.class,
                TeiidPresenter.MyProxy.class);         
    }
}
