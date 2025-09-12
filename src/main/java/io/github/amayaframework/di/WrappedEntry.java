package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ObjectFactory;

import java.lang.reflect.Type;

/**
 * Represents a pair of an object factory and a service wrapper to be applied within a scoped container.
 * <p>
 * {@code WrappedEntry} is used to register services that must be wrapped during scope creation.
 * The {@link ServiceWrapper} is applied to the {@link ObjectFactory} when entering a new scope,
 * producing a scope-local provider instance.
 * <p>
 * This class is internal to the builder implementation and is not intended for public use.
 *
 * @see ServiceWrapper
 * @see AbstractScopedProviderBuilder#buildWrapped(SchemaProvider, io.github.amayaframework.di.stub.StubFactory, java.util.List, io.github.amayaframework.di.stub.CacheMode)
 */
// TODO Update class javadoc
public final class WrappedEntry {

    /**
     * TODO
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
     * TODO
     * @param type
     * @param factory
     * @param wrapper
     */
    public WrappedEntry(Type type, ObjectFactory factory, ServiceWrapper wrapper) {
        this.type = type;
        this.factory = factory;
        this.wrapper = wrapper;
    }

    /**
     * TODO
     *
     * @return
     */
    public ObjectFactory wrap() {
        return wrapper.wrap(factory);
    }
}
