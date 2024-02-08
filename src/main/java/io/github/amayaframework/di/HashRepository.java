package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class HashRepository implements Repository {
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
    public boolean contains(Artifact artifact) {
        Objects.requireNonNull(artifact);
        return body.containsKey(artifact);
    }

    @Override
    public boolean add(Artifact artifact, Function0<Object> supplier) {
        Objects.requireNonNull(artifact);
        Objects.requireNonNull(supplier);
        if (body.containsKey(artifact)) {
            return false;
        }
        body.put(artifact, supplier);
        return true;
    }

    @Override
    public boolean remove(Artifact artifact) {
        Objects.requireNonNull(artifact);
        return body.remove(artifact) != null;
    }

    @Override
    public void clear() {
        body.clear();
    }
}
