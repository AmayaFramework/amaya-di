package io.github.amayaframework.di.constructors;

import io.github.amayaframework.di.containers.Container;

/**
 * Internal interface for packaging some methods used inside the {@link ConstructorFactory}
 */
public interface ContainerAccessor {
    Container get();
}
