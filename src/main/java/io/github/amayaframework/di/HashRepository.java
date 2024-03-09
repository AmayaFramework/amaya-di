package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * A {@link Repository} implementation using a hash map.
 */
public class HashRepository implements Repository {
    private final Map<Artifact, Function0<Object>> body;

    /**
     * Constructs {@link HashRepository} instance with map instance, specified by the supplier.
     *
     * @param supplier the specified map supplier, must be non-null
     */
    public HashRepository(Supplier<Map<Artifact, Function0<Object>>> supplier) {
        Objects.requireNonNull(supplier);
        this.body = Objects.requireNonNull(supplier.get());
    }

    /**
     * Constructs {@link HashRepository} thread-safe instance with {@link ConcurrentHashMap}.
     */
    public HashRepository() {
        this.body = new ConcurrentHashMap<>();
    }

    @Override
    public Function0<Object> get(Artifact artifact) {
        Objects.requireNonNull(artifact);
        return body.get(artifact);
    }

    @Override
    public Iterable<Artifact> getAll() {
        return Collections.unmodifiableCollection(body.keySet());
    }

    @Override
    public boolean contains(Artifact artifact) {
        Objects.requireNonNull(artifact);
        return body.containsKey(artifact);
    }

    @Override
    public void add(Artifact artifact, Function0<Object> supplier) {
        Objects.requireNonNull(artifact);
        Objects.requireNonNull(supplier);
        body.put(artifact, supplier);
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
