package org.jboss.as.console.client.teiid.runtime;

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
	
}
