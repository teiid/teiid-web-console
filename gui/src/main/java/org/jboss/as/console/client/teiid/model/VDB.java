/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.teiid.model;

import java.util.List;

import org.jboss.as.console.client.widgets.forms.Binding;


public interface VDB {
    
	@Binding(detypedName="vdb-name", key = true)
    public String getName();
    public void setName(String name);
    
    @Binding(detypedName="vdb-version")
    public Integer getVersion();
    public void setVersion(Integer version);
	
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
