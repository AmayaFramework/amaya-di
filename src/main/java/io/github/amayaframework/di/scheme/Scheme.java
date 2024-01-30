package io.github.amayaframework.di.scheme;

import java.util.Set;

public interface Scheme<T> {
    T getTarget();

    Set<Artifact> getArtifacts();
}
