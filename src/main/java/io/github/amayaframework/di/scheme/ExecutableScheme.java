package io.github.amayaframework.di.scheme;

import io.github.amayaframework.di.Artifact;

import java.lang.reflect.Executable;

/**
 * An interface describing some abstract schema that defines
 * the correspondence between artifacts and an {@link Executable}
 * entity that depends on them.
 *
 * @param <T> type of executable implementation
 */
public interface ExecutableScheme<T extends Executable> extends Scheme<T> {

    /**
     * Returns an array containing the artifacts in the order in which
     * they should be passed when calling the {@link Executable} entity.
     * Changing the returned array will not change this scheme.
     *
     * @return non-null array, containing artifacts
     */
    Artifact[] getMapping();
}
