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
