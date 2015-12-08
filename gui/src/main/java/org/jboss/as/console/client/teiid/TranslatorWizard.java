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

import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.teiid.model.Translator;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.FormValidator;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.window.DialogueOptions;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;
import org.jboss.dmr.client.ModelNode;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TranslatorWizard {

    private TranslatorEditor editor;

    public TranslatorWizard(TranslatorEditor translatorEditor) {
        this.editor = translatorEditor;
    }

    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("window-content");

        final Form<Translator> form = new Form<Translator>(Translator.class);

        TextBoxItem name = new TextBoxItem("name", "Name", true);
        final TextBoxItem moduleName = new TextBoxItem("moduleName", "Module Name", true);

        form.setFields(name, moduleName);

        form.addFormValidator(new FormValidator() {
            @Override
            public void validate(List<FormItem> items, FormValidation outcome) {
                setError(findItem(items, "name"), "Name is required", outcome);
                setError(findItem(items, "moduleName"), "Module Name is required", outcome);
            }

            private FormItem findItem(List<FormItem> items, String name) {
                for (FormItem item : items) {
                    if (name.equals(item.getName())) {
                        return item;
                    }
                }
                return null;
            }
        });
        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "teiid");
                        address.add("translator", "*");
                        return address;
                    }
                }, form
        );

        layout.add(helpPanel.asWidget());
        layout.add(form.asWidget());

        DialogueOptions options = new DialogueOptions(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FormValidation validation = form.validate();
                if (!validation.hasErrors()) {
                    finishStep(form.getUpdatedEntity());
                }
            }
        }, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                closeDialogue();
            }
        });
        return new WindowContentBuilder(layout, options).build();
    }
    
    private void setError(FormItem item, String errorMsg, FormValidation outcome) {
        if (item.isRequired()) {
            boolean errored = false;
            boolean isNull = item.getValue() == null;
            if (!isNull) {
                if (item.getValue() instanceof String) {
                    String value = (String)item.getValue();
                    value = value.trim();
                    errored = value.isEmpty();
                }
            } else {
                errored = true;
            }
            
            if (errored) {
                outcome.addError(errorMsg);
                item.setErrMessage(errorMsg);
                item.setErroneous(true);
            }
        }
    }
    
    private void finishStep(Translator updated) {
        this.editor.createNewTranslator(updated);
    }
    
    private void closeDialogue() {
        this.editor.closeNewTranslatorWizard();
    }    
}
