package io.github.amayaframework.di.schema;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;

/**
 * A scheme that defines the correspondence between set of types and class constructor.
 */
public final class ConstructorSchema extends AbstractExecutableSchema<Constructor<?>> {

    /**
     * Constructs constructor scheme for specified constructor, type set and its mapping.
     *
     * @param target  the specified constructor, must be non-null
     * @param types   the type set, must be non-null
     * @param mapping the type mapping, must be non-null
     */
    public ConstructorSchema(Constructor<?> target, Set<Type> types, Type[] mapping) {
        super(target, types, mapping);
    }

    @Override
    public String toString() {
        return "ConstructorScheme{" +
                "types=" + types +
                ", mapping=" + Arrays.toString(mapping) +
                ", target=" + target +
                '}';
    }
}
