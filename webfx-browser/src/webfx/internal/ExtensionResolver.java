/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package webfx.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author attila
 */
public class ExtensionResolver {
     public static Map<String, Function<Object, ?>> EXTENSIONS = new HashMap<>();

    public static boolean has(String string) {
        return EXTENSIONS.containsKey(string);
    }
}
