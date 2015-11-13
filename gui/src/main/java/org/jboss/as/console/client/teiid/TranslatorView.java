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
import org.jboss.as.console.client.rbac.SecurityFramework;
import org.jboss.as.console.client.teiid.model.Translator;
import org.jboss.as.console.client.v3.ResourceDescriptionRegistry;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;
import org.jboss.ballroom.client.rbac.SecurityContext;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.ballroom.client.widgets.window.Feedback;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;

public class TranslatorView extends SuspendableViewImpl implements TranslatorPresenter.MyView {
    private final ResourceDescriptionRegistry descriptionRegistry;
    private final SecurityFramework securityFramework;
    private DefaultCellTable<Translator> table;
    private ListDataProvider<Translator> dataProvider;
    
    private TeiidModelForm<Translator> form;
    private TranslatorPresenter presenter;
    
    @Inject
    public TranslatorView(ResourceDescriptionRegistry descriptionRegistry, SecurityFramework securityFramework) {
        this.descriptionRegistry = descriptionRegistry;
        this.securityFramework = securityFramework;
    } 

    @Override
    public void setPresenter(TranslatorPresenter presenter) {
        this.presenter = presenter;
    }
    
    @Override
    public Widget createWidget() {
        SecurityContext securityContext = securityFramework.getSecurityContext(this.presenter.getProxy().getNameToken());

        ToolStrip topLevelTools = new ToolStrip();
        topLevelTools.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchTranslatorWizard();
            }
        }));

        ClickHandler clickHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final Translator selection = getCurrentSelection();
                Feedback.confirm(
                        Console.MESSAGES.deleteTitle("Translator"),
                        Console.MESSAGES.deleteConfirm("Translator " + selection.getName()),
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
        
        this.table = new DefaultCellTable<Translator>(5, new ProvidesKey<Translator>() {
                    @Override
                    public Object getKey(Translator item) {
                        return item.getName();
                    }
                });

        this.dataProvider = new ListDataProvider<Translator>();
        this.dataProvider.addDataDisplay(this.table);        
        
        TextColumn<Translator> nameColumn = new TextColumn<Translator>() {
            @Override
            public String getValue(Translator record) {
                return record.getName();
            }
        };

        TextColumn<Translator> moduleNameColumn = new TextColumn<Translator>() {
            @Override
            public String getValue(Translator record) {
                return  record.getModuleName();
            }
        };
        
        this.table.addColumn(nameColumn, "Name");
        this.table.addColumn(moduleNameColumn, "Module Name");
        
        TextBoxItem name = new TextBoxItem("name", "Name", true);
        TextBoxItem moduleName = new TextBoxItem("moduleName", "Module Name", true);
        
        this.form = new TeiidModelForm<Translator>(Translator.class, this.presenter, name, moduleName);

        DefaultTabLayoutPanel tabLayoutpanel = new DefaultTabLayoutPanel(40, Style.Unit.PX);
        tabLayoutpanel.addStyleName("default-tabpanel");
        tabLayoutpanel.add(this.form.asWidget(), "Translators", true);
        
        tabLayoutpanel.selectTab(0);

        return tabLayoutpanel;
        
    }
    
    private Translator getCurrentSelection() {
        return ((SingleSelectionModel<Translator>) this.table.getSelectionModel()).getSelectedObject();
    }

    @Override
    public void setTranslators(List<Translator> translators) {
        this.dataProvider.setList(translators);
        this.table.selectDefaultEntity();
    }
}
