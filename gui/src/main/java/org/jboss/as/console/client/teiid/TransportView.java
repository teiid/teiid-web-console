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

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.layout.MultipleToOneLayout;
import org.jboss.as.console.client.rbac.SecurityFramework;
import org.jboss.as.console.client.teiid.model.Translator;
import org.jboss.as.console.client.teiid.model.Transport;
import org.jboss.as.console.client.v3.ResourceDescriptionRegistry;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.FormItem;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;
import org.jboss.ballroom.client.rbac.SecurityContext;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.dispatch.DispatchAsync;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;

public class TransportView extends SuspendableViewImpl implements TransportPresenter.MyView {
    
    private final ResourceDescriptionRegistry descriptionRegistry;
    private final SecurityFramework securityFramework;
    private DefaultCellTable<Transport> table;
    private ListDataProvider<Transport> dataProvider;
    
    private TeiidModelForm<Transport> formCommon;
    private TeiidModelForm<Transport> formSSL;
    private TransportPresenter presenter;
    
    @Inject
    public TransportView(ResourceDescriptionRegistry descriptionRegistry, SecurityFramework securityFramework) {
        this.descriptionRegistry = descriptionRegistry;
        this.securityFramework = securityFramework;
    } 

    @Override
    public Widget createWidget() {
        SecurityContext securityContext = securityFramework.getSecurityContext(this.presenter.getProxy().getNameToken());

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                //presenter.launchTransportWizard();
            }
        }));

        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final Transport selection = getCurrentSelection();
                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("Transport"),
                        Console.MESSAGES.deleteConfirm("Transport " + selection.getName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.delete(selection);
                                }
                            }
                        });
            }
        };
        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.addClickHandler(clickHandler);
        topLevelTools.addToolButtonRight(deleteBtn);
        
        this.table = new DefaultCellTable<Transport>(5, new ProvidesKey<Transport>() {
                    @Override
                    public Object getKey(Transport item) {
                        return item.getName();
                    }
                });

        this.dataProvider = new ListDataProvider<Transport>();
        this.dataProvider.addDataDisplay(this.table);        
        
        TextColumn<Transport> nameColumn = new TextColumn<Transport>() {
            @Override
            public String getValue(Transport record) {
                return record.getName();
            }
        };

        TextColumn<Transport> protocolColumn = new TextColumn<Transport>() {
            @Override
            public String getValue(Transport record) {
                return  record.getProtocol();
            }
        };
        
        this.table.addColumn(nameColumn, "Name");
        this.table.addColumn(protocolColumn, "Protocol");
        
        
        TextBoxItem name = new TextBoxItem("name", "Name", true);
        
        ComboBoxItem protocol = new ComboBoxItem("protocol", "Wire Protocol");
        protocol.setRequired(true);
        protocol.setValueMap(new String[] {"teiid", "pg"});
        protocol.setDefaultToFirstOption(true);
        
        TextBoxItem socketBinding = new TextBoxItem("socketBinding", "Socket Binding Name (refers to port)");
        NumberBoxItem maxSocketThreads = new NumberBoxItem("maxSocketThreads", "Max Socket Threads");
        maxSocketThreads.setValue(0);
        
        TextBoxItem authenticationDomain = new TextBoxItem("authenticationDomain", "Authentication Security Domain");
        authenticationDomain.setValue("teiid-security");
        
        NumberBoxItem maxSessionsAllowed = new NumberBoxItem("maxSessionsAllowed", "Max Sessions Allowed");
        maxSessionsAllowed.setValue(5000);
        
        NumberBoxItem sessionExpirationTime = new NumberBoxItem("sessionExpirationTime", "Session Expiration Time Limit");
        maxSessionsAllowed.setValue(0);
        
        
        CheckBoxItem sslEnabled = new CheckBoxItem("sslEnabled", "SSL Enabled");
        sslEnabled.setValue(false);
        
        ComboBoxItem sslMode = new ComboBoxItem("sslMode", "Mode");
        sslMode.setValueMap(new String[] {"logIn", "disabled", "enabled"});
        
        ComboBoxItem sslAuthMode = new ComboBoxItem("sslAuthMode", "Auth Mode");
        sslAuthMode.setValueMap(new String[] {"1-way", "2-way", "anonymous"});
        
        TextBoxItem sslProtocol = new TextBoxItem("sslProtocol", "Protocol");
        TextBoxItem keyManagementAlgorithm = new TextBoxItem("keyManagementAlgorithm", "Key Management Algorithm");
        
        TextBoxItem enabledCipherSuites = new TextBoxItem("enabledCipherSuites", "Enabled Cipher Suites");
        
        TextBoxItem keystoreName = new TextBoxItem("keystoreName", "Keystore Name");
        TextBoxItem keystorePassword = new TextBoxItem("keystorePassword", "Keystore Password");        
        TextBoxItem keystoreType = new TextBoxItem("keystoreType", "Keystore Type");
        TextBoxItem truststoreName = new TextBoxItem("truststoreName", "Truststore Name");
        TextBoxItem truststorePassword = new TextBoxItem("truststorePassword", "Truststore Password");
        
        
        this.formCommon = new TeiidModelForm<Transport>(Transport.class,
                this.presenter, name, protocol, socketBinding,
                authenticationDomain, maxSessionsAllowed, sessionExpirationTime);

        this.formSSL = new TeiidModelForm<Transport>(Transport.class,
                this.presenter, sslEnabled, sslMode, sslAuthMode, sslProtocol,
                keyManagementAlgorithm, enabledCipherSuites, keystoreName,
                keystorePassword, keystoreType, truststoreName,
                truststorePassword);
        
        MultipleToOneLayout layoutBuilder = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle("Teiid Transports")
                .setHeadline("Teiid Transports")
                .setDescription(new SafeHtmlBuilder().appendEscaped("Transport provides a mechanism to "
                        + "connect to Teiid, For ex: jdbc, odbc connections").toSafeHtml())
                .setMaster(Console.MESSAGES.available("Transports"), table)
                .setMasterTools(topLevelTools.asWidget())
                .addDetail("Common", this.formCommon.asWidget())
                .addDetail("SSL", this.formSSL.asWidget());

        return layoutBuilder.build();

    }

    @Override
    public void setPresenter(TransportPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setTransports(List<Transport> transports) {
        this.dataProvider.setList(transports);
        this.table.selectDefaultEntity();
    }
    
    private Transport getCurrentSelection() {
        return ((SingleSelectionModel<Transport>) this.table.getSelectionModel()).getSelectedObject();
    }    
}
