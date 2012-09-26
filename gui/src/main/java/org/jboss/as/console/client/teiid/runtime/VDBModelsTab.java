package org.jboss.as.console.client.teiid.runtime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jboss.as.console.client.teiid.model.DataModelFactory;
import org.jboss.as.console.client.teiid.model.KeyValuePair;
import org.jboss.as.console.client.teiid.model.Model;
import org.jboss.as.console.client.teiid.model.SourceMapping;
import org.jboss.as.console.client.teiid.model.VDB;
import org.jboss.as.console.client.teiid.model.ValidityError;
import org.jboss.as.console.client.teiid.runtime.VDBView.TableSelectionCallback;
import org.jboss.as.console.client.teiid.widgets.DefaultPopUpWindow;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

@SuppressWarnings("nls")
public class VDBModelsTab extends VDBProvider {
	private DataModelFactory factory;
	private VDBPresenter presenter;
	
	public VDBModelsTab(VDBPresenter presenter) {
		this.presenter = presenter;
	}	
	
    public VerticalPanel getPanel(DefaultCellTable vdbTable) {

    	final ListDataProvider<Model> modelProvider = new ListDataProvider<Model>();
    	final ListDataProvider<KeyValuePair> propertyProvider = new ListDataProvider<KeyValuePair>();
    	final ListDataProvider<ValidityError> errorProvider = new ListDataProvider<ValidityError>();
    	ListHandler<Model> sortHandler = new ListHandler<Model>(modelProvider.getList());
    	
        final DefaultCellTable modelsTable = getModelTable(sortHandler);
        modelProvider.addDataDisplay(modelsTable);
        
        VDBView.onTableSectionChange(vdbTable, new TableSelectionCallback<VDB> (){
			@Override
			public void onSelectionChange(VDB selection) {
				if (selection != null && !selection.getModels().isEmpty()) {
					setVdbName(selection.getName());
					setVdbVersion(selection.getVersion());
					modelProvider.getList().clear();
					propertyProvider.getList().clear();
					errorProvider.getList().clear();
					
					List<Model> models = addMultiSourceModels(selection.getModels());
					modelProvider.getList().addAll(models);
					modelsTable.getSelectionModel().setSelected(models.get(0), true);
				}
				else {
					setVdbName(null);
					setVdbVersion(0);					
					modelProvider.getList().clear();
					propertyProvider.getList().clear();
					errorProvider.getList().clear();
				}
			}
        });   
        DefaultPager modelsTablePager = new DefaultPager();
        modelsTablePager.setDisplay(modelsTable);
        
        // Details about Model
        final Form<Model> form = new Form<Model>(Model.class);
        form.setNumColumns(1);
        form.setEnabled(false);

        TextItem pathLabel = new TextItem("modelPath", "Path");
        TextItem descriptionLabel = new TextItem("description", "Description");
        
        form.setFields(pathLabel, descriptionLabel);
        form.bind(modelsTable);        
        
        // Properties in the Model.
        Label propertiesLabel = new Label("Properties");
        propertiesLabel.getElement().setAttribute("style", "margin-top:10px;margin-bottom:10px;font-weight:bold;");
        
        final DefaultCellTable propertiesTable = VDBView.buildPropertiesTable();
        
        propertyProvider.addDataDisplay(propertiesTable);   
        VDBView.onTableSectionChange(modelsTable, new TableSelectionCallback<Model> (){
			@Override
			public void onSelectionChange(Model model) {
				setModelName(model.getName());
				if (!model.getProperties().isEmpty()) {
					propertyProvider.getList().clear();
					propertyProvider.getList().addAll(model.getProperties());
				}
				else {
					propertyProvider.getList().clear();
				}
			}
        });    
        DefaultPager propertiesTablePager = new DefaultPager();
        propertiesTablePager.setDisplay(propertiesTable);
        
        // Errors and warnings in the Model.
        Label errorLabel = new Label("Errors");
        errorLabel.getElement().setAttribute("style", "margin-top:10px;margin-bottom:10px;font-weight:bold;");
        
        final DefaultCellTable errorsTable = VDBView.buildErrorTable();        
        errorProvider.addDataDisplay(errorsTable);   
        VDBView.onTableSectionChange(modelsTable, new TableSelectionCallback<Model> (){
			@Override
			public void onSelectionChange(Model model) {
				errorProvider.getList().clear();
				errorProvider.getList().addAll(model.getValidityErrors());
			}
        });
        DefaultPager errorsTablePager = new DefaultPager();
        errorsTablePager.setDisplay(errorsTable);
        
        // build overall panel
        VerticalPanel formPanel = new VerticalPanel();
        formPanel.add(modelsTable.asWidget());
        formPanel.add(modelsTablePager);
        formPanel.add(form.asWidget());
        formPanel.add(propertiesLabel.asWidget());
        formPanel.add(propertiesTable.asWidget());
        formPanel.add(propertiesTablePager);
        formPanel.add(errorLabel.asWidget());
        formPanel.add(errorsTable.asWidget());  
        formPanel.add(errorsTablePager);
        return formPanel;    	
    }


