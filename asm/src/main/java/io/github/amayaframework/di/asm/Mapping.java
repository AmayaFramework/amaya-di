package io.github.amayaframework.di.asm;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class Mapping {
    final Map<Type, String> types;
    final Map<String, Type> fields;
    final String[] order;

    private Mapping(Map<Type, String> types, Map<String, Type> fields, String[] order) {
        this.types = types;
        this.fields = fields;
        this.order = order;
    }

    static Mapping of(Set<Type> types) {
        var from = new HashMap<Type, String>();
        var to = new HashMap<String, Type>();
        var order = new String[types.size()];
        var count = 0;
        for (var type : types) {
            var field = Integer.toString(count);
            from.put(type, field);
            to.put(field, type);
            order[count] = field;
            ++count;
        }
        return new Mapping(from, to, order);
    }
}
