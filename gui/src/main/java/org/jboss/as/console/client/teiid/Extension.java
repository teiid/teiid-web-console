package org.jboss.as.console.client.teiid;

import org.jboss.as.console.client.teiid.runtime.VDBPresenter;
import org.jboss.as.console.spi.GinExtension;

import com.google.gwt.inject.client.AsyncProvider;


@GinExtension("org.jboss.as.console.TeiidExtension")
public interface Extension {
    AsyncProvider<SubsystemPresenter> getSubsystemPresenter();
    AsyncProvider<TranslatorPresenter> getTranslatorPresenter();
    AsyncProvider<TransportPresenter> getTransportPresenter();
    AsyncProvider<VDBPresenter> getVDBPresenter();
}
