package io.github.amayaframework.di.constructors;

import io.github.amayaframework.di.containers.Container;
import io.github.amayaframework.di.containers.ProviderType;

import java.util.concurrent.Callable;

/**
 * <p>An interface describing a factory that creates a "constructor" lambda</p>
 * <p>that creates an object with injected dependencies.</p>
 */
public interface ConstructorFactory {
    /**
     * Creates a lambda that creates an object of the passed class.
     *
     * @param clazz    required object type
     * @param provider supplier of singleton container for {@link Container}
     * @param <E>      object type
     * @return the resulting lambda
     * @throws Throwable if there are any problems during the lambda construction process
     */
    <E> Callable<E> getConstructor(Class<E> clazz, ProviderType provider) throws Throwable;
}
