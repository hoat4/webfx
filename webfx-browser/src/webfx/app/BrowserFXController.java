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

import webfx.render.fxml.FXTab;
import webfx.api.plugin.BrowserTab;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.PopupWindow;
import webfx.api.page.Adapter;
import webfx.api.page.TabContext;
import webfx.api.page.WindowContext;
import webfx.internal.BindingUtils;

/**
 *
 * @author Bruno Borges at oracle.com
 * @author attila at hontvari.net
 */
public class BrowserFXController implements WindowContext, Initializable {

    private static final Logger LOGGER = Logger.getLogger(BrowserFXController.class.getName());
    /**
     * Components
     */
    @FXML
    private TabPane tabPane;
    @FXML
    private TextField urlField;
    @FXML
    private Button reloadButton, stopButton;
    @FXML
    private Button backButton, forwardButton;
    @FXML
    private Button menuButton;
    private MainMenu menu;
    /**
     * Internal
     */
    private SingleSelectionModel<Tab> selectionTab;
    private final ConcurrentHashMap<Integer, SwitchableTab> browserMap = new ConcurrentHashMap<>();
    Locale locale;

    public void exit() {
        LOGGER.info("Exiting...");
        System.exit(0);
    }

    public void newTab() {
        Tab tab = new Tab("New tab");
        tab.setClosable(true);
        tabPane.getTabs().add(tabPane.getTabs().size() - 1, tab);
        selectionTab.select(tabPane.getTabs().size() - 2);
        openPage("chrome://newtab", "New tab");
    }

    private SwitchableTab selectedBrowserTab() {
        return browserMap.get(selectionTab.getSelectedIndex());
    }

    public void tabLoading() {
        stopButton.setDisable(false);
    }

    public void stop() {
        selectedBrowserTab().stop();
        stopButton.setDisable(true);
    }

    public void reload() {
        selectedBrowserTab().reload();
    }

    public void back() {
        selectedBrowserTab().back();
    }

    public void forward() {
        selectedBrowserTab().forward();
    }

    public void closeTab() {
        LOGGER.info("Closing Tab...");
        /*
         if (tabPane.getTabs().size() > 1) {
         int indexBrowserTab = selectionTab.getSelectedIndex();
         browserMap.remove(indexBrowserTab);
         tabPane.getTabs().remove(selectionTab.getSelectedIndex());
         }*/
        closeTab((SwitchableTab) selectedBrowserTab());
    }

    public void openFXPage() {
        openPage(urlField.getText());
    }

    public void openPage(String location) {
        //      System.out.println("**************************"+location);
//        Platform.runLater(()->     urlField.setText(location));
        openPage(location, "Untitled");
    }

    public void openPage(String location, String title) {
        if (!location.startsWith("http://") && !location.startsWith("https://") && !location.startsWith("chrome://"))
            location = "http://" + location;

        final String newloc = location;
        Runnable task = () -> {
            SwitchableTab tab = new SwitchableTab(this);

            tab.go(newloc);

            selectionTab.getSelectedItem().contentProperty().bind(tab.contentProperty());
            browserMap.put(selectionTab.getSelectedIndex(), tab);
            System.err.println("Put new tab @ " + selectionTab.getSelectedIndex());
            urlField.textProperty().bind(BindingUtils.convert(tab.locationProperty()));
            stopButton.disableProperty().set(!tab.isStoppable());
            selectionTab.getSelectedItem().textProperty().bind(tab.titleProperty());
            LOGGER.log(Level.INFO, "Title used for new tab: {0}", tab.titleProperty().get());
        };
        if (Platform.isFxApplicationThread())
            task.run();
        else
            Platform.runLater(task);
    }

