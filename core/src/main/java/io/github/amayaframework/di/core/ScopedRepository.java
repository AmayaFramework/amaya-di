package io.github.amayaframework.di.core;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * A specialized {@link TypeRepository} representing a scoped repository.
 * <br>
 * Provides access to both local (scope-specific) and inherited factories,
 * with additional methods to iterate only over the local scope.
 */
public interface ScopedRepository extends TypeRepository {

    /**
     * Gets the object factory for the specified type from the local scope only.
     * <br>
     * Does not attempt to resolve from the parent repository.
     *
     * @param type the type to resolve, must be non-null
     * @return the associated {@link ObjectFactory}, or {@code null} if not found locally
     */
    ObjectFactory getLocal(Type type);

    /**
     * Checks whether the specified type can be resolved from the local scope only.
     * <br>
     * Does not check the parent repository.
     *
     * @param type the type to check, must be non-null
     * @return {@code true} if a factory exists locally, {@code false} otherwise
     */
    boolean canProvideLocal(Type type);

    /**
     * Iterates over all type-factory pairs defined in the local scope only.
     *
     * @param action the action to perform on each local type-factory pair
     */
    void forEachLocal(BiConsumer<Type, ObjectFactory> action);

    /**
     * Iterates over all types defined in the local scope only.
     *
     * @param action the action to perform on each local type
     */
    void forEachLocal(Consumer<Type> action);

    /**
     * Returns an {@link Iterator} over all types defined in the local scope only.
     *
     * @return an iterator over local types
     */
    Iterator<Type> localIterator();

    /**
     * Returns a {@link Spliterator} over all types defined in the local scope only.
     *
     * @return a spliterator over local types
     */
    Spliterator<Type> localSpliterator();

    /**
     * Returns an {@link Iterator} over all factories defined in the local scope only.
     *
     * @return an iterator over local factories
     */
    Iterator<ObjectFactory> localFactoryIterator();

    /**
     * Returns a {@link Spliterator} over all factories defined in the local scope only.
     *
     * @return a spliterator over local factories
     */
    Spliterator<ObjectFactory> localFactorySpliterator();
}
