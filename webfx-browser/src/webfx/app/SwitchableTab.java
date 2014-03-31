/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import webfx.api.ObjectWrapper;
import webfx.api.SecurityHolder;
import webfx.api.page.TabContext;
import webfx.api.page.WindowContext;
import webfx.api.plugin.BrowserTab;
import webfx.api.plugin.PageContext;
import webfx.internal.URLVerifier;
import webfx.render.fxml.FXTab;
import webfx.render.fxml.WebFXView;
import webfx.render.html.HTMLTab;

/**
 *
 * @author attila
 */
public class SwitchableTab implements BrowserTab, TabContext {

    private BrowserTab tab;
    private final Locale locale;
    private final BrowserFXController app;
    private final SimpleStringProperty titleProp = new SimpleStringProperty("<null>");
    private final SimpleObjectProperty<URL> locProp = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Node> contentProp = new SimpleObjectProperty<>(new ProgressBar());

    public SwitchableTab(BrowserFXController app) {
        this.locale = app.locale;
        this.app = app;
    }

    @Override
    public ObjectProperty<Node> contentProperty() {
        return contentProp;
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        return titleProp;
    }

    @Override
    public ReadOnlyObjectProperty<URL> locationProperty() {
        return locProp;
    }

    @Override
    public void stop() {
        tab.stop();
    }

    @Override
    public boolean isStoppable() {
        return tab.isStoppable();
    }

    @Override
    public boolean isLoading() {
        return tab.isLoading();
    }

    @Override
    public void setWindowContext(WindowContext tm) {
        tab.setWindowContext(tm);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.tab);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SwitchableTab other = (SwitchableTab) obj;
        if (!Objects.equals(this.tab, other.tab))
            return false;
        return true;
    }

    @Override
    public void forward() {
        tab.forward();
    }

    @Override
    public void back() {
        tab.back();
    }

    @Override
    public void go(String url) {
        goTo(url, "Untitled", true);
    }

    @Override
    public void reload() {
        tab.reload();
    }

    @Override
    public void go(URL destination, String originalURL) {
        go(originalURL);
    }

    @Override
    public PageContext getPageContext() {
        return tab.getPageContext();
    }
    private final SimpleBooleanProperty hasHistoryBack = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty hasHistoryForward = new SimpleBooleanProperty(true);

    @Override
    public ObservableBooleanValue hasHistoryBack() {
        return hasHistoryBack;
    }

    @Override
    public ObservableBooleanValue hasHistoryForward() {
        return hasHistoryForward;
    }
    private final InvalidationListener titleListener = (ignored) -> titleProp.set(tab.titleProperty().get());
    private final InvalidationListener locListener = (ignored) -> {
        URL newloc = tab.locationProperty().get();
        if (URLVerifier.isUrlKeeper(newloc))
            return;
        if (URLVerifier.isHided(newloc)) {
            System.out.println("Set4 locProp to null");
            locProp.set(null);
        } else {
            System.out.println("Set4 locprop to " + newloc);
            locProp.set(newloc);
        }
    };
    private final InvalidationListener contentListener = (ignored) -> contentProp.set(tab.contentProperty().get());

    private void goTo(String url, String title, boolean updateLocProp) {
        WebFX.perf("SwitchableTab::goTo begin");
        if (tab != null) {
            tab.titleProperty().removeListener(titleListener);
            tab.locationProperty().removeListener(locListener);
            tab.contentProperty().removeListener(contentListener);
        }
        URL destination = null;
        BrowserTab impl;
        if (URLVerifier.isFXML(url))
            impl = new FXTab(locale, this, app).setTitle(title);
        else
            impl = new HTMLTab(app);
        tab = impl;
        impl.setWindowContext(app);
        String errorLoc = null;
        Throwable error = null;
        if (url.startsWith("file:/") || url.startsWith("jar:/") || url.startsWith("wfx:/") || url.startsWith("http:/") || url.startsWith("https:/") || url.startsWith("chrome://"))
            try {
                destination = new URL(url);
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                errorLoc = "webfx.page.urlparse";
                error = ex;
            }
        else {
            URL basePath = tab.getPageContext().getBasePath();
            try {
                destination = new URL(basePath.toString() + "/" + url);
            } catch (MalformedURLException ex) {
                Logger.getLogger(WebFXView.class.getName()).log(Level.SEVERE, null, ex);
                errorLoc = "webfx.page.urlparse";
                error = ex;
            }
        }
        if (destination != null && destination.getProtocol().equals("chrome")) {
            URLConnection conn;
            try {
                conn = destination.openConnection();
                if (conn == null) {
                    locProp.set(destination);
                    System.out.println("Set2 locProp to " + destination);
                    showError("webfx.chromeuri.notfound", "Chrome URL not found: " + destination.toExternalForm());
                    return;
                }
            } catch (IOException ex) {
                Logger.getLogger(WebFXView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (errorLoc != null || error != null)
            try {
                destination = new URL("chrome://error?details=" + ObjectWrapper.create(error).allowRead(tab.security()).uuid() + "&code=" + errorLoc);
            } catch (MalformedURLException ex1) {
                throw new RuntimeException(ex1);
            }
        titleProp.set(tab.titleProperty().get());
        if (!URLVerifier.isUrlKeeper(destination))
            if (URLVerifier.isHided(destination)) {
                System.out.println("Set3 locProp to null");
                locProp.set(null);
            } else {
                System.out.println("set3 locProp to " + tab.locationProperty().get());
                locProp.set(tab.locationProperty().get());
            }
        contentProp.set(tab.contentProperty().get());
        System.out.println("Set locProp to " + locProp.get());

        tab.titleProperty().addListener(titleListener);
        tab.locationProperty().addListener(locListener);
        tab.contentProperty().addListener(contentListener);

        WebFX.perf("BrowserTab::go");
        tab.go(destination, url);
        WebFX.perf("SwitchableTab::goTo end");
    }

    @Override
    public void close() {
        app.closeTab(this);
    }

    @Override
    public URL getUserURL() {
        return locProp.get();
    }

    @Override
    public WindowContext getWindow() {
        return app;
    }

    public FXTab asFX() {
        return (FXTab) tab;
    }

    @Override
    public SecurityHolder security() {
        return tab.security();
    }

    @Override
    public URL getRealURL() {
        return tab.locationProperty().get();
    }

    @Override
    public void showError(String code, String details) {
        try {
            goTo("chrome://error?details=" + URLEncoder.encode(details, "UTF-8") + "&code=" + code, "Error", false);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("System don't support UTF-8", ex);
        }
    }
}
