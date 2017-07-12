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
    
    @Binding(detypedName="input-buffer-size")    
    public Integer getInputBufferSize();
    public void setInputBufferSize(Integer i);
    
    @Binding(detypedName="output-buffer-size")    
    public Integer getOutputBufferSize();
    public void setOutputBufferSize(Integer i);
    
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
    
    @Binding(detypedName="ssl-enabled-cipher-suites")
    public String getEnabledCipherSuites();
    public void setEnabledCipherSuites(String str);   
    
    @Binding(detypedName="keystore-name")    
    public String getKeystoreName();
    public void setKeystoreName(String str);  
    
    @Binding(detypedName="keystore-key-password")    
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
