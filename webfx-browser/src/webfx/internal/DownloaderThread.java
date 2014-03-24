/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import javafx.concurrent.Task;

/**
 *
 * @author attila
 */
public class DownloaderThread extends Task<File> {

    private final String uri;

    public DownloaderThread(String uri) {
        this.uri = uri;
    }

    @Override
    protected File call() throws Exception {
        File dldir = new File(System.getProperty("user.home"), "Downloads");
        if (!dldir.exists())
            if (!dldir.mkdirs())
                throw new IOException("Cannot create the downloads directory");
        File result = new File(dldir, new URLVerifier(uri).getPageName());
        URL url = new URL(uri);
        URLConnection conn = url.openConnection();
        InputStream in = url.openStream();
        OutputStream out = new FileOutputStream(result);
        long max = conn.getContentLengthLong();
        long i = 0;
        for(int ch; (ch = in.read()) != -1;) {
            out.write(ch);
            if(max != -1)
                updateProgress(i, max);
            i++;
        }
        return result;
    }

}
