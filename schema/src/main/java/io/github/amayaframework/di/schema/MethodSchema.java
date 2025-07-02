package io.github.amayaframework.di.schema;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;

/**
 * A scheme that defines the correspondence between set of types and class method.
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
        return "MethodScheme{" +
                "types=" + types +
                ", mapping=" + Arrays.toString(mapping) +
                ", target=" + target +
                '}';
    }
}
