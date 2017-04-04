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
import java.util.List;

import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.teiid.model.VDB;
import org.jboss.as.console.client.widgets.tabs.DefaultTabLayoutPanel;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelNode;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;

@SuppressWarnings("nls")
public class SQLWorkbenchEditor {
	private AceEditor editor;
	private DefaultTabLayoutPanel resultPanelTabs;
	private VDBPresenter presenter;
	private ComboBoxItem vdbSelector;

	public void setPresenter(VDBPresenter presenter) {
		this.presenter = presenter;
	}

	public void setVDBList(List<VDB> vdblist) {
		vdbSelector.setValueMap(getVdbNames(vdblist));
	}

	public Widget createWidget(VDBPresenter presenter) {
		this.presenter = presenter;
		final ToolStrip toolStrip = new ToolStrip();
		toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.getVDBs();
			}
		}));

		VerticalPanel workBenchPanel = new VerticalPanel();
		workBenchPanel.setBorderWidth(1);
		workBenchPanel.setSpacing(10);
		
		HorizontalPanel toolbarPanel = new HorizontalPanel();
		toolbarPanel.getElement().getStyle().setPadding(10, Style.Unit.PX);
		Label vdbNameLabel = new Label("VDB Name");
		vdbNameLabel.getElement().setAttribute("style",
				"margin-top:10px;margin-bottom:10px;margin-right:10px;font-weight:bold;");
		toolbarPanel.add(vdbNameLabel);

		vdbSelector = new ComboBoxItem("type", "Type");
		vdbSelector.setDefaultToFirstOption(true);

		toolbarPanel.add(vdbSelector.asWidget());

		ToolButton applyBtn = new ToolButton("Run", new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				String sqlCommand = editor.getText();
				String name = vdbSelector.getValue();
				setSqlResult(sqlCommand, name);
			}
		});
		toolbarPanel.add(applyBtn);
		workBenchPanel.add(toolbarPanel);
		workBenchPanel.setCellHeight(toolbarPanel, "50px");

		editor = new AceEditor() {
			public native String getText() /*-{
				var editor = this.@edu.ycp.cs.dh.acegwt.client.ace.AceEditor::editor;
				return editor.getSelectedText();
			}-*/;			
		};
		editor.setHeight("200px");	
        editor.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                if (event.isAttached()) {
                    Scheduler.get().scheduleDeferred(
                            new Scheduler.ScheduledCommand() {
                                @Override
                                public void execute() {
                                    editor.startEditor();                                	
                                    editor.setAutocompleteEnabled(true);
                                    editor.setShowGutter(true);
                                    editor.setShowPrintMargin(false);
                            	    editor.setMode(AceEditorMode.SQL);
                            	    editor.setTheme(AceEditorTheme.TWILIGHT);
                                    editor.setFontSize("11px");
                                }
                            }
                    );
                }
            }
        });
		
        HorizontalPanel editorPanel = new HorizontalPanel();
        editorPanel.setStyleName("fill-layout-width");
        editorPanel.add(editor);
        
        workBenchPanel.add(editorPanel);		
        workBenchPanel.setCellHeight(editorPanel, "200px");
		
		this.resultPanelTabs = new DefaultTabLayoutPanel(40, Style.Unit.PX, true, true);
		this.resultPanelTabs.addStyleName("default-tabpanel");
		
		VerticalPanel resultPanel = new VerticalPanel();
		resultPanel.add(new HTML("<h3> No Results</h3>"));
		this.resultPanelTabs.add(resultPanel, "No Results");
		
		workBenchPanel.add(this.resultPanelTabs);

		return workBenchPanel.asWidget();
	}

	public void setSqlResult(String sqlCommand, String vdb) {
		int index = vdb.indexOf(" version:");
		presenter.executeQuery(vdb.substring(0, index), vdb.substring(index+9), sqlCommand, "SQLWorkbench");
	}

	public <T> void setQueryResults(List<T> results, String sql, String clazz) {
		if (clazz.equals("SQLWorkbench")) {
			DefaultCellTable resultTable = VDBView.buildSQLResultTable((List<ModelNode>)results);
			
			final ListDataProvider<ModelNode> resultProvider = new ListDataProvider<ModelNode>();
			resultProvider.addDataDisplay(resultTable);
			resultProvider.setList((List<ModelNode>)results);

			VerticalPanel resultTab = new VerticalPanel();
			resultTab.setStyleName("fill-layout-width");
			resultTab.setSpacing(5);
			resultTab.add(new HTML("<h4>"+sql+"</h4>"));
			
	        DefaultPager resultTablePager = new DefaultPager();
			resultTablePager.setDisplay(resultTable);
			resultTab.add(resultTable);
			resultTab.add(resultTablePager);
			this.resultPanelTabs.add(resultTab, sql);
			resultPanelTabs.selectTab(resultTab);
		}
	}	

	private String[] getVdbNames(List<VDB> vdblist) {
		List<String> names = new ArrayList<String>();
		for (VDB item : vdblist) {
			names.add(item.getName()+" version:"+item.getVersion());
		}
		String[] vdbNames = (String[]) names.toArray(new String[names.size()]);
		return vdbNames;
	}
}
