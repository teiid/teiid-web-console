package org.jboss.as.console.client.teiid;

import com.google.gwt.inject.client.AsyncProvider;
import org.jboss.as.console.client.teiid.runtime.VDBPresenter;
import org.jboss.as.console.spi.GinExtension;


@GinExtension("org.jboss.as.console.TeiidExtension")
public interface Extension {
    AsyncProvider<SubsystemPresenter> getSubsystemPresenter();
    AsyncProvider<TranslatorPresenter> getTranslatorPresenter();
    AsyncProvider<TransportPresenter> getTransportPresenter();
    AsyncProvider<VDBPresenter> getVDBPresenter();
}
