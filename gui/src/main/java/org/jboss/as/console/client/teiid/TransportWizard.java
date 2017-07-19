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
