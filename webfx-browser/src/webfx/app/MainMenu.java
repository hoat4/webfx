/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.app;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.PopupWindow;

/**
 * FXML Controller class
 *
 * @author attila
 */
public class MainMenu {

    private final BrowserFXController app;
    private final ContextMenu menu = new ContextMenu();

    public MainMenu(BrowserFXController app) {
        this.app = app;
        init();
    }

    private void init() {
        MenuItem item = new MenuItem("Downloads");
        item.setOnAction((evt) -> app.blankTab().go("chrome://downloads"));
        menu.getItems().add(item);
    }

    public void open(Button menuButton) {
        if (menu.isShowing())
            menu.hide();
        else
            menu.show(menuButton, Side.BOTTOM, 0, 0);
    }
}
