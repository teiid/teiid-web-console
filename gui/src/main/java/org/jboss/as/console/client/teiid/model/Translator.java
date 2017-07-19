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
