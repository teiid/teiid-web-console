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
package org.jboss.as.console.client.teiid;

import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.layout.SimpleLayout;
import org.jboss.as.console.client.rbac.SecurityFramework;
import org.jboss.as.console.client.teiid.model.SubsystemConfiguration;
import org.jboss.as.console.client.v3.ResourceDescriptionRegistry;
import org.jboss.ballroom.client.rbac.SecurityContext;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SubsystemView extends SuspendableViewImpl implements SubsystemPresenter.MyView {
    private final ResourceDescriptionRegistry descriptionRegistry;
    private final SecurityFramework securityFramework;
    
    private SubsystemPresenter presenter; 
    private TeiidModelForm<SubsystemConfiguration> commonForm;
    private TeiidModelForm<SubsystemConfiguration> threadsForm;
    private TeiidModelForm<SubsystemConfiguration> bufferManagerForm;
    private TeiidModelForm<SubsystemConfiguration> preparedPlanForm;
    private TeiidModelForm<SubsystemConfiguration> resultsetCacheForm;
    private TeiidModelForm<SubsystemConfiguration> distributedCacheForm;
    
    
    @Inject
    public SubsystemView(ResourceDescriptionRegistry descriptionRegistry, SecurityFramework securityFramework) {
        this.descriptionRegistry = descriptionRegistry;
        this.securityFramework = securityFramework;
    }    
    
    @Override
    public void setPresenter(SubsystemPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setBean(SubsystemConfiguration bean) {
        this.commonForm.edit(bean);
        this.threadsForm.edit(bean);
        this.bufferManagerForm.edit(bean);
        this.preparedPlanForm.edit(bean);
        this.resultsetCacheForm.edit(bean);
        this.distributedCacheForm.edit(bean);
    }
    
    @Override
    public Widget createWidget() {
        SecurityContext securityContext = securityFramework.getSecurityContext(this.presenter.getProxy().getNameToken());
        
        // common
        CheckBoxItem allowEnvFunction = new CheckBoxItem("allowEnvFunction", "Allow ENV Function");
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
        NumberBoxItem processorBatchSize = new NumberBoxItem("processorBatchSize", "Processor Batch Size");
        NumberBoxItem connectorBatchSize = new NumberBoxItem("connectorBatchSize", "Connector Batch Size");
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
        TextBoxItem infinispanChannel = new TextBoxItem("infinispanChannel", "Infinispan Channel");
        
        // Resultset Cache
        TextBoxItem rscName = new TextBoxItem("rscName", "Infinispan Cache Name");
        CheckBoxItem rscEnable = new CheckBoxItem("rscEnable", "Enable");
        TextBoxItem rscContainerName = new TextBoxItem("rscContainerName", "Infinispan Cache Container Name");
        NumberBoxItem rscMaxStaleness = new NumberBoxItem("rscMaxStaleness", "Max Staleness", true);
        

        this.commonForm = new TeiidModelForm<SubsystemConfiguration>(
                SubsystemConfiguration.class, this.presenter, allowEnvFunction, maxAsyncThreadCount,
                maxRowsFetchSize, lobChunkSize, queryThreshold, maxSourceRows,
                throwExceptionOnMaxSourceRows, detectChangeEvents, queryTimeout);

        this.threadsForm = new TeiidModelForm<SubsystemConfiguration>(
                SubsystemConfiguration.class, this.presenter, asyncThreadPool, maxThreads,
                maxActivePlans, maxConcurrentThreads, timeSlice, workManager);

        this.bufferManagerForm = new TeiidModelForm<SubsystemConfiguration>(
                SubsystemConfiguration.class, this.presenter, useDisk, processorBatchSize,
                connectorBatchSize, maxProcessingSize, maxReserveSize,
                maxFileSize, maxBufferSize, maxOpenFiles, directMemorySize,
                useOffHeapMemory, objectStorageSize, inlineLobs);

        this.distributedCacheForm = new TeiidModelForm<SubsystemConfiguration>(
                SubsystemConfiguration.class, this.presenter, dcJGroupsStack, infinispanChannel);

        this.preparedPlanForm = new TeiidModelForm<SubsystemConfiguration>(
                SubsystemConfiguration.class, this.presenter, ppcEnable, ppcName,
                ppcContainerName);

        this.resultsetCacheForm = new TeiidModelForm<SubsystemConfiguration>(
                SubsystemConfiguration.class, this.presenter, rscName, rscEnable,
                rscContainerName, rscMaxStaleness);
        
        TabPanel tabs = new TabPanel();
        tabs.setStyleName("default-tabpanel");
        tabs.addStyleName("master_detail-detail");
        tabs.getElement().setAttribute("style", "margin-top:15px;");

        tabs.add(this.commonForm.asWidget(), "Common");
        tabs.add(this.threadsForm.asWidget(), "Threads");
        tabs.add(this.bufferManagerForm.asWidget(), "Buffer Manager");        
        tabs.add(this.preparedPlanForm.asWidget(), "Prepared Plan Cache");
        tabs.add(this.resultsetCacheForm.asWidget(), "Resultset Cache");
        tabs.add(this.distributedCacheForm.asWidget(), "Distributed Cache");

        tabs.selectTab(0);

        SimpleLayout layout = new SimpleLayout()
                .setTitle("Transactions")
                .setHeadline("Transaction Manager")
                .setDescription(new SafeHtmlBuilder().appendEscaped("Distributed query engine, "
                        + "that parses, plans and excutes user's SQL commands and "
                        + "provides results").toSafeHtml())
                .addContent("", tabs);

        return layout.build();        
    }
}
