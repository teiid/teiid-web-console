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

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.layout.OneToOneLayout;
import org.jboss.as.console.client.teiid.model.TeiidLogger;
import org.jboss.ballroom.client.widgets.tools.ToolButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("nls")
public class AuditEditor {
    public static final String CTX_COMMANDLOGGING = "org.teiid.COMMAND_LOG"; //$NON-NLS-1$
    public static final String CTX_AUDITLOGGING = "org.teiid.AUDIT_LOG"; //$NON-NLS-1$
    public static final String CTX_TRACELOGGING = "org.teiid"; //$NON-NLS-1$
    
	private static final String DEBUG = "DEBUG";
    private SubsystemPresenter presenter;    
	
    // buttons
    final CheckBox auditBtn = new CheckBox("Enable Audit Logging");
    final CheckBox commandBtn = new CheckBox("Enable Command Logging");
    final CheckBox traceBtn = new CheckBox("Enable Trace Logging");
    
    public AuditEditor(SubsystemPresenter presenter) {
        this.presenter = presenter;
    }

	public Widget asWidget() {
        HTML title = new HTML();
        title.setStyleName("content-header-label");
        title.setText("Audit/Command Logging");
        
        OneToOneLayout layoutBuilder = new OneToOneLayout()
                .setPlain(true)
                .setTitle("Audit/Command Logging")
                .setHeadlineWidget(title)
                .setDescription("Turn On/Off Audit/Command Logging. By default file handler(s) " +
                        "will be added. Alternatively, add TEIID_COMMAND_LOG/TEIID_AUDIT_LOG " +
                        "handlers to override the default handlers")
                .addDetail("Logging", createLogPanel());
        return layoutBuilder.build();        
	}
	
    private CaptionPanel createLogPanel() {
        CaptionPanel captionPanel = new CaptionPanel("Select Logging Type");
        
        ToolButton applyBtn = new ToolButton("Apply", new ClickHandler() {
            @Override
            public void onClick(ClickEvent arg0) {
                presenter.addOrRemoveLogger(CTX_AUDITLOGGING, auditBtn.getValue());
                presenter.addOrRemoveLogger(CTX_COMMANDLOGGING, commandBtn.getValue());
                presenter.addOrRemoveLogger(CTX_TRACELOGGING, traceBtn.getValue());
            }
        });
        
        VerticalPanel selectionPanel = new VerticalPanel();
        selectionPanel.add(this.auditBtn);
        selectionPanel.add(this.commandBtn);
        selectionPanel.add(this.traceBtn);
        selectionPanel.add(applyBtn);
        selectionPanel.setCellHorizontalAlignment(this.auditBtn,HasHorizontalAlignment.ALIGN_LEFT);
        selectionPanel.setCellHorizontalAlignment(this.commandBtn,HasHorizontalAlignment.ALIGN_LEFT);
        selectionPanel.setCellHorizontalAlignment(this.traceBtn,HasHorizontalAlignment.ALIGN_LEFT);
        captionPanel.add(selectionPanel);        
        captionPanel.setWidth("40%");
        captionPanel.getElement().setAttribute("style", "font-weight:bold;");
        return captionPanel;
    }	

    public void loggingStatus(String context, TeiidLogger logger) {
        
        if (CTX_AUDITLOGGING.equals(context)) {
            if (logger != null && logger.getLevel().equals(DEBUG)) {
                this.auditBtn.setValue(Boolean.TRUE);
            } else {
                this.auditBtn.setValue(Boolean.FALSE);
            }
        }

        if (CTX_COMMANDLOGGING.equals(context)) {
            if (logger != null && logger.getLevel().equals(DEBUG)) {
                this.commandBtn.setValue(Boolean.TRUE);
            } else {
                this.commandBtn.setValue(Boolean.FALSE);
            }
        }
        
        if (CTX_TRACELOGGING.equals(context)) {
            if (logger != null && logger.getLevel().equals("TRACE")) {
                this.traceBtn.setValue(Boolean.TRUE);
            } else {
                this.traceBtn.setValue(Boolean.FALSE);
            }
        }
    }
        
    public void logHandlerAdded(String handlerName, boolean added) {
        if (!added) {
            Console.info("Addition Logging handler "+handlerName+" failed");
        }
    }

    public void logHandlerRemoved(String handlerName, boolean removed) {
        if (!removed) {
            Console.info("Removal of Logging handler "+handlerName+" failed");
        }
    }

    public void loggerAdded(String context, boolean added) {
        if (!added) {
            Console.info("Addition of Logger "+context+" failed");
        }
    }

    public void loggerRemoved(String context, boolean removed) {
        if (!removed) {
            Console.info("Removal of Logger "+context+" failed");
        }
    }

    public void addLogger(String context) {
        // first check if DB based handler exists
        if (context.equals(CTX_AUDITLOGGING) ) {
            presenter.checkLogHandlerStatus(context, "TEIID_AUDIT_LOG", true);
        }
        
        if (context.equals(CTX_COMMANDLOGGING)) {
            presenter.checkLogHandlerStatus(context, "TEIID_COMMAND_LOG", true);
        }
        
        // no handler for this, should go in root log
        if (context.equals(CTX_TRACELOGGING)) {
            presenter.addLogger(context, "TRACE", null);
        }
    }
    
    public void setLogHandlerStatus(String context, String name, boolean dbAppender, boolean exists) {
        // if handler exists, proceed to creating the logger
        if (exists) {            
            if (context.equals(CTX_AUDITLOGGING) ) {
                presenter.addLogger(context, "DEBUG", "TEIID_AUDIT_LOG");
            }
            
            if (context.equals(CTX_COMMANDLOGGING)) {
                presenter.addLogger(context, "DEBUG", "TEIID_COMMAND_LOG");
            }            
        }
        else {
            // if the first request was to check db handler, now check for file based handler
            if (dbAppender) {
                if (context.equals(CTX_AUDITLOGGING) ) {
                    presenter.checkLogHandlerStatus(context, "TEIID_AUDIT_LOG", false);
                }
                
                if (context.equals(CTX_COMMANDLOGGING)) {
                    presenter.checkLogHandlerStatus(context, "TEIID_COMMAND_LOG", false);
                }                
            }
            else {
                // if file based handler also does not exist, then add a file based handler
                if (context.equals(CTX_AUDITLOGGING) ) {
                    presenter.addFileHandler("TEIID_AUDIT_LOG", "teiid-audit.log");
                    presenter.addLogger(context, "DEBUG", "TEIID_AUDIT_LOG");
                }
                
                if (context.equals(CTX_COMMANDLOGGING)) {
                    presenter.addFileHandler("TEIID_COMMAND_LOG", "teiid-command.log");
                    presenter.addLogger(context, "DEBUG", "TEIID_COMMAND_LOG");
                }     
            }
        }
    }

    public void deleteLogger(String context) {
        if (context.equals(CTX_AUDITLOGGING)) {
            presenter.removeLogger(context);
            //presenter.removeFileHandler("TEIID_AUDIT_LOG");
        }
        
        if (context.equals(CTX_COMMANDLOGGING)) {
            presenter.removeLogger(context);
            //presenter.removeFileHandler("TEIID_COMMAND_LOG");
        }
        
        // no handler for this, should go in root log
        if (context.equals(CTX_TRACELOGGING)) {
            presenter.removeLogger(context);
        }
    }
}
