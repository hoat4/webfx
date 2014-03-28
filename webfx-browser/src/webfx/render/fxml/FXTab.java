/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package webfx.render.fxml;

import webfx.api.plugin.PageContext;
import webfx.api.plugin.BrowserTab;
import webfx.render.fxml.WebFXRegion;
import java.net.URL;
import java.util.Locale;
import java.util.Properties;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.Node;
import webfx.api.SecurityHolder;
import webfx.api.page.Adapter;
import webfx.api.page.TabContext;
import webfx.api.page.WindowContext;

/**
 *
 * @author Bruno Borges <bruno.borges at oracle.com>
 */
public class FXTab implements BrowserTab {
    private final ReadOnlyObjectWrapper<URL> locationProperty = new ReadOnlyObjectWrapper<>();
    private final SimpleObjectProperty<Node> contentProperty = new SimpleObjectProperty<>();
    private final WebFXRegion webfx;
    private WindowContext tabManager;

    public FXTab(TabContext nav, WindowContext window) {
        webfx = new WebFXRegion(nav, window);
        locationProperty.bind(webfx.locationProperty());
        contentProperty.set(webfx);
    }

    public FXTab(Locale locale, TabContext nav, WindowContext window) {
        this(nav, window);
        webfx.setLocale(locale);
    }

    @Override
    public ObjectProperty<Node> contentProperty() {
        return contentProperty;
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        return webfx.getCurrentViewTitleProperty();
    }
    public FXTab setTitle(String title) {
        webfx.getCurrentViewTitleProperty().set(title);
        return this;
    }
    @Override
    public ReadOnlyObjectProperty<URL> locationProperty() {
        return locationProperty;
    }

    @Override
    public void stop() {
        contentProperty.set(null);
    }

    @Override
    public void setWindowContext(WindowContext tm) {
        this.tabManager = tm;
    }

    @Override
    public boolean isStoppable() {
        return false;
    }

    @Override
    public boolean isLoading() {
        return false;
    }

    @Override
    public void go(URL destination, String originalURL) {
        webfx.loadUrl(destination, originalURL.startsWith("chrome://"));
    }

    @Override
    public PageContext getPageContext() {
        return webfx.getPageContext();
    }

    @Override
    public ObservableBooleanValue hasHistoryBack() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ObservableBooleanValue hasHistoryForward() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void back() {
        
    }

    @Override
    public void forward() {
    }

    @Override
    public void reload() {
        webfx.load();
    }

    public Adapter getAdapter() {
        return webfx.getAdapter();
    }

    @Override
    public SecurityHolder security() {
        return webfx.defaultView.secholder;
    }
 
}
