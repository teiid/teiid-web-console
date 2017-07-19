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
package org.jboss.as.console.client.teiid.runtime;

import org.jboss.as.console.client.teiid.model.VDB;
import org.jboss.as.console.client.teiid.runtime.VDBView.TabProvider;

public abstract class VDBProvider implements TabProvider{
	private String vdbName;
	private String vdbVersion;
	private String modelName;
	private String policyName;
	
	public String getVdbName() {
		return vdbName;
	}
	public void setVdbName(String vdbName) {
		this.vdbName = vdbName;
	}
	public String getVdbVersion() {
		return vdbVersion;
	}
	public void setVdbVersion(String vdbVersion) {
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
