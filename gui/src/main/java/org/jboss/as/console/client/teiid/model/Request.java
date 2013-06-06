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
