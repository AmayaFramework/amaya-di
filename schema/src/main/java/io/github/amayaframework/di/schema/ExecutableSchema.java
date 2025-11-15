package io.github.amayaframework.di.schema;

import java.lang.reflect.Executable;
import java.lang.reflect.Type;

/**
 * A schema describing an {@link Executable} entity (e.g., constructor or method)
 * and the types required to invoke it.
 *
 * @param <T> type of the executable (constructor or method)
 */
public interface ExecutableSchema<T extends Executable> extends Schema<T> {

    /**
     * Returns the mapping array with dependency types in the order they
     * correspond to the parameters of the executable (constructor or method).
     * <p>
     * The returned array reflects invocation order and is independent
     * of internal representation: modifications to the array do not affect the schema.
     *
     * @return non-null array of {@link Type}, matching executable parameters order
     */
    Type[] mapping();
}
