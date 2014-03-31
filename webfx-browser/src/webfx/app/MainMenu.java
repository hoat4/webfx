/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.app;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    public static final List<MenuItem> list = new ArrayList<>();
    private final BrowserFXController app;
    private ContextMenu menu ;
    private boolean inited;
    public MainMenu(BrowserFXController app) {
        this.app = app;
    }

    public void init() {
        menu = new ContextMenu();
        menu.getItems().addAll(list);
        inited = true;
        System.out.println("Menu inited");
    }

    public void open(Button menuButton) {
        System.out.println("Opening menu...");
        if(!inited)init();
        if (menu.isShowing())
            menu.hide();
        else
            menu.show(menuButton, Side.BOTTOM, 0, 0);
        System.out.println("Menu open");
    }

    
}
