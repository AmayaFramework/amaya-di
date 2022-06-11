package io.github.amayaframework.di;

import java.util.concurrent.Callable;

/**
 * An interface that describes a universal class that allows to inject dependencies.
 */
public interface DI {

    /**
     * Creates a lambda that returns a class object with injected values.
     *
     * @param clazz class for instantiation
     * @param <E>   type of the class and the object being received
     * @return the resulting lambda
     */
    <E> Callable<E> prepare(Class<E> clazz);

    /**
     * Modifies classes in such a way as to inject values.
     *
     * @param clazz class to modify
     */
    void transform(Class<?> clazz);

    /**
     * Finds all classes to change and edits them.
     *
     * @return array containing modified classes
     */
    Class<?>[] transform();
}
