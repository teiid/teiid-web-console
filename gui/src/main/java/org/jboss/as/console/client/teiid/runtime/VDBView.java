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

import java.util.Comparator;
import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.layout.MultipleToOneLayout;
import org.jboss.as.console.client.teiid.model.CacheStatistics;
import org.jboss.as.console.client.teiid.model.DataModelFactory;
import org.jboss.as.console.client.teiid.model.EngineStatistics;
import org.jboss.as.console.client.teiid.model.KeyValuePair;
import org.jboss.as.console.client.teiid.model.Request;
import org.jboss.as.console.client.teiid.model.Session;
import org.jboss.as.console.client.teiid.model.VDB;
import org.jboss.as.console.client.teiid.model.ValidityError;
import org.jboss.as.console.client.teiid.widgets.TeiidIcons;
import org.jboss.as.console.client.teiid.widgets.TextAreaCell;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelNode;
import org.jboss.gwt.circuit.Dispatcher;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;

@SuppressWarnings("nls")
public class VDBView extends SuspendableViewImpl implements VDBPresenter.MyView {
	private ListDataProvider<VDB> vdbProvider = new ListDataProvider<VDB>();
	private DefaultCellTable vdbTable;
	private VDBPresenter presenter;
	private VDBRequestsTab vdbRequestsTab;
	private VDBModelsTab vdbModelsTab;
	private VDBSessionsTab vdbSessionsTab;
	private VDBCachingTab vdbCachingTab;
	private DataModelFactory factory;
	private TeiidMetricsEditor metricsEditor;
	private SQLWorkbenchEditor sqlWorkbenchEditor;
	private Dispatcher circuit;
	
    @Inject
    public VDBView(Dispatcher circuit) {
        this.circuit = circuit;
    }	
	
	public void setDataModelFactory(DataModelFactory factory) {
		this.factory = factory;					
	}
	
	@Override
	public void setDeployedVDBs(List<VDB> vdbs) {
		if (vdbs != null && !vdbs.isEmpty()) {
			this.vdbProvider.getList().clear();
			this.vdbProvider.getList().addAll(vdbs);
	        this.vdbTable.getSelectionModel().setSelected(vdbs.get(0), true);
	        sqlWorkbenchEditor.setVDBList(vdbs);
		}
		else {
			this.vdbProvider.getList().clear();
			sqlWorkbenchEditor.setVDBList(vdbs);
		}
	}
	
    @Override
    public void setPresenter(VDBPresenter presenter) {
        this.presenter = presenter;
    }
    
	@Override
	public void setVDBRequests(List<Request> requests) {
		this.vdbRequestsTab.setRequests(requests);
	}
	
	@Override
	public void setQueryPlan(String plan) {
		this.vdbRequestsTab.setQueryPlan(plan);
	}	

	@Override
	public void cancelSubmitted(Request request) {
		this.vdbRequestsTab.cancelSubmitted(request);
	}
	
	@Override
	public void setModelSchema(String ddl) {
		vdbModelsTab.setSchema(ddl);
	}
	
	
	@Override
	public Widget createWidget() {

        DefaultTabLayoutPanel layout  = new DefaultTabLayoutPanel(40, Style.Unit.PX);
        layout.addStyleName("default-tabpanel");
        PagedView pages = new PagedView(true);
        
        this.metricsEditor = new TeiidMetricsEditor();
        this.sqlWorkbenchEditor = new SQLWorkbenchEditor();
        
		pages.addPage("Virtual Databases", mainPageAsWidget());
		pages.addPage("Metrics", this.metricsEditor.createWidget());
		pages.addPage("SQL Workbench", this.sqlWorkbenchEditor.createWidget(presenter));

		// default page
		pages.showPage(0);

		// Top Most Tab
		Widget pagesWidget = pages.asWidget();
		layout.add(pagesWidget, "Teiid");

		return layout;

	}

