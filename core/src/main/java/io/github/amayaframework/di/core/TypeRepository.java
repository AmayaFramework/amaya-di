package io.github.amayaframework.di.core;

import java.lang.reflect.Type;
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
     * @param type     the specified type, must be non-null
     * @param factory the specified instantiator, must be non-null
     */
    void set(Type type, ObjectFactory factory);

    /**
     * Removes the instantiator associated with the specified type.
     *
     * @param type the specified type, must be non-null
     * @return the removed {@link ObjectFactory} instance
     */
    ObjectFactory remove(Type type);

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
}
