package io.github.amayaframework.di.schema;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * An interface describing some abstract scheme that defines
 * the correspondence between types and a dependent entity.
 *
 * @param <T> dependent entity type
 */
public interface Schema<T> {

    /**
     * Returns an object describing the dependent entity.
     *
     * @return non-null dependent entity
     */
    T getTarget();

    /**
     * Returns the set of types required by the dependent entity.
     *
     * @return non-null set of types
     */
    Set<Type> getTypes();
}
