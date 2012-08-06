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
 *
 */
@Address("/subsystem=teiid")
public interface SubsystemConfiguration {
	
    @Binding(detypedName="async-thread-pool")
    @FormItem(label="Asynchronous Thread Pool", required=true, tabName="subsys_teiid_common")
    public String getAsyncThreadPool();
    public void setAsyncThreadPool(String pool);
    
    @Binding(detypedName= "allow-env-function")
    @FormItem(label="Allow ENV Function", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX",tabName="subsys_teiid_common")
    public Boolean isAllowEnvFunction();
    public void setAllowEnvFunction(Boolean allow);    
    
    @Binding(detypedName="max-threads")
    @FormItem(label="Max Threads", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_common")
    public Integer getMaxThreads();
    public void setMaxThreads(Integer i);  
    
    @Binding(detypedName="max-active-plans")
    @FormItem(label="Max Active Plans", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_common")
    public Integer getMaxActivePlans();
    public void setMaxActivePlans(Integer i);

    
    @Binding(detypedName="thread-count-for-source-concurrency")
    @FormItem(label="Max # Source Concurrent Threads", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_common")
    public Integer getMaxConcurrentThreads();
    public void setMaxConcurrentThreads(Integer i);
    
    @Binding(detypedName="time-slice-in-millseconds")
    @FormItem(label="Time Slice", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_common")
    public Integer getTimeSlice();
    public void setTimeSlice(Integer i);    
    
    
    @Binding(detypedName="max-row-fetch-size")
    @FormItem(label="Max Rows Fetch Size", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_common")
    public Integer getMaxRowsFetchSize();
    public void setMaxRowsFetchSize(Integer i);    
    
    @Binding(detypedName="lob-chunk-size-in-kb")
    @FormItem(label="Lob Chunk Size", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_common")
    public Integer getLobChunkSize();
    public void setLobChunkSize(Integer i);
    
    @Binding(detypedName="query-threshold-in-seconds")
    @FormItem(label="Query Threshhold", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_common")
    public Integer getQueryThreshold();
    public void setQueryThreshold(Integer i);
    
    @Binding(detypedName="max-source-rows-allowed")
    @FormItem(label="Max Source Rows", required=false, formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE", formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE", tabName="subsys_teiid_common")
    public Integer getMaxSourceRows();
    public void setMaxSourceRows(Integer i);
    
    @Binding(detypedName= "exception-on-max-source-rows")
    @FormItem(label="Throw Exception on Max Source Rows", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX", tabName="subsys_teiid_common")
    public Boolean isThrowExceptionOnMaxSourceRows();
    public void setThrowExceptionOnMaxSourceRows(Boolean allow);
    
    @Binding(detypedName= "detect-change-events")
    @FormItem(label="Detect Change Event", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX", tabName="subsys_teiid_common")
    public Boolean isDetectChangeEvents();
    public void setDetectChangeEvents(Boolean allow);    
    
    @Binding(detypedName="query-timeout")
    @FormItem(label="Query Timeout", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_common")
    public Long getQueryTimeout();
    public void setQueryTimeout(Long i);   
    
    @Binding(detypedName="workmanager")
    @FormItem(label="Work Manager", required=false, tabName="subsys_teiid_common")
    public String getWorkManager();
    public void setWorkManager(String pool);    
    
    // buffer manager stuff
    @Binding(detypedName= "buffer-service-use-disk")
    @FormItem(label="Use Disk", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX", tabName="subsys_teiid_buffermanager")
    public Boolean isUseDisk();
    public void setUseDisk(Boolean allow); 
    
    @Binding(detypedName="buffer-service-processor-batch-size")
    @FormItem(label="Processor Batch Size", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_buffermanager")
    public Integer getProcessorBatchSize();
    public void setProcessorBatchSize(Integer i);
    
    @Binding(detypedName="buffer-service-connector-batch-size")
    @FormItem(label="Connector Batch Size", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_buffermanager")
    public Integer getConnectorBatchSize();
    public void setConnectorBatchSize(Integer i);
    
    @Binding(detypedName="buffer-service-max-processing-kb")
    @FormItem(label="Max Processing Size(KB)", required=false, formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE", formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE", tabName="subsys_teiid_buffermanager")
    public Integer getMaxProcessingSize();
    public void setMaxProcessingSize(Integer i);
    
    @Binding(detypedName="buffer-service-max-reserve-kb")
    @FormItem(label="Max Reserve Size(KB)", required=false, formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE", formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE", tabName="subsys_teiid_buffermanager")
    public Integer getMaxReserveSize();
    public void setMaxReserveSize(Integer i);
    
    @Binding(detypedName="buffer-service-max-file-size")
    @FormItem(label="Max File Size", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_buffermanager")
    public Long getMaxFileSize();
    public void setMaxFileSize(Long i);
    
    @Binding(detypedName="buffer-service-max-buffer-space")
    @FormItem(label="Max Buffer Size", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_buffermanager")
    public Long getMaxBufferSize();
    public void setMaxBufferSize(Long i);
    
    @Binding(detypedName="buffer-service-max-open-files")
    @FormItem(label="Max Reserve Size(KB)", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_buffermanager")
    public Integer getMaxOpenFiles();
    public void setMaxOpenFiles(Integer i);    
    
    @Binding(detypedName="buffer-service-memory-buffer-space")
    @FormItem(label="Direct Memory Size", required=false, formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE", formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE", tabName="subsys_teiid_buffermanager")
    public Integer getDirectMemorySize();
    public void setDirectMemorySize(Integer i);    
    
    @Binding(detypedName= "buffer-service-memory-buffer-off-heap")
    @FormItem(label="Use Off Heap Memory", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX", tabName="subsys_teiid_buffermanager")
    public Boolean isUseOffHeapMemory();
    public void setUseOffHeapMemory(Boolean allow); 
    
    @Binding(detypedName="buffer-service-max-storage-object-size")
    @FormItem(label="Max Object Storage Size", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_buffermanager")
    public Integer getObjectStorageSize();
    public void setObjectStorageSize(Integer i);    
    
    @Binding(detypedName= "buffer-service-inline-lobs")
    @FormItem(label="Inline LOBs", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX", tabName="subsys_teiid_buffermanager")
    public Boolean isInlineLobs();
    public void setInlineLobs(Boolean allow);   
    
    // cache settings
    @Binding(detypedName="preparedplan-cache-max-entries")
    @FormItem(label="Max Entries", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_cache", subgroup="subsys_teiid_pp_cache")
    public Integer getPPCacheMaxEntries();
    public void setPPCacheMaxEntries(Integer i);     
    
    @Binding(detypedName="preparedplan-cache-max-age-in-seconds")
    @FormItem(label="Max Age(secs)", required=false, formItemTypeForEdit="NUMBER_BOX", formItemTypeForAdd="NUMBER_BOX", tabName="subsys_teiid_cache", subgroup="subsys_teiid_pp_cache")
    public Integer getPPCacheMaxAge();
    public void setPPCacheMaxAge(Integer i);  
    
    @Binding(detypedName="distributed-cache-jgroups-stack")
    @FormItem(label="JGroups Stack", required=false, tabName="subsys_teiid_cache", subgroup="subsys_teiid_ds_cache")
    public String getDCJGroupsStack();
    public void setDCJGroupsStack(String str);    
    
    @Binding(detypedName="distributed-cache-channel")
    @FormItem(label="Infinispan Channel", required=false, tabName="subsys_teiid_cache", subgroup="subsys_teiid_ds_cache")
    public String getInfinispanChannel();
    public void setInfinispanChannel(String str);     
    
    @Binding(detypedName= "resultset-cache-enable")
    @FormItem(label="Enable", required=false, formItemTypeForEdit="CHECK_BOX", formItemTypeForAdd="CHECK_BOX", tabName="subsys_teiid_cache", subgroup="subsys_teiid_rs_cache")
    public Boolean isRSCEnable();
    public void setRSCEnable(Boolean flag); 
    
    @Binding(detypedName="resultset-cache-name")
    @FormItem(label="Infinispan Cache Name", required=false, tabName="subsys_teiid_cache", subgroup="subsys_teiid_rs_cache")
    public String getRSCName();
    public void setRSCName(String str);    
    
    @Binding(detypedName="resultset-cache-container-name")
    @FormItem(label="Infinispan Cache Container Name", required=false, tabName="subsys_teiid_cache", subgroup="subsys_teiid_rs_cache")
    public String getRSCContainerName();
    public void setRSCContainerName(String str);   
    
    @Binding(detypedName="resultset-cache-max-staleness")
    @FormItem(label="Max Staleness", required=false, formItemTypeForEdit="NUMBER_BOX_ALLOW_NEGATIVE", formItemTypeForAdd="NUMBER_BOX_ALLOW_NEGATIVE", tabName="subsys_teiid_cache", subgroup="subsys_teiid_rs_cache")
    public Integer getRSCMaxStaleness();
    public void setRSCMaxStaleness(Integer str);     
    
    // modules
//    @Binding(detypedName="policy-decider-module")
//    @FormItem(label="Policy Decider Module", required=false, tabName="subsys_teiid_extensions")
//    public String getPolicyDeciderModules();
//    public void setPolicyDeciderModule(String s); 
//    
//    @Binding(detypedName="authorization-validator-module")
//    @FormItem(label="Authorization Validator Module", required=false, tabName="subsys_teiid_extensions")
//    public String getAuthValidatorModule();
//    public void setAuthValidatorModule(String s); 
//    
//    @Binding(detypedName="metadata-repository-module")
//    @FormItem(label="Metadata Repository Module", required=false, tabName="subsys_teiid_extensions")
//    public String getMetadaRepositoryModule();
//    public void setMetadaRepositoryModule(String s); 
    
}
