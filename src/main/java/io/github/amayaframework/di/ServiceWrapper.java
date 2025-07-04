package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ObjectFactory;

/**
 * An interface that describes an abstract mechanism
 * that allows you to proxy or modify providers of service implementations.
 */
@FunctionalInterface
public interface ServiceWrapper {

    /**
     * Applies changes to the specified service factory.
     *
     * @param factory the specified service factory
     * @return modified service provider
     */
    ObjectFactory wrap(ObjectFactory factory);
}
