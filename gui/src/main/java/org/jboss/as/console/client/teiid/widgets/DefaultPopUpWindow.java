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

import org.jboss.ballroom.client.widgets.window.DefaultWindow;
import org.jboss.ballroom.client.widgets.window.TrappedFocusPanel;
import org.jboss.ballroom.client.widgets.window.WindowContentBuilder;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("nls")
public class DefaultPopUpWindow extends DefaultWindow {
	
	public DefaultPopUpWindow(String title, String content) {
		super(title);
        setWidth(500);
        setHeight(400);
        addCloseHandler(new CloseHandler<PopupPanel>() {
            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
            }
        });
        center();
        
		// Add a close button at the bottom of the dialog
        HorizontalPanel closePanel = new HorizontalPanel();
        closePanel.getElement().setAttribute("style", "margin-top:10px;width:100%");
		Button closeButton = new Button("Close", new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});   
		closePanel.add(closeButton);
		closeButton.getElement().setAttribute("style", "min-width:60px;");
		closeButton.getElement().getParentElement().setAttribute("align", "right");
		closeButton.getElement().getParentElement().setAttribute("width", "100%");
		
		
        SafeHtmlBuilder html = new SafeHtmlBuilder();
        html.appendHtmlConstant("<pre style='font-family:tahoma, verdana, sans-serif;' id='detail-message'>");
        html.appendHtmlConstant(content == null? "No Content Available":content);
        html.appendHtmlConstant("</pre>");

        final HTML widget = new HTML(html.toSafeHtml());
        widget.getElement().setAttribute("style", "margin:5px");

        Widget windowContent = new WindowContentBuilder(widget, closePanel).build();

        TrappedFocusPanel trap = new TrappedFocusPanel(windowContent)
        {
            @Override
            protected void onAttach() {
                super.onAttach();

                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        getFocus().onFirstButton();
                    }
                });
            }
        };

        setWidget(trap);
        center();
	}

}
