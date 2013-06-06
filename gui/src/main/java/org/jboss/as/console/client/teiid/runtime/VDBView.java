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
package org.jboss.as.console.client.teiid.runtime;

import java.util.Comparator;
import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.SuspendableViewImpl;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.teiid.model.CacheStatistics;
import org.jboss.as.console.client.teiid.model.DataModelFactory;
import org.jboss.as.console.client.teiid.model.KeyValuePair;
import org.jboss.as.console.client.teiid.model.Request;
import org.jboss.as.console.client.teiid.model.Session;
import org.jboss.as.console.client.teiid.model.VDB;
import org.jboss.as.console.client.teiid.model.ValidityError;
import org.jboss.as.console.client.teiid.widgets.TeiidIcons;
import org.jboss.as.console.client.widgets.pages.PagedView;
import org.jboss.ballroom.client.widgets.icons.Icons;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tabs.FakeTabPanel;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

@SuppressWarnings("nls")
public class VDBView extends SuspendableViewImpl implements VDBPresenter.MyView {
	private PagedView pages;
	private ListDataProvider<VDB> vdbProvider = new ListDataProvider<VDB>();
	private DefaultCellTable vdbTable;
	private VDBPresenter presenter;
	private VDBRequestsTab vdbRequestsTab;
	private VDBModelsTab vdbModelsTab;
	private VDBSessionsTab vdbSessionsTab;
	private VDBCachingTab vdbCachingTab;
	private DataModelFactory factory;

	public void setDataModelFactory(DataModelFactory factory) {
		this.factory = factory;					
	}
	
	@Override
	public void setDeployedVDBs(List<VDB> vdbs) {
		if (vdbs != null && !vdbs.isEmpty()) {
			this.vdbProvider.getList().clear();
			this.vdbProvider.getList().addAll(vdbs);
	        this.vdbTable.getSelectionModel().setSelected(vdbs.get(0), true);
		}
		else {
			this.vdbProvider.getList().clear();
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

		pages = new PagedView();

		pages.addPage(Console.CONSTANTS.common_label_back(), mainPageAsWidget());

		// default page
		pages.showPage(0);

		LayoutPanel layout = new LayoutPanel();

		// Top Most Tab
		FakeTabPanel titleBar = new FakeTabPanel("Virtual Databases");
		layout.add(titleBar);

		Widget pagesWidget = pages.asWidget();
		layout.add(pagesWidget);

		layout.setWidgetTopHeight(titleBar, 0, Style.Unit.PX, 40, Style.Unit.PX);
		layout.setWidgetTopHeight(pagesWidget, 40, Style.Unit.PX, 100,
				Style.Unit.PCT);

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

		Column<VDB, Number> versionColumn = new Column<VDB, Number>(
				new NumberCell()) {
			@Override
			public Number getValue(VDB vdb) {
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
		TextColumn<ValidityError> errorMsg = new TextColumn<ValidityError>() {
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
	public void setVDBSessions(String vdbName, int version, List<Session> sessions) {
		this.vdbSessionsTab.setSessions(sessions);
	}

	@Override
	public <T> void setQueryResults(List<T> results, String clazz) {
		this.vdbCachingTab.setQueryResults(results, clazz);
	}
	
	public void connectionTypeChanged(String vdbName, int version) {
		this.presenter.refresh(false);
	}
	
	private void reloadVDB(VDB vdb) {
		this.presenter.reloadVDB(vdb.getName(), vdb.getVersion());
	}

	@Override
	public void vdbReloaded(String vdbName, int version) {
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
}
