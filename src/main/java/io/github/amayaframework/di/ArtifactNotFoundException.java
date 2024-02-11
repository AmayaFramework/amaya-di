package io.github.amayaframework.di;

/**
 * Thrown to indicate that the dependency specialized by the specified artifact was not found.
 */
public class ArtifactNotFoundException extends RuntimeException {
    private final Artifact artifact;

    /**
     * Constructs an {@link ArtifactNotFoundException} with the missing artifact.
     *
     * @param artifact the missing artifact
     */
    public ArtifactNotFoundException(Artifact artifact) {
        super("The artifact was not found");
        this.artifact = artifact;
    }

    /**
     * Returns missing artifact.
     *
     * @return missing artifact
     */
    public Artifact getArtifact() {
        return artifact;
    }
}
