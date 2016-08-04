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
import java.util.LinkedList;
import java.util.List;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.layout.OneToOneLayout;
import org.jboss.as.console.client.teiid.model.VDB;
import org.jboss.ballroom.client.widgets.forms.ComboBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tables.DefaultPager;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.tools.ToolStrip;
import org.jboss.dmr.client.ModelNode;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;

@SuppressWarnings("nls")
public class SQLWorkbenchEditor {
	private AceEditor editor;
	private VerticalPanel resultPanel;
	private ListDataProvider<VDB> vdbProvider = new ListDataProvider<VDB>();
	private List<ModelNode> resultList;
	private VDBPresenter presenter;
	private List<VDB> vdblist = new LinkedList<VDB>();
	private ComboBoxItem vdbName;

	public void setPresenter(VDBPresenter presenter) {
		this.presenter = presenter;
	}

	public void setResultList(List<ModelNode> reusltlist) {
		this.resultList = reusltlist;
	}

	public void setVDBList(List<VDB> vdblist) {
		this.vdblist = vdblist;
		vdbName.setValueMap(getVdbNames());
	}

	public Widget createWidget(VDBPresenter presenter) {

		this.presenter = presenter;
		final ToolStrip toolStrip = new ToolStrip();
		toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
			}
		}));

		VerticalPanel workBenchPanel = new VerticalPanel();
		HorizontalPanel variablePanel = new HorizontalPanel();

		Label vdbNameLabel = new Label("VDB Name");
		vdbNameLabel.getElement().setAttribute("style",
				"margin-top:10px;margin-bottom:10px;margin-right:10px;font-weight:bold;");
		variablePanel.add(vdbNameLabel);

		vdbName = new ComboBoxItem("type", "Type");
		vdbName.setDefaultToFirstOption(true);

		variablePanel.add(vdbName.asWidget());

		Label vdbVersionLabel = new Label("VDB Version");
		vdbVersionLabel.getElement().setAttribute("style",
				"margin-top:10px;margin-bottom:10px;margin-right:10px;margin-left:10px;font-weight:bold;");
		variablePanel.add(vdbVersionLabel);

		TextBoxItem vdbVersion = new TextBoxItem("VDB Version", "Deployed Virtual Database Version ");
		variablePanel.add(vdbVersion.asWidget());

		ToolButton applyBtn = new ToolButton("Run", new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				String SQL = editor.getText();
				String VDB = vdbName.getValue();
				int VDB_Version = Integer.parseInt(vdbVersion.getValue());
				setSqlResult(SQL, VDB, VDB_Version);
			}
		});
		variablePanel.add(applyBtn);
		workBenchPanel.add(variablePanel);

		Label resultLabel = new Label("Result");
		resultLabel.getElement().setAttribute("style",
				"margin-top:10px;margin-bottom:20px;margin-right:10px;font-weight:bold;");
		editor = new AceEditor();
		editor.setWidth("800px");
		editor.setHeight("100px");
		workBenchPanel.add(this.editor);

		editor.startEditor();
		workBenchPanel.add(resultLabel);
		resultPanel = new VerticalPanel();
		resultPanel.setSize("800px", "500px");

		Label resultPanelLabel = new Label("No result !");
		resultPanelLabel.getElement().setAttribute("style", "font-size :15px;font-weight:bold;");
		resultPanel.add(resultPanelLabel);
		workBenchPanel.add(resultPanel);

		HTML title = new HTML();
		title.setStyleName("content-header-label");
		title.setText("SQL");

		OneToOneLayout layoutBuilder = new OneToOneLayout().setPlain(true).setTitle("SQL workbench")
				.setHeadlineWidget(title).setDescription("Run queries against a deployed VDB. ")
				.addDetail("SQL workbench", workBenchPanel);
		return layoutBuilder.build();
	}

	public void setSqlResult(String SQL, String VDB, int VDB_Version) {

		presenter.getExecuteSQL(VDB, VDB_Version, SQL);
		if (resultList.size() == 0) {
			return;
		}
		resultPanel.clear();
		provideAsTable(resultList);
	}

	public void provideAsTable(List<ModelNode> list) {
		DefaultCellTable resultTable = VDBView.buildSQLResultTable(list);
		final ListDataProvider<ModelNode> resultProvider = new ListDataProvider<ModelNode>();
		resultProvider.addDataDisplay(resultTable);
		resultProvider.setList(list);
		DefaultPager resultTablePager = new DefaultPager();
		resultTablePager.setDisplay(resultTable);
		resultPanel.add(resultTable.asWidget());
		resultPanel.add(resultTablePager);
	}

	private String[] getVdbNames() {
		List<String> names = new ArrayList<String>();
		for (VDB item : vdblist) {
			names.add(item.getName());
		}
		String[] vdbNames = (String[]) names.toArray(new String[names.size()]);
		return vdbNames;
	}

}
