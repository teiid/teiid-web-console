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
