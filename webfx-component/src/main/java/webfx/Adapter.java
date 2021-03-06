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
package webfx;

import com.webfx.PluginRegistry;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;

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

    public Adapter(ResourceBundle resourceBundle, TabContext tab) {
        this.i18n = resourceBundle;
        this.tab = tab;
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
        Function<Adapter, T> factory = (Function<Adapter, T>) PluginRegistry.EXTENSIONS.get(type);
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
        return tab.window();
    }
}
