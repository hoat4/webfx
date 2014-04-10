/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.render.builtin;

import java.net.URL;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.scene.Node;
import webfx.api.SecurityHolder;
import webfx.api.page.Adapter;
import webfx.api.page.TabContext;
import webfx.api.page.WindowContext;
import webfx.api.plugin.BrowserTab;
import webfx.api.plugin.PageContext;

/**
 *
 * @author attila
 */
public abstract class PageBase implements BrowserTab {

    protected final SimpleObjectProperty<Node> content = new SimpleObjectProperty<>();
    protected final SimpleObjectProperty<URL> location = new SimpleObjectProperty<>();
    protected final SimpleStringProperty title = new SimpleStringProperty();
    protected WindowContext window;
    protected final SimpleBooleanProperty historyCanBack = new SimpleBooleanProperty();
    protected final SimpleBooleanProperty historyCanForward = new SimpleBooleanProperty();
    protected SecurityHolder sec = new SecurityHolder();
    protected Adapter context;
    private final ObjectBinding<PageContext> pageContext = new ObjectBinding<PageContext>() {
        {
            bind(location);
        }

        @Override
        protected PageContext computeValue() {
            return new PageContext(location.get());
        }
    };
    private boolean autoSecChangeEnabled;
    protected final TabContext tab;

    public PageBase(TabContext tab) {
        this.tab = tab;
    }
            
    protected void initSecChanger() {
        content.addListener((a, b, c) -> updateContext());
        autoSecChangeEnabled = true;
    }

    @Override
    public ObjectProperty<Node> contentProperty() {
        return content;
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        return title;
    }
    @Override
    public final void reload() {
        if(autoSecChangeEnabled)
            updateContext();
        doReload();
    }
    protected abstract void doReload();
    @Override
    public ReadOnlyObjectProperty<URL> locationProperty() {
        return location;
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Stop not supported on " + getClass().getName());
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
    public void setWindowContext(WindowContext tm) {
        window = tm;
    }

    @Override
    public ObservableBooleanValue hasHistoryBack() {
        return historyCanBack;
    }

    @Override
    public ObservableBooleanValue hasHistoryForward() {
        return historyCanForward;
    }

    @Override
    public void back() {
        if (hasHistoryBack().get()) {
            throw new IllegalStateException(this + " has historyCanBack flag, but it doesn't has back() implementation");
        }
        throw new UnsupportedOperationException(this + " doesn't support back()");
    }

    @Override
    public void forward() {
        if (hasHistoryBack().get()) {
            throw new IllegalStateException(this + " has historyCanForward flag, but it doesn't has forward() implementation");
        }
        throw new UnsupportedOperationException(this + " doesn't support forward()");
    }

    @Override
    public PageContext getPageContext() {
        return pageContext.get();
    }

    @Override
    public SecurityHolder security() {
        return sec;
    }

    private void updateContext() {
        sec = new SecurityHolder();
        context = new Adapter(null, tab, sec);
    }

}
