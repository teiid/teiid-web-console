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
package org.jboss.as.console.client.teiid.runtime;

import java.util.List;

import org.jboss.as.console.client.teiid.model.*;
import org.jboss.as.console.client.teiid.model.ListBoxItem;
import org.jboss.as.console.client.teiid.runtime.VDBView.TableSelectionCallback;
import org.jboss.as.console.client.teiid.widgets.TeiidIcons;
import org.jboss.ballroom.client.widgets.forms.*;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

@SuppressWarnings("nls")
public class VDBDataRolesTab extends VDBProvider {
	private VDBPresenter presenter;
	private ListDataProvider<DataPermission> dataPermissionProvider = new ListDataProvider<DataPermission>();
	private ListDataProvider<DataPolicy> dataPolicyProvider = new ListDataProvider<DataPolicy>();
	private DefaultCellTable dataPermissionsTable;
	private Form<DataPolicy> mappedRolesForm;
	private Form<DataPermission> conditionPanel;
	
	public VDBDataRolesTab(VDBPresenter presenter) {
		this.presenter = presenter;
	}	
	
	@Override
    public VerticalPanel getPanel(DefaultCellTable vdbTable) {
        // Policies in VDB
        Label policyLabel = new Label("Policies In VDB");
        policyLabel.getElement().setAttribute("style", "margin-top:10px;margin-bottom:10px;font-weight:bold;");		
        
        final DefaultCellTable policyTable = getPolicyTable();
        this.dataPolicyProvider.addDataDisplay(policyTable);
        VDBView.onTableSectionChange(vdbTable, new TableSelectionCallback<VDB> (){
			@Override
			public void onSelectionChange(VDB selection) {
				List<DataPolicy> policies = null;
				if (selection != null) {
					setVdbName(selection.getName());
					setVdbVersion(selection.getVersion());
					policies = selection.getDataPolicies();
				}
				else {
					setVdbName(null);
					setVdbVersion("0");	
				}	
				
				if (policies == null || policies.isEmpty()) {
					setPolicyName(null);
					dataPolicyProvider.getList().clear();
					dataPermissionProvider.getList().clear();
					mappedRolesForm.clearValues();
				}
				else {
					dataPolicyProvider.getList().clear();
					dataPolicyProvider.getList().addAll(policies);
					policyTable.getSelectionModel().setSelected(policies.get(0), true);
					setPolicyName(policies.get(0).getName());
					
					List<DataPermission> permissions = policies.get(0).getPermissions();
					dataPermissionProvider.getList().clear();
					if (!permissions.isEmpty()) {
						dataPermissionProvider.getList().addAll(permissions);
						conditionPanel.edit(permissions.get(0));
					}
					mappedRolesForm.edit(policies.get(0));
				}
			}
        });        
        
        DefaultPager policyTablePager = new DefaultPager();
        policyTablePager.setDisplay(policyTable);

        
        // Details about Model
        final Form<Model> form = new Form<Model>(DataPolicy.class);
        form.setNumColumns(1);
        form.setEnabled(false);

        TextItem descriptionLabel = new TextItem("description", "Description");
        form.setFields(descriptionLabel);
        form.bind(policyTable);        
        
        // Permissions in Policy
        Label permissionsLabel = new Label("Permissions");
        permissionsLabel.getElement().setAttribute("style", "margin-top:10px;margin-bottom:10px;font-weight:bold;");
        
        this.dataPermissionsTable = getPermissionsTable();
        this.dataPermissionProvider = new ListDataProvider<DataPermission>();
        this.dataPermissionProvider.addDataDisplay(this.dataPermissionsTable);        
        
        VDBView.onTableSectionChange(policyTable, new TableSelectionCallback<DataPolicy> (){
			@Override
			public void onSelectionChange(DataPolicy selection) {
				if (selection != null) {
					setPolicyName(selection.getName());					
					dataPermissionProvider.getList().clear();
					dataPermissionProvider.getList().addAll(selection.getPermissions());
					if (!selection.getPermissions().isEmpty()) {
					    dataPermissionsTable.getSelectionModel().setSelected(selection.getPermissions().get(0), true);
					}
				}
				else {
					setPolicyName(null);
					dataPermissionProvider.getList().clear();
				}
			}
        });        
        DefaultPager dataPermissionsTablePager = new DefaultPager();
        dataPermissionsTablePager.setDisplay(this.dataPermissionsTable);
        
        // conditions panel
        this.conditionPanel = new Form<DataPermission>(DataPermission.class);
        this.conditionPanel.setNumColumns(2);
        this.conditionPanel.setEnabled(false);

        TextItem conditionLabel = new TextItem("condition", "Condition");
        CheckBoxItem constraintLabel = new CheckBoxItem("constraint", "Filter");
        TextItem maskLabel = new TextItem("mask", "Mask");
        NumberBoxItem maskOrderLabel = new NumberBoxItem("order", "Mask Order");
        this.conditionPanel.setFields(constraintLabel, conditionLabel,maskLabel, maskOrderLabel);
        this.conditionPanel.bind(this.dataPermissionsTable);  
        
        // mapped role names
        this.mappedRolesForm = new Form<DataPolicy>(DataPolicy.class);
        this.mappedRolesForm.setNumColumns(1);
        this.mappedRolesForm.setEnabled(true);
        final TextItem addInput = new TextItem("newrole", "New Role");
        addInput.setEnabled(true);
        final ListBoxItem mappedRoleNames = new ListBoxItem("mappedRoleNames", "Mapped Role Names");
        mappedRoleNames.setVisibleItemCount(4);
        mappedRoleNames.setEnabled(true);
        this.mappedRolesForm.setFields(addInput, mappedRoleNames);
        this.mappedRolesForm.bind(policyTable);
        
        HorizontalPanel btnPanel = new HorizontalPanel();
        ButtonItem addBtn = new ButtonItem("add", "Add", "Add");
        ButtonItem removeBtn = new ButtonItem("remove", "Remove", "Remove");
        btnPanel.add(new Label("           ")); // to align under the box.
        btnPanel.add(addBtn.asWidget());
        btnPanel.add(removeBtn.asWidget());
                
        CaptionPanel captionPanel = new CaptionPanel("Manage Data Roles");
        VerticalPanel mappedRolesPanel = new VerticalPanel();
        mappedRolesPanel.add(mappedRolesForm.asWidget());
        mappedRolesPanel.add(btnPanel.asWidget());
        mappedRolesPanel.getElement().setAttribute("style", "margin-top:5px;margin-bottom:5px");
        captionPanel.add(mappedRolesPanel);
        captionPanel.getElement().setAttribute("style", "margin-top:10px;margin-bottom:10px;font-weight:bold;");
        addBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				String roleName = addInput.getValue();
				if (getPolicyName() != null && !roleName.isEmpty() && mappedRoleNames.addItem(roleName)) {
					addInput.clearValue();
					presenter.addRoleName(getVdbName(), getVdbVersion(), getPolicyName(), roleName);
				}
			}
		});
        removeBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				String removedRole = mappedRoleNames.removeSelected();
				presenter.removeRoleName(getVdbName(), getVdbVersion(), getPolicyName(), removedRole);
			}
		});
        
        // build overall panel
        VerticalPanel formPanel = new VerticalPanel();
        formPanel.add(policyLabel.asWidget());
        formPanel.add(policyTable.asWidget());
        formPanel.add(policyTablePager);
        formPanel.add(form.asWidget());
        formPanel.add(permissionsLabel.asWidget());
        formPanel.add(this.dataPermissionsTable.asWidget()); 
        formPanel.add(dataPermissionsTablePager);        
        formPanel.add(conditionPanel.asWidget());
        formPanel.add(captionPanel.asWidget());
        return formPanel;  
    }


	private DefaultCellTable getPolicyTable() {
		ProvidesKey<DataPolicy> keyProvider = new ProvidesKey<DataPolicy>() {
            @Override
            public Object getKey(DataPolicy item) {
                return getVdbName()+"."+getVdbVersion()+"."+item.getName();
            }
        };
        
		final DefaultCellTable table = new DefaultCellTable<DataPolicy>(8, keyProvider);   
        
        TextColumn<DataPolicy> nameColumn = new TextColumn<DataPolicy>() {
            @Override
            public String getValue(DataPolicy record) {
                return record.getName();
            }
        };
        
		Column<DataPolicy, ImageResource> anyAuthenticatedColumn = new Column<DataPolicy, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(DataPolicy record) {
				ImageResource res = null;
				if (record.isAnyAuthenticated()) {
					res = Icons.INSTANCE.status_good();
					
				} else {
					res = TeiidIcons.INSTANCE.status_not_ok();
				}
				return res;
			}
		};        
        
		Column<DataPolicy, ImageResource> allowCreateTempTablesColumn = new Column<DataPolicy, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(DataPolicy record) {
				ImageResource res = null;
				if (record.isAllowCreateTemporaryTables()) {
					res = Icons.INSTANCE.status_good();
					
				} else {
					res = TeiidIcons.INSTANCE.status_not_ok();
				}
				return res;
			}
		};  
		
        Column<DataPolicy, ImageResource> grantAllColumn = new Column<DataPolicy, ImageResource>(
                new ImageResourceCell()) {
            @Override
            public ImageResource getValue(DataPolicy record) {
                ImageResource res = null;
                if (record.isGrantAll()) {
                    res = Icons.INSTANCE.status_good();
                    
                } else {
                    res = TeiidIcons.INSTANCE.status_not_ok();
                }
                return res;
            }
        };		
        
        table.setTitle("Data Policies");
        table.addColumn(nameColumn, "Policy Name");
        table.addColumn(anyAuthenticatedColumn, "Allows Any Authenticated User");
        table.addColumn(allowCreateTempTablesColumn, "Allows Creation Of Temp Tables");
        table.addColumn(grantAllColumn, "Grant All");
        table.setSelectionModel(new SingleSelectionModel<DataPolicy>(keyProvider));
		return table;
	}	
	
	private DefaultCellTable getPermissionsTable() {
		ProvidesKey<DataPermission> keyProvider = new ProvidesKey<DataPermission>() {
            @Override
            public Object getKey(DataPermission item) {
                return getVdbName()+"."+getVdbVersion()+"."+getPolicyName()+"."+item.getResourceName();
            }
        };
		
		final DefaultCellTable table = new DefaultCellTable<DataPermission>(8, keyProvider);   
        
		
        TextColumn<DataPermission> nameColumn = new TextColumn<DataPermission>() {
            @Override
            public String getValue(DataPermission record) {
                return record.getResourceName();
            }
        };
        
		Column<DataPermission, ImageResource> createColumn = new Column<DataPermission, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(DataPermission permission) {
				ImageResource res = null;
				if (permission.isAllowCreate() == null || !permission.isAllowCreate()) {
					res = TeiidIcons.INSTANCE.status_not_ok();
				} else {
					res = Icons.INSTANCE.status_good();
				}
				return res;
			}
		};        
		
		Column<DataPermission, ImageResource> readColumn = new Column<DataPermission, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(DataPermission permission) {
				ImageResource res = null;
				if (permission.isAllowRead() == null || !permission.isAllowRead()) {
					res = TeiidIcons.INSTANCE.status_not_ok();
				} else {
					res = Icons.INSTANCE.status_good();
				}
				return res;
			}
		}; 		
		
		Column<DataPermission, ImageResource> updateColumn = new Column<DataPermission, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(DataPermission permission) {
				ImageResource res = null;
				if (permission.isAllowUpdate() == null || !permission.isAllowUpdate()) {
					res = TeiidIcons.INSTANCE.status_not_ok();
				} else {
					res = Icons.INSTANCE.status_good();
				}
				return res;
			}
		}; 
		
		Column<DataPermission, ImageResource> deleteColumn = new Column<DataPermission, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(DataPermission permission) {
				ImageResource res = null;
				if (permission.isAllowDelete() == null || !permission.isAllowDelete()) {
					res = TeiidIcons.INSTANCE.status_not_ok();
				} else {
					res = Icons.INSTANCE.status_good();
				}
				return res;
			}
		}; 	
		Column<DataPermission, ImageResource> executeColumn = new Column<DataPermission, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(DataPermission permission) {
				ImageResource res = null;
				if (permission.isAllowExecute() == null || !permission.isAllowExecute()) {
					res = TeiidIcons.INSTANCE.status_not_ok();
				} else {
					res = Icons.INSTANCE.status_good();
				}
				return res;
			}
		}; 		
        Column<DataPermission, ImageResource> alterColumn = new Column<DataPermission, ImageResource>(
                new ImageResourceCell()) {
            @Override
            public ImageResource getValue(DataPermission permission) {
                ImageResource res = null;
                if (permission.isAllowAlter() == null || !permission.isAllowAlter()) {
                    res = TeiidIcons.INSTANCE.status_not_ok();
                } else {
                    res = Icons.INSTANCE.status_good();
                }
                return res;
            }
        };		
        
        table.setTitle("Data Permissions");
        table.addColumn(nameColumn, "Resource Name");
        table.addColumn(createColumn, "Create");
        table.addColumn(readColumn, "Read");
        table.addColumn(updateColumn, "Update");
        table.addColumn(deleteColumn, "Delete");
        table.addColumn(executeColumn, "Execute");
        table.addColumn(alterColumn, "Alter");
        table.setSelectionModel(new SingleSelectionModel<DataPermission>(keyProvider));
		return table;
	}	
}
