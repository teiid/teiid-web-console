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

import org.jboss.as.console.client.widgets.forms.Binding;

public interface DataPermission {
	
	@Binding(detypedName="resource-name", key=true)
	public String getResourceName();
	public void setResourceName(String name);
	
	@Binding(detypedName="allow-create")
	public Boolean isAllowCreate();
	public void setAllowCreate(Boolean flag);

	@Binding(detypedName="allow-read")
	public Boolean isAllowRead();
	public void setAllowRead(Boolean flag);

	@Binding(detypedName="allow-update")
	public Boolean isAllowUpdate();
	public void setAllowUpdate(Boolean flag);

	@Binding(detypedName="allow-delete")
	public Boolean isAllowDelete();
	public void setAllowDelete(Boolean flag);

	@Binding(detypedName="allow-alter")
	public Boolean isAllowAlter();
	public void setAllowAlter(Boolean flag);

	@Binding(detypedName="allow-execute")
	public Boolean isAllowExecute();
	public void setAllowExecute(Boolean flag);
	
    @Binding(detypedName="allow-language")
    public Boolean isAllowLanguage();
    public void setAllowLanguage(Boolean flag);	
    
    @Binding(detypedName="condition")
    public String getCondition();
    public void setCondition(String value);
    
    @Binding(detypedName="mask")
    public String getMask();
    public void setMask(String value);

    @Binding(detypedName="order")
    public Integer getOrder();
    public void setOrder(Integer value);
    
    @Binding(detypedName="constraint")
    public Boolean isConstraint();
    public void setConstraint(Boolean value);
}
