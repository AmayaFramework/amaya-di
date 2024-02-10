package io.github.amayaframework.di.scheme;

import io.github.amayaframework.di.Artifact;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * A scheme that defines the correspondence between set of artifacts and class method.
 */
public final class MethodScheme extends AbstractExecutableScheme<Method> {

    /**
     * Constructs method scheme for specified method, artifact set and its mapping.
     *
     * @param target    the specified method, must be non-null
     * @param artifacts the artifact set, must be non-null
     * @param mapping   the artifact mapping, must be non-null
     */
    public MethodScheme(Method target, Set<Artifact> artifacts, Artifact[] mapping) {
        super(target, artifacts, mapping);
    }

    @Override
    public String toString() {
        return "MethodScheme{" +
                "artifacts=" + artifacts +
                ", mapping=" + Arrays.toString(mapping) +
                ", target=" + target +
                '}';
    }
}
