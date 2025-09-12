package io.github.amayaframework.di.core;

/**
 * An {@link ObjectFactory} that also supports lifecycle management via {@link Closeable}.
 * <br>
 * Implementations may release resources or perform cleanup when {@link #close()} is called.
 * <p>
 * This interface provides a default no-op implementation of {@link #close()},
 * allowing factories that do not need cleanup to ignore it.
 */
public interface CloseableObjectFactory extends ObjectFactory, Closeable {

    /**
     * Performs cleanup of this factory and any associated resources.
     * <br>
     * Default implementation does nothing.
     */
    default void close() {
        // Do nothing
    }
}
