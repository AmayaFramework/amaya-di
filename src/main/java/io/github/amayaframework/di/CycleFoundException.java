package io.github.amayaframework.di;

import java.util.Set;

public final class CycleFoundException extends RuntimeException {
    private final Set<Artifact> cycle;

    public CycleFoundException(Set<Artifact> cycle) {
        super("A cycle has been found in the dependency graph");
        this.cycle = cycle;
    }

    public Set<Artifact> getCycle() {
        return cycle;
    }
}
