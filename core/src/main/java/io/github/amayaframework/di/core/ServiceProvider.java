package io.github.amayaframework.di.core;

import com.github.romanqed.jtype.JType;

import java.lang.reflect.Type;

/**
 * A universal dependency injection container capable of resolving services by type.
 * <br>
 * Provides access to the underlying {@link TypeRepository}, creation of new scopes,
 * and methods to retrieve service instances.
 * <p>
 * This interface also extends {@link Closeable} to optionally support lifecycle
 * management. Calling {@link #close()} does not make the provider unusable —
 * it simply propagates {@link Closeable#close()} calls to factories or services
 * stored in the repository that implement {@link Closeable}.
 */
public interface ServiceProvider extends Closeable {

    /**
     * Returns the repository used by this {@link ServiceProvider} instance.
     *
     * @return the {@link TypeRepository} instance
     */
    TypeRepository repository();

    /**
     * Creates a new scoped instance of this {@link ServiceProvider}.
     * <br>
     * The new scope shares the same parent {@link TypeRepository} but maintains
     * its own local services. Scoped providers are typically used to model
     * request or session lifetimes.
     *
     * @return a new {@link ScopedServiceProvider} instance
     */
    ScopedServiceProvider createScoped();

    /**
     * Instantiates the service requested by specified type.
     *
     * @param type the specified type
     * @param <T>  service type
     * @return null, if type not found, service instance otherwise
     */
    <T> T get(Type type);

    /**
     * Instantiates the service requested by the specified class.
     *
     * @param type the specified class
     * @param <T>  service type
     * @return null, if class not found, service instance otherwise
     */
    <T> T get(Class<T> type);

    /**
     * Instantiates the service requested by the specified type.
     *
     * @param type the specified type
     * @param <T>  service type
     * @return null, if class not found, service instance otherwise
     */
    <T> T get(JType<T> type);

    /**
     * Invokes {@link Closeable#close()} on all factories or services in the repository
     * that implement {@link Closeable}.
     * <br>
     * This does not make the provider unusable: further lookups and resolutions
     * remain possible, but any previously registered closeable components
     * are considered closed.
     */
    @Override
    default void close() {
        // Do nothing
    }
}
