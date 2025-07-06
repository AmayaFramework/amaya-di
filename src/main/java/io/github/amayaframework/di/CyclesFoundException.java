package io.github.amayaframework.di;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Thrown to indicate that cycles have been found in the dependency graph.
 */
public class CyclesFoundException extends RuntimeException {
    private final List<List<Type>> cycles;
    private final boolean scoped;

    /**
     * Constructs an {@link CycleFoundException} with the found cycle.
     * TODO
     *
     * @param cycles the found cycle
     */
    public CyclesFoundException(List<List<Type>> cycles, boolean scoped) {
        super(getMessage(scoped));
        this.cycles = cycles;
        this.scoped = scoped;
    }

    /**
     * Constructs an {@link CycleFoundException} with the found cycle.
     *
     * @param cycles the found cycle
     */
    public CyclesFoundException(List<List<Type>> cycles) {
        this(cycles, false);
    }

    private static String getMessage(boolean scoped) {
        var ret = "A cycles has been found in the ";
        return ret + (scoped ? "scoped dependency graph: " : "dependency graph: ");
    }

    /**
     * Returns {@link List}, containing found cycles.
     *
     * @return {@link List}, containing found cycles
     */
    public List<List<Type>> getCycles() {
        return cycles;
    }

    /**
     * TODO
     *
     * @return
     */
    public boolean isScoped() {
        return scoped;
    }
}
