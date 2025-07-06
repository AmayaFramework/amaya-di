package io.github.amayaframework.di;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Thrown to indicate that a cycle has been found in the dependency graph.
 */
public class CycleFoundException extends RuntimeException {
    private final List<Type> cycle;

    /**
     * Constructs an {@link CycleFoundException} with the found cycle.
     *
     * @param cycle the found cycle
     */
    public CycleFoundException(List<Type> cycle) {
        super("A cycle has been found in the dependency graph: " + FormatUtil.getNames(cycle));
        this.cycle = Collections.unmodifiableList(cycle);
    }

    /**
     * Returns {@link List}, containing found cycle.
     *
     * @return unmodifiable {@link List}, containing found cycle
     */
    public List<Type> getCycle() {
        return cycle;
    }
}
