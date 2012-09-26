package org.jboss.as.console.client.teiid.model;

import java.util.List;

import org.jboss.as.console.client.widgets.forms.Binding;

public interface VDBTranslator {

	@Binding(detypedName="translator-name", key = true)
    public String getName();
    public void setName(String name);	
    
    @Binding(detypedName="base-type")
    public String getType();
    public void setType(String type);
    
    @Binding(detypedName="translator-description")
    public String getDescription();
    public void setDescription(String desc);
    
    @Binding(detypedName="properties", listType="org.jboss.as.console.client.teiid.model.KeyValuePair")
    public List<KeyValuePair> getProperties();
    public void setProperties(List<KeyValuePair> props);
    
    @Binding(detypedName="module-name")
    public String getModuleName();
    public void setModuleName(String name);
}
