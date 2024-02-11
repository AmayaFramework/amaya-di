package io.github.amayaframework.di.scheme;

import io.github.amayaframework.di.Artifact;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;

/**
 * A scheme that defines the correspondence between artifact and class field.
 */
public final class FieldScheme extends AbstractScheme<Field> {
    final Artifact artifact;

    /**
     * Constructs field scheme for specified field and artifact.
     *
     * @param target   the specified field, must be non-null
     * @param artifact the specified artifact, must be non-null
     */
    public FieldScheme(Field target, Artifact artifact) {
        super(target);
        this.artifact = Objects.requireNonNull(artifact);
    }

    /**
     * Returns the artifact associated with this field.
     *
     * @return the artifact associated with this field
     */
    public Artifact getArtifact() {
        return artifact;
    }

    @Override
    public Set<Artifact> getArtifacts() {
        return Set.of(artifact);
    }

    @Override
    public String toString() {
        return "FieldScheme{" +
                "artifact=" + artifact +
                ", target=" + target +
                '}';
    }
}
