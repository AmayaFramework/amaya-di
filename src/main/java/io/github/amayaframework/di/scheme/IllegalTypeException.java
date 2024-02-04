package io.github.amayaframework.di.scheme;

import java.lang.reflect.Type;

public class IllegalTypeException extends RuntimeException {
    private final Type type;

    public IllegalTypeException(String message, Type type) {
        super(message);
        this.type = type;
    }

    public IllegalTypeException(Type type) {
        super("Illegal type");
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
