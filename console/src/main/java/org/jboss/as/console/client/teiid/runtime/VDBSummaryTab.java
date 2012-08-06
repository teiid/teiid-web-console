package org.jboss.as.console.client.teiid.runtime;

import java.util.ArrayList;
import java.util.Collections;

import org.jboss.as.console.client.teiid.model.ImportedVDB;
import org.jboss.as.console.client.teiid.model.KeyValuePair;
import org.jboss.as.console.client.teiid.model.Model;
import org.jboss.as.console.client.teiid.model.VDB;
import org.jboss.as.console.client.teiid.model.ValidityError;
import org.jboss.as.console.client.teiid.runtime.VDBView.TableSelectionCallback;
import org.jboss.ballroom.client.widgets.common.DefaultButton;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;

@SuppressWarnings("nls")
public class VDBSummaryTab extends VDBProvider {
	private VDBPresenter presenter;
	
	public VDBSummaryTab(VDBPresenter presenter) {
		this.presenter = presenter;
	}
	
	@Override
    public VerticalPanel getPanel(DefaultCellTable vdbTable) {
        final Form<VDB> form = new Form<VDB>(VDB.class);
        form.setNumColumns(2);
        form.setEnabled(false);

        TextItem description = new TextItem("description", "Description");        
        form.setFields(description);
        form.bind(vdbTable);
        
        // Errors and warnings in the Model.
        Label errorLabel = new Label("Errors");
        errorLabel.getElement().setAttribute("style", "margin-top:10px;margin-bottom:10px;font-weight:bold;");
        
        DefaultCellTable errorTable = VDBView.buildErrorTable();
        final ListDataProvider<ValidityError> errorProvider = new ListDataProvider<ValidityError>();
        errorProvider.addDataDisplay(errorTable);   
        VDBView.onTableSectionChange(vdbTable, new TableSelectionCallback<VDB> (){
			@Override
			public void onSelectionChange(VDB vdb) {
				ArrayList<ValidityError> errorList = new ArrayList<ValidityError>();
				if (vdb != null) {
					for (Model m:vdb.getModels()) {
						for (ValidityError ve:m.getValidityErrors()) {
							if (ve.getSeverity().equals("ERROR")) {
								errorList.add(ve);
							}
						}
					}
				}
				errorProvider.setList(errorList);
			}
        });
        DefaultPager errorsTablePager = new DefaultPager();
        errorsTablePager.setDisplay(errorTable);
        
        // change connection type
        CaptionPanel connectionTypePanel = createConnectionTypePanel(vdbTable);
        
        // Imported VDBS in the VDB
        Label importVDBLabel = new Label("Imported VDBs");
        importVDBLabel.getElement().setAttribute("style", "margin-top:10px;margin-bottom:10px;font-weight:bold;");
        
        DefaultCellTable importedVDBTable = buildImportVDBTable();
        final ListDataProvider<ImportedVDB> importedVDBProvider = new ListDataProvider<ImportedVDB>();
        importedVDBProvider.addDataDisplay(importedVDBTable);   
        VDBView.onTableSectionChange(vdbTable, new TableSelectionCallback<VDB> (){
			@Override
			public void onSelectionChange(VDB vdb) {
				if (!vdb.getVDBImports().isEmpty()) {
					importedVDBProvider.setList(vdb.getVDBImports());
				}
				else {
					importedVDBProvider.setList(Collections.EMPTY_LIST);
				}
			}
        });   
        DefaultPager importedVDBTablePager = new DefaultPager();
        importedVDBTablePager.setDisplay(importedVDBTable);
        
        
        // VDB properties
        Label propertiesLabel = new Label("Properties");
        propertiesLabel.getElement().setAttribute("style", "margin-top:10px;margin-bottom:10px;font-weight:bold;");
        
        final DefaultCellTable propertiesTable = VDBView.buildPropertiesTable();
        final ListDataProvider<KeyValuePair> propertyProvider = new ListDataProvider<KeyValuePair>();
        propertyProvider.addDataDisplay(propertiesTable);   
        VDBView.onTableSectionChange(vdbTable, new TableSelectionCallback<VDB> (){
			@Override
			public void onSelectionChange(VDB vdb) {
				if (!vdb.getProperties().isEmpty()) {
					propertyProvider.setList(vdb.getProperties());
				}
				else {
					propertyProvider.setList(Collections.EMPTY_LIST);
				}
			}
        });
        DefaultPager propertiesTablePager = new DefaultPager();
        propertiesTablePager.setDisplay(propertiesTable);
        
        
        VerticalPanel formPanel = new VerticalPanel();
        formPanel.add(form.asWidget()); 
        formPanel.add(errorLabel.asWidget());
        formPanel.add(errorTable.asWidget());
        formPanel.add(errorsTablePager);
        formPanel.add(connectionTypePanel);
        formPanel.add(importVDBLabel.asWidget());
        formPanel.add(importedVDBTable.asWidget());      
        formPanel.add(importedVDBTablePager);
        formPanel.add(propertiesLabel.asWidget());
        formPanel.add(propertiesTable.asWidget());
        formPanel.add(propertiesTablePager);
        return formPanel;
    }

