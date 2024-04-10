package io.github.amayaframework.di.scheme;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;

/**
 * A scheme that defines the correspondence between set of artifacts and class constructor.
 */
public final class ConstructorScheme extends AbstractExecutableScheme<Constructor<?>> {

    /**
     * Constructs constructor scheme for specified constructor, artifact set and its mapping.
     *
     * @param target  the specified constructor, must be non-null
     * @param types   the artifact set, must be non-null
     * @param mapping the artifact mapping, must be non-null
     */
    public ConstructorScheme(Constructor<?> target, Set<Type> types, Type[] mapping) {
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
