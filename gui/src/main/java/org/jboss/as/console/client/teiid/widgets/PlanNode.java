/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.jboss.as.console.client.teiid.widgets;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;



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
    
    /*
	public static PlanNode fromXml(String planString) {
		Document messageDom = XMLParser.parse(planString);
		NodeList nodes = messageDom.getElementsByTagName("node");//$NON-NLS-1$
		Node node = nodes.item(0);
		String nodeName = node.getAttributes().getNamedItem("name").getNodeValue(); //$NON-NLS-1$
		PlanNode planNode = new PlanNode(nodeName);
		planNode.setParent(null);
		buildNode(node, planNode);
		return planNode;
	}

	private static PlanNode buildNode(Node parentXMLNode, PlanNode node) {
		NodeList childNodes = parentXMLNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			
			Node childXMLNode = childNodes.item(i);
			if (childXMLNode.getNodeName().equals("property")) { //$NON-NLS-1$
				String propertyName = childXMLNode.getAttributes().getNamedItem("name").getNodeValue(); //$NON-NLS-1$
				
				NodeList valueXMLNodes = childXMLNode.getChildNodes();
				for (int valueIdx=0; valueIdx < valueXMLNodes.getLength(); valueIdx++) {
					Node valueXMLNode = valueXMLNodes.item(valueIdx);
					ArrayList<String> values = new ArrayList<String>();
					if (valueXMLNode.getNodeName().equals("value")) { //$NON-NLS-1$
						values.add(valueXMLNode.getNodeValue());
					}
					else if (valueXMLNode.getNodeName().equals("node")) { //$NON-NLS-1$
						String name = valueXMLNode.getAttributes().getNamedItem("name").getNodeValue(); //$NON-NLS-1$
						PlanNode childNode = new PlanNode(name);
						node.addChildNode(buildNode(valueXMLNode, childNode));
					}
					node.addProperty(propertyName, values);
				}
			}
		}
	   return node;
	}
	*/
}
