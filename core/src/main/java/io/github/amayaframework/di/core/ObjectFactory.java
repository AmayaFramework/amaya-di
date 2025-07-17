package io.github.amayaframework.di.core;

/**
 * An abstract factory interface for creating object instances using the provided {@link TypeProvider}.
 */
@FunctionalInterface
public interface ObjectFactory {

    /**
     * Creates an instance of the required object using the given {@link TypeProvider}.
     *
     * @param provider the type provider used to resolve dependencies
     * @return the created object instance, or null if instantiation failed
     * @throws Throwable if any problems occurred
     */
    Object create(TypeProvider provider) throws Throwable;
}
