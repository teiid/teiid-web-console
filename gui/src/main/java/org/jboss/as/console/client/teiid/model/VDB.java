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


public interface VDB {
    
	@Binding(detypedName="vdb-name", key = true)
    public String getName();
    public void setName(String name);
    
    @Binding(detypedName="vdb-version")
    public String getVersion();
    public void setVersion(String version);
	
    @Binding(detypedName="models", listType="org.jboss.as.console.client.teiid.model.Model")
	public List<Model> getModels();
    public void setModels(List<Model> models);

    @Binding(detypedName="status")
    public String getStatus();
    public void setStatus(String status);
    
    @Binding(detypedName="connection-type")
    public String getConnectionType();
    public void setConnectionType(String type);

    @Binding(detypedName="vdb-description")
    public String getDescription();
    public void setDescription(String description);

    @Binding(detypedName="data-policies", listType="org.jboss.as.console.client.teiid.model.DataPolicy")
    public List<DataPolicy> getDataPolicies();
    public void setDataPolicies(List<DataPolicy> policies);
    
    @Binding(detypedName="xml-deployment")
    public Boolean isDynamic();
    public void setDynamic(Boolean flag);
    
    @Binding(detypedName="override-translators", listType="org.jboss.as.console.client.teiid.model.VDBTranslator")
    public List<VDBTranslator> getOverrideTranslators();
    public void setOverrideTranslators(List<VDBTranslator> translators);
    
    @Binding(detypedName="import-vdbs", listType="org.jboss.as.console.client.teiid.model.ImportedVDB")
    public List<ImportedVDB> getVDBImports();
    public void setVDBImports(List<ImportedVDB> imports);
    
    @Binding(detypedName="properties", listType="org.jboss.as.console.client.teiid.model.KeyValuePair")
    public List<KeyValuePair> getProperties();
    public void setProperties(List<KeyValuePair> props);
    
    public Boolean isValid();
    public void setValid(Boolean flag);    
}
