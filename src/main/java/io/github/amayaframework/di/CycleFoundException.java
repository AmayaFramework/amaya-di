package io.github.amayaframework.di;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Thrown to indicate that a cycle has been found in the dependency graph.
 */
public class CycleFoundException extends RuntimeException {
    private final List<Type> cycle;
    private final boolean scoped;

    /**
     * Constructs an {@link CycleFoundException} with the found cycle.
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
     * TODO
     *
     * @return
     */
    public boolean isScoped() {
        return scoped;
    }
}
