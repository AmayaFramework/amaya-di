package io.github.amayaframework.di.scheme;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public final class MethodScheme extends AbstractExecutableScheme<Method> {
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
