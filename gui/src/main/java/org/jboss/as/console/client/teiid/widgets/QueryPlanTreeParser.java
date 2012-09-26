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
package org.jboss.as.console.client.teiid.widgets;

import org.jboss.as.console.client.widgets.tree.DefaultCellTree;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

@SuppressWarnings("nls")
public class QueryPlanTreeParser {
    
    private SingleSelectionModel<PlanNode> selectionModel;

    SingleSelectionModel<PlanNode> getSelectionModel() {
        return selectionModel;
    }

    public CellTree parse(PlanNode node) {
    	selectionModel = new SingleSelectionModel<PlanNode>(new ProvidesKey<PlanNode>() {
			@Override
			public Object getKey(PlanNode arg0) {
				return arg0.getName();
			}
		});
        TreeViewModel treeModel = new QueryPlanTreeModel(node);
        CellTree cellTree = new DefaultCellTree(treeModel, "root");
        return cellTree;
    }


    class JndiEntryCell extends AbstractCell<PlanNode> {
        @Override
        public void render(Context context, PlanNode value, SafeHtmlBuilder sb) {
            sb.appendHtmlConstant("<table width='100%'>");
            sb.appendHtmlConstant("<tr>");
                sb.appendHtmlConstant("<td width='60%'>");
                sb.appendEscaped(value.getName());
                sb.appendHtmlConstant("</td>");

                sb.appendHtmlConstant("<td width='40%' align='right'>");
                sb.appendEscaped("props- here");
                sb.appendHtmlConstant("</td>");

            sb.appendHtmlConstant("</tr>");
            sb.appendHtmlConstant("</table>");
        }
    }

    class QueryPlanTreeModel implements TreeViewModel {
        PlanNode rootEntry;

        QueryPlanTreeModel(PlanNode root) {
            this.rootEntry = root;
        }

        public <T> NodeInfo<?> getNodeInfo(T value) {

            final ListDataProvider<PlanNode> dataProvider = new ListDataProvider<PlanNode>();

            if (value instanceof PlanNode) {
                PlanNode entry = (PlanNode)value;
                dataProvider.setList(entry.getChildNodes());
            }

            return new DefaultNodeInfo<PlanNode>(dataProvider, new JndiEntryCell(), selectionModel, null);
        }

		public boolean isLeaf(Object value) {
			if (value instanceof PlanNode) {
				return ((PlanNode) value).getChildNodes().isEmpty();
			}
			return false;
		}
    }
}


