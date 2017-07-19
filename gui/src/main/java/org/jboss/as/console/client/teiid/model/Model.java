/* 
 * Copyright Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags and
 * the COPYRIGHT.txt file distributed with this work.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.console.client.teiid.model;

import java.util.List;

import org.jboss.as.console.client.widgets.forms.Binding;

public interface Model {
	@Binding(detypedName="model-name", key = true)
    public String getName();
    public void setName(String name);	

    @Binding(detypedName="description")
    public String getDescription();
    public void setDescription(String description); 
    
    @Binding(detypedName="visible")
    public Boolean isVisible();
    public void setVisible(Boolean visible);

    @Binding(detypedName="model-type")
    public String getModelType();
    public void setModelType(String type);
    
    @Binding(detypedName="model-path")
    public String getModelPath();
    public void setModelPath(String path);
    
    @Binding(detypedName="source-mappings", listType="org.jboss.as.console.client.teiid.model.SourceMapping")
    public List<SourceMapping> getSourceMappings();
    public void setSourceMappings(List<SourceMapping> mappings);
    
	@Binding(detypedName = "properties", listType="org.jboss.as.console.client.teiid.model.KeyValuePair")
	public List<KeyValuePair> getProperties();
	public void setProperties(List<KeyValuePair> props);   
	
    @Binding(detypedName="validity-errors", listType="org.jboss.as.console.client.teiid.model.ValidityError")
    public List<ValidityError> getValidityErrors();
    public void setValidityErrors(List<ValidityError> mappings);	
    
    /*
    @Binding(detypedName="metadata")
    public String getMetadata();
    public void setMetadata(String metadata);
    */
    
    @Binding(detypedName="metadata-type")
    public String getMetadataType();
    public void setMetadataType(String type);
    
    @Binding(detypedName="metadata-status")
    public String getMetadataStatus();
    public void setMetadataStatus(String status);
}
