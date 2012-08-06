package org.jboss.as.console.client.teiid.model;

import org.jboss.as.console.client.widgets.forms.Binding;

public interface ValidityError {
	@Binding(detypedName="error-path")
	public String getPath();
	public void setPath(String path);
	
	@Binding(detypedName="severity")
	public String getSeverity();
	public void setSeverity(String severity);	
	
	@Binding(detypedName="message")
	public String getMessage();
	public void setMessage(String msg);	
}
