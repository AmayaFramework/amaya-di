package io.github.amayaframework.di.schema;

import java.lang.reflect.Executable;
import java.lang.reflect.Type;

/**
 * An interface describing some abstract scheme that defines
 * the correspondence between types and an {@link Executable}
 * entity that depends on them.
 *
 * @param <T> type of executable implementation
 */
public interface ExecutableSchema<T extends Executable> extends Schema<T> {

    /**
     * Returns an array containing the types in the order in which
     * they should be passed when calling the {@link Executable} entity.
     * Changing the returned array will not change this scheme.
     *
     * @return non-null array, containing types
     */
    Type[] getMapping();
}
