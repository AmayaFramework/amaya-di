package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class HashRepository implements Repository {
    private final Map<Artifact, Function0<Object>> body;

    public HashRepository(Supplier<Map<Artifact, Function0<Object>>> supplier) {
        Objects.requireNonNull(supplier);
        this.body = Objects.requireNonNull(supplier.get());
    }

    public HashRepository() {
        this.body = new ConcurrentHashMap<>();
    }

    @Override
    public Function0<Object> get(Artifact artifact) {
        Objects.requireNonNull(artifact);
        return body.get(artifact);
    }

    @Override
    public void add(Artifact artifact, Function0<Object> supplier) {
        Objects.requireNonNull(artifact);
        Objects.requireNonNull(supplier);
        if (body.containsKey(artifact)) {
            return;
        }
        body.put(artifact, supplier);
    }

    @Override
    public void remove(Artifact artifact) {
        Objects.requireNonNull(artifact);
        body.remove(artifact);
    }

    @Override
    public void clear() {
        body.clear();
    }
}