	static DefaultCellTable buildImportVDBTable() {
		DefaultCellTable table = new DefaultCellTable<ImportedVDB>(4,
				new ProvidesKey<ImportedVDB>() {
					@Override
					public Object getKey(ImportedVDB item) {
						return item.getName()+"_"+item.getVersion();
					}
				});

		TextColumn<ImportedVDB> nameColumn = new TextColumn<ImportedVDB>() {
			@Override
			public String getValue(ImportedVDB record) {
				return record.getName();
			}
		};
		TextColumn<ImportedVDB> versionColumn = new TextColumn<ImportedVDB>() {
			@Override
			public String getValue(ImportedVDB record) {
				return String.valueOf(record.getVersion());
			}
		};
		TextColumn<ImportedVDB> policiesImported = new TextColumn<ImportedVDB>() {
			@Override
			public String getValue(ImportedVDB record) {
				return String.valueOf(record.isImportPolicies());
			}
		};		
		table.setTitle("Imported VDBS");
		table.addColumn(nameColumn, "VDB Name");
		table.addColumn(versionColumn, "VDB Version");
		table.addColumn(policiesImported, "Inherit Data Role Policies?");
		return table;
	}

	private CaptionPanel createConnectionTypePanel(DefaultCellTable vdbTable) {
		CaptionPanel connTypeCaptionPanel = new CaptionPanel("Connection Type");
		final RadioButton noneBtn = new RadioButton("ConnectionType", "None");
		final RadioButton byVersionBtn = new RadioButton("ConnectionType", "By Version");
		final RadioButton byAnyBtn = new RadioButton("ConnectionType", "Any");
		DefaultButton applyBtn = new DefaultButton("Apply", new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				if (noneBtn.getValue()) {
					changeConnectionType("NONE");	
				}
				else if (byVersionBtn.getValue()) {
					changeConnectionType("BY_VERSION");
				}
				else {
					changeConnectionType("ANY");
				}
			}
		});
		VerticalPanel connTypePanel = new VerticalPanel();
		connTypePanel.add(noneBtn);
		connTypePanel.add(byVersionBtn);
		connTypePanel.add(byAnyBtn);
		connTypePanel.add(applyBtn);
		connTypePanel.setCellHorizontalAlignment(noneBtn,HasHorizontalAlignment.ALIGN_LEFT);
		connTypePanel.setCellHorizontalAlignment(byVersionBtn,HasHorizontalAlignment.ALIGN_LEFT);
		connTypePanel.setCellHorizontalAlignment(byAnyBtn,HasHorizontalAlignment.ALIGN_LEFT);
		connTypeCaptionPanel.add(connTypePanel);
		connTypeCaptionPanel.setWidth("40%");
		connTypeCaptionPanel.getElement().setAttribute("style", "font-weight:bold;");
		
        VDBView.onTableSectionChange(vdbTable, new TableSelectionCallback<VDB> (){
			@Override
			public void onSelectionChange(VDB vdb) {
				setVdbName(vdb.getName());
				setVdbVersion(vdb.getVersion());
				if (vdb.getConnectionType().equals("NONE")){
					noneBtn.setValue(true);
					byVersionBtn.setValue(false);
					byAnyBtn.setValue(false);
				}
				else if (vdb.getConnectionType().equals("BY_VERSION")) {
					noneBtn.setValue(false);
					byVersionBtn.setValue(true);
					byAnyBtn.setValue(false);
				}
				else if (vdb.getConnectionType().equals("ANY")) {
					noneBtn.setValue(false);
					byVersionBtn.setValue(false);
					byAnyBtn.setValue(true);
				}
			}
        });		
		return connTypeCaptionPanel;
	}
	
	private void changeConnectionType(String type) {
		this.presenter.changeConnectionType(getVdbName(), getVdbVersion(), type);
	}	
}
