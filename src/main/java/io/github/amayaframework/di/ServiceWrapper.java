package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function1;
import io.github.amayaframework.di.core.ObjectFactory;

/**
 * An interface that describes an abstract mechanism
 * that allows you to proxy or modify providers of service implementations.
 */
public interface ServiceWrapper extends Function1<ObjectFactory, ObjectFactory> {

    /**
     * Applies changes to the specified service factory.
     *
     * @param factory the specified service factory
     * @return modified service provider
     * @throws Throwable if any errors occur
     */
    @Override
    ObjectFactory invoke(ObjectFactory factory) throws Throwable;
}
