package io.github.amayaframework.di;

public final class ArtifactNotFoundException extends RuntimeException {
    private final Artifact artifact;

    public ArtifactNotFoundException(Artifact artifact) {
        super("The artifact was not found");
        this.artifact = artifact;
    }

    public Artifact getArtifact() {
        return artifact;
    }
}
