package io.github.amayaframework.di.scheme;

import io.github.amayaframework.di.Artifact;

import java.util.Set;

public interface Scheme<T> {
    T getTarget();

    Set<Artifact> getArtifacts();
}
