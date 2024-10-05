package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A {@link ServiceRepository} implementation using a hash map.
 */
public class HashRepository implements ServiceRepository {
    private final Map<Type, Function0<Object>> body;
    private final Set<Type> keys;

    /**
     * Constructs {@link HashRepository} instance with map instance, specified by the supplier.
     *
     * @param supplier the specified map supplier, must be non-null
     */
    public HashRepository(Supplier<Map<Type, Function0<Object>>> supplier) {
        this.body = Objects.requireNonNull(supplier.get());
        this.keys = Collections.unmodifiableSet(this.body.keySet());
    }

    /**
     * Constructs {@link HashRepository} thread-safe instance with {@link ConcurrentHashMap}.
     */
    public HashRepository() {
        this.body = new ConcurrentHashMap<>();
        this.keys = Collections.unmodifiableSet(this.body.keySet());
    }

    @Override
    public Function0<Object> get(Type type) {
        Objects.requireNonNull(type);
        return body.get(type);
    }

    @Override
    public boolean contains(Type type) {
        Objects.requireNonNull(type);
        return body.containsKey(type);
    }

    @Override
    public void add(Type type, Function0<Object> supplier) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(supplier);
        body.put(type, supplier);
    }

    @Override
    public boolean remove(Type type) {
        Objects.requireNonNull(type);
        return body.remove(type) != null;
    }

    @Override
    public void clear() {
        body.clear();
    }

    @Override
    public void forEach(Consumer<? super Type> action) {
        keys.forEach(action);
    }

    @Override
    public Spliterator<Type> spliterator() {
        return keys.spliterator();
    }

    @Override
    public void forEach(BiConsumer<Type, Function0<Object>> action) {
        body.forEach(action);
    }

    @Override
    public Iterator<Type> iterator() {
        return keys.iterator();
    }
}
