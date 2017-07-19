package org.jboss.as.console.client.teiid.widgets;

/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/*
 * Copyright 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
 
import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;

/**
 * A {@link Cell} used to render text.
 */
public class TextAreaCell extends AbstractSafeHtmlCell<String> {


	  private static Template template;
	
  /**
   * Constructs a TextCell that uses a {@link SimpleSafeHtmlRenderer} to render
   * its text.
   */
  public TextAreaCell() {
    //super(SimpleSafeHtmlRenderer.getInstance());
	  this(SimpleSafeHtmlRenderer.getInstance());
  }

  /**
   * Constructs a TextCell that uses the provided {@link SafeHtmlRenderer} to
   * render its text.
   * 
   * @param renderer a {@link SafeHtmlRenderer SafeHtmlRenderer<String>} instance
   */
  public TextAreaCell(SafeHtmlRenderer<String> renderer) {
	    super(renderer);
	  }

  @Override
  public void render(Context context, SafeHtml value, SafeHtmlBuilder sb) {
    	
   
    sb.appendHtmlConstant("<div style=\"overflow-y: auto;white-space: normal;height: 25px;\" >");
    if (value != null) {
      sb.append(value);
    }
    sb.appendHtmlConstant("</div>");
    }
  
}
