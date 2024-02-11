package io.github.amayaframework.di.scheme;

import java.lang.reflect.Type;

/**
 * Thrown to indicate that the specified type cannot be used to build an artifact.
 */
public class IllegalTypeException extends RuntimeException {
    private final Type type;

    /**
     * Constructs an {@link IllegalTypeException} with the specified detail message and illegal type.
     *
     * @param message the detail message
     * @param type    the illegal type
     */
    public IllegalTypeException(String message, Type type) {
        super(message);
        this.type = type;
    }

    /**
     * Constructs an {@link IllegalTypeException} with the specified illegal type.
     *
     * @param type the illegal type
     */
    public IllegalTypeException(Type type) {
        super("Illegal type");
        this.type = type;
    }

    /**
     * Returns the illegal type.
     *
     * @return the illegal type
     */
    public Type getType() {
        return type;
    }
}
