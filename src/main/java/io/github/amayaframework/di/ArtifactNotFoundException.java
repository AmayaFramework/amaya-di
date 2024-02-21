package io.github.amayaframework.di;

/**
 * Thrown to indicate that the dependency specialized by the specified artifact was not found.
 */
public class ArtifactNotFoundException extends ArtifactException {

    /**
     * Constructs an {@link ArtifactNotFoundException} with the missing artifact.
     *
     * @param artifact the missing artifact
     */
    public ArtifactNotFoundException(Artifact artifact) {
        super("The artifact was not found", artifact);
    }
}
