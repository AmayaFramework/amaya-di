package io.github.amayaframework.di.core;

import java.lang.reflect.Type;

/**
 * An interface for providing type-based object factories used in dependency injection.
 * <br>
 * Allows querying for available factories and checking whether instantiation is possible.
 */
@FunctionalInterface
public interface TypeProvider {

    /**
     * Gets the instantiator associated with the specified type.
     * <br>
     *
     * @param type the specified type, must be non-null
     * @return null or {@link ObjectFactory} instance
     */
    ObjectFactory get(Type type);

    /**
     * Checks whether the provider can provide an object factory for the specified type.
     *
     * @param type the specified type, must be non-null
     * @return true, if contains, false otherwise
     */
    default boolean canProvide(Type type) {
        return get(type) != null;
    }
}
