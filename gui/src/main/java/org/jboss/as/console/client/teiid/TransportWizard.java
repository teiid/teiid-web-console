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
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.teiid.model.Transport;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.TrappedFocusPanel;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TransportWizard {
    
    private DeckPanel deck;
    private TrappedFocusPanel trap;
    private TransportEditor editor;
    
    public TransportWizard (TransportEditor editor) {
        this.editor = editor;
    }
    
    public Widget asWidget() {
        this.deck = new DeckPanel() {
            @Override
            public void showWidget(int index) {
                super.showWidget(index);
                trap.getFocus().reset(getWidget(index).getElement());
                trap.getFocus().onFirstInput();
            }
        };
        
        List<FormItem<?>> commonItems = TransportEditor.buildCommonFormItems();
        deck.add(stepWizard(commonItems.toArray(new FormItem<?>[commonItems.size()]), 0, true));
        /*
        List<FormItem<?>> sslItems = TransportEditor.buildSSLFormItems();
        deck.add(stepWizard(this.transport, sslItems.toArray(new FormItem<?>[sslItems.size()]), 1, true));
        */
        trap = new TrappedFocusPanel(deck);
        deck.showWidget(0);
        return trap;
    }
    
    Widget stepWizard(FormItem<?>[] formItems, int currentStep, boolean finalStep) {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");
        layout.add(new HTML("<h3> Add Transport</h3>"));

        Form<Transport> form = new Form<Transport>(Transport.class);

        form.setFields(formItems);

        final FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "teiid");
                address.add("transport", "*");
                return address;
            }
        }, form);
        layout.add(helpPanel.asWidget());
        layout.add(form.asWidget());

        ClickHandler nextHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FormValidation validation = form.validate();
                if (!validation.hasErrors()) {
                    Transport t = form.getUpdatedEntity();
                    nextStep(t, currentStep);
                }
            }
        };
        ClickHandler cancelHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                closeDialogue();
            }
        };

        ClickHandler submitHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FormValidation validation = form.validate();
                if (!validation.hasErrors()) {
                    Transport t = form.getUpdatedEntity();
                    finishStep(t);
                }
            }
        };
        
        DialogueOptions options = null;
        if (finalStep) {
            options = new DialogueOptions(
                    Console.CONSTANTS.common_label_finish(), submitHandler,
                    Console.CONSTANTS.common_label_cancel(), cancelHandler
            );
            
        } else {
            options = new DialogueOptions(
                    Console.CONSTANTS.common_label_next(), nextHandler,
                    Console.CONSTANTS.common_label_cancel(), cancelHandler
            );
        }
        return new WindowContentBuilder(layout, options).build();
    }
    
    private void nextStep(Transport updated, int currentStep) {
        if (currentStep < 2) {
            this.deck.showWidget(currentStep+1);
        }
    }
    
    private void finishStep(Transport updated) {
        this.editor.createNewTransport(updated);
    }
    
    private void closeDialogue() {
        this.editor.closeNewTransportWizard();
    }
}
