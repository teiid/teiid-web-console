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
 * Model for a Engine
 */
@Address("/subsystem=teiid")
public interface SubsystemConfiguration {
	    
    @Binding(detypedName= "allow-env-function")    
    public Boolean isAllowEnvFunction();
    public void setAllowEnvFunction(Boolean allow);    
    
    @Binding(detypedName="async-thread-pool")
    public String getAsyncThreadPool();
    public void setAsyncThreadPool(String pool);
    
    @Binding(detypedName="max-threads")    
    public Integer getMaxThreads();
    public void setMaxThreads(Integer i);  
    
    @Binding(detypedName="max-active-plans")    
    public Integer getMaxActivePlans();
    public void setMaxActivePlans(Integer i);
    
    @Binding(detypedName="thread-count-for-source-concurrency")    
    public Integer getMaxConcurrentThreads();
    public void setMaxConcurrentThreads(Integer i);
    
    @Binding(detypedName="time-slice-in-millseconds")
    public Integer getTimeSlice();
    public void setTimeSlice(Integer i);    
    
    
    @Binding(detypedName="max-row-fetch-size")    
    public Integer getMaxRowsFetchSize();
    public void setMaxRowsFetchSize(Integer i);    
    
    @Binding(detypedName="lob-chunk-size-in-kb")    
    public Integer getLobChunkSize();
    public void setLobChunkSize(Integer i);
    
    @Binding(detypedName="query-threshold-in-seconds")    
    public Integer getQueryThreshold();
    public void setQueryThreshold(Integer i);
    
    @Binding(detypedName="max-source-rows-allowed")    
    public Integer getMaxSourceRows();
    public void setMaxSourceRows(Integer i);
    
    @Binding(detypedName= "exception-on-max-source-rows") 
    public Boolean isThrowExceptionOnMaxSourceRows();
    public void setThrowExceptionOnMaxSourceRows(Boolean allow);
    
    @Binding(detypedName= "detect-change-events")    
    public Boolean isDetectChangeEvents();
    public void setDetectChangeEvents(Boolean allow);    
    
    @Binding(detypedName="query-timeout")    
    public Long getQueryTimeout();
    public void setQueryTimeout(Long i);   
    
    @Binding(detypedName="workmanager")    
    public String getWorkManager();
    public void setWorkManager(String pool);    
    
    // buffer manager stuff
    @Binding(detypedName= "buffer-service-use-disk")    
    public Boolean isUseDisk();
    public void setUseDisk(Boolean allow); 
    
    @Binding(detypedName="buffer-service-processor-batch-size")    
    public Integer getProcessorBatchSize();
    public void setProcessorBatchSize(Integer i);
    
    @Binding(detypedName="buffer-service-connector-batch-size")
    public Integer getConnectorBatchSize();
    public void setConnectorBatchSize(Integer i);
    
    @Binding(detypedName="buffer-service-max-processing-kb")    
    public Integer getMaxProcessingSize();
    public void setMaxProcessingSize(Integer i);
    
    @Binding(detypedName="buffer-service-max-reserve-kb")
    public Integer getMaxReserveSize();
    public void setMaxReserveSize(Integer i);
    
    @Binding(detypedName="buffer-service-max-file-size")
    public Long getMaxFileSize();
    public void setMaxFileSize(Long i);
    
    @Binding(detypedName="buffer-service-max-buffer-space")
    public Long getMaxBufferSize();
    public void setMaxBufferSize(Long i);
    
    @Binding(detypedName="buffer-service-max-open-files")    
    public Integer getMaxOpenFiles();
    public void setMaxOpenFiles(Integer i);    
    
    @Binding(detypedName="buffer-service-memory-buffer-space")    
    public Integer getDirectMemorySize();
    public void setDirectMemorySize(Integer i);    
    
    @Binding(detypedName= "buffer-service-memory-buffer-off-heap")    
    public Boolean isUseOffHeapMemory();
    public void setUseOffHeapMemory(Boolean allow); 
    
    @Binding(detypedName="buffer-service-max-storage-object-size")
    public Integer getObjectStorageSize();
    public void setObjectStorageSize(Integer i);    
    
    @Binding(detypedName= "buffer-service-inline-lobs")
    public Boolean isInlineLobs();
    public void setInlineLobs(Boolean allow);   
    
    // cache settings
    @Binding(detypedName= "preparedplan-cache-enable")
    public Boolean isPpcEnable();
    public void setPpcEnable(Boolean flag); 
    
    @Binding(detypedName="preparedplan-cache-name")    
    public String getPpcName();
    public void setPpcName(String str);    
    
    @Binding(detypedName="preparedplan-cache-infinispan-container")
    public String getPpcContainerName();
    public void setPpcContainerName(String str);
    
    @Binding(detypedName="distributed-cache-jgroups-stack")    
    public String getDcJGroupsStack();
    public void setDcJGroupsStack(String str);    
    
    @Binding(detypedName="distributed-cache-channel")    
    public String getInfinispanChannel();
    public void setInfinispanChannel(String str);     
    
    @Binding(detypedName= "resultset-cache-enable")
    public Boolean isRscEnable();
    public void setRscEnable(Boolean flag); 
    
    @Binding(detypedName="resultset-cache-name")
    public String getRscName();
    public void setRscName(String str);    
    
    @Binding(detypedName="resultset-cache-infinispan-container")
    public String getRscContainerName();
    public void setRscContainerName(String str);   
    
    @Binding(detypedName="resultset-cache-max-staleness")
    public Integer getRscMaxStaleness();
    public void setRscMaxStaleness(Integer str);     
}
