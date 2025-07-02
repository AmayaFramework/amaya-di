package io.github.amayaframework.di.stub;

import io.github.amayaframework.di.core.ObjectFactory;

import java.lang.reflect.Type;

/**
 * An extension of {@link ObjectFactory} that supports runtime caching
 * of factories associated with types.
 * <br>
 * Typically used in stub scenarios for resolving type bindings dynamically and
 * avoiding repeated lookup or instantiation.
 */
public interface CachedObjectFactory extends ObjectFactory {

    /**
     * Registers or replaces an object factory for the specified type.
     * <br>
     * Cached factories may be used during subsequent creation requests.
     *
     * @param type    the type to associate with the factory, must be non-null
     * @param factory the factory to cache, must be non-null
     */
    void set(Type type, ObjectFactory factory);
}
