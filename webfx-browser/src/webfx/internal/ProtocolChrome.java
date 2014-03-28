/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webfx.internal;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.URLStreamHandler;
import java.util.HashSet;
import java.util.Set;
import webfx.api.ObjectWrapper;

/**
 *
 * @author attila
 */
 public class ProtocolChrome extends URLStreamHandler{
    private static final Set<String> hidedHosts = new HashSet();
static {
    hidedHosts.add("newtab");
    hidedHosts.add("error");
}
    public static boolean isHided(URL url) {
        if(url.getProtocol().equals("chrome")) {
            return hidedHosts.contains(url.getHost());
        }
        return false;
    }
    private final String name;

    ProtocolChrome(String name) {
        this.name = name;
    }
    
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        System.out.println("Opening chrome ("+name+") connection: "+u);
        URL url = getClass().getResource("/chrome/"+name+"/"+u.getHost()+".fxml");
        if(url == null)
            return null;
        return url.openConnection();
    }
}
