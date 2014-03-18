/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

/**
 *
 * @author attila
 */
public class OS {

    private boolean l, w, m, s, u;

    public OS() {
        String OS = System.getProperty("os.name").toLowerCase();
        w = (OS.contains("win"));
        m = (OS.contains("mac"));
        u = (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
        l = OS.contains("inux");
        s = (OS.contains("sunos"));
    }

    public boolean isWindows() {
        return w;
    }

    public boolean isMac() {
        return m;
    }

    public boolean isLinux() {
        return l;
    }

    public boolean isUNIX() {
        return u;
    }

}
