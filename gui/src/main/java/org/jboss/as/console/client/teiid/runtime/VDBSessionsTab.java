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
import java.util.Date;
import java.util.List;

import org.jboss.as.console.client.teiid.model.Session;
import org.jboss.as.console.client.teiid.model.VDB;
import org.jboss.as.console.client.teiid.runtime.VDBView.TableSelectionCallback;
import org.jboss.ballroom.client.widgets.common.DefaultButton;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

@SuppressWarnings("nls")
public class VDBSessionsTab extends VDBProvider {
	private VDBPresenter presenter;
	private ListDataProvider<Session> sessionProvider = new ListDataProvider<Session>();
	private DefaultCellTable sessionTable;
	static DateTimeFormat dtf = DateTimeFormat.getFormat("MM/dd/yy HH:mm:ss");
	
	public VDBSessionsTab(VDBPresenter presenter) {
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
		
		ListHandler<Session> sortHandler = new ListHandler<Session>(this.sessionProvider.getList());
        this.sessionTable = getSessionTable(sortHandler);
        this.sessionProvider.addDataDisplay(this.sessionTable);        
        
        VDBView.onTableSectionChange(vdbTable, new TableSelectionCallback<VDB> (){
			@Override
			public void onSelectionChange(VDB selection) {
				if (selection != null && isActive(selection)) {
					setVdbName(selection.getName());
					setVdbVersion(selection.getVersion());
					refresh();
				}
				else {
					sessionProvider.getList().clear();
				}
			}
        });        
        DefaultPager sessionTablePager = new DefaultPager();
        sessionTablePager.setDisplay(this.sessionTable);
     
        final Form<Session> form = new Form<Session>(Session.class);
        form.setNumColumns(1);
        form.setEnabled(false);

        TextItem clientHostNameLabel = new TextItem("clientHostName", "Client Host Name");
        TextItem clientHostAddressLabel = new TextItem("clientHardwareAddress", "Client Hardware Address");
        TextItem ipAddressLabel = new TextItem("iPAddress", "IP Address");
        TextItem securityDomainLabel = new TextItem("securityDomain", "Security Domain");
        
        form.setFields(clientHostNameLabel, clientHostAddressLabel, ipAddressLabel, securityDomainLabel);
        form.bind(this.sessionTable);        
        
		DefaultButton terminateAllBtn = new DefaultButton("Terminate All", new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				terminateAll();
			}
		});
        
        
        // build overall panel
        VerticalPanel formPanel = new VerticalPanel();
        formPanel.add(refreshBtn);
        formPanel.add(this.sessionTable.asWidget());
        formPanel.add(sessionTablePager);
        formPanel.add(terminateAllBtn);
        formPanel.add(form);
        formPanel.setCellHorizontalAlignment(refreshBtn,HasHorizontalAlignment.ALIGN_RIGHT);
        formPanel.setCellHorizontalAlignment(terminateAllBtn,HasHorizontalAlignment.ALIGN_RIGHT);
        
        return formPanel;  
    }

	private DefaultCellTable getSessionTable(ListHandler<Session> sortHandler) {
		ProvidesKey<Session> keyProvider = new ProvidesKey<Session>() {
            @Override
            public Object getKey(Session item) {
                return item.getSessionId();
            }
        };
        
		final DefaultCellTable table = new DefaultCellTable<Session>(20, keyProvider);   
		table.addColumnSortHandler(sortHandler);
        
        TextColumn<Session> idColumn = new TextColumn<Session>() {
            @Override
            public String getValue(Session record) {
                return record.getSessionId();
            }
        };
        idColumn.setSortable(true);
        sortHandler.setComparator(idColumn, new Comparator<Session>() {
			@Override
			public int compare(Session o1, Session o2) {
				return o1.getSessionId().compareTo(o2.getSessionId());
			}
		});	        
        
        TextColumn<Session> userNameColumn = new TextColumn<Session>() {
            @Override
            public String getValue(Session record) {
                return String.valueOf(record.getUserName());
            }
        };
        userNameColumn.setSortable(true);
        sortHandler.setComparator(userNameColumn, new Comparator<Session>() {
			@Override
			public int compare(Session o1, Session o2) {
				return o1.getUserName().compareTo(o2.getUserName());
			}
		});	        
        
        TextColumn<Session> createdColumn = new TextColumn<Session>() {
            @Override
            public String getValue(Session record) {
                return dtf.format(new Date(record.getCreatedTime()), TimeZone.createTimeZone(0));
            }
        };           
        createdColumn.setSortable(true);
        sortHandler.setComparator(createdColumn, new Comparator<Session>() {
 			@Override
 			public int compare(Session o1, Session o2) {
 				return o1.getCreatedTime().compareTo(o2.getCreatedTime());
 			}
 		});	         
                
        TextColumn<Session> lastPingColumn = new TextColumn<Session>() {
            @Override
            public String getValue(Session record) {
                return dtf.format(new Date(Long.parseLong(record.getLastPingTime())), TimeZone.createTimeZone(0));
            }
        };           
        lastPingColumn.setSortable(true);
        sortHandler.setComparator(lastPingColumn, new Comparator<Session>() {
 			@Override
 			public int compare(Session o1, Session o2) {
 				return o1.getLastPingTime().compareTo(o2.getLastPingTime());
 			}
 		});	        
        
        TextColumn<Session> applicationNameColumn = new TextColumn<Session>() {
            @Override
            public String getValue(Session record) {
                return record.getApplicationName();
            }
        };
        applicationNameColumn.setSortable(true);        
        sortHandler.setComparator(applicationNameColumn, new Comparator<Session>() {
 			@Override
 			public int compare(Session o1, Session o2) {
 				return o1.getApplicationName().compareTo(o2.getApplicationName());
 			}
 		});	        

        Column<Session, String> terminateBtn = new Column<Session, String>(new ButtonCell()) {
            @Override
            public String getValue(Session record) {
                return "Terminate"; 
            }        	
        };
        terminateBtn.setFieldUpdater(new FieldUpdater<Session, String>() {
			@Override
			public void update(int index, Session session, String value) {
				terminate(session);
			}
        });        
        
        table.setSelectionModel(new SingleSelectionModel<Session>(keyProvider));
        
        table.setTitle("Sessions");
        table.addColumn(idColumn, "Session Id");
        table.addColumn(userNameColumn, "User");
        table.addColumn(createdColumn, "Created");
        table.addColumn(lastPingColumn, "Last Ping");
        table.addColumn(applicationNameColumn, "Application");
        table.addColumn(terminateBtn, "Terminate");
        
		// sets initial sorting
        table.getColumnSortList().push(idColumn);
        
		return table;
	}	
	
	public void setSessions(List<Session> sessions) {
		if (sessions != null && !sessions.isEmpty()) {
			this.sessionProvider.getList().clear();
			this.sessionProvider.getList().addAll(sessions);
			this.sessionTable.getSelectionModel().setSelected(sessions.get(0), true);
		}
		else {
			this.sessionProvider.getList().clear();
		}
	}
	
	private void refresh() {
		presenter.getSessions(getVdbName(), getVdbVersion());
	}
	
	private void terminate(Session session) {
		presenter.terminateSession(session);
	}
	
	public void terminateSubmitted(Session session) {
		refresh();
	}
	
	private void terminateAll() {
		for(Session s:this.sessionProvider.getList()) {
			terminate(s);
		}
	}
}
