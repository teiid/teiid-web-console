package org.jboss.as.console.client.teiid.model;

import org.jboss.as.console.client.widgets.forms.Binding;

public interface ImportedVDB {
	
	@Binding(detypedName="import-vdb-name", key = true)
    public String getName();
    public void setName(String name);
    
    @Binding(detypedName="import-vdb-version")
    public Integer getVersion();
    public void setVersion(Integer version);
    
    @Binding(detypedName="import-policies")
    public Boolean isImportPolicies();
    public void setImportPolicies(Boolean flag);    
}
