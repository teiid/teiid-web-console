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

public interface Request {
	@Binding(detypedName="execution-id", key = true)
	public String getExecutionId();
    public void setExecutionId(String id);
    
    @Binding(detypedName="session-id")
    public String getSessionId();
    public void setSessionId(String id);

    @Binding(detypedName="command")
    public String getCommand();
    public void setCommand(String cmd);

    @Binding(detypedName="start-time")
    public Long getStartTime();
    public void setStartTime(Long time);

    @Binding(detypedName="transaction-id")
    public String getTransactionId();
    public void setTransactionId(String id);
    
    @Binding(detypedName="source-request")
    public Boolean isSourceRequest();
    public void setSourceRequest(Boolean flag);
    
    @Binding(detypedName="node-id")
    public Integer getNodeId();
    public void setNodeId(Integer id);

    @Binding(detypedName="processing-state")
	public String getState();
	public void setState(String state);

	@Binding(detypedName="thread-state")
	public String getThreadState();
	public void setThreadState(String state);
}
