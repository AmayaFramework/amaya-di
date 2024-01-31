package io.github.amayaframework.di.scheme;

import io.github.amayaframework.di.Artifact;

import java.lang.reflect.Field;
import java.util.Set;

public final class FieldScheme extends AbstractScheme<Field> {
    final Artifact artifact;

    public FieldScheme(Field target, Artifact artifact) {
        super(target);
        this.artifact = artifact;
    }

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
