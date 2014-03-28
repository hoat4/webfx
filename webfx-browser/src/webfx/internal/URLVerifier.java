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
package webfx.internal;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bruno
 */
public class URLVerifier {

    public static String getQueryParameter(String query, String name) {
        System.out.println("gqp " + query);
        query = query.substring(query.lastIndexOf("?") + 1);
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            try {
                int idx = pair.indexOf("=");
                String n = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                if (n.equals(name)) {
                    String result = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                    System.out.println("gqpr " + result);
                    return result;
                }
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
        }
        System.out.println("qpnf: " + name + " in " + query);
        return null;
    }

    private URI location;
    private URI basePath;
    private String pageName;
    private boolean fxml;

    public URLVerifier(String location) throws URISyntaxException {
        if (!location.startsWith("http://") && !location.startsWith("https://") && !location.startsWith("chrome://"))
            location = "http://" + location;

        this.location = new URI(location);
        this.basePath = extractBasePath();
    }

    public URLVerifier(URI location) throws URISyntaxException {
        this.location = location;
        this.basePath = extractBasePath();
    }

    private URI extractBasePath() throws URISyntaxException {
        int lastSlash = location.getPath().lastIndexOf('/');

        if (lastSlash == -1) {
            pageName = "index";
            return location;
        }

        String file = location.getPath();
        String path = file.substring(0, lastSlash);
        URI base = null;

        base = new URI(location.getScheme(), null, location.getHost(), location.getPort(), path, null, null);

        pageName = file.substring(lastSlash + 1);
        int indexOfExtension = pageName.indexOf('.');
        if (indexOfExtension != 1) {
            String extension = file.substring(file.lastIndexOf('.') + 1);

            if ("fxml".equals(extension)) {
                fxml = true;
                pageName = pageName.substring(0, indexOfExtension);
            }
        }

        return base;
    }

    public URI getBasePath() {
        return basePath;
    }

    public URI getLocation() {
        return location;
    }

    /**
     * Returns the string after the last slash (/) in the path, or "index" if a
     * slash is the last character.
     *
     * @return the pageName
     */
    public String getPageName() {
        return pageName;
    }

    public boolean isFxml() {
        return fxml;
    }

    public static boolean isFXML(String url) {
        return url.endsWith(".fxml") || url.startsWith("chrome://");
    }
    private static final Set<String> hidedHosts = new HashSet();

    static {
        hidedHosts.add("newtab");
        hidedHosts.add("error");
    }

    public static boolean isHided(URL url) {
        if (url.getProtocol().equals("chrome"))
            return hidedHosts.contains(url.getHost());
        return false;
    }
    private static final Set<String> sameUrlHosts = new HashSet();

    static {
        sameUrlHosts.add("error");
    }

    public static boolean isUrlKeeper(URL url) {
        if (url.getProtocol().equals("chrome"))
            return sameUrlHosts.contains(url.getHost());
        return false;
    }
}
