/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webfx.api;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import webfx.api.page.Adapter;

/**
 *
 * @author attila
 * @param <T> Object type
 */
public class ObjectWrapper<T> {
    private static int idGenerator = 42;
    private T val;
    private final Set<SecurityHolder> allowedRead = new HashSet<>();
    private final Set<SecurityHolder> allowedWrite = new HashSet<>();
    private final UUID uuid = new UUID(UUID.randomUUID().getMostSignificantBits(), idGenerator++);
    private ObjectWrapper() {
    }
    public static <T> ObjectWrapper<T> create(T object) {
        return new ObjectWrapper<T>().set(object);
    }
    public ObjectWrapper<T> set(T object) {
        val = object;
        return this;
    }
    public T get() {
        return val;
    }
    public T setValue(T object) {
        set(object);
        return object;
    }
    public T getValue() {
        return get();
    }
    public void free() {
        val = null;
    }

    public ObjectWrapper<T> allowRead(SecurityHolder sec) {
        if(allowedRead.contains(sec))
            throw new IllegalArgumentException("SecurityContext "+sec+" already allowed in "+this);
        allowedRead.add(sec);
        return this;
    }public ObjectWrapper<T> allowWrite(SecurityHolder sec) {
        if(allowedWrite.contains(sec))
            throw new IllegalArgumentException("SecurityContext "+sec+" already allowed in "+this);
        allowedWrite.add(sec);
        return this;
    }
    public UUID uuid() {
        return uuid;
    }

   public void checkRead(SecurityHolder sec) {
        if (!allowedRead.contains(sec))
            throw new SecurityException(sec + " cannot read " + this);
    }
   public void checkWrite(SecurityHolder securityHolder) {
        if (!allowedRead.contains(securityHolder))
            throw new SecurityException(securityHolder + " cannot write " + this);
    }

}
