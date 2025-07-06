package io.github.amayaframework.di.stub;

import io.github.amayaframework.di.core.ObjectFactory;

import java.lang.reflect.Type;

/**
 * An extension of {@link ObjectFactory} that supports local caching
 * of per-type factories to avoid repeated lookup from a {@code TypeProvider}.
 * <p>
 * This interface is intended for scenarios where dependency injection
 * stubs resolve their sub-dependencies via {@code TypeProvider}, and lookup costs
 * should be minimized by storing resolved {@link ObjectFactory} instances.
 */
public interface CachedObjectFactory extends ObjectFactory {

    /**
     * Registers or replaces a factory for the specified type in the local cache.
     * <p>
     * Cached factories may be used to satisfy future resolution requests
     * without querying the external {@code TypeProvider}.
     *
     * @param type    the type to associate with the factory, must be non-null
     * @param factory the factory to cache, must be non-null
     */
    void set(Type type, ObjectFactory factory);
}
