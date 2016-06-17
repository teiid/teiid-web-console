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

import org.jboss.as.console.client.shared.viewframework.NamedEntity;
import org.jboss.as.console.client.widgets.forms.Address;
import org.jboss.as.console.client.widgets.forms.Binding;
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * Model for a Transport
 */
@Address("/subsystem=teiid/transport={0}")
public interface Transport extends NamedEntity {
    @Override
    @Binding(detypedName="name", key=true)
    @FormItem(label="Name", required=true, localTabName = "", tabName="Common", order=1)
    public String getName();
    @Override
    public void setName(String str);

    @Binding(detypedName="protocol")
    @FormItem(label="Wire Protocol", required=true, formItemTypeForEdit="COMBO_BOX", formItemTypeForAdd="COMBO_BOX", acceptedValues= {"teiid", "pg"}, defaultValue="teiid", localTabName = "", tabName="Common")
    public String getProtocol();
    public void setProtocol(String str);
    
    @Binding(detypedName="socket-binding")
    @FormItem(label="Socket Binding Name (refers to port)", localTabName = "", tabName="Common")
    public String getSocketBinding();
    public void setSocketBinding(String str);
    
    @Binding(detypedName="max-socket-threads")
    @FormItem(label="Max Socket Threads", formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", localTabName = "", tabName="Common", defaultValue="0")
    public Integer getMaxSocketThreads();
    public void setMaxSocketThreads(Integer i);
    
    @Binding(detypedName="authentication-security-domain")
    @FormItem(label="Authentication Security Domain", localTabName = "", tabName="Common")
    public String getAuthenticationDomain();
    public void setAuthenticationDomain(String str);
    
    @Binding(detypedName="authentication-max-sessions-allowed")
    @FormItem(label="Max Sessions Allowed", formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", defaultValue="5000", localTabName = "", tabName="Common")
    public Integer getMaxSessionsAllowed();
    public void setMaxSessionsAllowed(Integer i); 

    @Binding(detypedName="authentication-sessions-expiration-timelimit")
    @FormItem(label="Session Expiration Time Limit", formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", defaultValue="0", localTabName = "", tabName="Common")
    public Integer getSessionExpirationTime();
    public void setSessionExpirationTime(Integer i);   
    
    @Binding(detypedName="ssl-mode")
    @FormItem(label="Mode", localTabName = "", tabName="SSL", formItemTypeForEdit="COMBO_BOX", formItemTypeForAdd="COMBO_BOX", acceptedValues= {"logIn", "disabled", "enabled"}, order=2)
    public String getSslMode();
    public void setSslMode(String str);    
    
    @Binding(detypedName="ssl-authentication-mode")
    @FormItem(label="Auth Mode", localTabName = "", tabName="SSL", formItemTypeForEdit="COMBO_BOX", formItemTypeForAdd="COMBO_BOX", acceptedValues= {"1-way", "2-way", "anonymous"}, order=3)
    public String getSslAuthMode();
    public void setSslAuthMode(String str);    
    
    @Binding(detypedName="ssl-ssl-protocol")
    @FormItem(label="Protocol", localTabName = "", tabName="SSL", order=4)
    public String getSslProtocol();
    public void setSslProtocol(String str);     
    
    @Binding(detypedName="ssl-keymanagement-algorithm")
    @FormItem(label="Key Management Algorithm", localTabName = "", tabName="SSL", order=5)
    public String getKeyManagementAlgorithm();
    public void setKeyManagementAlgorithm(String str);    
    
    @Binding(detypedName="ssl-enabled-cipher-suites")
    @FormItem(label="Enabled Cipher Suites", localTabName = "", tabName="SSL", order=6)
    public String getEnabledCipherSuites();
    public void setEnabledCipherSuites(String str);    
    
    @Binding(detypedName="keystore-name")
    @FormItem(label="Keystore Name", localTabName = "", tabName="SSL", order=7)
    public String getKeystoreName();
    public void setKeystoreName(String str);  
    
    @Binding(detypedName="keystore-key-password")
    @FormItem(label="Keystore Key Password", localTabName = "", tabName="SSL", order=8)
    public String getKeystoreKeyPassword();
    public void setKeystoreKeyPassword(String str); 
    
    @Binding(detypedName="keystore-password")
    @FormItem(label="Keystore Password", localTabName = "", tabName="SSL", order=9)
    public String getKeystorePassword();
    public void setKeystorePassword(String str);  
    
    @Binding(detypedName="keystore-type")
    @FormItem(label="Keystore Type", localTabName = "", tabName="SSL", order=10)
    public String getKeystoreType();
    public void setKeystoreType(String str);
    
    @Binding(detypedName="keystore-key-alias")
    @FormItem(label="Keystore Alias", localTabName = "", tabName="SSL", order=11)
    public String getKeystoreAlias();
    public void setKeystoreAlias(String str);    
    
    @Binding(detypedName="truststore-name")
    @FormItem(label="Truststore Name", localTabName = "", tabName="SSL", order=12)
    public String getTruststoreName();
    public void setTruststoreName(String str);  
    
    @Binding(detypedName="truststore-password")
    @FormItem(label="Truststore Password", localTabName = "", tabName="SSL", order=13)
    public String getTruststorePassword();
    public void setTruststorePassword(String str);     
}
