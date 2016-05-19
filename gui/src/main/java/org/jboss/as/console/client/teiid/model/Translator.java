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
 * Model for a Translator
 */
@Address("/subsystem=teiid/translator={0}")
public interface Translator {
    @Binding(detypedName="name", key=true)
    @FormItem(label="Name",required=true)
    public String getName();
    public void setName(String name);
    
    @Binding(detypedName="module")
    @FormItem(label="Module Name")
    public String getModuleName();
    public void setModuleName(String name);  
    
    @Binding(detypedName="slot")
    @FormItem(label="Slot")
    public String getSlot();
    public void setSlot(String slot);  
}
