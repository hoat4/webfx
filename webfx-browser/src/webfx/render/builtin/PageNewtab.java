/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.render.builtin;

import java.net.URL;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import webfx.api.extension.ExtensionRegistry;
import webfx.api.page.TabContext;

/**
 *
 * @author attila
 */
public class PageNewtab extends PageBase {

    public PageNewtab(TabContext tab) {
        super(tab);
        initSecChanger();
    }

    @Override
    public void go(URL destination, String originalURL) {
        reload();
    }

    @Override
    protected void doReload() {
        VBox root = new VBox();
        ImageView googlogo = new ImageView((Image) context.create("search.google.logo"));
        googlogo.setPreserveRatio(true);
        googlogo.setFitWidth(200);
        googlogo.setFitHeight(150);
        TextField searchTextField = new TextField();
        VBox.setMargin(searchTextField, new Insets(10, 30, 10, 30));
        searchTextField.setOnAction((evt) -> {
            context.tab.search(searchTextField.getText());
        });
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(googlogo, searchTextField);
        content.set(root);
        title.set("New tab");
    }

    public static void init() {
        ExtensionRegistry.putPage("chrome://newtab", PageNewtab::new);
    }

}
