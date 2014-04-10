/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webfx.app.search;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import javafx.scene.image.Image;
import webfx.api.extension.ExtensionRegistry;
import webfx.api.page.Adapter;

/**
 *
 * @author attila
 */
public class SearchGoogle {
    private static final Image LOGO = new Image("/external/logo11w.png");

    public static void init() {
            ExtensionRegistry.putExtension("search.google", SearchGoogle::new);
            ExtensionRegistry.putExtension("search.google.logo", () -> LOGO);
    }
    private final Adapter context;
    public SearchGoogle(Adapter context) {
        this.context = context;
    }
    public Image getLogo() {
        return LOGO;
    }
    public void search(String text) throws MalformedURLException  {
        context.getWindow().blankTab().go("http://www.google.com/search?q="+URLEncoder.encode(text)+"&ie=utf-8&oe=utf-8&aq=t");
    }
            
}
