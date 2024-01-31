package io.github.amayaframework.di.scheme;

import io.github.amayaframework.di.Artifact;

import java.lang.reflect.Executable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

abstract class AbstractExecutableScheme<T extends Executable>
        extends AbstractScheme<T>
        implements ExecutableScheme<T> {
    protected final Set<Artifact> artifacts;
    protected final Artifact[] mapping;

    protected AbstractExecutableScheme(T target, Set<Artifact> artifacts, Artifact[] mapping) {
        super(target);
        this.artifacts = Collections.unmodifiableSet(Objects.requireNonNull(artifacts));
        this.mapping = Objects.requireNonNull(mapping);
    }

    @Override
    public Set<Artifact> getArtifacts() {
        return artifacts;
    }

    @Override
    public Artifact[] getMapping() {
        return mapping.clone();
    }
}
