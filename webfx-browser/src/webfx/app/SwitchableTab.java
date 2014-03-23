/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.app;

import com.webfx.PageContext;
import com.webfx.WebFXView;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import webfx.TabContext;
import webfx.WindowContext;

/**
 *
 * @author attila
 */
public class SwitchableTab implements BrowserTab, TabContext {

    private BrowserTab tab;
    private final Locale locale;
    private final BrowserFXController app;
    private final SimpleStringProperty titleProp = new SimpleStringProperty("<null>");
private final SimpleStringProperty locProp = new SimpleStringProperty("<null>");
private final SimpleObjectProperty<Node> contentProp = new SimpleObjectProperty<Node>(new ProgressBar());
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
    public ReadOnlyStringProperty locationProperty() {
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
        goTo(url, "Untitled");
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
    private final InvalidationListener locListener = (ignored) -> locProp.set(tab.locationProperty().get());
private final InvalidationListener contentListener = (ignored) -> contentProp.set(tab.contentProperty().get());

    private void goTo(String url, String title) {
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
        if (url.startsWith("file:/") || url.startsWith("jar:/") || url.startsWith("wfx:/") || url.startsWith("http:/") || url.startsWith("https:/"))
            try {
                destination = new URL(url);
            } catch (MalformedURLException ex) {
                Logger.getLogger(WebFXView.class.getName()).log(Level.SEVERE, null, ex);
            }
        else if (url.startsWith("chrome://"))
            destination = getClass().getResource("/chrome_uris/" + url.substring("chrome://".length()) + ".fxml");
        else {
            URL basePath = tab.getPageContext().getBasePath();
            try {
                destination = new URL(basePath.toString() + "/" + url);
            } catch (MalformedURLException ex) {
                Logger.getLogger(WebFXView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        titleProp.set(tab.titleProperty().get());
       locProp.set(tab.locationProperty().get());
       contentProp.set(tab.contentProperty().get());
        System.out.println("Set locProp to "+locProp.get());
        
        tab.titleProperty().addListener(titleListener);
        tab.locationProperty().addListener(locListener);
        tab.contentProperty().addListener(contentListener);
        
        tab.go(destination, url);
    }

    @Override
    public void close() {
            app.closeTab(this);
    }

    @Override
    public String currentURL() {
        return locProp.get();
    }

    @Override
    public WindowContext window() {
        return app;
    }
public FXTab asFX() {
    return (FXTab)tab;
}
}
