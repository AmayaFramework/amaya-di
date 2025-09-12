package io.github.amayaframework.di.core;

/**
 * A base {@link ScopedServiceProvider} implementation that supports cleanup of locally
 * registered factories.
 * <br>
 * When {@link #close()} is invoked, only factories defined in the current
 * {@link ScopedRepository} that implement {@link Closeable} are closed.
 * Parent repositories remain unaffected.
 * <p>
 * This does not prevent further usage of the scoped provider; however, all resources
 * managed by closeable factories in the local scope are considered released and may
 * no longer be valid.
 */
public abstract class AbstractCloseableScopedProvider extends AbstractServiceProvider<ScopedRepository> implements ScopedServiceProvider {

    /**
     * Constructs a new closeable scoped provider backed by the given scoped repository.
     *
     * @param repository the {@link ScopedRepository} used to resolve and manage factories, must be non-null
     */
    protected AbstractCloseableScopedProvider(ScopedRepository repository) {
        super(repository);
    }

    /**
     * Closes all {@link Closeable} factories in the local scope of this provider.
     * <br>
     * Parent scope factories are not affected. Exceptions are not propagated.
     */
    @Override
    public void close() {
        var iterator = repository.localFactoryIterator();
        while (iterator.hasNext()) {
            var factory = iterator.next();
            if (factory instanceof Closeable) {
                ((Closeable) factory).close();
            }
        }
    }
}
