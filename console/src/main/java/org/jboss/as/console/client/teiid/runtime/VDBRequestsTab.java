package org.jboss.as.console.client.teiid.runtime;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.jboss.as.console.client.teiid.model.Request;
import org.jboss.as.console.client.teiid.model.VDB;
import org.jboss.as.console.client.teiid.runtime.VDBView.TableSelectionCallback;
import org.jboss.as.console.client.teiid.widgets.DefaultPopUpWindow;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

@SuppressWarnings("nls")
public class VDBRequestsTab extends VDBProvider {
	private VDBPresenter presenter;
	private ListDataProvider<Request> requestProvider = new ListDataProvider<Request>();
	private DefaultCellTable requestsTable;
	private boolean showSourceQueries = false;
	
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
		
		final CheckBox sourceQueryBtn = new CheckBox("Show Source Queries");
		sourceQueryBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				showSourceQueries = sourceQueryBtn.getValue();
				refresh();
			}
		});
		sourceQueryBtn.setValue(this.showSourceQueries);
		
		ListHandler<Request> sortHandler = new ListHandler<Request>(this.requestProvider.getList());
        this.requestsTable = getRequestsTable(sortHandler);
        this.requestProvider.addDataDisplay(this.requestsTable);        
        
        VDBView.onTableSectionChange(vdbTable, new TableSelectionCallback<VDB> (){
			@Override
			public void onSelectionChange(VDB selection) {
				if (selection != null) {
					setVdbName(selection.getName());
					setVdbVersion(selection.getVersion());
					refresh();
				}
				else {
					requestProvider.setList(Collections.EMPTY_LIST);
				}
			}
        });        
        DefaultPager requestsTablePager = new DefaultPager();
        requestsTablePager.setDisplay(this.requestsTable);
                
        // build overall panel
        VerticalPanel formPanel = new VerticalPanel();
        formPanel.add(refreshBtn);
        formPanel.add(sourceQueryBtn);
        formPanel.add(this.requestsTable.asWidget());
        formPanel.add(requestsTablePager);
        formPanel.setCellHorizontalAlignment(refreshBtn,HasHorizontalAlignment.ALIGN_RIGHT);
        formPanel.setCellHorizontalAlignment(sourceQueryBtn,HasHorizontalAlignment.ALIGN_LEFT);
        
        return formPanel;  
    }

	private DefaultCellTable getRequestsTable(ListHandler<Request> sortHandler) {
		ProvidesKey<Request> keyProvider = new ProvidesKey<Request>() {
            @Override
            public Object getKey(Request item) {
                return item.getExecutionId();
            }
        };
        
		final DefaultCellTable table = new DefaultCellTable<Request>(20, keyProvider);   
		table.addColumnSortHandler(sortHandler);
        
        TextColumn<Request> executionIdColumn = new TextColumn<Request>() {
            @Override
            public String getValue(Request record) {
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
        table.addColumn(executionIdColumn, "Execution Id");
        table.addColumn(sessionIdColumn, "Session Id");
        table.addColumn(timeColumn, "Start Time");
        table.addColumn(cmdColumn, "Command");
        table.addColumn(planBtn, "Query Plan");
        table.addColumn(cancelBtn, "Cancel Query");
		
        // sets initial sorting
        table.getColumnSortList().push(executionIdColumn);        
		return table;
	}	
	
	public void setRequests(List<Request> requests) {
		if (requests != null && !requests.isEmpty()) {
			this.requestProvider.getList().clear();
			this.requestProvider.getList().addAll(requests);
			this.requestsTable.getSelectionModel().setSelected(requests.get(0), true);
		}
		else {
			this.requestProvider.getList().clear();
		}
	}
	
	private void refresh() {
		presenter.getRequests(getVdbName(), getVdbVersion(), this.showSourceQueries);
	}
	
	private void cancelQuery(Request request) {
		presenter.cancelRequest(request);
	}
	
	private void showPlanDialog(Request request) {
		presenter.getQueryPlan(request);
	}
	
	public void setQueryPlan(String plan) {
		QueryPlanPopUpWindow showPlanDialogBox = new QueryPlanPopUpWindow("Query Plan", SafeHtmlUtils.htmlEscape(plan));
		showPlanDialogBox.show();
	}
	
	public void cancelSubmitted(Request request) {
		refresh();
	}
}
