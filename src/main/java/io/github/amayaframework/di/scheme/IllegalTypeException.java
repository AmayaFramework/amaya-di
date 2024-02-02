package io.github.amayaframework.di.scheme;

import java.lang.reflect.ParameterizedType;

public class IllegalTypeException extends RuntimeException {
    private final ParameterizedType type;

    public IllegalTypeException(ParameterizedType type) {
        super("Illegal parameterized type");
        this.type = type;
    }

    public ParameterizedType getType() {
        return type;
    }
}
