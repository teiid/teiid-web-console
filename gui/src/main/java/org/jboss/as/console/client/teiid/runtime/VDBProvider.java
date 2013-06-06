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
package org.jboss.as.console.client.teiid.runtime;

import org.jboss.as.console.client.teiid.model.VDB;
import org.jboss.as.console.client.teiid.runtime.VDBView.TabProvider;

public abstract class VDBProvider implements TabProvider{
	private String vdbName;
	private int vdbVersion;
	private String modelName;
	private String policyName;
	
	public String getVdbName() {
		return vdbName;
	}
	public void setVdbName(String vdbName) {
		this.vdbName = vdbName;
	}
	public int getVdbVersion() {
		return vdbVersion;
	}
	public void setVdbVersion(int vdbVersion) {
		this.vdbVersion = vdbVersion;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getPolicyName() {
		return policyName;
	}
	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}
	public boolean isActive(VDB vdb) {
		return vdb.getStatus().equals("ACTIVE");//$NON-NLS-1$
	}
}
