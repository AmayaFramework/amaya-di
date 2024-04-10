package io.github.amayaframework.di.scheme;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;

/**
 * A scheme that defines the correspondence between set of artifacts and class method.
 */
public final class MethodScheme extends AbstractExecutableScheme<Method> {

    /**
     * Constructs method scheme for specified method, artifact set and its mapping.
     *
     * @param target  the specified method, must be non-null
     * @param types   the artifact set, must be non-null
     * @param mapping the artifact mapping, must be non-null
     */
    public MethodScheme(Method target, Set<Type> types, Type[] mapping) {
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
