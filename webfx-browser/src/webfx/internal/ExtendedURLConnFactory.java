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
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.html.HTMLMapElement;

/**
 *
 * @author attila
 */
public class ExtendedURLConnFactory implements URLStreamHandlerFactory{
    private static Map<String, URLStreamHandler> map = new HashMap<>();
    public static void init() {
        map.put("http", new ProtocolDefault(new sun.net.www.protocol.http.Handler()));
        map.put("https", new ProtocolDefault(new sun.net.www.protocol.https.Handler()));
        map.put("file", new ProtocolDefault(new sun.net.www.protocol.file.Handler()));
        map.put("netdoc", new ProtocolDefault(new sun.net.www.protocol.netdoc.Handler()));
        map.put("jar", new ProtocolDefault(new sun.net.www.protocol.jar.Handler()));
        map.put("mailto", new ProtocolDefault(new sun.net.www.protocol.mailto.Handler()));
        map.put("ftp", new ProtocolDefault(new sun.net.www.protocol.ftp.Handler()));
        map.put("wfx", new ProtocolWFX("wfx"));
        map.put("chrome", new ProtocolChrome("chrome"));
        map.put("about", new ProtocolChrome("about"));
        URL.setURLStreamHandlerFactory(new ExtendedURLConnFactory());
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        return map.get(protocol);
    }
}