	Widget mainPageAsWidget() {
		ListHandler<VDB> sortHandler = new ListHandler<VDB>(this.vdbProvider.getList());
		
        final ToolStrip toolStrip = new ToolStrip();
        toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.refresh(false);
            }
        }));		
        

		DefaultCellTable<VDB> table = new DefaultCellTable<VDB>(5, new ProvidesKey<VDB>() {
			@Override
			public Object getKey(VDB item) {
				return item.getName() + "_" + item.getVersion();
			}
        });
		
		table.addColumnSortHandler(sortHandler);		

		TextColumn<VDB> nameColumn = new TextColumn<VDB>() {
			@Override
			public String getValue(VDB record) {
				return record.getName();
			}
		};
        nameColumn.setSortable(true);
        sortHandler.setComparator(nameColumn, new Comparator<VDB>() {
			@Override
			public int compare(VDB o1, VDB o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});		

		TextColumn<VDB> versionColumn = new TextColumn<VDB>() {
			@Override
			public String getValue(VDB vdb) {
				return vdb.getVersion();
			}
		};
		versionColumn.setSortable(true);
        sortHandler.setComparator(versionColumn, new Comparator<VDB>() {
			@Override
			public int compare(VDB o1, VDB o2) {
				return o1.getVersion().compareTo(o2.getVersion());
			}
		});			

		TextColumn<VDB> dynamicColumn = new TextColumn<VDB>() {
			@Override
			public String getValue(VDB record) {
				return String.valueOf(record.isDynamic());
			}
		};
		dynamicColumn.setSortable(true);
        sortHandler.setComparator(dynamicColumn, new Comparator<VDB>() {
			@Override
			public int compare(VDB o1, VDB o2) {
				return o1.isDynamic().compareTo(o2.isDynamic());
			}
		});			
		
		TextColumn<VDB> statusColumn = new TextColumn<VDB>() {
			@Override
			public String getValue(VDB record) {
				return String.valueOf(record.getStatus());
			}
		};		
		statusColumn.setSortable(true);
        sortHandler.setComparator(statusColumn, new Comparator<VDB>() {
			@Override
			public int compare(VDB o1, VDB o2) {
				return o1.getStatus().compareTo(o2.getStatus());
			}
		});			
		
		
		Column<VDB, ImageResource> validColumn = new Column<VDB, ImageResource>(
				new ImageResourceCell()) {
			@Override
			public ImageResource getValue(VDB vdb) {
				ImageResource res = null;
				if (vdb.isValid()) {
					res = Icons.INSTANCE.status_good();
				} else {
					res = TeiidIcons.INSTANCE.status_not_ok();
				}
				return res;
			}
		};
		validColumn.setSortable(true);
        sortHandler.setComparator(validColumn, new Comparator<VDB>() {
			@Override
			public int compare(VDB o1, VDB o2) {
				return o1.isValid().compareTo(o2.isValid());
			}
		});		
        
        Column<VDB, String> reloadBtn = new Column<VDB, String>(new ButtonCell()) {
            @Override
            public String getValue(VDB record) {
        		return "Reload";
            }        	
        };
        reloadBtn.setFieldUpdater(new FieldUpdater<VDB, String>() {
			@Override
			public void update(int index, VDB vdb, String value) {
			    reloadVDB(vdb);
			}
        });        

		table.addColumn(nameColumn, "Name");
		table.addColumn(versionColumn, "Version");
		table.addColumn(dynamicColumn, "Dynamic");
		table.addColumn(statusColumn, "Status");
		table.addColumn(validColumn, "Valid");
		table.addColumn(reloadBtn, "Reload");

		// sets initial sorting
        table.getColumnSortList().push(nameColumn);

		this.vdbTable = table;
		this.vdbProvider.addDataDisplay(table);
		
		this.vdbRequestsTab = new VDBRequestsTab(presenter);
		this.vdbModelsTab = new VDBModelsTab(this.presenter);
		this.vdbModelsTab.setDataModelFactory(factory);
		
		this.vdbSessionsTab = new VDBSessionsTab(presenter);
		this.vdbCachingTab = new VDBCachingTab(presenter);

		// Page layout
		MultipleToOneLayout layout = new MultipleToOneLayout().setPlain(true)
				.setTitle("VDB Panel").setDescription(new SafeHtmlBuilder().appendHtmlConstant("").toSafeHtml())
				.setHeadline("Deployed Virtual Databases")
				.setTopLevelTools(toolStrip)
				.setMaster("Deployed VDBS", table)
				.addDetail("Summary", new VDBSummaryTab(this.presenter).getPanel(table))
				.addDetail("Models", this.vdbModelsTab.getPanel(table))
				.addDetail("Overrides", new VDBTranslatorsTab(this.presenter).getPanel(table))
				.addDetail("Caching", this.vdbCachingTab.getPanel(table))
				.addDetail("Data Roles", new VDBDataRolesTab(this.presenter).getPanel(table))
				.addDetail("Requests", this.vdbRequestsTab.getPanel(table))
				.addDetail("Sessions", this.vdbSessionsTab.getPanel(table));
		return layout.build();
	}

	interface TabProvider {
		VerticalPanel getPanel(DefaultCellTable vdbTable);
	}

	static interface TableSelectionCallback<T> {
		void onSelectionChange(T selection);
	}

	static void onTableSectionChange(DefaultCellTable table, final TableSelectionCallback callback) {
		SingleSelectionModel selectionModel = (SingleSelectionModel) table.getSelectionModel();
		if (selectionModel == null) {
			throw new RuntimeException("Define selection model for table");
		}
		final SingleSelectionModel finalSelectionModel = selectionModel;
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				Scheduler.get().scheduleDeferred(
					new Scheduler.ScheduledCommand() {
						@Override
						public void execute() {
							Object selectedObject = finalSelectionModel.getSelectedObject();
							if (selectedObject != null) {
								callback.onSelectionChange(selectedObject);
							} else {
								callback.onSelectionChange(null);
							}
						}
					});
			}
		});
	}

	static DefaultCellTable<ValidityError> buildErrorTable() {
		ProvidesKey<ValidityError> keyProvider = new ProvidesKey<ValidityError>() {
			@Override
			public Object getKey(ValidityError item) {
				return item.getMessage();
			}
		};
		
		DefaultCellTable<ValidityError> errors = new DefaultCellTable<ValidityError>(5,keyProvider);

		TextColumn<ValidityError> modelPath = new TextColumn<ValidityError>() {
			@Override
			public String getValue(ValidityError record) {
				return record.getPath();
			}
		};
		Column<ValidityError, String> errorMsg = new Column<ValidityError, String>(new TextAreaCell()) {
			@Override
			public String getValue(ValidityError record) {
				return record.getMessage();
			}
		};
		
		errors.setTitle("Validation Errors");
		errors.addColumn(modelPath, "Path");
		errors.addColumn(errorMsg, "Error/Warnings");
		errors.setSelectionModel(new SingleSelectionModel<ValidityError>(keyProvider));
		return errors;
	}
	
	static DefaultCellTable<ModelNode> buildSQLResultTable( List<ModelNode> list) {
		DefaultCellTable<ModelNode> result = new DefaultCellTable<ModelNode>(8);
		result.setTitle("SQL Result");
		Object[] attributes = list.get(0).keys().toArray();
		
		for(int i = 0;i<attributes.length;i++) {
			String name = attributes[i].toString();
			TextColumn<ModelNode> modelPath = new TextColumn<ModelNode>() {
				@Override
				public String getValue(ModelNode record) {
					return record.get(name).asString();
				}
			};
			result.addColumn(modelPath, name);
		}
		return result;
	}
	
	
	static DefaultCellTable<KeyValuePair> buildPropertiesTable() {
		ProvidesKey<KeyValuePair> keyProvider = new ProvidesKey<KeyValuePair>() {
			@Override
			public Object getKey(KeyValuePair item) {
				return item.getKey();
			}
		};
		DefaultCellTable<KeyValuePair> propertiesTable = new DefaultCellTable<KeyValuePair>(8, keyProvider);

		TextColumn<KeyValuePair> nameColumn = new TextColumn<KeyValuePair>() {
			@Override
			public String getValue(KeyValuePair record) {
				return record.getKey();
			}
		};
		TextColumn<KeyValuePair> valueColumn = new TextColumn<KeyValuePair>() {
			@Override
			public String getValue(KeyValuePair record) {
				return record.getValue();
			}
		};
		propertiesTable.setTitle("Properties");
		propertiesTable.addColumn(nameColumn, "Key");
		propertiesTable.addColumn(valueColumn, "Value");
		propertiesTable.setSelectionModel(new SingleSelectionModel<KeyValuePair>(keyProvider));
		return propertiesTable;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void terminateSessionSubmitted(Session session) {
		this.vdbSessionsTab.terminateSubmitted(session);
	}

	@Override
	public void setVDBSessions(String vdbName, String version, List<Session> sessions) {
		this.vdbSessionsTab.setSessions(sessions);
	}

	@Override
	public <T> void setQueryResults(List<T> results, String sql, String clazz) {
		this.vdbCachingTab.setQueryResults(results, clazz);
		this.sqlWorkbenchEditor.setQueryResults(results, sql, clazz);
	}
	
	@Override
	public void connectionTypeChanged(String vdbName, String version) {
		this.presenter.refresh(false);
	}
	
	private void reloadVDB(VDB vdb) {
		this.presenter.reloadVDB(vdb.getName(), vdb.getVersion());
	}

	@Override
	public void vdbReloaded(String vdbName, String version) {
		this.presenter.refresh(false);
	}

	@Override
	public void setCacheStatistics(CacheStatistics cache) {
		this.vdbCachingTab.setCacheStatistics(cache);
	}

	@Override
	public void setSourceRequests(Request selection, List<Request> requests) {
		this.vdbRequestsTab.setSourceRequests(selection, requests);
	}

    @Override
    public void setEngineStatistics(EngineStatistics stats) {
        this.metricsEditor.setEngineStatistics(stats);
    }
}
