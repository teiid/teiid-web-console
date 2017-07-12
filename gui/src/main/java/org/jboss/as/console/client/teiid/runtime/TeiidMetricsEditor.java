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
package org.jboss.as.console.client.teiid.runtime;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.layout.OneToOneLayout;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.Sampler;
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.as.console.client.shared.runtime.charts.NumberColumn;
import org.jboss.as.console.client.shared.runtime.plain.PlainColumnView;
import org.jboss.as.console.client.teiid.model.EngineStatistics;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("nls")
public class TeiidMetricsEditor {
	private VDBPresenter presenter;
	private Sampler sampler;
	
    public void setPresenter(VDBPresenter presenter) {
        this.presenter = presenter;
    }

	public Widget createWidget() {
        final ToolStrip toolStrip = new ToolStrip();
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.getEngineStatistics();
            }
        }));
        
        NumberColumn sessionCount = new NumberColumn("session-count","Session Count");
        NumberColumn memoryInuse = new NumberColumn("total-memory-inuse-kb","Memory in Use(KB)");
        NumberColumn memoryInActivePlans = new NumberColumn("total-memory-inuse-active-plans-kb","Memory Used By Active Plans(KB)");
        NumberColumn diskReadCount = new NumberColumn("buffermgr-disk-read-count","Buffer Disk Reads");
        NumberColumn diskWriteCount = new NumberColumn("buffermgr-disk-write-count","Buffer Disk Writes");
        NumberColumn cacheReadCount = new NumberColumn("buffermgr-cache-read-count","Buffer Cache Reads");
        NumberColumn cacheWriteCount = new NumberColumn("buffermgr-cache-write-count","Buffer Cache Writes");
        NumberColumn spaceUsed = new NumberColumn("buffermgr-diskspace-used-mb","Disk Space In Use(MB)");
        NumberColumn activePlanCount = new NumberColumn("active-plans-count","Active Plan Count");
        NumberColumn waitingPlanCount = new NumberColumn("waiting-plans-count","Waiting Plan Count");
        NumberColumn maxWaitPlanTime = new NumberColumn("max-waitplan-watermark","Maximum Plans Waiting Water Mark");

        Column[] cols = new Column[] {
        		sessionCount,
        		memoryInuse,
        		memoryInActivePlans,
        		diskReadCount,
        		diskWriteCount,
        		cacheReadCount,
        		cacheWriteCount,
        		spaceUsed,
        		activePlanCount,
        		waitingPlanCount,
        		maxWaitPlanTime
        };

        sampler = new PlainColumnView("", null)
                .setColumns(cols)
                .setWidth(100, Style.Unit.PCT);		
		
        HTML title = new HTML();
        title.setStyleName("content-header-label");
        title.setText("Metrics");
        
        OneToOneLayout layoutBuilder = new OneToOneLayout()
                .setPlain(true)
                .setTitle("Metrics")
                .setHeadlineWidget(title)
                .setDescription( "Teiid subsytem metrics")
                .addDetail("Common", this.sampler.asWidget());
        return layoutBuilder.build();
	}

	public void setEngineStatistics(EngineStatistics stats) {
		Metric metric = new Metric(stats.getSessionCount(),
				stats.getTotalMemoryInUse(),
				stats.getTotalMemoryInUseByActivePlans(),
				stats.getBufferManagerDiskReadCount(),
				stats.getBufferManagerDiskWriteCount(),
				stats.getBufferManagerCacheReadCount(),
				stats.getBufferManagerCacheWriteCount(),
				stats.getBufferManagerDiskUsed(),
				stats.getActivePlansCount(),
				stats.getWaitingPlansCount(),
				stats.getMaxWaitPlanWatermark());
        sampler.addSample(metric);
	}
}
