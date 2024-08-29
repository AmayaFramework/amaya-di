package io.github.amayaframework.di;

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

    default <T> T get(JType<T> type) {
        return get(type.getType());
    }
}
