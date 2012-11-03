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
public class QueryPlanPopUpWindow extends DefaultWindow {
	
	public QueryPlanPopUpWindow(String title, String content) {
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
		
		Widget widget = buildQueryPlan(content);
        
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

	private Widget buildQueryPlan(String content) {
		if (content == null) {
	        SafeHtmlBuilder html = new SafeHtmlBuilder();
	        html.appendHtmlConstant("<pre style='font-family:tahoma, verdana, sans-serif;' id='detail-message'>");
	        html.appendHtmlConstant("No Content Available");
	        html.appendHtmlConstant("</pre>");
	        return new HTML(html.toSafeHtml());
		}
		
        SafeHtmlBuilder html = new SafeHtmlBuilder();
        html.appendHtmlConstant("<pre style='font-family:tahoma, verdana, sans-serif;' id='detail-message' word-break:break-all;>");
        html.appendEscaped(content);
        html.appendHtmlConstant("</pre>");

        final HTML widget = new HTML(html.toSafeHtml());
        widget.getElement().setAttribute("style", "margin:5px");
        return widget;
		
//		PlanNode node = new PlanNode("Limit Node");
//		node.addProperty("output columns", Arrays.asList("e1", "e2"));
//		PlanNode node1 = new PlanNode("Union node");
//		node.addChildNode(node1);
//
//		//PlanNode node = PlanNode.fromXML(content);
//		System.out.println(node);
//		return new QueryPlanTreeParser().parse(node).asWidget();
	}
}
