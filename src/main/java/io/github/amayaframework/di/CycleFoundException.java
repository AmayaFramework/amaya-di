package io.github.amayaframework.di;

import java.util.List;

public final class CycleFoundException extends RuntimeException {
    private final List<Artifact> cycle;

    public CycleFoundException(List<Artifact> cycle) {
        super("A cycle has been found in the dependency graph");
        this.cycle = cycle;
    }

    public List<Artifact> getCycle() {
        return cycle;
    }
}
