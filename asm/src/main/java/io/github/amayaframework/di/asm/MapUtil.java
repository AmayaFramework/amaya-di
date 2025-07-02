package io.github.amayaframework.di.asm;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class MapUtil {
    private MapUtil() {
    }

    static Map<Type, String> ofComplex(Set<Type> types) {
        var ret = new HashMap<Type, String>();
        var count = 0;
        for (var type : types) {
            if (type.getClass() == Class.class) {
                continue;
            }
            var field = Integer.toString(count++);
            ret.put(type, field);
        }
        return ret;
    }

    static Map<Type, String> ofAll(Set<Type> types) {
        var ret = new HashMap<Type, String>();
        var count = 0;
        for (var type : types) {
            ret.put(type, Integer.toString(count++));
        }
        return ret;
    }
}
