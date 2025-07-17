package io.github.amayaframework.di.asm;

import java.lang.reflect.Type;
import java.util.*;

final class MapUtil {
    private MapUtil() {
    }

    static Map<Type, String> ofComplex(Set<Type> types) {
        var ret = new TreeMap<Type, String>(Comparator.comparing(Type::getTypeName));
        for (var type : types) {
            if (type.getClass() == Class.class) {
                continue;
            }
            ret.put(type, null);
        }
        var count = 0;
        for (var entry : ret.entrySet()) {
            entry.setValue(Integer.toString(count++));
        }
        return ret;
    }

    static Map<Type, String> ofAll(Set<Type> types, String prefix) {
        var ret = new HashMap<Type, String>();
        var count = 0;
        for (var type : types) {
            ret.put(type, prefix + count++);
        }
        return ret;
    }
}
