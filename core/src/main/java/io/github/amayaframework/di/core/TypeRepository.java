package io.github.amayaframework.di.core;

import com.github.romanqed.jfunc.Function0;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.BiConsumer;

/**
 * A mutable repository of {@link ObjectFactory} instances, associated with specific {@link Type}s.
 * <br>
 * Provides methods to register, remove, and query factories for dependency injection.
 * <br>
 * Extends {@link TypeProvider} for read-only access and {@link Iterable} to allow iteration over types.
 */
public interface TypeRepository extends TypeProvider, Iterable<Type> {

    /**
     * Adds an instantiator associated with the specified type, overwriting the previous one.
     *
     * @param type    the specified type, must be non-null
     * @param factory the specified instantiator, must be non-null
     */
    void put(Type type, ObjectFactory factory);

    /**
     * Adds a provider function associated with the specified type, overwriting any existing factory.
     * <br>
     * The function is wrapped into an {@link ObjectFactory} internally.
     *
     * @param type     the type to associate with the provider, must be non-null
     * @param provider the provider function that returns an instance of the specified type, must be non-null
     */
    void put(Type type, Function0<?> provider);

    /**
     * Adds a constant instance associated with the specified type, overwriting any existing factory.
     * <br>
     * The instance is wrapped into an {@link ObjectFactory} that always returns it.
     *
     * @param type     the type to associate with the instance, must be non-null
     * @param instance the instance to associate, must be non-null
     */
    void put(Type type, Object instance);

    /**
     * Adds a constant {@link Closeable} instance associated with the specified type,
     * overwriting any existing factory.
     * <br>
     * The instance is wrapped into a {@link CloseableObjectFactory} that always returns it
     * and invokes {@link Closeable#close()} when the owning provider/scope is closed.
     * <br>
     * Useful for registering resources that should participate in DI-managed teardown
     * without relying on {@link AutoCloseable}.
     *
     * @param type     the type to associate with the instance, must be non-null
     * @param instance the closeable instance to associate, must be non-null
     */
    default void putCloseable(Type type, Closeable instance) {
        put(type, new CloseableObjectFactory() {
            @Override
            public Object create(TypeProvider provider) {
                return instance;
            }

            @Override
            public void close() {
                instance.close();
            }
        });
    }

    /**
     * Adds a constant instance associated with its runtime class.
     * <br>
     * The instance is wrapped into an {@link ObjectFactory} that always returns it.
     * <br>
     * Useful for quickly registering singletons without explicitly specifying the type.
     *
     * @param instance the instance to associate, must be non-null
     */
    void put(Object instance);

    /**
     * Adds a constant {@link Closeable} instance associated with its runtime class,
     * overwriting any existing factory.
     * <br>
     * The instance is wrapped into a {@link CloseableObjectFactory} that always returns it
     * and invokes {@link Closeable#close()} when the owning provider/scope is closed.
     * <br>
     * Useful for quickly registering singleton-like closeable resources without explicitly specifying the type.
     *
     * @param instance the closeable instance to associate, must be non-null
     */
    default void putCloseable(Closeable instance) {
        put(new CloseableObjectFactory() {
            @Override
            public Object create(TypeProvider provider) {
                return instance;
            }

            @Override
            public void close() {
                instance.close();
            }
        });
    }

    /**
     * Removes the instantiator associated with the specified type.
     *
     * @param type the specified type, must be non-null
     * @return the removed {@link ObjectFactory} instance
     */
    ObjectFactory remove(Type type);

    /**
     * Copies all entries from the given {@link TypeRepository} into this repository.
     * <br>
     * Existing entries with the same types will be overwritten.
     *
     * @param repository the repository to copy from, must be non-null
     */
    void putAll(TypeRepository repository);

    /**
     * Copies all entries from the given map into this repository.
     * <br>
     * Existing entries with the same types will be overwritten.
     *
     * @param map a map containing type-factory pairs to copy, must be non-null
     */
    void putAll(Map<Type, ObjectFactory> map);

    /**
     * Clears this repository.
     */
    void clear();

    /**
     * Iterates over repository entries.
     *
     * @param action the action to be performed for each element
     */
    void forEach(BiConsumer<Type, ObjectFactory> action);

    /**
     * Returns an {@link Iterator} over all object factories contained in this repository.
     * <br>
     * The iteration order is implementation-dependent.
     *
     * @return an iterator over the factories
     */
    Iterator<ObjectFactory> factoryIterator();

    /**
     * Returns a {@link Spliterator} over all object factories contained in this repository.
     * <br>
     * The spliterator characteristics are implementation-dependent.
     *
     * @return a spliterator over the factories
     */
    Spliterator<ObjectFactory> factorySpliterator();
}
