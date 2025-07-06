package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ObjectFactory;

/**
 * An interface that describes a mechanism for wrapping or proxying
 * {@link ObjectFactory} instances.
 * <p>
 * This is typically used to implement scopes, lazy instantiation,
 * monitoring, or other behavioral decorations of service providers.
 */
@FunctionalInterface
public interface ServiceWrapper {

    /**
     * Applies wrapping logic to the given service factory.
     *
     * @param factory the factory to wrap, must be non-null
     * @return the wrapped factory
     */
    ObjectFactory wrap(ObjectFactory factory);
}
