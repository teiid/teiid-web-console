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
    
    @Binding(detypedName="metadata")
    public String getMetadata();
    public void setMetadata(String metadata);
    
    @Binding(detypedName="metadata-type")
    public String getMetadataType();
    public void setMetadataType(String type);
}
