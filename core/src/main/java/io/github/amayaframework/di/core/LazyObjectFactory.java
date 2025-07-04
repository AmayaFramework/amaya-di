package io.github.amayaframework.di.core;

/**
 * A thread-safe {@link ObjectFactory} implementation that creates and caches an object lazily.
 * <br>
 * The wrapped factory is only invoked once upon the first call to {@link #create(TypeProvider)},
 * and the result is cached for future calls.
 * <p>
 * This class is suitable for implementing singleton-style scoping in dependency injection.
 */
public final class LazyObjectFactory implements ObjectFactory {
    private final ObjectFactory body;
    private final Object lock;
    private volatile Object value;

    /**
     * Constructs a lazy factory based on the given underlying factory.
     *
     * @param body the underlying {@link ObjectFactory}, must be non-null
     */
    public LazyObjectFactory(ObjectFactory body) {
        this.body = body;
        this.lock = new Object();
    }

    /**
     * Returns the underlying (wrapped) factory used for instantiation.
     *
     * @return the delegate {@link ObjectFactory}
     */
    public ObjectFactory getFactory() {
        return body;
    }

    /**
     * Returns the cached object if it exists, otherwise creates it by invoking
     * the wrapped factory. Ensures thread-safe lazy initialization.
     *
     * @param provider the type provider used to resolve dependencies
     * @return the created or cached instance
     * @throws Throwable if the wrapped factory throws an exception during creation
     */
    @Override
    public Object create(TypeProvider provider) throws Throwable {
        if (value == null) {
            synchronized (lock) {
                if (value == null) {
                    value = body.create(provider);
                }
            }
        }
        return value;
    }
}
