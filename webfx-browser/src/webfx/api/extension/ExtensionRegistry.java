/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.api.extension;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.scene.control.MenuItem;
import webfx.api.page.Adapter;
import webfx.api.page.TabContext;
import webfx.api.plugin.BrowserTab;
import webfx.app.MainMenu;
import webfx.internal.ExtensionResolver;

/**
 *
 * @author attila
 */
public class ExtensionRegistry {

    public static void putExtension(String name, Supplier<?> factory) {
        putExtension(name, (context) -> factory.get());
    }

    public static void putExtension(String name, Function<Adapter, ?> factory) {
        ///System.getSecurityManager().checkPermission(new PluginPermission("webfx.extension.add"));
        ExtensionResolver.EXTENSIONS.put(name, (Function<Object, ?>) (Function<?, ?>) factory);
    }

    public static void putPage(String url, Function<TabContext, ? extends BrowserTab> factory) {
        ExtensionResolver.EXTENSIONS.put("url." + url, (Function<Object, ?>) (Function<?, ?>) factory);
    }

    public static void putMenuItem(MenuItem item) {
        MainMenu.list.add(item);
    }
}
