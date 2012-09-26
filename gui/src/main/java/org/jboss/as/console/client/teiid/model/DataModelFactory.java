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

import org.jboss.as.console.spi.BeanFactoryExtension;

import com.google.gwt.autobean.shared.AutoBean;

@SuppressWarnings("deprecation")
@BeanFactoryExtension
public interface DataModelFactory {
	AutoBean<SubsystemConfiguration> getSubSystemModel();
	AutoBean<Translator> getTranslatorModel();
	AutoBean<Transport> getTransportModel();
	AutoBean<VDB> getVDB();
	AutoBean<Model> getModel();
	AutoBean<SourceMapping> getSourceMapping();
	AutoBean<ValidityError> getValidityError();
	AutoBean<KeyValuePair> getProperties();
	AutoBean<ImportedVDB> getImportedVDB();
	AutoBean<VDBTranslator> getVDBTranslator();
	AutoBean<DataPolicy> getDataPolicy();
	AutoBean<DataPermission> getDataPermission();
	AutoBean<Request> getRequest();
	AutoBean<Session> getSession();
	AutoBean<MaterializedView> getMaterializedView();
	AutoBean<CacheStatistics> getCacheStatistics();
}
