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

public interface MaterializedView {
	@Binding(detypedName="VDBName")
	public String getVDBName();
	public void setVDBName(String name);
	
	@Binding(detypedName="SchemaName")
	public String getModelName();
	public void setModelName(String name);
	
	@Binding(detypedName="Name")
	public String getTableName();
	public void setTableName(String name);
	
	@Binding(detypedName="Valid")
	public Boolean isValid();
	public void setValid(Boolean flag);
	
	@Binding(detypedName="LoadState")
	public String getLoadState();
	public void setLoadState(String state);
	
	@Binding(detypedName="Updated")
	public String getLastUpdatedTime();
	public void setLastUpdatedTime(String time);
	
	@Binding(detypedName="Cardinality")
	public Integer getCardinality();
	public void setCardinality(Integer cardinality);
}
