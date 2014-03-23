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
package webfx.app;

import com.webfx.PageContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;
import webfx.WindowContext;

/**
 *
 * @author bruno
 */
public class HTMLTab implements BrowserTab {

    private final WebView browser;
    private final WebEngine webEngine;
    private SimpleObjectProperty<Node> contentProperty;
    private WindowContext window;
    private boolean isLoading;
    private final ObservableBooleanValue hasHistoryBack;
    private final SimpleBooleanProperty hasHistoryForward = new SimpleBooleanProperty();

    public HTMLTab(BrowserFXController app) {
        browser = new WebView();
        webEngine = browser.getEngine();
        this.hasHistoryBack = webEngine.getHistory().currentIndexProperty().greaterThan(0);
        webEngine.getHistory().currentIndexProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                hasHistoryForward.set(webEngine.getHistory().getCurrentIndex() < webEngine.getHistory().getEntries().size() - 1);
            }
        });
        contentProperty = new SimpleObjectProperty<>((Node) browser);
        webEngine.getLoadWorker().stateProperty().addListener((ObservableValue<? extends State> ov, State oldv, State newv) -> {
            if (newv == State.SUCCEEDED) {
                isLoading = false;
                app.stop();
                Document document = (Document) webEngine.executeScript("document");
                NodeList nodeList = document.getElementsByTagName("a");
                for (int i = 0; i < nodeList.getLength(); i++) {
                    EventTarget n = (EventTarget) nodeList.item(i);
                    n.addEventListener("click", (Event event) -> {
                        EventTarget eventTarget = event.getTarget();

                        if (eventTarget instanceof HTMLAnchorElement == false)
                            return;

                        HTMLAnchorElement hrefObj = (HTMLAnchorElement) event.getTarget();
                        String href = hrefObj.getHref();
                        if (href.endsWith(".fxml")) {
                            System.out.println("OPENING IN NEW TAB" + href);
                            window.blankTab().go(href);
                            event.preventDefault();
                        }
                    }, true);
                }
            } else if (newv == State.FAILED || newv == State.READY || newv == State.CANCELLED) {
                isLoading = false;
                app.stop();
            } else {
                isLoading = true;
                app.tabLoading();
            }
        });
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        return webEngine.titleProperty();
    }

    @Override
    public ReadOnlyStringProperty locationProperty() {
        return webEngine.locationProperty();
    }

    public ReadOnlyBooleanProperty loadingProperty() {
        return webEngine.getLoadWorker().runningProperty();
    }

    @Override
    public void stop() {
        webEngine.getLoadWorker().cancel();
    }

    @Override
    public ObjectProperty<Node> contentProperty() {
        return contentProperty;
    }

    @Override
    public void setWindowContext(WindowContext tm) {
        this.window = tm;
    }

    public WindowContext getTabManager() {
        return window;
    }

    @Override
    public boolean isStoppable() {
        return true;
    }

    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void go(URL destination, String originalURL) {
        webEngine.load(destination.toExternalForm());
    }

    @Override
    public PageContext getPageContext() {
        if (webEngine.getLocation().isEmpty())
            return null;
        else
            try {
                return new PageContext(new URL(webEngine.getLocation()));
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
    }

    @Override
    public ObservableBooleanValue hasHistoryBack() {
        return hasHistoryBack;
    }

    @Override
    public ObservableBooleanValue hasHistoryForward() {
        return hasHistoryForward;
    }

    @Override
    public void back() {
        webEngine.executeScript("history.back()");
    }

    @Override
    public void forward() {
        webEngine.executeScript("history.forward()");
    }

    @Override
    public void reload() {
        webEngine.reload();
    }
}
