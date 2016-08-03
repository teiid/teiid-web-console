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
import java.util.Set;

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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import edu.ycp.cs.dh.acegwt.client.ace.AceCompletion;
import edu.ycp.cs.dh.acegwt.client.ace.AceCompletionCallback;
import edu.ycp.cs.dh.acegwt.client.ace.AceCompletionProvider;
import edu.ycp.cs.dh.acegwt.client.ace.AceCompletionSnippet;
import edu.ycp.cs.dh.acegwt.client.ace.AceCompletionSnippetSegment;
import edu.ycp.cs.dh.acegwt.client.ace.AceCompletionSnippetSegmentLiteral;
import edu.ycp.cs.dh.acegwt.client.ace.AceCompletionSnippetSegmentTabstopItem;
import edu.ycp.cs.dh.acegwt.client.ace.AceCompletionValue;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorCursorPosition;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;

@SuppressWarnings("nls")
public class SQLWorkbenchEditor {
	private AceEditor editor;
	private TextArea result;
	private VerticalPanel resultPanel;
	private ListDataProvider<VDB> vdbProvider = new ListDataProvider<VDB>();
	private List<ModelNode> resultList;
	private VDBPresenter presenter;
	private ArrayList<String> vdbNameList;
	private List<VDB> vdblist;

	public void setPresenter(VDBPresenter presenter) {
		this.presenter = presenter;
	}
	
	public void setResultList(List<ModelNode> reusltlist) {
		this.resultList = reusltlist;
	}

	
	public void setVDBList(List<VDB> vdblist) {
		this.vdblist = vdblist;
	}
	
	public Widget createWidget(VDBPresenter presenter) {

		vdbNameList = new ArrayList();
		String jsfile = "app/ace/ace.js";
		ScriptInjector.fromUrl(jsfile).inject();
		String jsfile2 = "app/ace/theme-twilight.js ";
		ScriptInjector.fromUrl(jsfile2).inject();
		String jsfile3 = "app/ace/mode-sql.js ";
		ScriptInjector.fromUrl(jsfile3).inject();

		this.presenter = presenter;
		final ToolStrip toolStrip = new ToolStrip();
		toolStrip.addToolButtonRight(new ToolButton(Console.CONSTANTS.common_label_refresh(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
			}
		}));
		
		VerticalPanel workBenchPanel = new VerticalPanel();
		// result = new TextArea();
		HorizontalPanel variablePanel = new HorizontalPanel();
		
		ComboBoxItem vdbNames = new ComboBoxItem("vdb-name", "VDB Name");
		 vdbNames.setValueMap(new String[] { "Portfolio", "GSS" });
//		vdbNames.setValueMap(getVdbNames());
		vdbNames.setDefaultToFirstOption(true);
		
		
		
		variablePanel.add(vdbNames.asWidget());
		Label vdbNameLabel = new Label("VDB Name");
		vdbNameLabel.getElement().setAttribute("style",
				"margin-top:10px;margin-bottom:10px;margin-right:10px;font-weight:bold;");
		variablePanel.add(vdbNameLabel);

		TextBoxItem vdbName = new TextBoxItem("VDB Name", "Deployed Virtual Database Name ");
		variablePanel.add(vdbName.asWidget());

		Label vdbVersionLabel = new Label("VDB Version");
		vdbVersionLabel.getElement().setAttribute("style",
				"margin-top:10px;margin-bottom:10px;margin-right:10px;font-weight:bold;");
		variablePanel.add(vdbVersionLabel);

		TextBoxItem vdbVersion = new TextBoxItem("VDB Version", "Deployed Virtual Database Version ");
		variablePanel.add(vdbVersion.asWidget());

		// result.setSize("800px", "600px");

		ToolButton applyBtn = new ToolButton("Run", new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				String SQL = editor.getText();
				String VDB = vdbName.getValue();
				int VDB_Version = Integer.parseInt(vdbVersion.getValue());
				editor.setText(getVdbNames().toString());
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
		// editor.setAutocompleteEnabled(true);
		// editor.setMode(AceEditorMode.SQL);
		// editor.setModeByName("SQL");

		editor.setTheme(AceEditorTheme.TWILIGHT);
		// AceEditor.removeAllExistingCompleters();

		// AceEditor.addCompletionProvider(new
		// SqlWorkbenchCompletionProvider());
		// editor.setMode(AceEditorMode.SQL);

		workBenchPanel.add(resultLabel);
		// workBenchPanel.add(result);
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

	private String[] getVdbNames() {
		// TODO Auto-generated method stub
		if (vdblist != null) {
			if (!vdblist.isEmpty()) {
				for (VDB vdb : vdblist) {
					vdbNameList.add(vdb.getName());
				}
				String[] resu = (String[]) vdbNameList.toArray(new String[vdbNameList.size()]);
				return resu;
			}
			else {
				String[] resu = { "1" };
				return resu;
			}
		} else {
			String[] resu = {  "2" };
			return resu;
		}
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

	public void provideAsText(List<ModelNode> list) {
		Set<String> attributes = list.get(0).keys();
		TextArea resultAstxt = new TextArea();
		// String result = attributes.toString();
		String result = "---------";
		for (String title : attributes) {
			result = result + title + "----------------";
		}
		result += "\n\n" + "                ";
		for (ModelNode k : list) {
			for (String title : attributes) {
				result += k.get(title) + "                ";
			}
			result += "\n" + "                ";
		}
		resultAstxt.setText(result);
		resultAstxt.setSize("800px", "600px");
		resultPanel.add(resultAstxt);
	}

	

	private static class SqlWorkbenchCompletionProvider implements AceCompletionProvider {
		@Override

		public void getProposals(AceEditor editor, AceEditorCursorPosition pos, String prefix,
				AceCompletionCallback callback) {
			GWT.log("sending completion proposals");
			callback.invokeWithCompletions(new AceCompletion[] {
					new AceCompletionValue("first", "firstcompletion", "custom", 10),
					new AceCompletionValue("second", "secondcompletion", "custom", 11),
					new AceCompletionValue("third", "thirdcompletion", "custom", 12),
					new AceCompletionSnippet("fourth (snippets)",
							new AceCompletionSnippetSegment[] { new AceCompletionSnippetSegmentLiteral("filler_"),
									new AceCompletionSnippetSegmentTabstopItem("tabstop1"),
									new AceCompletionSnippetSegmentLiteral("_\\filler_"), // putting
																							// backslash
																							// in
																							// here
																							// to
																							// prove
																							// escaping
																							// is
																							// working
									new AceCompletionSnippetSegmentTabstopItem("tabstop2"),
									new AceCompletionSnippetSegmentLiteral("_$filler_"), // putting
																							// dollar
																							// in
																							// here
																							// to
																							// prove
																							// escaping
																							// is
																							// working
									new AceCompletionSnippetSegmentTabstopItem("tabstop3"),
									new AceCompletionSnippetSegmentLiteral("\nnextlinefiller_"),
									new AceCompletionSnippetSegmentTabstopItem("tabstop}4"),
									new AceCompletionSnippetSegmentLiteral("_filler_"),
									new AceCompletionSnippetSegmentTabstopItem(
											"") /*
												 * Empty tabstop -- tab to end
												 * of replacement text
												 */
							}, "csnip", 14) });
		}
	}
}
