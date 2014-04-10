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

import com.sun.javafx.Logging;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javax.script.ScriptEngineManager;
import webfx.api.extension.ExtensionRegistry;
import webfx.api.plugin.PluginRegistry;
import webfx.app.search.SearchGoogle;
import webfx.internal.ExtendedURLConnFactory;
import webfx.render.builtin.PageNewtab;

/**
 *
 * @author bruno
 * @author hoat4
 */
public class WebFX extends Application {

    private static final RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
    private static Writer perflog;
    public static Thread nashornLoader;
    static {
        long uptime = rt.getUptime();
        try {
            perflog = new BufferedWriter(new FileWriter("perf.log"));
            perflog.write(uptime + "\t<clinit>\n");
        } catch (IOException ex) {
            perflog = null;
            Logger.getLogger(WebFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        perf("perf.log open");
        ExtendedURLConnFactory.init();
        perf("URL factory registered");
    }

    @Override
    public void start(Stage stage) throws Exception {
        perf("start() begin");
        nashornLoader = new Thread(() -> {// ~300 ms faster startup
            new ScriptEngineManager().getEngineByName("nashorn");
        }, "Nashorn preloader");
        Thread extensionLoader = new Thread(() -> {
            ExtensionRegistry.putExtension("clock.analog", AnalogClock::new);
            ExtensionRegistry.putExtension("network.info", NetworkInfo::new);
            SearchGoogle.init();
            PageNewtab.init();
        }, "WebFX extension loader");
        nashornLoader.start();
        extensionLoader.start();
        perf("Preloaders started");
        Locale locale = getCurrentLocale();
        perf("Locale loaded");

        BorderPane rootPane = new BorderPane();
        //rootPane.setCenter(new Label("Loading..."));
        Scene scene = new Scene(rootPane, 800, 600);
        perf("Scene created");

        stage.setTitle("WebFX Browser");
        stage.setScene(scene);
        stage.show();
        extensionLoader.join();
        perf("Stage showing");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("browser.fxml"), ResourceBundle.getBundle("webfx/app/browser", locale));
        perf("FXMLL created");
        rootPane.setCenter(fxmlLoader.load());
        perf("BFX loaded");
        BrowserFXController controller = fxmlLoader.getController();
        controller.setLocale(locale);

        perf("BFX getted");

        Platform.runLater(() -> {
            BrowserShortcuts shortcuts = new BrowserShortcuts(scene);
            shortcuts.setup(controller);
            perf("Keys set up");
        });
        perf("start() end");
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        perf("main() begin");
        launch(args);
        perf("main() end");
    }

    private Locale getCurrentLocale() {
        Map<String, String> namedParams = getParameters().getNamed();

        String languageParamObj = null;
        String countryParamObj = null;

        if (namedParams != null) {
            languageParamObj = namedParams.get("language");
            countryParamObj = namedParams.get("country");
        }

        Locale locale = Locale.getDefault();
        System.out.println("Locale: " + locale);

        if ((languageParamObj != null)
                && ((String) languageParamObj).trim().length() > 0)
            if ((countryParamObj != null)
                    && ((String) countryParamObj).trim().length() > 0)
                locale = new Locale(((String) languageParamObj).trim(),
                        ((String) countryParamObj).trim());
            else
                locale = new Locale(((String) languageParamObj).trim());

        return locale;
    }

    public static void perf(String name) {
        if (perflog == null)
            return;
        try {
            perflog.write(rt.getUptime() + "\t" + name + '\n');
            perflog.flush();
        } catch (IOException ex) {
            Logger.getLogger(WebFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
