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
package org.jboss.as.console.client.teiid;

import org.jboss.as.console.client.layout.OneToOneLayout;
import org.jboss.as.console.client.teiid.model.SubsystemConfiguration;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ConfigurationEditor {
    
    private SubsystemPresenter presenter; 
    private TeiidModelForm<SubsystemConfiguration> commonForm;
    private TeiidModelForm<SubsystemConfiguration> threadsForm;
    private TeiidModelForm<SubsystemConfiguration> bufferManagerForm;
    private TeiidModelForm<SubsystemConfiguration> preparedPlanForm;
    private TeiidModelForm<SubsystemConfiguration> resultsetCacheForm;
    private TeiidModelForm<SubsystemConfiguration> distributedCacheForm;    
    private TeiidModelForm<SubsystemConfiguration> authenticationForm;

    public ConfigurationEditor(SubsystemPresenter presenter) {
        this.presenter = presenter;
    }

    public void setConfigurationBean(SubsystemConfiguration bean) {
        this.commonForm.edit(bean);
        this.threadsForm.edit(bean);
        this.bufferManagerForm.edit(bean);
        this.preparedPlanForm.edit(bean);
        this.resultsetCacheForm.edit(bean);
        this.distributedCacheForm.edit(bean);
        this.authenticationForm.edit(bean);
    }    
    
    public Widget asWidget() {
        // common
        CheckBoxItem allowEnvFunction = new CheckBoxItem("allowEnvFunction", "Allow ENV Function");
        CheckBoxItem dataRolesRequired = new CheckBoxItem("dataRolesRequired", "Data Roles Required");
        NumberBoxItem maxAsyncThreadCount = new NumberBoxItem("asyncThreadPoolCount", "Asynchronous Max Thread Count");
        NumberBoxItem maxRowsFetchSize = new NumberBoxItem("maxRowsFetchSize", "Max Rows Fetch Size");
        NumberBoxItem lobChunkSize = new NumberBoxItem("lobChunkSize", "Lob Chunk Size");
        NumberBoxItem queryThreshold = new NumberBoxItem("queryThreshold", "Query Threshhold");
        NumberBoxItem maxSourceRows = new NumberBoxItem("maxSourceRows", "Max Source Rows", true);
        CheckBoxItem throwExceptionOnMaxSourceRows = new CheckBoxItem("throwExceptionOnMaxSourceRows", "Throw Exception on Max Source Rows");
        CheckBoxItem detectChangeEvents = new CheckBoxItem("detectChangeEvents", "Detect Change Event");
        NumberBoxItem queryTimeout = new NumberBoxItem("queryTimeout", "Query Timeout");
        
        //threads
        NumberBoxItem asyncThreadPool = new NumberBoxItem("asyncThreadPool", "Asynchronous Thread Pool");
        NumberBoxItem maxThreads = new NumberBoxItem("maxThreads", "Max Threads");
        NumberBoxItem maxActivePlans = new NumberBoxItem("maxActivePlans", "Max Active Plans");
        NumberBoxItem maxConcurrentThreads = new NumberBoxItem("maxConcurrentThreads", "Max # Source Concurrent Threads");
        NumberBoxItem timeSlice = new NumberBoxItem("timeSlice", "Time Slice");
        TextBoxItem workManager = new TextBoxItem("workManager", "Work Manager");
        
        // Buffer Manager
        // subgroup Batch Sizes
        CheckBoxItem useDisk = new CheckBoxItem("useDisk", "Use Disk");   
        CheckBoxItem encryptFiles = new CheckBoxItem("encryptFiles", "Encrypt Files");   
        NumberBoxItem processorBatchSize = new NumberBoxItem("processorBatchSize", "Processor Batch Size");
        NumberBoxItem maxProcessingSize = new NumberBoxItem("maxProcessingSize", "Max Processing Size(KB)", true);
        NumberBoxItem maxReserveSize = new NumberBoxItem("maxReserveSize", "Max Reserved Memory(KB)", true);
        NumberBoxItem maxFileSize = new NumberBoxItem("maxFileSize", "Max Buffer File Size (MB)");
        NumberBoxItem maxBufferSize = new NumberBoxItem("maxBufferSize", "Max Storage Space(MB)");
        NumberBoxItem maxOpenFiles = new NumberBoxItem("maxOpenFiles", "Max Open Files");
        NumberBoxItem directMemorySize = new NumberBoxItem("directMemorySize", "Direct Memory Size(MB)", true);
        CheckBoxItem useOffHeapMemory = new CheckBoxItem("useOffHeapMemory", "Use Off Heap Memory");
        NumberBoxItem objectStorageSize = new NumberBoxItem("objectStorageSize", "Max Single Object Storage Size(bytes)");
        CheckBoxItem inlineLobs = new CheckBoxItem("inlineLobs", "Inline LOBs");

        // Prepared Plan Cache
        CheckBoxItem ppcEnable = new CheckBoxItem("ppcEnable", "Enable");
        TextBoxItem ppcName = new TextBoxItem("ppcName", "Infinispan Cache Name");
        TextBoxItem ppcContainerName = new TextBoxItem("ppcContainerName", "Infinispan Cache Container Name");
        
        // Distributed Cache
        TextBoxItem dcJGroupsStack = new TextBoxItem("dcJGroupsStack", "JGroups Stack");
        
        // Resultset Cache
        TextBoxItem rscName = new TextBoxItem("rscName", "Infinispan Cache Name");
        CheckBoxItem rscEnable = new CheckBoxItem("rscEnable", "Enable");
        TextBoxItem rscContainerName = new TextBoxItem("rscContainerName", "Infinispan Cache Container Name");
        NumberBoxItem rscMaxStaleness = new NumberBoxItem("rscMaxStaleness", "Max Staleness", true);
        
        //Authentication
        TextBoxItem securityDomain = new TextBoxItem("securityDomain","Security Domain");
		NumberBoxItem maxSessionsAllowed = new NumberBoxItem("maxSessionsAllowed", "Max # of Sessions Allowed");  
		NumberBoxItem sessionExpirationTimelimit = new NumberBoxItem("sessionExpirationTimelimit", "Session Expiration Timelimit",true);
		ComboBoxItem type = new ComboBoxItem("type", "Type");
		type.setValueMap(new String[] {"USERPASSWORD", "GSS"});
		type.setDefaultToFirstOption(true);
		CheckBoxItem trustAllLocal = new CheckBoxItem("trustAllLocal", "Trust All Local Connections");

        this.commonForm = new TeiidModelForm<SubsystemConfiguration>(
                SubsystemConfiguration.class, this.presenter, allowEnvFunction, dataRolesRequired, maxAsyncThreadCount,
                maxRowsFetchSize, lobChunkSize, queryThreshold, maxSourceRows,
                throwExceptionOnMaxSourceRows, detectChangeEvents, queryTimeout);

        this.threadsForm = new TeiidModelForm<SubsystemConfiguration>(
                SubsystemConfiguration.class, this.presenter, asyncThreadPool, maxThreads,
                maxActivePlans, maxConcurrentThreads, timeSlice, workManager);

        this.bufferManagerForm = new TeiidModelForm<SubsystemConfiguration>(
                SubsystemConfiguration.class, this.presenter, useDisk, encryptFiles, processorBatchSize,
                maxProcessingSize, maxReserveSize,
                maxFileSize, maxBufferSize, maxOpenFiles, directMemorySize,
                useOffHeapMemory, objectStorageSize, inlineLobs);

        this.distributedCacheForm = new TeiidModelForm<SubsystemConfiguration>(
                SubsystemConfiguration.class, this.presenter, dcJGroupsStack);

        this.preparedPlanForm = new TeiidModelForm<SubsystemConfiguration>(
                SubsystemConfiguration.class, this.presenter, ppcEnable, ppcName,
                ppcContainerName);

        this.resultsetCacheForm = new TeiidModelForm<SubsystemConfiguration>(
                SubsystemConfiguration.class, this.presenter, rscName, rscEnable,
                rscContainerName, rscMaxStaleness);
        
        this.authenticationForm = new TeiidModelForm<SubsystemConfiguration>(
                SubsystemConfiguration.class, this.presenter,securityDomain, maxSessionsAllowed,
				sessionExpirationTimelimit, type, trustAllLocal);
        
        HTML title = new HTML();
        title.setStyleName("content-header-label");
        title.setText("Query Engine");
        
        OneToOneLayout layoutBuilder = new OneToOneLayout()
                .setPlain(true)
                .setTitle("Query Engine")
                .setHeadlineWidget(title)
                .setDescription( "Distributed query engine, that parses, plans and "
                        + "excutes user's SQL commands and provides results")
                .addDetail("Common", this.commonForm.asWidget())
                .addDetail("Threads", this.threadsForm.asWidget())
                .addDetail("Buffer Manager", this.bufferManagerForm.asWidget())
                .addDetail("Prepared Plan Cache", this.preparedPlanForm.asWidget())
                .addDetail("Resultset Cache", this.resultsetCacheForm.asWidget())
                .addDetail("Distributed Cache", this.distributedCacheForm.asWidget())
                .addDetail("Authentication", this.authenticationForm.asWidget());
        return layoutBuilder.build();
    }
}
