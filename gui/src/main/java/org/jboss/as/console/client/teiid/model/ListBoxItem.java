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
package org.jboss.as.console.client.teiid.model;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.ballroom.client.widgets.forms.FormItem;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author David Bosschaert
 */
public class ListBoxItem extends FormItem<List<String>> {
    protected ListBox listBox;
    private HorizontalPanel wrapper;
    ChangeHandler valueChangeHandler;

    public ListBoxItem(String name, String title) {
        super(name, title);

        listBox = new ListBox();
        listBox.setName(name);
        listBox.setTitle(title);
        listBox.setVisibleItemCount(5);
        listBox.setTabIndex(0);
        listBox.setHeight("50px");  //$NON-NLS-1$

        valueChangeHandler = new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
            }
        };
        listBox.addChangeHandler(valueChangeHandler);

        wrapper = new HorizontalPanel();
        wrapper.add(listBox);
    }
    
    public void setVisibleItemCount(int count) {
    	listBox.setVisibleItemCount(count);
    }

    @Override
    public Widget asWidget() {
        return wrapper;
    }

    @Override
    public List<String> getValue() {
        return null;
    }

    @Override
    public void setValue(List<String> value) {
        Set<String> sortedChoices = new TreeSet<String>(value);
        listBox.clear();
        for (String c : sortedChoices) {
            listBox.addItem(c);
        }
        listBox.setSelectedIndex(-1);
    }
    
    public boolean addItem(String value) {
    	for (int i = 0; i < listBox.getItemCount(); i++) {
    		String item = listBox.getItemText(i);
    		if (item.equals(value)) {
    			return false;
    		}
    	}
    	listBox.addItem(value);
    	return true;
    }
    
    public String removeSelected() {
    	int idx = listBox.getSelectedIndex();
    	String value = listBox.getItemText(idx);
    	listBox.removeItem(idx);
    	if (listBox.getItemCount() > 0) {
    		listBox.setSelectedIndex(0);
    	}
    	return value;
    }

    @Override
    public void setEnabled(boolean b) {
        listBox.setEnabled(b);
    }

    @Override
    public boolean validate(List<String> value) {
        return true;
    }

    @Override
    public void clearValue() {
        listBox.clear();
    }
}

