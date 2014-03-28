/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2014 Sun Microsystems, Inc.
 */
package webfx.api.page;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import webfx.api.ObjectWrapper;
import webfx.api.SecurityHolder;
import webfx.api.extension.ExtensionRegistry;
import webfx.api.plugin.PluginRegistry;
import webfx.app.SharedSecrets;
import webfx.internal.URLVerifier;

/**
 *
 * @author attila
 */
public class Adapter {

    public final ResourceBundle i18n;
    /**
     * Information of the operating system.
     */
    public final OS os = new OS();
    /**
     * Reference to the {@link TabContext} object.
     */
    public final TabContext tab;
    private final SecurityHolder securityHolder;

    public Adapter(ResourceBundle resourceBundle, TabContext tab, SecurityHolder securityHolder) {
        this.i18n = resourceBundle;
        this.tab = tab;
        this.securityHolder = securityHolder;
    }

    /**
     * Creates a new extension object.
     *
     * @param <T> the type of the result
     * @param type name of the extension
     * @return the extension object
     * @throws ExtensionNotFoundException if the extension not found
     */
    public <T> T create(String type) throws ExtensionNotFoundException {
        Function<Adapter, T> factory = (Function<Adapter, T>) ExtensionRegistry.EXTENSIONS.get(type);
        if (factory == null)
            throw new ExtensionNotFoundException("Extension " + type + " not found");
        else
            return factory.apply(this);
    }

    /**
     * Defers the specified {@link Runnable} task to the next 'pulse'.
     *
     * @param runnable the task which to be deferred
     * @return this {@link Adapter} object
     */
    public Adapter defer(Runnable runnable) {
        Platform.runLater(runnable);
        return this;
    }

    /**
     * Returns the window that contains this tab.
     *
     * @return tab's window
     */
    public WindowContext getWindow() {
        return tab.getWindow();
    }

    public <T> T query(String name) {
        switch (name.charAt(0)) {
            case '$':
                return (T)getQueryParam(name.substring(1));
            case '_':
                ObjectWrapper ow = SharedSecrets.ow.get(getQueryParam(name.substring(1)));
                if(ow == null)
                    return null;
                ow.checkRead(securityHolder);
                return (T) ow.get();
            default:
                throw new IllegalArgumentException("Unknown query type: '" + name.charAt(0) + "'");
        }
    }

    private String getQueryParam(String name) {
        return URLVerifier.getQueryParameter(tab.getRealURL().toExternalForm(), name);
    }

    public SecurityHolder getSecurityHolder() {
        return securityHolder;
    }
    
}
