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

import static com.google.gwt.dom.client.Style.Unit.PX;

import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.ballroom.client.widgets.forms.FormValidator;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.dmr.client.ModelNode;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


public class TeiidModelForm<T> {

    private Form<T> form;
    private Class<T> type;
    private FormItem<?>[] fields;
    private Label formValidationError;
    private Persistable<T> presenter;
    private DefaultCellTable<T> table;

    public TeiidModelForm(Class<T> type, Persistable<T> presenter, FormItem<?>... fields) {
        this.type = type;
        this.presenter = presenter;
        this.fields = fields;
    }

    Widget asWidget() {
        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout");

        this.form = new Form<T>(type);
        this.form.setNumColumns(2);
        
        this.form.addFormValidator(new FormValidator() {
            @Override
            public void validate(List<FormItem> formItems, FormValidation outcome) {
                validateForm(outcome);
            }
        });
        
        FormToolStrip<T> attributesToolStrip = new FormToolStrip<T>(this.form,
                new FormToolStrip.FormCallback<T>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.save(form.getEditedEntity(),form.getChangedValues());
                    }

                    @Override
                    public void onDelete(T entity) {
                        // this is not delete, it is Cancel
                    }
                });
        layout.add(attributesToolStrip.asWidget());
        
        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback() {
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "teiid");
                return address;
            }
        }, this.form);
        
        layout.add(helpPanel.asWidget());

        this.formValidationError = new Label("Form is invalid!");
        this.formValidationError.addStyleName("form-error-desc");
        this.formValidationError.getElement().getStyle().setLineHeight(9, PX);
        this.formValidationError.getElement().getStyle().setMarginBottom(5, PX);
        this.formValidationError.setVisible(false);
        layout.add(formValidationError.asWidget());

        if (this.table != null) {
            this.form.bind(this.table);
        }
        
        this.form.setFields(this.fields);
        this.form.setEnabled(false);

        layout.add(this.form.asWidget());

        return layout;
    }

    public void edit(T t) {
        form.edit(t);
    }

    public void clearValues() {
        form.clearValues();
    }

    protected FormValidation validateForm(FormValidation formValidation) {
        return formValidation;
    }
    
    public void setTable(DefaultCellTable<T> table) {
        this.table = table;
    }
}
