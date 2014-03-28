/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webfx.api.extension;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyDoubleProperty;
import webfx.internal.DownloaderThread;

/**
 * Represents a downloaded file or a downloading file. 
 * @author hoat4
 */
public class Download {
    private static final List<Download> all = new ArrayList<>();
    private final DownloaderThread bg;
    public static List<Download> listAll() {
        return all;
    }
    private final String url;

    private Download(String url) {
        this.url = url;
        bg = new DownloaderThread(url);
    }
    /**
     * Returns the progress of the downlading, or -1 if it's unknown. 
     * @return the progress property
     */
    public ReadOnlyDoubleProperty progressProperty() {
        return bg.progressProperty();
    }
    /**
     * Returns the progress of the downlading, or -1 if it's unknown. 
     * @return the value of the progress property
     */
    public double getProgress() {
        return bg.getProgress();
    }
    /**
     * Starts downloading. 
     */
    public void start() {
        new Thread(bg).start();
    }
    /**
     * Creates a new Download object, and adds it to the list of Downloads. 
     * @param url the Download's source
     * @return the newly create Download object
     */
    public static Download create(String url) {
        Download result = new Download(url);
        all.add(result);
        return result;
    }
}
