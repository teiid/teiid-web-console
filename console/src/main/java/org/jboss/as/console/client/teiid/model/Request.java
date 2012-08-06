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
