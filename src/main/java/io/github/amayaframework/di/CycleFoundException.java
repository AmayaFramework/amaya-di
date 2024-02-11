package io.github.amayaframework.di;

import java.util.Collections;
import java.util.List;

/**
 * Thrown to indicate that a cycle has been found in the dependency graph.
 */
public class CycleFoundException extends RuntimeException {
    private final List<Artifact> cycle;

    /**
     * Constructs an {@link CycleFoundException} with the found cycle.
     *
     * @param cycle the found cycle
     */
    public CycleFoundException(List<Artifact> cycle) {
        super("A cycle has been found in the dependency graph");
        this.cycle = Collections.unmodifiableList(cycle);
    }

    /**
     * Returns {@link List}, containing found cycle.
     *
     * @return unmodifiable {@link List}, containing found cycle
     */
    public List<Artifact> getCycle() {
        return cycle;
    }
}
