package io.github.amayaframework.di;

import io.github.amayaframework.di.scheme.IllegalTypeException;

import java.lang.reflect.Type;

/**
 * An interface describing an abstract artifact factory.
 */
public interface ArtifactFactory {

    /**
     * Creates an artifact of the specified type.
     *
     * @param type the specified type, must be non-null
     * @return {@link Artifact} instance
     * @throws IllegalTypeException if a type has been detected that cannot be used as an artifact (optional)
     */
    Artifact create(Type type);
}
