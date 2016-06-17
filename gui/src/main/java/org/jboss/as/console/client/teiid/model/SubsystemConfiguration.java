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
 * Model for a Engine
 * the deprecated "localLabel" and "localTabName" with empty strings are required to workaround the i18n issues that are not
 * fixed.
 */
@Address("/subsystem=teiid")
public interface SubsystemConfiguration {
	    
    @Binding(detypedName= "allow-env-function")
    @FormItem(label="Allow ENV Function", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX",localTabName = "", tabName="Common")
    public Boolean isAllowEnvFunction();
    public void setAllowEnvFunction(Boolean allow);    
    
    @Binding(detypedName="async-thread-pool")
    @FormItem(label="Asynchronous Thread Pool", required=true, localTabName = "", tabName="Threads")
    public String getAsyncThreadPool();
    public void setAsyncThreadPool(String pool);
    
    @Binding(detypedName="max-threads")
    @FormItem(label="Max Threads", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", localTabName = "", tabName="Threads")
    public Integer getMaxThreads();
    public void setMaxThreads(Integer i);  
    
    @Binding(detypedName="max-active-plans")
    @FormItem(label="Max Active Plans", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", localTabName = "", tabName="Threads")
    public Integer getMaxActivePlans();
    public void setMaxActivePlans(Integer i);
    
    @Binding(detypedName="thread-count-for-source-concurrency")
    @FormItem(label="Max # Source Concurrent Threads", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", localTabName = "", tabName="Threads")
    public Integer getMaxConcurrentThreads();
    public void setMaxConcurrentThreads(Integer i);
    
    @Binding(detypedName="time-slice-in-milliseconds")
    @FormItem(label="Time Slice", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", localTabName = "", tabName="Threads")
    public Integer getTimeSlice();
    public void setTimeSlice(Integer i);    
    
    
    @Binding(detypedName="max-row-fetch-size")
    @FormItem(label="Max Rows Fetch Size", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", localTabName = "", tabName="Common")
    public Integer getMaxRowsFetchSize();
    public void setMaxRowsFetchSize(Integer i);    
    
    @Binding(detypedName="lob-chunk-size-in-kb")
    @FormItem(label="Lob Chunk Size", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", localTabName = "", tabName="Common")
    public Integer getLobChunkSize();
    public void setLobChunkSize(Integer i);
    
    @Binding(detypedName="query-threshold-in-seconds")
    @FormItem(label="Query Threshhold", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", localTabName = "", tabName="Common")
    public Integer getQueryThreshold();
    public void setQueryThreshold(Integer i);
    
    @Binding(detypedName="max-source-rows-allowed")
    @FormItem(label="Max Source Rows", required=false, formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE", formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE", localTabName = "", tabName="Common")
    public Integer getMaxSourceRows();
    public void setMaxSourceRows(Integer i);
    
    @Binding(detypedName= "exception-on-max-source-rows")
    @FormItem(label="Throw Exception on Max Source Rows", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX", localTabName = "", tabName="Common")
    public Boolean isThrowExceptionOnMaxSourceRows();
    public void setThrowExceptionOnMaxSourceRows(Boolean allow);
    
    @Binding(detypedName= "detect-change-events")
    @FormItem(label="Detect Change Event", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX", localTabName = "", tabName="Common")
    public Boolean isDetectChangeEvents();
    public void setDetectChangeEvents(Boolean allow);    
    
    @Binding(detypedName="query-timeout")
    @FormItem(label="Query Timeout", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", localTabName = "", tabName="Common")
    public Long getQueryTimeout();
    public void setQueryTimeout(Long i);   
    
    @Binding(detypedName="workmanager")
    @FormItem(label="Work Manager", required=false, localTabName = "", tabName="Threads")
    public String getWorkManager();
    public void setWorkManager(String pool);    
    
    // buffer manager stuff
    @Binding(detypedName= "buffer-service-use-disk")
    @FormItem(label="Use Disk", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX", localTabName = "", tabName="BufferManager")
    public Boolean isUseDisk();
    public void setUseDisk(Boolean allow); 
    
    @Binding(detypedName="buffer-service-processor-batch-size")
    @FormItem(label="Processor Batch Size", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", localTabName = "", tabName="BufferManager", subgroup="Batch Sizes")
    public Integer getProcessorBatchSize();
    public void setProcessorBatchSize(Integer i);
    
    @Binding(detypedName="buffer-service-max-processing-kb")
    @FormItem(label="Max Processing Size(KB)", required=false, formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE", formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE", localTabName = "", tabName="BufferManager")
    public Integer getMaxProcessingSize();
    public void setMaxProcessingSize(Integer i);
    
    @Binding(detypedName="buffer-service-max-reserve-kb")
    @FormItem(label="Max Reserved Memory(KB)", required=false, formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE", formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE", localTabName = "", tabName="BufferManager")
    public Integer getMaxReserveSize();
    public void setMaxReserveSize(Integer i);
    
    @Binding(detypedName="buffer-service-max-file-size")
    @FormItem(label="Max Buffer File Size (MB)", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", localTabName = "", tabName="BufferManager")
    public Long getMaxFileSize();
    public void setMaxFileSize(Long i);
    
    @Binding(detypedName="buffer-service-max-buffer-space")
    @FormItem(label="Max Storage Space(MB)", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", localTabName = "", tabName="BufferManager")
    public Long getMaxBufferSize();
    public void setMaxBufferSize(Long i);
    
    @Binding(detypedName="buffer-service-max-open-files")
    @FormItem(label="Max Open Files", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", localTabName = "", tabName="BufferManager")
    public Integer getMaxOpenFiles();
    public void setMaxOpenFiles(Integer i);    
    
    @Binding(detypedName="buffer-service-memory-buffer-space")
    @FormItem(label="Direct Memory Size(MB)", required=false, formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE", formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE", localTabName = "", tabName="BufferManager")
    public Integer getDirectMemorySize();
    public void setDirectMemorySize(Integer i);    
    
    @Binding(detypedName= "buffer-service-memory-buffer-off-heap")
    @FormItem(label="Use Off Heap Memory", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX", localTabName = "", tabName="BufferManager")
    public Boolean isUseOffHeapMemory();
    public void setUseOffHeapMemory(Boolean allow); 
    
    @Binding(detypedName="buffer-service-max-storage-object-size")
    @FormItem(label="Max Single Object Storage Size(bytes)", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", localTabName = "", tabName="BufferManager")
    public Integer getObjectStorageSize();
    public void setObjectStorageSize(Integer i);    
    
    @Binding(detypedName= "buffer-service-inline-lobs")
    @FormItem(label="Inline LOBs", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX", localTabName = "", tabName="BufferManager")
    public Boolean isInlineLobs();
    public void setInlineLobs(Boolean allow);   
    
    // cache settings
    @Binding(detypedName= "preparedplan-cache-enable")
    @FormItem(label="Enable", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX", localTabName = "", tabName="Prepared Plan Cache")
    public Boolean isPpcEnable();
    public void setPpcEnable(Boolean flag); 
    
    @Binding(detypedName="preparedplan-cache-name")
    @FormItem(label="Infinispan Cache Name", required=false, localTabName = "", tabName="Prepared Plan Cache")
    public String getPpcName();
    public void setPpcName(String str);    
    
    @Binding(detypedName="preparedplan-cache-infinispan-container")
    @FormItem(label="Infinispan Cache Container Name", required=false, localTabName = "", tabName="Prepared Plan Cache")
    public String getPpcContainerName();
    public void setPpcContainerName(String str);
    
    @Binding(detypedName="distributed-cache-jgroups-stack")
    @FormItem(label="JGroups Stack", required=false, localTabName = "", tabName="Distributed Cache")
    public String getDcJGroupsStack();
    public void setDcJGroupsStack(String str);    
    
    @Binding(detypedName= "resultset-cache-enable")
    @FormItem(label="Enable", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX", localTabName = "", tabName="Resultset Cache")
    public Boolean isRscEnable();
    public void setRscEnable(Boolean flag); 
    
    @Binding(detypedName="resultset-cache-name")
    @FormItem(label="Infinispan Cache Name", required=false, localTabName = "", tabName="Resultset Cache")
    public String getRscName();
    public void setRscName(String str);    
    
    @Binding(detypedName="resultset-cache-infinispan-container")
    @FormItem(label="Infinispan Cache Container Name", required=false, localTabName = "", tabName="Resultset Cache")
    public String getRscContainerName();
    public void setRscContainerName(String str);   
    
    @Binding(detypedName="resultset-cache-max-staleness")
    @FormItem(label="Max Staleness", required=false, formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE", formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE", localTabName = "", tabName="Resultset Cache")
    public Integer getRscMaxStaleness();
    public void setRscMaxStaleness(Integer str);     
}
