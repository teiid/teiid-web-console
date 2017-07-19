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
package org.jboss.as.console.client.teiid.i18n;

import org.jboss.as.console.client.core.UIConstants;
import org.jboss.as.console.client.core.UIMessages;

import javax.inject.Inject;

public class I18n {

    private final UIConstants consoleConstants;
    private final UIMessages consoleMessages;
    private final ExtensionConstants extensionConstants;
    private final ExtensionMessages extensionMessages;

    @Inject
    public I18n(final UIConstants consoleConstants,
            final UIMessages consoleMessages,
            final ExtensionConstants extensionConstants,
            final ExtensionMessages extensionMessages) {
        this.consoleConstants = consoleConstants;
        this.consoleMessages = consoleMessages;
        this.extensionConstants = extensionConstants;
        this.extensionMessages = extensionMessages;
    }

    public UIConstants consoleConstants() {
        return consoleConstants;
    }

    public UIMessages consoleMessages() {
        return consoleMessages;
    }

    public ExtensionConstants extensionConstants() {
        return extensionConstants;
    }

    public ExtensionMessages extensionMessages() {
        return extensionMessages;
    }
}
