package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class RepositoryImpl implements Repository {
    private final Map<Artifact, Function0<Object>> body;

    RepositoryImpl() {
        this.body = new HashMap<>();
    }

    @Override
    public Function0<Object> get(Artifact artifact) {
        return body.get(artifact);
    }

    @Override
    public Iterable<Artifact> getAll() {
        return Collections.unmodifiableCollection(body.keySet());
    }

    @Override
    public boolean contains(Artifact artifact) {
        return body.containsKey(artifact);
    }

    @Override
    public void add(Artifact artifact, Function0<Object> supplier) {
        body.put(artifact, supplier);
    }

    @Override
    public boolean remove(Artifact artifact) {
        return body.remove(artifact) != null;
    }

    @Override
    public void clear() {
        body.clear();
    }
}
