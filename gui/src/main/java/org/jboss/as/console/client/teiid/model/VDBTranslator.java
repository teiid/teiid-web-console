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
