package io.github.amayaframework.di;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Thrown to indicate that a cycle has been found in the dependency graph during
 * the construction of a {@link io.github.amayaframework.di.core.ServiceProvider}.
 * <p>
 * A cycle occurs when a service directly or indirectly depends on itself, making it impossible
 * to resolve the dependency tree without infinite recursion. This exception may be thrown
 * during validation if cycle detection is enabled via builder configuration.
 * <p>
 * The {@code scoped} flag indicates whether the cycle originates from scoped services.
 * In that case, it means the cycle was detected within a scope-limited part of the graph.
 *
 * @see CyclesFoundException
 * @see BuilderChecks#VALIDATE_CYCLES
 */
public class CycleFoundException extends RuntimeException {
    private final List<Type> cycle;
    private final boolean scoped;

    /**
     * Constructs an {@link CycleFoundException} with the found cycle and scoped a flag.
     *
     * @param cycle  the found cycle
     * @param scoped the scoped flag
     */
    public CycleFoundException(List<Type> cycle, boolean scoped) {
        super(getMessage(cycle, scoped));
        this.cycle = cycle;
        this.scoped = scoped;
    }

    /**
     * Constructs an {@link CycleFoundException} with the found cycle.
     *
     * @param cycle the found cycle
     */
    public CycleFoundException(List<Type> cycle) {
        this(cycle, false);
    }

    private static String getMessage(List<Type> cycle, boolean scoped) {
        var ret = "A cycle has been found in the ";
        ret += scoped ? "scoped dependency graph: " : "dependency graph: ";
        return ret + FormatUtil.getNames(cycle);
    }

    /**
     * Returns {@link List}, containing found cycle.
     *
     * @return {@link List}, containing found cycle
     */
    public List<Type> getCycle() {
        return cycle;
    }

    /**
     * Returns whether the cycle was found in the scoped dependency graph.
     *
     * @return {@code true} if the cycle was found in scoped services, {@code false} otherwise
     */
    public boolean isScoped() {
        return scoped;
    }
}
