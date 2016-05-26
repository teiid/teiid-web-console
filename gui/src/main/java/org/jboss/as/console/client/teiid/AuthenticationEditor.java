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
package org.jboss.as.console.client.teiid;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.layout.OneToOneLayout;
import org.jboss.as.console.client.teiid.model.Authentication;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.FormItem;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class AuthenticationEditor implements Persistable<Authentication> {

	private SubsystemPresenter presenter;
	private TeiidModelForm<Authentication> commonForm;

	public AuthenticationEditor(SubsystemPresenter presenter) {
		this.presenter = presenter;
	}

	public void setAuthentication(Authentication bean) {
		this.commonForm.edit(bean);
	}

	public Widget asWidget() {
		// authentication

		this.commonForm = new TeiidModelForm<Authentication>(
				Authentication.class, this, buildCommonFormItems().toArray(
						new FormItem<?>[2]));

		HTML title = new HTML();
		title.setStyleName("content-header-label");
		title.setText("Authentication");

		OneToOneLayout layoutBuilder = new OneToOneLayout().setPlain(true)
				.setTitle("Authentication").setHeadlineWidget(title)
				.setDescription("Authentication provides a way to control security, maximum session, etc")
				.addDetail("Common", this.commonForm.asWidget());
		return layoutBuilder.build();
	}

	static List<FormItem<?>> buildCommonFormItems() {
		TextBoxItem securityDomain = new TextBoxItem("securityDomain",
				"Security Domain");
		NumberBoxItem maxSessionsAllowed = new NumberBoxItem(
				"maxSessionsAllowed", "Max Sessions Allowed");  
		NumberBoxItem sessionExpirationTimelimit = new NumberBoxItem(
				"sessionExpirationTimelimit", "Session Expiration Timelimit",
				true);
		TextBoxItem type = new TextBoxItem("type", "Type");
		CheckBoxItem trustAllLocal = new CheckBoxItem("trustAllLocal", "Trust All Local");

		return Arrays.asList(securityDomain, maxSessionsAllowed,
				sessionExpirationTimelimit, type, trustAllLocal);
	}

	@Override
	public void save(Authentication authentication,
			Map<String, Object> changeset) {
		// TODO Auto-generated method stub
		this.presenter.saveAuthentication(authentication, changeset);
	}

}