package io.github.amayaframework.di.transformers;

import io.github.amayaframework.di.containers.Container;
import io.github.amayaframework.di.containers.ProviderType;

import java.lang.instrument.UnmodifiableClassException;

/**
 * An interface describing an apparatus
 * that modifies the passed class in such a way as to implement dependency injection.
 */
public interface Transformer {
    /**
     * Transforms the passed class.
     *
     * @param clazz    the class to be analyzed and modified
     * @param provider supplier of singleton container for {@link Container}
     * @throws UnmodifiableClassException if the class cannot be changed
     */
    default void transform(Class<?> clazz, ProviderType provider) throws UnmodifiableClassException {
        transform(new Class<?>[]{clazz}, provider);
    }

    /**
     * Transforms the passed classes.
     *
     * @param classes  the classes to be analyzed and modified
     * @param provider supplier of singleton container for {@link Container}
     * @throws UnmodifiableClassException if one of the classes cannot be changed
     */
    void transform(Class<?>[] classes, ProviderType provider) throws UnmodifiableClassException;
}
