/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx;

/**
 * Details about the platform that currently run this WebFX instance.
 * @author attila
 */
public class OS {

    private final boolean l, w, m, s, u;
    OS() {
        String OS = System.getProperty("os.name").toLowerCase();
        w = (OS.contains("win"));
        m = (OS.contains("mac"));
        u = (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
        l = OS.contains("inux");
        s = (OS.contains("sunos"));
    }

    OS(boolean l, boolean w, boolean m, boolean s, boolean u) {
        this.l = l;
        this.w = w;
        this.m = m;
        this.s = s;
        this.u = u;
    }
    /**
     * Returns true if this OS is Windows, otherwise false.
     *
     * @return this is Windows
     */
    public boolean isWindows() {
        return w;
    }

    /**
     * Returns true if this OS is Mac OS X, otherwise false.
     *
     * @return this is Mac OS X
     */
    public boolean isMac() {
        return m;
    }

    /**
     * Returns true if this OS is Windows, otherwise false.
     *
     * @return this OS is Linux
     */
    public boolean isLinux() {
        return l;
    }

    /**
     * Returns true if this OS is a Unix-like operating system (including
     * Linux), otherwise false.
     *
     * @return this is a Unix-line OS
     */
    public boolean isUNIX() {
        return u;
    }
/**
 * Returns a hash code of this operating system.
 * The hash is computed as the following: <ul>
 * <li>if Linux, result's bit 0 is set</li>
 * <li>if Unix-like, result's bit 1 is set</li>
 * <li>if Solaris, result's bit 2 is set</li>
 * <li>if Mac, result's bit 3 is set</li>
 * <li>if M$, result's bit 4 is set</li>
 * </ul>
 * @return this OS's hash code
 */
    @Override
    public int hashCode() {
        int hash = 0;
        if(l)
            hash |= 1;
        if(u)
            hash |= 2;
        if(s)
            hash |= 4;
        if(m)
            hash |= 8;
        if(w)
            hash |= 16;    
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return ((OS)obj).hashCode() == hashCode();
    }

}
