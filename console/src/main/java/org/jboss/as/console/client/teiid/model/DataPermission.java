package org.jboss.as.console.client.teiid.model;

import org.jboss.as.console.client.widgets.forms.Binding;

public interface DataPermission {
	
	@Binding(detypedName="resource-name", key=true)
	public String getResourceName();
	public void setResourceName(String name);
	
	@Binding(detypedName="allow-create")
	public Boolean isAllowCreate();
	public void setAllowCreate(Boolean flag);

	@Binding(detypedName="allow-read")
	public Boolean isAllowRead();
	public void setAllowRead(Boolean flag);

	@Binding(detypedName="allow-update")
	public Boolean isAllowUpdate();
	public void setAllowUpdate(Boolean flag);

	@Binding(detypedName="allow-delete")
	public Boolean isAllowDelete();
	public void setAllowDelete(Boolean flag);

	@Binding(detypedName="allow-alter")
	public Boolean isAllowAlter();
	public void setAllowAlter(Boolean flag);

	@Binding(detypedName="allow-execute")
	public Boolean isAllowExecute();
	public void setAllowExecute(Boolean flag);
}
