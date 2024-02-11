package io.github.amayaframework.di.stub;

import io.github.amayaframework.di.Artifact;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class Mapping {
    final Map<Artifact, String> artifacts;
    final Map<String, Artifact> fields;
    final String[] order;

    private Mapping(Map<Artifact, String> artifacts, Map<String, Artifact> fields, String[] order) {
        this.artifacts = artifacts;
        this.fields = fields;
        this.order = order;
    }

    static Mapping of(Set<Artifact> artifacts) {
        var from = new HashMap<Artifact, String>();
        var to = new HashMap<String, Artifact>();
        var order = new String[artifacts.size()];
        var count = 0;
        for (var artifact : artifacts) {
            var field = Integer.toString(count);
            from.put(artifact, field);
            to.put(field, artifact);
            order[count] = field;
            ++count;
        }
        return new Mapping(from, to, order);
    }
}
