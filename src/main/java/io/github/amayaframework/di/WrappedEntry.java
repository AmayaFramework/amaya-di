package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ObjectFactory;

import java.lang.reflect.Type;

/**
 * Internal DTO that couples a target {@link Type}, its {@link ObjectFactory},
 * and a {@link ServiceWrapper} to be applied at scope creation time.
 * <p>
 * Used by scoped builders to defer wrapping until a new scope is entered:
 * the raw factory is stored here and {@link #wrap()} produces the
 * scope-local factory by applying the wrapper.
 * <p>
 * Not intended for public use.
 *
 * @see ServiceWrapper
 * @see AbstractScopedProviderBuilder#buildWrapped(SchemaProvider, io.github.amayaframework.di.stub.StubFactory, java.util.List, io.github.amayaframework.di.stub.CacheMode)
 */
public final class WrappedEntry {

    /**
     * The service {@link Type} this entry belongs to (used as the registration key).
     */
    public final Type type;

    /**
     * The object factory producing service instances.
     */
    public final ObjectFactory factory;

    /**
     * The wrapper to be applied to the factory when creating a new scope.
     */
    public final ServiceWrapper wrapper;

    /**
     * Creates a new wrapped entry.
     *
     * @param type    the service {@link Type}, must be non-null
     * @param factory the underlying {@link ObjectFactory}, must be non-null
     * @param wrapper the {@link ServiceWrapper} to apply per-scope, must be non-null
     */
    public WrappedEntry(Type type, ObjectFactory factory, ServiceWrapper wrapper) {
        this.type = type;
        this.factory = factory;
        this.wrapper = wrapper;
    }

    /**
     * Applies the {@link #wrapper} to the underlying {@link #factory} and
     * returns the resulting scope-local {@link ObjectFactory}.
     *
     * @return a wrapped factory to be used inside a scope
     */
    public ObjectFactory wrap() {
        return wrapper.wrap(factory);
    }
}
