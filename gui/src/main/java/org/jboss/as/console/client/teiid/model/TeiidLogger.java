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

import java.util.List;

import org.jboss.as.console.client.widgets.forms.Binding;

public interface TeiidLogger {
    @Binding(detypedName="category", key=true)
    public String getName();
    public void setName(String value);

    @Binding(detypedName="level")
    String getLevel();
    public void setLevel(String value);

    @Binding(detypedName="use-parent-handlers")
    public Boolean isUseParentHandlers();
    public void setUseParentHandlers(Boolean value);
    
    @Binding(detypedName="handlers", listType="java.lang.String")
    public List<String> getHandlers();
    public void setHandlers(List<String> value);
}
