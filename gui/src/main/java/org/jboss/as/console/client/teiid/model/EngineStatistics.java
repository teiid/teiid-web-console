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

public interface EngineStatistics {

	@Binding(detypedName="session-count")
	public Integer getSessionCount();
	public void setSessionCount(Integer value);
	
	@Binding(detypedName="total-memory-inuse-kb")
	public Long getTotalMemoryInUse();
	public void setTotalMemoryInUse(Long value);
	
	@Binding(detypedName="total-memory-inuse-active-plans-kb")
	public Long getTotalMemoryInUseByActivePlans();
	public void setTotalMemoryInUseByActivePlans(Long value);

	@Binding(detypedName="buffermgr-disk-write-count")
	public Long getBufferManagerDiskWriteCount();
	public void setBufferManagerDiskWriteCount(Long value);

	@Binding(detypedName="buffermgr-disk-read-count")
	public Long getBufferManagerDiskReadCount();
	public void setBufferManagerDiskReadCount(Long value);
	
	@Binding(detypedName="buffermgr-cache-write-count")
	public Long getBufferManagerCacheWriteCount();
	public void setBufferManagerCacheWriteCount(Long value);
	
	@Binding(detypedName="buffermgr-cache-read-count")
	public Long getBufferManagerCacheReadCount();
	public void setBufferManagerCacheReadCount(Long value);
	
	@Binding(detypedName="buffermgr-diskspace-used-mb")
	public Long getBufferManagerDiskUsed();
	public void setBufferManagerDiskUsed(Long value);

	@Binding(detypedName="active-plans-count")
	public Integer getActivePlansCount();
	public void setActivePlansCount(Integer value);

	@Binding(detypedName="waiting-plans-count")
	public Integer getWaitingPlansCount();
	public void setWaitingPlansCount(Integer value);
	
    @Binding(detypedName="max-waitplan-watermark")
    public Integer getMaxWaitPlanWatermark();
    public void setMaxWaitPlanWatermark(Integer value);   
}
