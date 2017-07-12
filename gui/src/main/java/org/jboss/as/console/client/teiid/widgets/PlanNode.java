/*
 * Copyright Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags and
 * the COPYRIGHT.txt file distributed with this work.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.as.console.client.teiid.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class PlanNode {

	public static class Property {
		private String name;
		private List<String> values;
		
		public Property() {
		}
		
		public Property(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
		public List<String> getValues() {
			return values;
		}
		
		public String getValuesAsCSV() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < values.size(); i++) {
				sb.append(values.get(i));
				if (i < values.size()) {
					sb.append(",");//$NON-NLS-1$
				}
			}
			return sb.toString();
		}		
		
		public void setValues(List<String> values) {
			this.values = values;
		}
	}
	
    private List<Property> properties = new LinkedList<Property>();
    private List<PlanNode> childNodes = new LinkedList<PlanNode>();
    private PlanNode parent;
    private String name;
    
    public PlanNode() {
    	
    }
	
    public PlanNode(String name) {
    	this.name = name;
    }
    
    public String getName() {
		return name;
	}
    
    void setParent(PlanNode parent) {
        this.parent = parent;
    }

    public PlanNode getParent() {
        return this.parent;
    }
    
    public List<Property> getProperties() {
		return properties;
	}
    
    public void addChildNode(PlanNode value) {
    	this.childNodes.add(value);
    }
    
    public List<PlanNode> getChildNodes() {
    	return this.childNodes;
    }    
    
    public void addProperty(String pname, List<String> value) {
    	Property p = new Property(pname);
    	if (value == null) {
    		value = Collections.emptyList();
    	}
    	p.setValues(value);
    	this.properties.add(p);
    }
	
    @Override
    public String toString() {
    	StringBuilder builder = new StringBuilder();
    	visitNode(this, 0, builder);
    	return builder.toString();
    }
    
    protected void visitNode(PlanNode node, int nodeLevel, StringBuilder text) {
        for(int i=0; i<nodeLevel; i++) {
            text.append("  "); //$NON-NLS-1$
        }
        text.append(node.getName());        
        text.append("\n"); //$NON-NLS-1$
        
        // Print properties appropriately
        int propTabs = nodeLevel + 1;
        for (PlanNode.Property property : node.getProperties()) {
            // Print leading spaces for prop name
        	for(int t=0; t<propTabs; t++) {
				text.append("  "); //$NON-NLS-1$
			}
            printProperty(nodeLevel, property, text);
        }
        
        for (PlanNode node1: node.getChildNodes()) {
        	visitNode(node1, nodeLevel+1, text);
        }
    }

    private void printProperty(int nodeLevel, Property p, StringBuilder text) {
        text.append("+ "); //$NON-NLS-1$
        text.append(p.getName());
        
        if (p.getValues().size() > 1){
        	text.append(":\n"); //$NON-NLS-1$ 
        	for (int i = 0; i < p.getValues().size(); i++) {
            	for(int t=0; t<nodeLevel+2; t++) {
            		text.append("  "); //$NON-NLS-1$
            	}
                text.append(i);
                text.append(": "); //$NON-NLS-1$
            	text.append(p.getValues().get(i));
                text.append("\n"); //$NON-NLS-1$
			}
        } else if (p.getValues().size() == 1) {
        	text.append(":"); //$NON-NLS-1$
        	text.append(p.getValues().get(0));
        	text.append("\n"); //$NON-NLS-1$
        } else {
        	text.append("\n"); //$NON-NLS-1$
        }
    }	
    
	public static PlanNode fromXml(String planString) {
		Document messageDom = XMLParser.parse(planString);
		NodeList nodes = messageDom.getElementsByTagName("node");//$NON-NLS-1$
		Node node = nodes.item(0);
		String nodeName = node.getAttributes().getNamedItem("name").getNodeValue(); //$NON-NLS-1$
		PlanNode root = new PlanNode("QueryPlan"); //$NON-NLS-1$
		PlanNode planNode = new PlanNode(nodeName);
		planNode.setParent(root);
		root.addChildNode(planNode);
		buildNode(node, planNode);
		return planNode;
	}

	private static PlanNode buildNode(Node parentXMLNode, PlanNode node) {
		NodeList childNodes = parentXMLNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			
//			String n =
//					rootElement.getElementsByTagName("ID").item(0).getNodeName();
//					String v =
//					rootElement.getElementsByTagName("ID").item(0).getChildNodes().item(0).getNodeValue(); 			
			
			Node childXMLNode = childNodes.item(i);
			ArrayList<String> values = new ArrayList<String>();
			if (childXMLNode.getNodeName().equals("property")) { //$NON-NLS-1$
				String propertyName = childXMLNode.getAttributes().getNamedItem("name").getNodeValue(); //$NON-NLS-1$
				
				NodeList valueXMLNodes = childXMLNode.getChildNodes();
				for (int valueIdx=0; valueIdx < valueXMLNodes.getLength(); valueIdx++) {
					Node valueXMLNode = valueXMLNodes.item(valueIdx);
					if (valueXMLNode.getNodeName().equals("value")) { //$NON-NLS-1$
						String v = valueXMLNode.getChildNodes().item(0).getNodeValue();
						System.out.println(propertyName+":"+v);//$NON-NLS-1$
						values.add(v);
					}
					else if (valueXMLNode.getNodeName().equals("node")) { //$NON-NLS-1$
						String name = valueXMLNode.getAttributes().getNamedItem("name").getNodeValue(); //$NON-NLS-1$
						PlanNode childNode = new PlanNode(name);
						node.addChildNode(buildNode(valueXMLNode, childNode));
					}
				}
				if (!values.isEmpty()) {
					node.addProperty(propertyName, values);
				}
			}
		}
	   return node;
	}
}
