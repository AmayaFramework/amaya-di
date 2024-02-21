package io.github.amayaframework.di;

/**
 * Thrown to indicate that the dependency specialized by the specified artifact has some problems.
 */
public class ArtifactException extends RuntimeException {
    private final Artifact artifact;

    /**
     * Constructs an {@link ArtifactException} with the specified detail message, artifact and cause.
     *
     * @param message  the specified detail message
     * @param artifact the specified artifact
     * @param cause    the specified cause
     */
    public ArtifactException(String message, Artifact artifact, Throwable cause) {
        super(message, cause);
        this.artifact = artifact;
    }

    /**
     * Constructs an {@link ArtifactException} with the specified detail message and artifact.
     *
     * @param message  the specified detail message
     * @param artifact the specified artifact
     */
    public ArtifactException(String message, Artifact artifact) {
        super(message);
        this.artifact = artifact;
    }

    /**
     * Returns an artifact that has problems.
     *
     * @return the {@link Artifact} instance
     */
    public Artifact getArtifact() {
        return artifact;
    }
}
