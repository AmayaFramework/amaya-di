package io.github.amayaframework.di.constructors;

import io.github.amayaframework.di.containers.ProviderType;

import java.util.concurrent.Callable;

public interface ConstructorFactory {
    <E> Callable<E> getConstructor(Class<E> clazz, ProviderType provider) throws Throwable;
}
