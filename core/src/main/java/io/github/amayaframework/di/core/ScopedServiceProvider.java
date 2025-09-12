package io.github.amayaframework.di.core;

/**
 * A {@link ServiceProvider} implementation representing a scoped provider.
 * <br>
 * Uses a {@link ScopedRepository} to resolve services, allowing access
 * to both local and parent scope factories.
 * <p>
 * Calling {@link #close()} on a scoped provider invokes
 * {@link Closeable#close()} on all factories or services in the
 * local repository that implement {@link Closeable}.
 * <br>
 * This does not make the scope unusable: further lookups and resolutions
 * remain possible, but any previously registered closeable components
 * are considered closed.
 */
public interface ScopedServiceProvider extends ServiceProvider {

    /**
     * Returns the {@link ScopedRepository} used by this provider.
     *
     * @return the {@link ScopedRepository} instance
     */
    @Override
    ScopedRepository repository();
}
