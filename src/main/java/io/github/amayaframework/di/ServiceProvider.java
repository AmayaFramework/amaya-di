package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;

/**
 * An interface describing an abstract provider that instantiates the requested service by specified type.
 */
public interface ServiceProvider {

    /**
     * Returns the repository used by this {@link ServiceProvider} instance.
     *
     * @return {@link Repository} instance
     */
    Repository getRepository();

    /**
     * Searches for an instantiator for the specified artifact.
     *
     * @param artifact the specified artifact
     * @param <T>      service type
     * @return null or empty stub, if artifact not found, instantiator implementation otherwise
     */
    <T> Function0<T> get(Artifact artifact);

    /**
     * Searches for an instantiator for the specified class and its generics.
     *
     * @param type     the specified class
     * @param generics the specified generics
     * @param <T>      service type
     * @return null or empty stub, if parameterized class not found, instantiator implementation otherwise
     */
    <T> Function0<T> get(Class<T> type, Class<?>... generics);

    /**
     * Searches for an instantiator for the specified class.
     *
     * @param type the specified class
     * @param <T>  service type
     * @return null or empty stub, if class not found, instantiator implementation otherwise
     */
    <T> Function0<T> get(Class<T> type);

    /**
     * Instantiates the service requested by specified artifact.
     *
     * @param artifact the specified artifact
     * @param <T>      service type
     * @return null, if artifact not found, service instance otherwise
     */
    <T> T instantiate(Artifact artifact);

    /**
     * Instantiates the service requested by the specified class and its generics.
     *
     * @param type     the specified class
     * @param generics the specified generics
     * @param <T>      service type
     * @return null, if parameterized class not found, service instance otherwise
     */
    <T> T instantiate(Class<T> type, Class<?>... generics);

    /**
     * Instantiates the service requested by the specified class.
     *
     * @param type the specified class
     * @param <T>  service type
     * @return null, if class not found, service instance otherwise
     */
    <T> T instantiate(Class<T> type);
}
