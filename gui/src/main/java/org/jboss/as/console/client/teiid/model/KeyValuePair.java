package org.jboss.as.console.client.teiid.model;

import org.jboss.as.console.client.widgets.forms.Binding;

public interface KeyValuePair {
	@Binding(detypedName="property-name", key=true)
	public String getKey();
	public void setKey(String key);

	@Binding(detypedName="property-value")
	public String getValue();
	public void setValue(String value);
	
}
