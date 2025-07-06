package io.github.amayaframework.di;

import com.github.romanqed.jtype.IllegalTypeException;

import java.lang.reflect.Type;

/**
 * Thrown to indicate that the dependency specialized by the specified type was not found.
 */
public class TypeNotFoundException extends IllegalTypeException {
    private final boolean scoped;

    /**
     * Constructs an {@link TypeNotFoundException} with the missing type.
     *
     * @param type the missing type
     * @param scoped whether the type was missing in scoped services
     */
    public TypeNotFoundException(Type type, boolean scoped) {
        super(getMessage(type, scoped), type);
        this.scoped = scoped;
    }

    public TypeNotFoundException(Type type) {
        this(type, false);
    }

    private static String getMessage(Type type, boolean scoped) {
        var ret = scoped ? "The scoped type " : "The type ";
        return ret + FormatUtil.getName(type) + " was not found";
    }

    /**
     * Returns whether the type was missing in the scoped dependency graph.
     *
     * @return {@code true} if the type was missing in scoped services, {@code false} otherwise
     */
    public boolean isScoped() {
        return scoped;
    }
}
