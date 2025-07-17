package io.github.amayaframework.di;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Thrown to indicate that multiple cycles have been found in the dependency graph.
 * <p>
 * This exception can occur when analyzing service dependencies during container validation,
 * particularly when multiple independent cycles are present. The {@code scoped} flag indicates
 * whether these cycles originate from scoped services.
 *
 * @see CycleFoundException
 */
public class CyclesFoundException extends RuntimeException {
    private final List<List<Type>> cycles;
    private final boolean scoped;

    /**
     * Constructs a {@link CyclesFoundException} with the given list of cycles and the scoped flag.
     *
     * @param cycles a list of detected cycles, where each cycle is a list of types forming a circular dependency
     * @param scoped whether the cycles were detected in scoped services
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
     * Returns whether the cycles were found in the scoped dependency graph.
     *
     * @return {@code true} if the cycles were found in scoped services, {@code false} otherwise
     */
    public boolean isScoped() {
        return scoped;
    }
}
