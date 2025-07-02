package io.github.amayaframework.di.core;

import com.github.romanqed.jtype.JType;

import java.lang.reflect.Type;

/**
 * An interface describing an abstract provider that instantiates the requested service by specified type.
 */
public interface ServiceProvider {

    /**
     * Returns the repository used by this {@link ServiceProvider} instance.
     *
     * @return the {@link TypeRepository} instance
     */
    TypeRepository getRepository();

    /**
     * Creates a new scoped instance of this {@link ServiceProvider}.
     * <br>
     * The new instance shares the same {@link TypeRepository}, but maintains its own scoped services.
     *
     * @return a new scoped {@link ServiceProvider} instance
     */
    ServiceProvider createScoped();

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
}
