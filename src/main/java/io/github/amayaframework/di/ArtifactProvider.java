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
}
