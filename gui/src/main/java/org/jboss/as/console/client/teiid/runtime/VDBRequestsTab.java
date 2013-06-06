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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.jboss.as.console.client.teiid.model.Request;
import org.jboss.as.console.client.teiid.model.VDB;
import org.jboss.as.console.client.teiid.runtime.VDBView.TableSelectionCallback;
import org.jboss.as.console.client.teiid.widgets.QueryPlanPopUpWindow;
import org.jboss.ballroom.client.widgets.common.DefaultButton;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

@SuppressWarnings("nls")
public class VDBRequestsTab extends VDBProvider {
	private VDBPresenter presenter;
	private ListDataProvider<Request> requestProvider = new ListDataProvider<Request>();
	private DefaultCellTable requestsTable;
	private ListDataProvider<Request> sourceRequestProvider = new ListDataProvider<Request>();
	private DefaultCellTable sourceRequestsTable;
	
	
	public VDBRequestsTab(VDBPresenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
    public VerticalPanel getPanel(DefaultCellTable vdbTable) {
		DefaultButton refreshBtn = new DefaultButton("Refresh", new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				refresh();
			}
		});
		refreshBtn.getElement().setAttribute("style", "margin-bottom:10px;");
		
		ListHandler<Request> sortHandler = new ListHandler<Request>(this.requestProvider.getList());
        this.requestsTable = getRequestsTable(sortHandler, false);
        this.requestProvider.addDataDisplay(this.requestsTable);        
        
        VDBView.onTableSectionChange(vdbTable, new TableSelectionCallback<VDB> (){
			@Override
			public void onSelectionChange(VDB selection) {
				if (selection != null && isActive(selection)) {
					setVdbName(selection.getName());
					setVdbVersion(selection.getVersion());
					refresh();
				}
				else {
					requestProvider.getList().clear();
					sourceRequestProvider.getList().clear();
				}
			}
        });        
        DefaultPager requestsTablePager = new DefaultPager();
        requestsTablePager.setDisplay(this.requestsTable);
        
        
        // source requests table
		ListHandler<Request> sourceSortHandler = new ListHandler<Request>(this.sourceRequestProvider.getList());
        this.sourceRequestsTable = getRequestsTable(sourceSortHandler, true);
        this.sourceRequestProvider.addDataDisplay(this.sourceRequestsTable);        
        
        VDBView.onTableSectionChange(this.requestsTable, new TableSelectionCallback<Request> (){
			@Override
			public void onSelectionChange(Request selection) {
				if (selection != null) {
					refreshSources(selection);
				}
				else {
					sourceRequestProvider.getList().clear();
				}
			}
        });        
        DefaultPager sourceRequestsTablePager = new DefaultPager();
        sourceRequestsTablePager.setDisplay(this.sourceRequestsTable);
        
                
        // build overall panel
        VerticalPanel formPanel = new VerticalPanel();
        formPanel.add(refreshBtn);
        formPanel.add(this.requestsTable.asWidget());
        formPanel.add(requestsTablePager);
        formPanel.add(new Label("Source Queries"));
        formPanel.add(this.sourceRequestsTable.asWidget());
        formPanel.add(sourceRequestsTablePager);
        formPanel.setCellHorizontalAlignment(refreshBtn,HasHorizontalAlignment.ALIGN_RIGHT);
        
