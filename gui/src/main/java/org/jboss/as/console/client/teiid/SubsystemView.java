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

import java.util.List;

import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.rbac.SecurityFramework;
import org.jboss.as.console.client.teiid.model.Authentication;
import org.jboss.as.console.client.teiid.model.SubsystemConfiguration;
import org.jboss.as.console.client.teiid.model.TeiidLogger;
import org.jboss.as.console.client.teiid.model.Translator;
import org.jboss.as.console.client.teiid.model.Transport;
import org.jboss.as.console.client.v3.ResourceDescriptionRegistry;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

public class SubsystemView extends SuspendableViewImpl implements SubsystemPresenter.MyView {
    private final ResourceDescriptionRegistry descriptionRegistry;
    private final SecurityFramework securityFramework;
    private ConfigurationEditor configurationEditor;
    private TransportEditor transportEditor;
    private AuditEditor auditEditor;
    private SubsystemPresenter presenter;
    private TranslatorEditor translatorEditor;
    private AuthenticationEditor authenticationEditor;
    
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
    public void setConfigurationBean(SubsystemConfiguration bean) {
        this.configurationEditor.setConfigurationBean(bean);
    }
    
    @Override
    public void setTranslators(List<Translator> translators) {
        this.translatorEditor.setTranslators(translators);
    }     
    
    @Override
	public void setAuthentication(Authentication bean) {
		this.authenticationEditor.setAuthentication(bean);
	}
    
    @Override
    public Widget createWidget() {

        DefaultTabLayoutPanel layout  = new DefaultTabLayoutPanel(40, Style.Unit.PX);
        layout.addStyleName("default-tabpanel");
        PagedView panel = new PagedView(true);

        this.configurationEditor = new ConfigurationEditor(this.presenter);
        this.auditEditor = new AuditEditor(this.presenter);
        this.transportEditor = new TransportEditor(this.presenter);
        this.translatorEditor = new TranslatorEditor(this.presenter);
        this.authenticationEditor = new AuthenticationEditor(this.presenter);

        panel.addPage("Query Engine", this.configurationEditor.asWidget());
        panel.addPage("Logging", this.auditEditor.asWidget());
        panel.addPage("Transports", this.transportEditor.asWidget());
        panel.addPage("Translators", this.translatorEditor.asWidget());
        panel.addPage("Authentication", this.authenticationEditor.asWidget());
        
        // default page
        panel.showPage(0);

        Widget panelWidget = panel.asWidget();
        layout.add(panelWidget, "Teiid");

        return layout;
    }

    @Override
    public void addLogger(String context) {
       this.auditEditor.addLogger(context);
    }

    @Override
    public void deleteLogger(String context) {
        this.auditEditor.deleteLogger(context);
    }

    @Override
    public void loggingStatus(String context, TeiidLogger logger) {
        this.auditEditor.loggingStatus(context, logger);
    }

    @Override
    public void logHandlerAdded(String handlerName, boolean added) {
        this.auditEditor.logHandlerAdded(handlerName, added);
    }

    @Override
    public void logHandlerRemoved(String handlerName, boolean removed) {
        this.auditEditor.logHandlerRemoved(handlerName, removed);
    }

    @Override
    public void loggerAdded(String context, boolean added) {
        this.auditEditor.loggerAdded(context, added);
    }

    @Override
    public void loggerRemoved(String context, boolean removed) {
        this.auditEditor.loggerRemoved(context, removed);
    }

    @Override
    public void setLogHandlerStatus(String context, String name, boolean dbAppender,
            boolean exists) {
        this.auditEditor.setLogHandlerStatus(context, name, dbAppender, exists);
    }

    @Override
    public void setTransports(List<Transport> transports) {
        this.transportEditor.setTransports(transports);
    }
   
}
