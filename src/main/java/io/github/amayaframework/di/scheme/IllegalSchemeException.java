package io.github.amayaframework.di.scheme;

public class IllegalSchemeException extends Error {
    private final Class<?> clazz;

    public IllegalSchemeException(Class<?> clazz, String message, Throwable cause) {
        super(message, cause);
        this.clazz = clazz;
    }

    public IllegalSchemeException(Class<?> clazz, String message) {
        super(message);
        this.clazz = clazz;
    }

    public IllegalSchemeException(Class<?> clazz) {
        super("Cannot create class scheme");
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