        return formPanel;  
    }

	private DefaultCellTable getRequestsTable(ListHandler<Request> sortHandler, final boolean sourceTable) {
		ProvidesKey<Request> keyProvider = new ProvidesKey<Request>() {
            @Override
            public Object getKey(Request item) {
            	if (sourceTable) {
            		return item.getNodeId();
            	}
                return item.getExecutionId();
            }
        };
        
		final DefaultCellTable table = new DefaultCellTable<Request>(20, keyProvider);   
		table.addColumnSortHandler(sortHandler);
        
        TextColumn<Request> executionIdColumn = new TextColumn<Request>() {
            @Override
            public String getValue(Request record) {
            	if (sourceTable) {
            		return String.valueOf(record.getNodeId());
            	}
                return String.valueOf(record.getExecutionId());
            }
        };
        executionIdColumn.setSortable(true);
        sortHandler.setComparator(executionIdColumn, new Comparator<Request>() {
			@Override
			public int compare(Request o1, Request o2) {
				return o1.getExecutionId().compareTo(o2.getExecutionId());
			}
		});        
        
        TextColumn<Request> sessionIdColumn = new TextColumn<Request>() {
            @Override
            public String getValue(Request record) {
                return String.valueOf(record.getSessionId());
            }
        };    
        sessionIdColumn.setSortable(true);
        sortHandler.setComparator(sessionIdColumn, new Comparator<Request>() {
			@Override
			public int compare(Request o1, Request o2) {
				return o1.getSessionId().compareTo(o2.getSessionId());
			}
		});        
                
        Column<Request, Date> timeColumn = new Column<Request, Date>(new DateCell()) {
            @Override
            public Date getValue(Request record) {
                return new Date(record.getStartTime());
            }
        };           
        timeColumn.setSortable(true);
        sortHandler.setComparator(timeColumn, new Comparator<Request>() {
			@Override
			public int compare(Request o1, Request o2) {
				return o1.getStartTime().compareTo(o2.getStartTime());
			}
		});         

        Column<Request, String> cmdColumn = new Column<Request, String>(new ClickableTextCell()) {
            @Override
            public String getValue(Request record) {
                return record.getCommand();
            }
        };
        sortHandler.setComparator(cmdColumn, new Comparator<Request>() {
			@Override
			public int compare(Request o1, Request o2) {
				return o1.getCommand().compareTo(o2.getCommand());
			}
		});        
        
        Column<Request, String> planBtn = new Column<Request, String>(new ButtonCell()) {
            @Override
            public String getValue(Request record) {
                return "Plan"; 
            }        	
        };
        planBtn.setFieldUpdater(new FieldUpdater<Request, String>() {
			@Override
			public void update(int index, Request request, String value) {
				showPlanDialog(request);
			}
        });        
        Column<Request, String> cancelBtn = new Column<Request, String>(new ButtonCell()) {
            @Override
            public String getValue(Request record) {
        		return "Cancel";
            }        	
        };
        cancelBtn.setFieldUpdater(new FieldUpdater<Request, String>() {
			@Override
			public void update(int index, Request request, String value) {
			    cancelQuery(request);
			}
        });
        
        table.setSelectionModel(new SingleSelectionModel<Request>(keyProvider));
        
        table.setTitle("Requests");
        if (sourceTable) {
        	table.addColumn(executionIdColumn, "Node Id");
        }
        else {
        	table.addColumn(executionIdColumn, "Execution Id");
        }
        table.addColumn(sessionIdColumn, "Session Id");
        table.addColumn(timeColumn, "Start Time");
        table.addColumn(cmdColumn, "Command");
        if (!sourceTable) {
        	table.addColumn(planBtn, "Query Plan");
        	table.addColumn(cancelBtn, "Cancel Query");
        }
		
        // sets initial sorting
        table.getColumnSortList().push(executionIdColumn);        
		return table;
	}	
	
	public void setRequests(List<Request> requests) {
		this.requestProvider.getList().clear();
		if (requests != null && !requests.isEmpty()) {
			this.requestProvider.getList().addAll(requests);
			this.requestsTable.getSelectionModel().setSelected(requests.get(0), true);
		}
		else {
			this.sourceRequestProvider.getList().clear();
		}
	}
	
	private void refresh() {
		presenter.getRequests(getVdbName(), getVdbVersion(), false);
	}
	
	private void cancelQuery(Request request) {
		presenter.cancelRequest(request);
	}
	
	private void showPlanDialog(Request request) {
		presenter.getQueryPlan(request);
	}
	
	public void setQueryPlan(String plan) {
		QueryPlanPopUpWindow showPlanDialogBox = new QueryPlanPopUpWindow("Query Plan", plan);
		showPlanDialogBox.show();
	}
	
	public void cancelSubmitted(Request request) {
		refresh();
	}
	
	private void refreshSources(Request selection) {
		presenter.getSourceRequests(selection);
	}	
	
	public void setSourceRequests(Request selection, List<Request> requests) {
		this.sourceRequestProvider.getList().clear();
		if (requests != null && !requests.isEmpty()) {
			ArrayList<Request> sourceOnly = new ArrayList<Request>();
			for (Request request:requests) {
				if (request.isSourceRequest() && request.getExecutionId().equals(selection.getExecutionId())) {
					sourceOnly.add(request);
				}
			}
			if (!sourceOnly.isEmpty()) {
				this.sourceRequestProvider.getList().addAll(sourceOnly);
				this.sourceRequestsTable.getSelectionModel().setSelected(sourceOnly.get(0), true);
			}
		}
	}	
}
