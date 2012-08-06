package org.jboss.as.console.client.teiid.runtime;

import java.util.Comparator;

import org.jboss.as.console.client.teiid.model.KeyValuePair;
import org.jboss.as.console.client.teiid.model.MaterializedView;
import org.jboss.as.console.client.teiid.model.VDB;
import org.jboss.as.console.client.teiid.model.VDBTranslator;
import org.jboss.as.console.client.teiid.runtime.VDBView.TableSelectionCallback;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

@SuppressWarnings("nls")
public class VDBTranslatorsTab extends VDBProvider {
	private VDBPresenter presenter;
	
	public VDBTranslatorsTab(VDBPresenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
    public VerticalPanel getPanel(DefaultCellTable vdbTable) {
		final Form<VDBTranslator> form = new Form<VDBTranslator>(VDBTranslator.class);
        final ListDataProvider<VDBTranslator> translatorProvider = new ListDataProvider<VDBTranslator>();
        final ListDataProvider<KeyValuePair> propertyProvider = new ListDataProvider<KeyValuePair>();
        ListHandler<VDBTranslator> sortHandler = new ListHandler<VDBTranslator>(translatorProvider.getList());

        // the main translator table
		final DefaultCellTable<VDBTranslator> translatorsTable = getTranslatorsTable(sortHandler);
        translatorProvider.addDataDisplay(translatorsTable);        
        VDBView.onTableSectionChange(vdbTable, new TableSelectionCallback<VDB> (){
			@Override
			public void onSelectionChange(VDB selection) {
				if (selection != null && !selection.getOverrideTranslators().isEmpty()) {
						translatorProvider.getList().clear();
						translatorProvider.getList().addAll(selection.getOverrideTranslators());
						translatorsTable.getSelectionModel().setSelected(selection.getOverrideTranslators().get(0), true);
						VDBTranslator selected = translatorsTable.getPreviousSelectedEntity();
						if (selected != null) {
							propertyProvider.getList().clear();
							propertyProvider.getList().addAll(selected.getProperties());
						}
				}
				else {
					translatorProvider.getList().clear();
					propertyProvider.getList().clear();
					form.clearValues();
				}
			}
        });
        DefaultPager translatorsTablePager = new DefaultPager();
        translatorsTablePager.setDisplay(translatorsTable);
        
        
        // Details about Model        
        form.setNumColumns(1);
        form.setEnabled(false);

        TextItem descriptionLabel = new TextItem("description", "Description");
        
        form.setFields(descriptionLabel);
        form.bind(translatorsTable);        
        
        // Properties in translator
        Label propertiesLabel = new Label("Properties");
        propertiesLabel.getElement().setAttribute("style", "margin-top:10px;margin-bottom:10px;font-weight:bold;");
        
        final DefaultCellTable propertiesTable = VDBView.buildPropertiesTable(); 
        //propertiesTable.p
        propertyProvider.addDataDisplay(propertiesTable);   
        VDBView.onTableSectionChange(translatorsTable, new TableSelectionCallback<VDBTranslator> (){
			@Override
			public void onSelectionChange(VDBTranslator translator) {
				propertyProvider.getList().clear();
				propertyProvider.getList().addAll(translator.getProperties());
			}
        });
        DefaultPager propertiesTablePager = new DefaultPager();
        propertiesTablePager.setDisplay(propertiesTable);
        
        // build overall panel
        VerticalPanel formPanel = new VerticalPanel();
        formPanel.add(translatorsTable.asWidget());
        formPanel.add(translatorsTablePager);
        formPanel.add(form.asWidget());
        formPanel.add(propertiesLabel.asWidget());
        formPanel.add(propertiesTable.asWidget());   
        formPanel.add(propertiesTablePager);
        return formPanel;  
    }


	private DefaultCellTable<VDBTranslator> getTranslatorsTable(ListHandler<VDBTranslator> sortHandler) {
		ProvidesKey<VDBTranslator> keyProvider = new ProvidesKey<VDBTranslator>() {
            @Override
            public Object getKey(VDBTranslator item) {
                return getVdbName()+"."+getVdbVersion()+"."+item.getName();
            }
        };
        
		final DefaultCellTable<VDBTranslator> table = new DefaultCellTable<VDBTranslator>(5, keyProvider);   
		table.addColumnSortHandler(sortHandler);
        
        TextColumn<VDBTranslator> nameColumn = new TextColumn<VDBTranslator>() {
            @Override
            public String getValue(VDBTranslator record) {
                return record.getName();
            }
        };
        nameColumn.setSortable(true);
        sortHandler.setComparator(nameColumn, new Comparator<VDBTranslator>() {
			@Override
			public int compare(VDBTranslator o1, VDBTranslator o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});	        
        
        TextColumn<VDBTranslator> typeColumn = new TextColumn<VDBTranslator>() {
            @Override
            public String getValue(VDBTranslator record) {
                return record.getType();
            }
        };
        typeColumn.setSortable(true);
        sortHandler.setComparator(typeColumn, new Comparator<VDBTranslator>() {
			@Override
			public int compare(VDBTranslator o1, VDBTranslator o2) {
				return o1.getType().compareTo(o2.getType());
			}
		});        
        
        TextColumn<VDBTranslator> moduleColumn = new TextColumn<VDBTranslator>() {
            @Override
            public String getValue(VDBTranslator record) {
                return String.valueOf(record.getModuleName());
            }
        };  
        moduleColumn.setSortable(true);
        sortHandler.setComparator(moduleColumn, new Comparator<VDBTranslator>() {
			@Override
			public int compare(VDBTranslator o1, VDBTranslator o2) {
				return o1.getModuleName().compareTo(o2.getModuleName());
			}
		});          
        
        table.setSelectionModel(new SingleSelectionModel<VDBTranslator>(keyProvider));
        
        table.setTitle("Translators");
        table.addColumn(nameColumn, "Name");
        table.addColumn(typeColumn, "Type");
        table.addColumn(moduleColumn, "Module Name");     
        
		// sets initial sorting
        table.getColumnSortList().push(nameColumn);
        
		return table;
	}	
}
