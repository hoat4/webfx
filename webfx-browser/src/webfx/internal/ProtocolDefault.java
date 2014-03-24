/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.internal;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author attila
 */
public class ProtocolDefault extends URLStreamHandler {

    private final URLStreamHandler delegated;
    private Method method;

    ProtocolDefault(URLStreamHandler delegated) {
        this.delegated = delegated;
        try {
            method = delegated.getClass().getDeclaredMethod("openConnection", URL.class);
            method.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    @SuppressWarnings("ThrowableResultIgnored")
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        System.out.println("Open connection: " + u);
        try {
            return (URLConnection) method.invoke(delegated, u);
        } catch (IllegalAccessException | IllegalArgumentException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof IOException)
                throw (IOException) ex.getCause();
            else
                throw new IOException(ex.getCause());
        }
    }

}
