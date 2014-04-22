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
