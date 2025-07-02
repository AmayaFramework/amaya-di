package io.github.amayaframework.di.core;

/**
 * A thread-safe {@link ObjectFactory} implementation that creates and caches an object lazily.
 * <br>
 * The wrapped factory is only invoked once upon the first call to {@link #create(TypeProvider)},
 * and the result is cached for future calls.
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
     * Resets the cached value, allowing the wrapped factory to be invoked again.
     */
    public void reset() {
        synchronized (lock) {
            this.value = null;
        }
    }

    @Override
    public Object create(TypeProvider provider) {
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