    public void initialize(URL location, ResourceBundle resources) {
        WebFX.perf("BFX init begin in " + Thread.currentThread().getName());
        menu = new MainMenu(this);
        WebFX.perf("Menu created");
        reloadButton.disableProperty().bind(stopButton.disabledProperty().not());
        WebFX.perf("Binded reload disable");
        urlField.focusedProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue.booleanValue())
                urlField.textProperty().unbind();
            else if (selectedBrowserTab() != null)
                urlField.textProperty().bind(BindingUtils.convert(selectedBrowserTab().locationProperty()));
        });
        WebFX.perf("Binded urlField.focused");

        tabPane.getTabs().addListener((ListChangeListener.Change<? extends Tab> change) -> {
            ObservableList<? extends Tab> tabs = change.getList();

            // disabled the close tab menu item if selected tab is not cloeable
            //closeTab.disableProperty().bind(selectionTab.getSelectedItem().closableProperty().not());
            // set the first tab closeable if more than one tab
            tabs.get(0).setClosable(tabs.size() > 2);

            // set others tab closeable, if they exist
            for (int i = 1; i < tabs.size() - 1; i++) {
                tabs.get(i).setClosable(true);
            }
        });

        selectionTab = tabPane.selectionModelProperty().getValue();
        WebFX.perf("Inited tabs");

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> {
            LOGGER.info("Tab selection changed");
            BrowserTab selectedBrowserTab = selectedBrowserTab();
            if (selectedBrowserTab == null) {
                LOGGER.info("No tab selected");
                urlField.textProperty().unbind();
                urlField.textProperty().setValue("");
                urlField.setText("");
            } else {
                LOGGER.info("There's a tab selected");
                urlField.textProperty().bind(BindingUtils.convert(selectedBrowserTab.locationProperty()));
                stopButton.setDisable(!selectedBrowserTab.isLoading());
            }
        });
        WebFX.perf("Inited tab selection");

            plusTab.setClosable(false);
        plusTab.setContent(new Label());
        tabPane.getTabs().add(plusTab);
        plusTab.selectedProperty().addListener((a, b, val) -> {
            if (val)
                newTab();
        });
        WebFX.perf("Inited plusTab"); 
        
        newTab();
        WebFX.perf("Opened first tab");

        final int size = 16;
        Platform.runLater(() -> {
            setButtonIcon(menuButton, "gear", size);
            setButtonIcon(stopButton, "stop", size);
            setButtonIcon(backButton, "left", size);
            setButtonIcon(forwardButton, "right", size);
            setButtonIcon(reloadButton, "clock", size);
            WebFX.perf("Loaded button icons");
        });

  


        Platform.runLater(menu::init);
    }

    private void setButtonIcon(Button button, String icon, int size) {
        InputStream is = getClass().getResourceAsStream("/webfx/icons/" + icon + "_" + size + ".png");
        Image block = new Image(is);
        ImageView iv = new ImageView(block);
        button.setGraphic(iv);
    }

    @FXML
    public void showMenu() {
        menu.open(menuButton);
    }

    void setLocale(Locale locale) {
        this.locale = locale;
    }

    private final Tab plusTab = new Tab("+");

    public Adapter currentFXTab() {
        return ((FXTab) selectedBrowserTab().asFX()).getAdapter();
    }

    @Override
    public TabContext openTab() {
        newTab();
        return currentFXTab().tab;
    }

    @Override
    public TabContext currentTab() {
        return selectedBrowserTab();
    }

    public void closeTab(SwitchableTab aThis) {
        if (tabPane.getTabs().size() < 3) {
            browserMap.get(0).go("chrome://newtab");
            return;
        }
        browserMap.entrySet().stream().
                filter((entry) -> entry.getValue() == aThis).map((entry) -> entry.getKey()).forEach((index) -> {
                    System.out.println("Removing " + index);
                    Platform.runLater(() -> {
                        browserMap.remove(index);
                        tabPane.getTabs().remove((int) index);
                    });
                });
        /*for (Map.Entry<Integer, BrowserTab> entry : browserMap.entrySet()) {
         if (entry.getValue() == aThis) {
         Platform.runLater(() -> browserMap.remove(entry.getKey()));
         tabPane.getTabs().remove(entry.getKey());
         }
         }*/
    }

}
