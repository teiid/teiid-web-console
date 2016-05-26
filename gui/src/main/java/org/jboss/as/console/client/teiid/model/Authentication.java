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
import org.jboss.as.console.client.widgets.forms.FormItem;

/**
 * Model for a Authentication
 */
@Address("/subsystem=teiid")
public interface Authentication {
    
    @Binding(detypedName="authentication-security-domain") 
    public String getSecurityDomain();
    public void setSecurityDomain(String str);
    
    @Binding(detypedName="authentication-max-sessions-allowed")    
    public Integer getMaxSessionsAllowed();
    public void setMaxSessionsAllowed(Integer i);
    
//   @Binding(detypedName="authentication-sessions-expiration-timelimit")    
//    public Integer getSessionsExpirationTimelimit();
//    public void setSessionsExpirationTimelimit(Integer i);   
    @Binding(detypedName="authentication-sessions-expiration-timelimit")    
    public Integer getSessionExpirationTimelimit();
    public void setSessionExpirationTimelimit(Integer i);   

    
    @Binding(detypedName="authentication-type")    
    public String getType();
    public void setType(String str);   
    
}
