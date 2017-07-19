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
        final TextBoxItem slot = new TextBoxItem("slot", "Slot", true);

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
