package io.github.amayaframework.di;

public class InjectError extends RuntimeException {
    public InjectError(Class<?> clazz, Throwable cause) {
        super("It was impossible to inject the dependency into " + clazz + "due to", cause);
    }

    public InjectError(String message) {
        super("It was impossible to inject the dependency due to " + message);
    }
}
