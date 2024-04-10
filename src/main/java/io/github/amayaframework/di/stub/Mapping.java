package io.github.amayaframework.di.stub;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class Mapping {
    final Map<Type, String> artifacts;
    final Map<String, Type> fields;
    final String[] order;

    private Mapping(Map<Type, String> artifacts, Map<String, Type> fields, String[] order) {
        this.artifacts = artifacts;
        this.fields = fields;
        this.order = order;
    }

    static Mapping of(Set<Type> types) {
        var from = new HashMap<Type, String>();
        var to = new HashMap<String, Type>();
        var order = new String[types.size()];
        var count = 0;
        for (var artifact : types) {
            var field = Integer.toString(count);
            from.put(artifact, field);
            to.put(field, artifact);
            order[count] = field;
            ++count;
        }
        return new Mapping(from, to, order);
    }
}
