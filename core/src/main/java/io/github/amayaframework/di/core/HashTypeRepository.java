package io.github.amayaframework.di.core;

import com.github.romanqed.jfunc.Function0;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A {@link TypeRepository} implementation backed by a {@link HashMap}.
 * <br>
 * Provides constant-time lookup for registered factories in the average case.
 * This implementation is not thread-safe; external synchronization is required
 * if accessed concurrently.
 * <p>
 * The repository stores a mapping from {@link Type} to {@link ObjectFactory}.
 * Convenience overloads allow registering constant instances or providers
 * without manually wrapping them into factories.
 */
public final class HashTypeRepository implements TypeRepository {
    private final Map<Type, ObjectFactory> body;
    private final Set<Type> keys;

    /**
     * Constructs a repository using a custom map supplier.
     * <br>
     * This allows using specialized or preconfigured map implementations.
     *
     * @param supplier a supplier that creates the internal {@link Map}, must be non-null
     */
    public HashTypeRepository(Supplier<Map<Type, ObjectFactory>> supplier) {
        this.body = supplier.get();
        this.keys = body.keySet();
    }

    /**
     * Constructs a repository using a custom map.
     * <br>
     * The provided map is used directly without copying.
     *
     * @param map a provided internal map, must be non-null
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

    /**
     * Returns the factory associated with the given type, or {@code null} if none is registered.
     *
     * @param type the type to resolve, must be non-null
     * @return the {@link ObjectFactory} for the type, or null if not found
     */
    @Override
    public ObjectFactory get(Type type) {
        return body.get(type);
    }

    /**
     * Checks if a factory is registered for the given type.
     *
     * @param type the type to check, must be non-null
     * @return true if a factory exists, false otherwise
     */
    @Override
    public boolean canProvide(Type type) {
        return body.get(type) != null;
    }

    /**
     * Associates the given factory with the specified type, replacing any previous one.
     *
     * @param type    the type key, must be non-null
     * @param factory the factory, must be non-null
     */
    @Override
    public void put(Type type, ObjectFactory factory) {
        body.put(type, factory);
    }

    /**
     * Associates a provider function with the specified type.
     * <br>
     * The provider is wrapped into an {@link ObjectFactory} internally.
     *
     * @param type     the type key, must be non-null
     * @param provider the provider function, must be non-null
     */
    @Override
    public void put(Type type, Function0<?> provider) {
        body.put(type, v -> provider.invoke());
    }

    /**
     * Associates a constant instance with the specified type.
     * <br>
     * The instance is wrapped into a factory that always returns it.
     *
     * @param type     the type key, must be non-null
     * @param instance the constant instance, must be non-null
     */
    @Override
    public void put(Type type, Object instance) {
        body.put(type, v -> instance);
    }

    /**
     * Associates a constant instance with its runtime class.
     * <br>
     * Equivalent to {@code put(instance.getClass(), instance)}.
     *
     * @param instance the constant instance, must be non-null
     */
    @Override
    public void put(Object instance) {
        body.put(instance.getClass(), v -> instance);
    }

    /**
     * Removes the factory associated with the given type.
     *
     * @param type the type key, must be non-null
     * @return the removed {@link ObjectFactory}, or null if none was registered
     */
    @Override
    public ObjectFactory remove(Type type) {
        return body.remove(type);
    }

    /**
     * Copies all entries from the given repository into this repository.
     * <br>
     * Existing entries with the same types are overwritten.
     *
     * @param repository the source repository, must be non-null
     */
    @Override
    public void putAll(TypeRepository repository) {
        if (repository == this) {
            return;
        }
        repository.forEach(body::put);
    }

    /**
     * Copies all entries from the given map into this repository.
     * <br>
     * Existing entries with the same types are overwritten.
     *
     * @param map a map of type-factory pairs, must be non-null
     */
    @Override
    public void putAll(Map<Type, ObjectFactory> map) {
        body.putAll(map);
    }

    /**
     * Removes all entries from this repository.
     */
    @Override
    public void clear() {
        body.clear();
    }

    /**
     * Applies the given action to each type-factory pair.
     *
     * @param action the action to perform, must be non-null
     */
    @Override
    public void forEach(BiConsumer<Type, ObjectFactory> action) {
        body.forEach(action);
    }

    /**
     * Returns an iterator over the factories in this repository.
     *
     * @return an {@link Iterator} of factories
     */
    @Override
    public Iterator<ObjectFactory> factoryIterator() {
        return body.values().iterator();
    }

    /**
     * Returns a spliterator over the factories in this repository.
     *
     * @return a {@link Spliterator} of factories
     */
    @Override
    public Spliterator<ObjectFactory> factorySpliterator() {
        return body.values().spliterator();
    }

    /**
     * Returns an iterator over the registered types.
     *
     * @return an {@link Iterator} of types
     */
    @Override
    public Iterator<Type> iterator() {
        return keys.iterator();
    }

    /**
     * Applies the given action to each registered type.
     *
     * @param action the action to perform, must be non-null
     */
    @Override
    public void forEach(Consumer<? super Type> action) {
        keys.forEach(action);
    }

    /**
     * Returns a spliterator over the registered types.
     *
     * @return a {@link Spliterator} of types
     */
    @Override
    public Spliterator<Type> spliterator() {
        return keys.spliterator();
    }
}
