package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

import java.util.function.Function;

/**
 * An interface describing the read-only version of the repository - the artifact provider.
 * It is used inside the builder implementation when resolving artifacts.
 */
public interface ArtifactProvider extends Function<Artifact, Function0<Object>> {

    /**
     * Gets the instantiator associated with the specified artifact.
     *
     * @param artifact the specified artifact
     * @return null or {@link Function0} instance
     */
    Function0<Object> apply(Artifact artifact);

    /**
     * Gets the instantiator associated with the specified type.
     *
     * @param type the specified type
     * @param <T>  service type
     * @return null or {@link Function0} instance
     */
    @SuppressWarnings("unchecked")
    default <T> Function0<T> apply(Class<T> type) {
        return (Function0<T>) apply(new Artifact(type));
    }
}
