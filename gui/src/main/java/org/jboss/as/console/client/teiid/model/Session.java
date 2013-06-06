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
