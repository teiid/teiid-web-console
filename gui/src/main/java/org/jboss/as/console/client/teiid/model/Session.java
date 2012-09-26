package org.jboss.as.console.client.teiid.model;

import org.jboss.as.console.client.widgets.forms.Binding;

public interface Session {

	@Binding(detypedName="last-ping-time")
	public String getLastPingTime();
	public void setLastPingTime(String time);

	@Binding(detypedName="application-name")
    public String getApplicationName();
    public void setApplicationName(String appName);

    @Binding(detypedName="session-id", key = true)
    public String getSessionId();
    public void setSessionId(String id);

    @Binding(detypedName="user-name")
    public String getUserName();
    public void setUserName(String name);

    @Binding(detypedName="vdb-name")
    public String getVDBName();
    public void setVDBName(String name);

    @Binding(detypedName="vdb-version")
    public Integer getVDBVersion();
    public void setVDBVersion(Integer version);

    @Binding(detypedName="ip-address")
    public String getIPAddress();
    public void setIPAddress(String address);
      
    @Binding(detypedName="client-host-address")
    public String getClientHostName();
    public void setClientHostName(String name);
    
    @Binding(detypedName="client-hardware-address")
    public String getClientHardwareAddress();
    public void setClientHardwareAddress(String address);

    @Binding(detypedName="created-time")
    public Long getCreatedTime();
    public void setCreatedTime(Long time);

    @Binding(detypedName="security-domain")
    public String getSecurityDomain();
    public void setSecurityDomain(String domain);
}
