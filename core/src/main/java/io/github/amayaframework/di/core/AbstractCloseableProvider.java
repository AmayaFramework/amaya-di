package io.github.amayaframework.di.core;

/**
 * A base {@link ServiceProvider} implementation that supports cleanup of registered factories.
 * <br>
 * When {@link #close()} is invoked, all factories in the underlying {@link TypeRepository}
 * that implement {@link Closeable} are closed.
 * <p>
 * This does not prevent further usage of the provider; however, all resources managed by
 * closeable factories are considered released and may no longer be valid.
 */
public abstract class AbstractCloseableProvider extends AbstractServiceProvider<TypeRepository> {

    /**
     * Constructs a new closeable provider backed by the given repository.
     *
     * @param repository the {@link TypeRepository} used to resolve and manage factories, must be non-null
     */
    protected AbstractCloseableProvider(TypeRepository repository) {
        super(repository);
    }

    /**
     * Closes all {@link Closeable} factories in the underlying repository.
     * <br>
     * Non-closeable factories are ignored. Exceptions are not propagated.
     */
    @Override
    public void close() {
        var iterator = repository.factoryIterator();
        while (iterator.hasNext()) {
            var factory = iterator.next();
            if (factory instanceof Closeable) {
                ((Closeable) factory).close();
            }
        }
    }
}
