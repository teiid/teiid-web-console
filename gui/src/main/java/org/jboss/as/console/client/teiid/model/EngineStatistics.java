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
}
