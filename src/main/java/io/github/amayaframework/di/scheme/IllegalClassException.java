package io.github.amayaframework.di.scheme;

/**
 * Thrown to indicate that it is not possible to build an injection scheme for the specified class.
 */
public class IllegalClassException extends RuntimeException {
    private final Class<?> clazz;

    /**
     * Constructs an {@link IllegalClassException} with the specified detail message and illegal class.
     *
     * @param message the detail message
     * @param clazz   the illegal class
     */
    public IllegalClassException(String message, Class<?> clazz) {
        super(message);
        this.clazz = clazz;
    }

    /**
     * Constructs an {@link IllegalClassException} with the specified illegal class.
     *
     * @param clazz the illegal class
     */
    public IllegalClassException(Class<?> clazz) {
        super("Cannot create class scheme");
        this.clazz = clazz;
    }

    /**
     * Returns the illegal class.
     *
     * @return the illegal class
     */
    public Class<?> getClazz() {
        return clazz;
    }
}
