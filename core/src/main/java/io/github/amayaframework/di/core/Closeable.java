package io.github.amayaframework.di.core;

/**
 * A DI-specific contract for releasing resources.
 * <br>
 * Unlike {@link AutoCloseable}, this interface:
 * <ul>
 *   <li>does <strong>not</strong> declare checked exceptions – {@link #close()} must never throw;</li>
 *   <li>is <strong>not</strong> intended for use with try-with-resources;</li>
 *   <li>is used internally by the DI framework to signal that a component
 *       or factory requires best-effort cleanup during provider or scope teardown.</li>
 * </ul>
 * <p>
 * Implementations should be idempotent (safe to call multiple times) and are expected
 * to swallow any internal errors instead of propagating them. Thread-safety is left
 * to the implementation.
 */
public interface Closeable {

    /**
     * Creates an adapter that wraps the given {@link AutoCloseable} into a {@link Closeable}.
     * <br>
     * Any exceptions thrown by {@link AutoCloseable#close()} are caught and suppressed.
     *
     * @param closeable the underlying {@link AutoCloseable}, must be non-null
     * @return a {@link Closeable} adapter that performs best-effort cleanup
     */
    static Closeable of(AutoCloseable closeable) {
        return () -> {
            try {
                closeable.close();
            } catch (Exception e) {
                // close() must not throw exceptions
            }
        };
    }

    /**
     * Releases resources in a best-effort manner.
     * <br>
     * This method must not throw exceptions. Repeated calls should be safe.
     */
    void close();
}
