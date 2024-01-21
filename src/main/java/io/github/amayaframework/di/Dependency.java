package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

import java.util.Objects;

public final class Dependency {
    private final Repository repository;
    private final Artifact artifact;

    public Dependency(Repository repository, Artifact artifact) {
        this.repository = Objects.requireNonNull(repository);
        this.artifact = Objects.requireNonNull(artifact);
    }

    public Repository getRepository() {
        return repository;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public Function0<Object> solve() {
        return repository.get(artifact);
    }
}
