package io.github.amayaframework.di.scheme;

import io.github.amayaframework.di.Artifact;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Set;

/**
 * A scheme that defines the correspondence between set of artifacts and class constructor.
 */
public final class ConstructorScheme extends AbstractExecutableScheme<Constructor<?>> {

    /**
     * Constructs constructor scheme for specified constructor, artifact set and its mapping.
     *
     * @param target    the specified constructor, must be non-null
     * @param artifacts the artifact set, must be non-null
     * @param mapping   the artifact mapping, must be non-null
     */
    public ConstructorScheme(Constructor<?> target, Set<Artifact> artifacts, Artifact[] mapping) {
        super(target, artifacts, mapping);
    }

    @Override
    public String toString() {
        return "ConstructorScheme{" +
                "artifacts=" + artifacts +
                ", mapping=" + Arrays.toString(mapping) +
                ", target=" + target +
                '}';
    }
}
