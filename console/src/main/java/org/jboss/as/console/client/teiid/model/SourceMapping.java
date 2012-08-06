package org.jboss.as.console.client.teiid.model;

import org.jboss.as.console.client.widgets.forms.Binding;

public interface SourceMapping {
	@Binding(detypedName="source-name")
	public String getSourceName();
	public void setSourceName(String name);

	@Binding(detypedName="jndi-name")
	public String getJndiName();
	public void setJndiName(String name);
	
	@Binding(detypedName="translator-name")
	public String getTranslatorName();
	public void setTranslatorName(String name);	
}
