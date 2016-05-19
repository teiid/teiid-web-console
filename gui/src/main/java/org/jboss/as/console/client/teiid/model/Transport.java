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

import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;

/**
 * Model for a Transport
 */
@Address("/subsystem=teiid/transport={0}")
public interface Transport {
    @Binding(detypedName="name", key=true)
    public String getName();
    public void setName(String str);

    @Binding(detypedName="protocol")
    public String getProtocol();
    public void setProtocol(String str);
    
    @Binding(detypedName="socket-binding")
    public String getSocketBinding();
    public void setSocketBinding(String str);
    
    @Binding(detypedName="max-socket-threads")    
    public Integer getMaxSocketThreads();
    public void setMaxSocketThreads(Integer i);
    
    @Binding(detypedName="authentication-security-domain")    
    public String getAuthenticationDomain();
    public void setAuthenticationDomain(String str);
    
    @Binding(detypedName="authentication-max-sessions-allowed")    
    public Integer getMaxSessionsAllowed();
    public void setMaxSessionsAllowed(Integer i); 

    @Binding(detypedName="authentication-sessions-expiration-timelimit")    
    public Integer getSessionExpirationTime();
    public void setSessionExpirationTime(Integer i);   
    
    @Binding(detypedName= "ssl-enable")    
    public Boolean isSslEnabled();
    public void setSslEnabled(Boolean b); 
    
    @Binding(detypedName="ssl-mode")    
    public String getSslMode();
    public void setSslMode(String str);    
    
    @Binding(detypedName="ssl-authentication-mode")    
    public String getSslAuthMode();
    public void setSslAuthMode(String str);    
    
    @Binding(detypedName="ssl-ssl-protocol")    
    public String getSslProtocol();
    public void setSslProtocol(String str);     
    
    @Binding(detypedName="ssl-keymanagement-algorithm")    
    public String getKeyManagementAlgorithm();
    public void setKeyManagementAlgorithm(String str);    
    
    @Binding(detypedName="enabled-cipher-suites")
    public String getEnabledCipherSuites();
    public void setEnabledCipherSuites(String str);    
    
    @Binding(detypedName="keystore-name")    
    public String getKeystoreName();
    public void setKeystoreName(String str);  
    
    @Binding(detypedName="keystore-password")    
    public String getKeystorePassword();
    public void setKeystorePassword(String str);  
    
    @Binding(detypedName="keystore-type")
    public String getKeystoreType();
    public void setKeystoreType(String str);   
    
    @Binding(detypedName= "truststore-check-expired")    
    public Boolean isTruststoreCheckExpired();
    public void setTruststoreCheckExpired(Boolean b); 
    
    @Binding(detypedName="truststore-name")    
    public String getTruststoreName();
    public void setTruststoreName(String str);  
    
    @Binding(detypedName="truststore-password")    
    public String getTruststorePassword();
    public void setTruststorePassword(String str);     
}
