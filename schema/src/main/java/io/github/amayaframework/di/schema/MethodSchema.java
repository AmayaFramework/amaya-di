package io.github.amayaframework.di.schema;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;

/**
 * A schema that describes a method and the types required to invoke it.
 * <p>
 * For static methods, the first parameter must be assignable from the declaring class.
 */
public final class MethodSchema extends AbstractExecutableSchema<Method> {

    /**
     * Constructs method scheme for specified method, type set and its mapping.
     *
     * @param target  the specified method, must be non-null
     * @param types   the type set, must be non-null
     * @param mapping the type mapping, must be non-null
     */
    public MethodSchema(Method target, Set<Type> types, Type[] mapping) {
        super(target, types, mapping);
    }

    @Override
    public String toString() {
        return "MethodSchema{" +
                "types=" + types +
                ", mapping=" + Arrays.toString(mapping) +
                ", target=" + target +
                '}';
    }
}
