package io.github.amayaframework.di.scheme;

import io.github.amayaframework.di.Artifact;

import java.util.Set;

/**
 * An interface describing some abstract schema that defines
 * the correspondence between artifacts and a dependent entity.
 *
 * @param <T> dependent entity type
 */
public interface Scheme<T> {

    /**
     * Returns an object describing the dependent entity.
     *
     * @return non-null dependent entity
     */
    T getTarget();

    /**
     * Returns the set of artifacts required by the dependent entity.
     *
     * @return non-null set of artifacts
     */
    Set<Artifact> getArtifacts();
}
