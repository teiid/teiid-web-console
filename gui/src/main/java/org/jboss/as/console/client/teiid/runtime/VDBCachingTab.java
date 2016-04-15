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

import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.teiid.model.CacheStatistics;
import org.jboss.as.console.client.teiid.model.MaterializedView;
import org.jboss.as.console.client.teiid.model.VDB;
import org.jboss.as.console.client.teiid.runtime.VDBView.TableSelectionCallback;
import org.jboss.ballroom.client.widgets.common.DefaultButton;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

@SuppressWarnings("nls")
public class VDBCachingTab extends VDBProvider {
	private static final String INVALIDATE = "Invalidate";
	private VDBPresenter presenter;
	static DateTimeFormat dtf = DateTimeFormat.getFormat("MM/dd/yy HH:mm:ss");
	private ListDataProvider<MaterializedView> matviewProvider = new ListDataProvider<MaterializedView>();
	private DefaultCellTable matviewTable;
	Label hitRatio = new Label();
	NumberLabel<Integer> totalEntries = new NumberLabel();
	NumberLabel<Integer> requestCount = new NumberLabel();
			
	public VDBCachingTab(VDBPresenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
    public VerticalPanel getPanel(DefaultCellTable vdbTable) {
		
		Label hitRatioLabel = new Label("Hit Ratio:");
        hitRatioLabel.getElement().setAttribute("style", "margin-right:5px;");
        hitRatio.getElement().setAttribute("style", "margin-right:20px;");
        
		Label totalEntriesLabel = new Label("Total Entries:");
        totalEntriesLabel.getElement().setAttribute("style", "margin-right:5px;");
        totalEntries.getElement().setAttribute("style", "margin-right:20px;");
        
		Label requestCountLabel = new Label("Request Count:");
        requestCountLabel.getElement().setAttribute("style", "margin-right:5px;");
        requestCount.getElement().setAttribute("style", "margin-right:20px;");
		
        HorizontalPanel statsPanel = new HorizontalPanel();
        statsPanel.add(hitRatioLabel);
        statsPanel.add(hitRatio);
        statsPanel.add(totalEntriesLabel);
        statsPanel.add(totalEntries);
        statsPanel.add(requestCountLabel);
        statsPanel.add(requestCount);      
        statsPanel.getElement().setAttribute("style", "margin-top:5px;margin-bottom:5px;");
        CaptionPanel cacheStatsPanel = new CaptionPanel("Cache Statistics");
        cacheStatsPanel.add(statsPanel);
		
		
		DefaultButton refreshBtn = new DefaultButton("Refresh", new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				refresh();
			}
		});

        // Materilized Tables
        Label matviewLabel = new Label("Materialized Tables");
        matviewLabel.getElement().setAttribute("style", "margin-top:10px;margin-bottom:5px;font-weight:bold;");
        
        this.matviewTable = getMaterializedTable();
        this.matviewProvider.addDataDisplay(matviewTable);   
        VDBView.onTableSectionChange(vdbTable, new TableSelectionCallback<VDB> (){
			@Override
			public void onSelectionChange(VDB vdb) {
				if (vdb != null && isActive(vdb)) {
					setVdbName(vdb.getName());
					setVdbVersion(vdb.getVersion());
					refresh();
				}
				else {
					setQueryResults(null, MaterializedView.class.getName());
				}
			}
        });
        DefaultPager propertiesTablePager = new DefaultPager();
        propertiesTablePager.setDisplay(matviewTable);
        
