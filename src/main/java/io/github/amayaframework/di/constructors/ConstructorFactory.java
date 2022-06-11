package io.github.amayaframework.di.constructors;

import java.util.concurrent.Callable;

public interface ConstructorFactory {
    <E> Callable<E> getConstructor(Class<E> clazz) throws Throwable;
}
