package io.github.amayaframework.di.scheme;

import io.github.amayaframework.di.Artifact;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Set;

public final class ConstructorScheme extends AbstractExecutableScheme<Constructor<?>> {
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