		DefaultButton invalidateAllBtn = new DefaultButton("Invalidate All", new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				invalidateAll();
			}
		});
		invalidateAllBtn.getElement().setAttribute("style", "margin-top:5px;margin-bottom:5px;");
        
		//clear cache option.
		CaptionPanel clearCacheCaptionPanel = new CaptionPanel("Clear Cache");
		VerticalPanel clearCachePanel = new VerticalPanel();
		final RadioButton resultSetBtn = new RadioButton("clearcache", "ResultSet");
		final RadioButton preparedPlanBtn = new RadioButton("clearcache", "Prepared Plan");
		DefaultButton clearBtn = new DefaultButton("Clear", new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				if (resultSetBtn.getValue()) {
					clearCache("QUERY_SERVICE_RESULT_SET_CACHE");	
				}
				else {
					clearCache("PREPARED_PLAN_CACHE");
				}
			}
		});
		resultSetBtn.setEnabled(true);
		resultSetBtn.setValue(true);
		preparedPlanBtn.setEnabled(true);
		clearCachePanel.add(resultSetBtn);
		clearCachePanel.add(preparedPlanBtn);
		clearCachePanel.setCellHorizontalAlignment(resultSetBtn, HasHorizontalAlignment.ALIGN_LEFT);
		clearCachePanel.setCellHorizontalAlignment(preparedPlanBtn, HasHorizontalAlignment.ALIGN_LEFT);
		clearCachePanel.add(clearBtn);
		clearCacheCaptionPanel.add(clearCachePanel);
        
        VerticalPanel formPanel = new VerticalPanel();
        formPanel.add(cacheStatsPanel);
        formPanel.add(matviewLabel.asWidget());
        formPanel.add(refreshBtn);
        formPanel.add(matviewTable.asWidget());
        formPanel.add(propertiesTablePager);
        formPanel.add(invalidateAllBtn);
        formPanel.add(clearCacheCaptionPanel);
        formPanel.setCellHorizontalAlignment(refreshBtn,HasHorizontalAlignment.ALIGN_RIGHT);
        formPanel.setCellHorizontalAlignment(invalidateAllBtn,HasHorizontalAlignment.ALIGN_RIGHT);
        
        return formPanel;
    }

	private DefaultCellTable getMaterializedTable() {
		ProvidesKey keyProvider = new ProvidesKey<MaterializedView>() {
            @Override
            public Object getKey(MaterializedView item) {
                return getVdbName()+"."+getVdbVersion()+"."+item.getModelName()+"."+item.getTableName();
            }
        };		
        
		final DefaultCellTable table = new DefaultCellTable<MaterializedView>(10, keyProvider);   
        
        TextColumn<MaterializedView> nameColumn = new TextColumn<MaterializedView>() {
            @Override
            public String getValue(MaterializedView record) {
                return record.getTableName();
            }
        };
        nameColumn.setSortable(true);
        
        TextColumn<MaterializedView> modelNameColumn = new TextColumn<MaterializedView>() {
            @Override
            public String getValue(MaterializedView record) {
                return String.valueOf(record.getModelName());
            }
        };
        modelNameColumn.setSortable(true);
        
        TextColumn<MaterializedView> validColumn = new TextColumn<MaterializedView>() {
            @Override
            public String getValue(MaterializedView record) {
                return String.valueOf(record.isValid());
            }
        };           
        validColumn.setSortable(true);
                
        TextColumn<MaterializedView> lastUpdatedTime = new TextColumn<MaterializedView>() {
            @Override
            public String getValue(MaterializedView record) {
            	//return dtf.format(dtf.parse(record.getLastUpdatedTime()), TimeZone.createTimeZone(0));
            	return record.getLastUpdatedTime();
            }
        };           
        lastUpdatedTime.setSortable(true);
        
        TextColumn<MaterializedView> loadStateColumn = new TextColumn<MaterializedView>() {
            @Override
            public String getValue(MaterializedView record) {
                return record.getLoadState();
            }
        };
        loadStateColumn.setSortable(true);
        
        TextColumn<MaterializedView> cardinalityColumn = new TextColumn<MaterializedView>() {
            @Override
            public String getValue(MaterializedView record) {
                return String.valueOf(record.getCardinality());
            }
        };
        loadStateColumn.setSortable(true);        
        

        Column<MaterializedView, String> invalidateBtn = new Column<MaterializedView, String>(new ButtonCell()) {
            @Override
            public String getValue(MaterializedView record) {
                return "Reload"; 
            }        	
        };
        invalidateBtn.setFieldUpdater(new FieldUpdater<MaterializedView, String>() {
			@Override
			public void update(int index, MaterializedView table, String value) {
				invalidate(table, "Reload");
			}
        });        
        
        table.setSelectionModel(new SingleSelectionModel<MaterializedView>(keyProvider));
        
        table.setTitle("Mateiralized Tables");
        table.addColumn(nameColumn, "View");
        table.addColumn(modelNameColumn, "Model Name");
        table.addColumn(validColumn, "Valid");
        table.addColumn(lastUpdatedTime, "Last Updated");
        table.addColumn(loadStateColumn, "State");
        table.addColumn(cardinalityColumn, "# Rows");
        table.addColumn(invalidateBtn, INVALIDATE);
		return table;
	}	

	private void invalidate(MaterializedView view, String queryId) {
		String viewName = view.getModelName()+"."+view.getTableName();
		String sql = "CALL SYSADMIN.refreshMatView(viewname=>'"+viewName+"', invalidate=>true)";
		this.presenter.executeQuery(getVdbName(), getVdbVersion(), sql, queryId);
	}
	
	public <T> void setQueryResults(List<T> results, String clazz) {
		if (clazz.equals(MaterializedView.class.getName())) {
			if (results != null) {
				this.matviewProvider.getList().clear();
				this.matviewProvider.getList().addAll((List<MaterializedView>) results);
				if (!results.isEmpty()) {
					this.matviewTable.getSelectionModel().setSelected(results.get(0), true);
				}
			}
			else {
				this.matviewProvider.getList().clear();
			}
		}
		else if (clazz.equals(INVALIDATE)) {
			refresh();
		}
	}
	
	private void refresh() {
	    String sql = "SELECT M.VDBName, M.SchemaName, M.Name, S.TargetSchemaName, S.TargetName, S.Valid, S.LoadState, S.Updated, S.Cardinality FROM SYSADMIN.MatViews AS M, TABLE(CALL SYSADMIN.matViewStatus(M.SchemaName, M.Name)) AS S";
		this.presenter.executeQuery(getVdbName(), getVdbVersion(), sql, MaterializedView.class.getName());
		this.presenter.getCacheStatistics();
	}
	


    private void invalidateAll() {
		List<MaterializedView> views = this.matviewProvider.getList();
		int i = 0;
		for (i = 0; i < views.size()-1; i++) {
			invalidate(views.get(i), "NoRefresh");
		}
		invalidate(views.get(i), INVALIDATE);
	}
	
	private void clearCache(String cacheType) {
		this.presenter.clearCache(getVdbName(), getVdbVersion(), cacheType);
	}

	public void setCacheStatistics(CacheStatistics cache) {
		if (cache != null) {
			this.hitRatio.setText(cache.getHitRatio());
			this.totalEntries.setValue(cache.getTotalEntries());
			this.requestCount.setValue(cache.getRequestCount());
		}
		else {
			this.hitRatio.setText("0.0");
			this.totalEntries.setValue(0);
			this.requestCount.setValue(0);			
		}
	}
}
