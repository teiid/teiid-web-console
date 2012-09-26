package org.jboss.as.console.client.teiid.model;

import java.util.List;

import org.jboss.as.console.client.widgets.forms.Binding;

public interface DataPolicy {
	
	@Binding(detypedName="policy-name", key=true)
	public String getName();
	public void setName(String name);
	
	@Binding(detypedName="policy-description")
	public String getDescription();
	public void setDescription(String desc);
	
	@Binding(detypedName="data-permissions", listType="org.jboss.as.console.client.teiid.model.DataPermission")
	public List<DataPermission> getPermissions();
	public void setPermissions(List<DataPermission> permissions);
	
	@Binding(detypedName="mapped-role-names", listType="java.lang.String")
	public List<String> getMappedRoleNames();
	public void setMappedRoleNames(List<String> roleNames);
	
	@Binding(detypedName="any-authenticated")
	public Boolean isAnyAuthenticated();
	public void setAnyAuthenticated(Boolean flag);
	
	@Binding(detypedName="allow-create-temp-tables")
	public Boolean isAllowCreateTemporaryTables();
	public void setAllowCreateTemporaryTables(Boolean flag);

}
