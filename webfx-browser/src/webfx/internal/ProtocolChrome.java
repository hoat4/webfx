/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webfx.internal;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 *
 * @author attila
 */
class ProtocolChrome extends URLStreamHandler{
    private final String name;

    public ProtocolChrome(String name) {
        this.name = name;
    }
    
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        System.out.println("Opening chrome ("+name+") connection: "+u);
        URL url = getClass().getResource("/chrome/"+name+"/"+u.getHost()+".fxml");
        if(url == null)
            System.err.println("Cannot find "+url.toExternalForm());
        return url.openConnection();
    }

}
