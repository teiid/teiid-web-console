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

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.layout.SimpleLayout;
import org.jboss.as.console.client.teiid.model.TeiidLogger;
import org.jboss.ballroom.client.widgets.common.DefaultButton;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("nls")
public class AuditView extends DisposableViewImpl implements AuditPresenter.MyView {
	private static final String DEBUG = "DEBUG";
    private AuditPresenter presenter;    
	
    // buttons
    final CheckBox auditBtn = new CheckBox("Enable Audit Logging");
    final CheckBox commandBtn = new CheckBox("Enable Command Logging");
    final CheckBox traceBtn = new CheckBox("Enable Trace Logging");
    
    @Override
    public void setPresenter(AuditPresenter presenter) {
        this.presenter = presenter;
    }

	@Override
	public Widget createWidget() {
		SimpleLayout layout = new SimpleLayout().setTitle("Audit Logging")
				.setHeadline("Audit/Command Logging")
				.setDescription("Turn On/Off Audit/Command Logging. By default file handler(s) " +
				        "will be added. Alternatively, add TEIID_COMMAND_LOG/TEIID_AUDIT_LOG " +
				        "handlers to override the default handlers")
				.addContent("Audit/Command Logging for Teiid Subsystem", createLogPanel());
        return layout.build();	
	}
	
    private CaptionPanel createLogPanel() {
        CaptionPanel captionPanel = new CaptionPanel("Select Logging Type");
        
        DefaultButton applyBtn = new DefaultButton("Apply", new ClickHandler() {
            @Override
            public void onClick(ClickEvent arg0) {
                presenter.addOrRemoveLogger(AuditPresenter.CTX_AUDITLOGGING, auditBtn.getValue());
                presenter.addOrRemoveLogger(AuditPresenter.CTX_COMMANDLOGGING, commandBtn.getValue());
                presenter.addOrRemoveLogger(AuditPresenter.CTX_TRACELOGGING, traceBtn.getValue());
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

    @Override
    public void loggingStatus(String context, TeiidLogger logger) {
        
        if (AuditPresenter.CTX_AUDITLOGGING.equals(context)) {
            if (logger != null && logger.getLevel().equals(DEBUG)) {
                this.auditBtn.setValue(Boolean.TRUE);
            } else {
                this.auditBtn.setValue(Boolean.FALSE);
            }
        }

        if (AuditPresenter.CTX_COMMANDLOGGING.equals(context)) {
            if (logger != null && logger.getLevel().equals(DEBUG)) {
                this.commandBtn.setValue(Boolean.TRUE);
            } else {
                this.commandBtn.setValue(Boolean.FALSE);
            }
        }
        
        if (AuditPresenter.CTX_TRACELOGGING.equals(context)) {
            if (logger != null && logger.getLevel().equals("TRACE")) {
                this.traceBtn.setValue(Boolean.TRUE);
            } else {
                this.traceBtn.setValue(Boolean.FALSE);
            }
        }
    }
    
    public void initialLoad() {
        presenter.getLogger(AuditPresenter.CTX_AUDITLOGGING);
        presenter.getLogger(AuditPresenter.CTX_COMMANDLOGGING);
        presenter.getLogger(AuditPresenter.CTX_TRACELOGGING);
        
    }
    
    @Override
    public void logHandlerAdded(String handlerName, boolean added) {
        if (!added) {
            Console.info("Addition Logging handler "+handlerName+" failed");
        }
    }

    @Override
    public void logHandlerRemoved(String handlerName, boolean removed) {
        if (!removed) {
            Console.info("Removal of Logging handler "+handlerName+" failed");
        }
    }

    @Override
    public void loggerAdded(String context, boolean added) {
        if (!added) {
            Console.info("Addition of Logger "+context+" failed");
        }
    }

    @Override
    public void loggerRemoved(String context, boolean removed) {
        if (!removed) {
            Console.info("Removal of Logger "+context+" failed");
        }
    }

    @Override
    public void addLogger(String context) {
        // first check if DB based handler exists
        if (context.equals(AuditPresenter.CTX_AUDITLOGGING) ) {
            presenter.checkHandler(context, "TEIID_AUDIT_LOG", true);
        }
        
        if (context.equals(AuditPresenter.CTX_COMMANDLOGGING)) {
            presenter.checkHandler(context, "TEIID_COMMAND_LOG", true);
        }
        
        // no handler for this, should go in root log
        if (context.equals(AuditPresenter.CTX_TRACELOGGING)) {
            presenter.addLogger(context, "TRACE", null);
        }
    }
    
    @Override
    public void handlerExists(String context, String name, boolean dbAppender, boolean exists) {
        // if handler exists, proceed to creating the logger
        if (exists) {            
            if (context.equals(AuditPresenter.CTX_AUDITLOGGING) ) {
                presenter.addLogger(context, "DEBUG", "TEIID_AUDIT_LOG");
            }
            
            if (context.equals(AuditPresenter.CTX_COMMANDLOGGING)) {
                presenter.addLogger(context, "DEBUG", "TEIID_COMMAND_LOG");
            }            
        }
        else {
            // if the first request was to check db handler, now check for file based handler
            if (dbAppender) {
                if (context.equals(AuditPresenter.CTX_AUDITLOGGING) ) {
                    presenter.checkHandler(context, "TEIID_AUDIT_LOG", false);
                }
                
                if (context.equals(AuditPresenter.CTX_COMMANDLOGGING)) {
                    presenter.checkHandler(context, "TEIID_COMMAND_LOG", false);
                }                
            }
            else {
                // if file based handler also does not exist, then add a file based handler
                if (context.equals(AuditPresenter.CTX_AUDITLOGGING) ) {
                    presenter.addFileHandler("TEIID_AUDIT_LOG", "teiid-audit.log");
                    presenter.addLogger(context, "DEBUG", "TEIID_AUDIT_LOG");
                }
                
                if (context.equals(AuditPresenter.CTX_COMMANDLOGGING)) {
                    presenter.addFileHandler("TEIID_COMMAND_LOG", "teiid-command.log");
                    presenter.addLogger(context, "DEBUG", "TEIID_COMMAND_LOG");
                }     
            }
        }
    }

    @Override
    public void deleteLogger(String context) {
        if (context.equals(AuditPresenter.CTX_AUDITLOGGING)) {
            presenter.removeLogger(context);
            //presenter.removeFileHandler("TEIID_AUDIT_LOG");
        }
        
        if (context.equals(AuditPresenter.CTX_COMMANDLOGGING)) {
            presenter.removeLogger(context);
            //presenter.removeFileHandler("TEIID_COMMAND_LOG");
        }
        
        // no handler for this, should go in root log
        if (context.equals(AuditPresenter.CTX_TRACELOGGING)) {
            presenter.removeLogger(context);
        }
    }
}
