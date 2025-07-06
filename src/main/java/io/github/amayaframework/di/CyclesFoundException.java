package io.github.amayaframework.di;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Thrown to indicate that cycles have been found in the dependency graph.
 */
public class CyclesFoundException extends RuntimeException {
    private final List<List<Type>> cycles;

    /**
     * Constructs an {@link CycleFoundException} with the found cycle.
     *
     * @param cycles the found cycle
     */
    public CyclesFoundException(List<List<Type>> cycles) {
        super("A cycles has been found in the dependency graph");
        this.cycles = cycles;
    }

    /**
     * Returns {@link List}, containing found cycles.
     *
     * @return {@link List}, containing found cycles
     */
    public List<List<Type>> getCycles() {
        return cycles;
    }
}
