/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webfx.api.extension;

import com.sun.webkit.plugin.Plugin;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author attila
 */
public class Download {
    private static final List<Download> all = new ArrayList<>();
    private final DoubleProperty progress = new SimpleDoubleProperty(-1);
    public static List<Download> listAll() {
        return all;
    }
    private final String url;

    private Download(String url) {
        this.url = url;
    }
    public ReadOnlyDoubleProperty progressProperty() {
        return progress;
    }
    public double getProgress() {
        return progress.get();
    }
    public void start() {
        
    }
    public static Download create(String url) {
        Download result = new Download(url);
        all.add(result);
        return result;
    }
}
