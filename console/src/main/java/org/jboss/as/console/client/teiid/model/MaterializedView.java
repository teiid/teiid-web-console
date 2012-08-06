package org.jboss.as.console.client.teiid.model;

import org.jboss.as.console.client.widgets.forms.Binding;

public interface MaterializedView {
	@Binding(detypedName="VDBName")
	public String getVDBName();
	public void setVDBName(String name);
	
	@Binding(detypedName="SchemaName")
	public String getModelName();
	public void setModelName(String name);
	
	@Binding(detypedName="Name")
	public String getTableName();
	public void setTableName(String name);
	
	@Binding(detypedName="Valid")
	public Boolean isValid();
	public void setValid(Boolean flag);
	
	@Binding(detypedName="LoadState")
	public String getLoadState();
	public void setLoadState(String state);
	
	@Binding(detypedName="Updated")
	public String getLastUpdatedTime();
	public void setLastUpdatedTime(String time);
	
	@Binding(detypedName="Cardinality")
	public Integer getCardinality();
	public void setCardinality(Integer cardinality);
}
