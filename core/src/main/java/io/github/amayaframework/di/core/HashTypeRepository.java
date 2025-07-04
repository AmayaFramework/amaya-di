package io.github.amayaframework.di.core;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A {@link TypeRepository} implementation using a hash map.
 */
public final class HashTypeRepository implements TypeRepository {
    private final Map<Type, ObjectFactory> body;
    private final Set<Type> keys;

    /**
     * Constructs a repository using a custom map supplier.
     * <br>
     * This allows using specialized or preconfigured map implementations.
     *
     * @param supplier a supplier that creates the internal {@link Map}
     */
    public HashTypeRepository(Supplier<Map<Type, ObjectFactory>> supplier) {
        this.body = supplier.get();
        this.keys = body.keySet();
    }

    /**
     * Constructs a repository using a custom map.
     * <br>
     * This allows using specialized or preconfigured map implementations.
     *
     * @param map a provided internal map
     */
    public HashTypeRepository(Map<Type, ObjectFactory> map) {
        this.body = map;
        this.keys = map.keySet();
    }

    /**
     * Constructs an empty repository backed by a {@link HashMap}.
     */
    public HashTypeRepository() {
        this.body = new HashMap<>();
        this.keys = body.keySet();
    }

    @Override
    public ObjectFactory get(Type type) {
        return body.get(type);
    }

    @Override
    public boolean canProvide(Type type) {
        return body.get(type) != null;
    }

    @Override
    public void put(Type type, ObjectFactory factory) {
        Objects.requireNonNull(type);
        body.put(type, factory);
    }

    @Override
    public ObjectFactory remove(Type type) {
        return body.remove(type);
    }

    @Override
    public void clear() {
        body.clear();
    }

    @Override
    public void forEach(BiConsumer<Type, ObjectFactory> action) {
        body.forEach(action);
    }

    @Override
    public Iterator<Type> iterator() {
        return keys.iterator();
    }

    @Override
    public void forEach(Consumer<? super Type> action) {
        keys.forEach(action);
    }

    @Override
    public Spliterator<Type> spliterator() {
        return keys.spliterator();
    }
}
