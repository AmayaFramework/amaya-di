package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jtype.JType;

import java.lang.reflect.Type;

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
     * Searches for an instantiator for the specified type.
     *
     * @param type the specified type
     * @param <T>  service type
     * @return null or empty stub, if type not found, instantiator implementation otherwise
     */
    <T> Function0<T> get(Type type);

    /**
     * Searches for an instantiator for the specified class.
     *
     * @param type the specified class
     * @param <T>  service type
     * @return null or empty stub, if class not found, instantiator implementation otherwise
     */
    <T> Function0<T> get(Class<T> type);

    default <T> Function0<T> get(JType<T> type) {
        return get(type.getType());
    }

    /**
     * Instantiates the service requested by specified type.
     *
     * @param type the specified type
     * @param <T>  service type
     * @return null, if type not found, service instance otherwise
     */
    <T> T instantiate(Type type);

    /**
     * Instantiates the service requested by the specified class.
     *
     * @param type the specified class
     * @param <T>  service type
     * @return null, if class not found, service instance otherwise
     */
    <T> T instantiate(Class<T> type);

    default <T> T instantiate(JType<T> type) {
        return instantiate(type.getType());
    }
}
