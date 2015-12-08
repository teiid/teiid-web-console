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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.layout.MultipleToOneLayout;
import org.jboss.as.console.client.teiid.model.Transport;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.Feedback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

public class TransportEditor implements Persistable<Transport> {
    
    private DefaultCellTable<Transport> table;
    private ListDataProvider<Transport> dataProvider;
    
    private TeiidModelForm<Transport> formCommon;
    private TeiidModelForm<Transport> formSSL;
    private SubsystemPresenter presenter;
    private DefaultWindow window;
    
    public TransportEditor(SubsystemPresenter presenter) {
        this.presenter = presenter;
    }    

    public Widget asWidget() {
        
        ClickHandler addClickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                launchTransportWizard();
            }
        };

        ClickHandler deleteClickHandler = new ClickHandler() {
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
                                    delete(selection);
                                }
                            }
                        });
            }
        };
        
        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add()); 
        addBtn.addClickHandler(addClickHandler);
        ToolButton deleteBtn = new ToolButton(Console.CONSTANTS.common_label_delete());
        deleteBtn.addClickHandler(deleteClickHandler);
        
        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(addBtn);
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
              
        this.formCommon = new TeiidModelForm<Transport>(Transport.class,
                this, buildCommonFormItems().toArray(new FormItem<?>[6]));

        this.formSSL = new TeiidModelForm<Transport>(Transport.class,
                this, buildSSLFormItems().toArray(new FormItem<?>[11]));
        
        this.formCommon.setTable(this.table);
        this.formSSL.setTable(this.table);
        
        MultipleToOneLayout layoutBuilder = new MultipleToOneLayout()
                .setPlain(true)
                .setTitle("Transports")
                .setHeadline("Transports")
                .setDescription(new SafeHtmlBuilder().appendEscaped("Transport provides a mechanism to "
                        + "connect to Teiid, For ex: jdbc, odbc connections").toSafeHtml())
                .setMaster(Console.MESSAGES.available("Transports"), table)
                .setMasterTools(topLevelTools.asWidget())
                .addDetail("Common", this.formCommon.asWidget())
                .addDetail("SSL", this.formSSL.asWidget());

        return layoutBuilder.build();

    }
    
    static List<FormItem<?>> buildCommonFormItems(){
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
        
        return Arrays.asList(name, protocol, socketBinding,
                authenticationDomain, maxSessionsAllowed, sessionExpirationTime);
    }
    
    static List<FormItem<?>> buildSSLFormItems(){
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
        return Arrays.asList(sslEnabled, sslMode, sslAuthMode, sslProtocol,
                keyManagementAlgorithm, enabledCipherSuites, keystoreName,
                keystorePassword, keystoreType, truststoreName,
                truststorePassword);
    }

    public void setTransports(List<Transport> transports) {
        this.dataProvider.setList(transports);
        this.table.selectDefaultEntity();
    }
    
    private Transport getCurrentSelection() {
        return ((SingleSelectionModel<Transport>) this.table.getSelectionModel()).getSelectedObject();
    }
    
    public void launchTransportWizard() {
        try {
            this.window = new DefaultWindow(Console.MESSAGES.createTitle("Transport"));
            this.window.setWidth(480);
            this.window.setHeight(360);

            TransportWizard wizard = new TransportWizard(this);
            
            this.window.trapWidget(wizard.asWidget());

            this.window.setGlassEnabled(true);
            this.window.center();
        } catch (Exception e) {
            Console.error("Error while starting the wizard for new Transport");
        }
    }    

    @Override
    public void save(Transport transport, Map<String, Object> changeset) {
        this.presenter.saveTransport(transport, changeset);
    }

    private void delete(Transport selection) {
        this.presenter.deleteTransport(selection);
    }    
    
    public void closeNewTransportWizard() {
        this.window.hide();
    }

    public void createNewTransport(Transport transport) {
        this.window.hide();
        this.presenter.createTransport(transport);
    }    
}
