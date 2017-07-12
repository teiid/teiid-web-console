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

    @Binding(detypedName="grant-all")
    public Boolean isGrantAll();
    public void setGrantAll(Boolean flag);  	
}