	private DefaultCellTable getModelTable(ListHandler<Model> sortHandler) {
		ProvidesKey keyProvider = new ProvidesKey<Model>() {
            @Override
            public Object getKey(Model item) {
            	String key = "";
            	if (!item.getSourceMappings().isEmpty()) {
            		SourceMapping sm = item.getSourceMappings().get(0);
            		key = sm.getSourceName();
            	}
                return getVdbName()+"."+getVdbVersion()+"."+item.getName()+"."+key;
            }
        };
		
		final DefaultCellTable modelsTable = new DefaultCellTable<Model>(5, keyProvider);   
		modelsTable.addColumnSortHandler(sortHandler);
        
        TextColumn<Model> nameColumn = new TextColumn<Model>() {
            @Override
            public String getValue(Model record) {
                return record.getName();
            }
        };
        nameColumn.setSortable(true);
        sortHandler.setComparator(nameColumn, new Comparator<Model>() {
			@Override
			public int compare(Model o1, Model o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
        
        TextColumn<Model> modelTypeColumn = new TextColumn<Model>() {
            @Override
            public String getValue(Model record) {
                return record.getModelType();
            }
        };
        modelTypeColumn.setSortable(true);
        sortHandler.setComparator(modelTypeColumn, new Comparator<Model>() {
			@Override
			public int compare(Model o1, Model o2) {
				return o1.getModelType().compareTo(o2.getModelType());
			}
		});
        
        
        TextColumn<Model> visibleColumn = new TextColumn<Model>() {
            @Override
            public String getValue(Model record) {
                return String.valueOf(record.isVisible());
            }
        };    
        visibleColumn.setSortable(true);
        sortHandler.setComparator(visibleColumn, new Comparator<Model>() {
			@Override
			public int compare(Model o1, Model o2) {
				return o1.isVisible().compareTo(o2.isVisible());
			}
		});        
        
        TextColumn<Model> multiSourceColumn = new TextColumn<Model>() {
            @Override
            public String getValue(Model record) {
            	return String.valueOf(isMultiSource(record));
            }
        };         
        
        final TextColumn<Model> sourceNameColumn = new TextColumn<Model>() {
            @Override
            public String getValue(Model record) {
            	if (isSource(record)) {
            		return record.getSourceMappings().get(0).getSourceName();
            	}
            	return "";
            }
        };
        sourceNameColumn.setSortable(true);
        sortHandler.setComparator(sourceNameColumn, new Comparator<Model>() {
			@Override
			public int compare(Model o1, Model o2) {
				SourceMapping sm1 = o1.getSourceMappings().get(0);
				SourceMapping sm2 = o2.getSourceMappings().get(0);
				return sm1.getSourceName().compareTo(sm2.getSourceName());
			}
		});        
        
        
        Column<Model, String> jndiNameColumn = new Column<Model, String>(new EditTextCell()) {
            @Override
            public String getValue(Model record) {
            	if (isSource(record)) {
            		return record.getSourceMappings().get(0).getJndiName();
            	}
            	return "";
            }
        };   
        jndiNameColumn.setFieldUpdater(new FieldUpdater<Model, String>() {
            public void update(int index, Model model, String value) {
            	changeDataSourceName(model, value);
            }
        });      
        jndiNameColumn.setSortable(true);
        sortHandler.setComparator(jndiNameColumn, new Comparator<Model>() {
			@Override
			public int compare(Model o1, Model o2) {
				SourceMapping sm1 = o1.getSourceMappings().get(0);
				SourceMapping sm2 = o2.getSourceMappings().get(0);
				return sm1.getJndiName().compareTo(sm2.getJndiName());
			}
		});         

        TextColumn<Model> translatorNameColumn = new TextColumn<Model>() {
            @Override
            public String getValue(Model record) {
            	if (isSource(record)) {
            		return record.getSourceMappings().get(0).getTranslatorName();
            	}
            	return "";
            }
        };    
        translatorNameColumn.setSortable(true);
        sortHandler.setComparator(translatorNameColumn, new Comparator<Model>() {
			@Override
			public int compare(Model o1, Model o2) {
				SourceMapping sm1 = o1.getSourceMappings().get(0);
				SourceMapping sm2 = o2.getSourceMappings().get(0);
				return sm1.getTranslatorName().compareTo(sm2.getTranslatorName());
			}
		});        
        
        Column<Model, String> schemaBtn = new Column<Model, String>(new ButtonCell()) {
            @Override
            public String getValue(Model record) {
        		return "DDL";
            }        	
        };
        schemaBtn.setFieldUpdater(new FieldUpdater<Model, String>() {
			@Override
			public void update(int index, Model model, String value) {
			    showSchema(model);
			}
        });        
        
        modelsTable.setTitle("Models");
        modelsTable.addColumn(nameColumn, "Name");
        modelsTable.addColumn(modelTypeColumn, "Type");
        modelsTable.addColumn(visibleColumn, "Visible?");
        modelsTable.addColumn(multiSourceColumn, "Multi-Source?");
        modelsTable.addColumn(sourceNameColumn, "Source Name");
        modelsTable.addColumn(translatorNameColumn, "Translator Name");
        modelsTable.addColumn(jndiNameColumn, "Datasource JNDI Name");
        modelsTable.addColumn(schemaBtn, "Schema");
        
        modelsTable.setSelectionModel(new SingleSelectionModel<Model>(keyProvider));
        
        modelsTable.getColumnSortList().push(nameColumn);
        
		return modelsTable;
	}

    
    private boolean isSource(Model model) {
    	if (model.getModelType() == null || model.getModelType().equals("PHYSICAL")) {
    		return true;
    	}
    	return false;
    }
    
	private boolean isMultiSource(Model record) {
		for (KeyValuePair kv:record.getProperties()) {
    		if (kv.getKey().equals("supports-multi-source-bindings")) {
    			return Boolean.TRUE.equals(Boolean.parseBoolean(kv.getValue()));
    		}
    	}
    	return Boolean.FALSE;
	}	
	
	private List<Model> addMultiSourceModels(List<Model> models) {
		ArrayList<Model> normalizedList  = new ArrayList<Model>();
		for (Model m:models) {
			
			if (m.getSourceMappings().size() > 1) {
				
				for (SourceMapping sm:m.getSourceMappings()) {
					ArrayList<SourceMapping> mappings = new ArrayList<SourceMapping>();
					Model copy = this.factory.getModel().as();
					copy.setName(m.getName());
					copy.setModelType(m.getModelType());
					copy.setDescription(m.getDescription());
					copy.setMetadata(m.getMetadata());
					copy.setMetadataType(m.getMetadataType());
					copy.setModelPath(m.getModelType());
					copy.setProperties(m.getProperties());
					copy.setVisible(m.isVisible());
					copy.setValidityErrors(m.getValidityErrors());	
					mappings.add(sm);
					copy.setSourceMappings(mappings);
					normalizedList.add(copy);
				}
			}
			else {
				normalizedList.add(m);
			}
		}
		return normalizedList;
	}
	
	public void setDataModelFactory(DataModelFactory factory) {
		this.factory = factory;
	}	
	
	private void showSchema(Model model) {
		this.presenter.getSchema(getVdbName(), getVdbVersion(), model.getName());
	}
	
	public void setSchema(String ddl) {
		DefaultPopUpWindow schemaDialogBox = new DefaultPopUpWindow("Schema", ddl);
		schemaDialogBox.show();
	}
	
	public void changeDataSourceName(Model model, String dataSourceName) {
		SourceMapping sm = model.getSourceMappings().get(0);
		this.presenter.assignDataSource(getVdbName(), getVdbVersion(), model.getName(), sm.getSourceName(), sm.getTranslatorName(), dataSourceName);
	}
}
