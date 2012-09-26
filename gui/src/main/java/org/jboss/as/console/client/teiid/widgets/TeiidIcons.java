package org.jboss.as.console.client.teiid.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface TeiidIcons extends ClientBundle {

    public static final TeiidIcons INSTANCE =  GWT.create(TeiidIcons.class);

    @Source("minus.png")
    ImageResource minus();
    
    @Source("status_not_ok.png")
    ImageResource status_not_ok();
}
