package org.jboss.as.console.client.teiid;

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
    }


}
