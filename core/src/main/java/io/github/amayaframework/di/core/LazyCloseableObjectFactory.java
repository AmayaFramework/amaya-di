package io.github.amayaframework.di.core;

/**
 * A {@link CloseableObjectFactory} implementation that wraps another {@link ObjectFactory}
 * and provides thread-safe lazy initialization with caching.
 * <br>
 * The first call to {@link #create(TypeProvider)} instantiates the object by invoking
 * the wrapped factory and stores it for subsequent calls.
 * <br>
 * On {@link #close()}, the cached instance is closed if it implements {@link Closeable},
 * and the wrapped factory is closed if it also implements {@link Closeable}.
 * <p>
 * This class is suitable for implementing singleton-like lifecycles where both the
 * instance and the factory may require cleanup.
 */
public final class LazyCloseableObjectFactory implements CloseableObjectFactory {
    private final ObjectFactory body;
    private final Object lock;
    private volatile Object value;

    /**
     * Constructs a new lazy closable factory wrapping the given factory.
     *
     * @param body the underlying {@link ObjectFactory}, must be non-null
     */
    public LazyCloseableObjectFactory(ObjectFactory body) {
        this.body = body;
        this.lock = new Object();
    }

    /**
     * Returns the underlying (wrapped) factory used for instantiation.
     *
     * @return the delegate {@link ObjectFactory}
     */
    public ObjectFactory getBody() {
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

    /**
     * Closes the cached instance and the underlying factory if they implement {@link Closeable}.
     * <br>
     * If the cached instance has been created and implements {@link Closeable}, its
     * {@link Closeable#close()} method is invoked and the reference is cleared.
     * If the instance has not been created yet, the method synchronizes to safely check
     * and close it if necessary.
     * <br>
     * Finally, if the wrapped {@link ObjectFactory} itself implements {@link Closeable},
     * it is also closed.
     * <p>
     * This operation is idempotent — calling it multiple times has no additional effect
     * beyond the first close.
     */
    @Override
    public void close() {
        if (value != null) {
            if (value instanceof Closeable) {
                ((Closeable) value).close();
            }
            value = null;
        } else {
            synchronized (lock) {
                if (value instanceof Closeable) {
                    ((Closeable) value).close();
                }
                value = null;
            }
        }
        if (body instanceof Closeable) {
            ((Closeable) body).close();
        }
    }
}
