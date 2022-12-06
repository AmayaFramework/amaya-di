package io.github.amayaframework.di;

/**
 * An error that is thrown when any errors occurred during the injection process.
 */
public class InjectException extends RuntimeException {
    public InjectException(Class<?> clazz, Throwable cause) {
        super("It was impossible to inject the dependency into " + clazz + "due to", cause);
    }

    public InjectException(Throwable cause) {
        super("It was impossible to inject the dependency due to", cause);
    }
}
