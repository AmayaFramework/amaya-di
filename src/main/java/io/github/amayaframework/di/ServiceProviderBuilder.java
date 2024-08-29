package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jtype.JType;

import java.lang.reflect.Type;

/**
 * An interface describing an abstract {@link ServiceProvider} builder.
 */
public interface ServiceProviderBuilder {

    /**
     * Sets the repository that will be used by built {@link ServiceProvider} instance.
     * If no repository has been set, or null has been set, an empty default repository will be created.
     * The repository used will not be changed in any way
     * until the {@link ServiceProviderBuilder#build} method is called.
     * If any error occurs during the build process, no changes will be made either.
     *
     * @param repository the specified {@link Repository} instance, may be null
     * @return this {@link ServiceProviderBuilder} instance
     */
    ServiceProviderBuilder setRepository(Repository repository);

    /**
     * Adds a service by its class, which is an implementation of the specified type.
     *
     * @param type           the specified type, must be non-null
     * @param implementation the specified implementation class, must extend the type type and be non-null
     * @param wrapper        the wrapper function that will be applied to the created instantiator, must be non-null
     * @param <T>            the service type
     * @return this {@link ServiceProviderBuilder} instance
     */
    <T> ServiceProviderBuilder addService(Type type, Class<? extends T> implementation, ServiceWrapper<T> wrapper);

    /**
     * Adds a singleton service by its class, which is an implementation of the specified type.
     * Singleton implies a dependency resolution policy in which each service request will return the same instance.
     * It is guaranteed that the implementation of the policy is thread-safe.
     *
     * @param type           the specified type, must be non-null
     * @param implementation the specified implementation class, must extend the type type and be non-null
     * @return this {@link ServiceProviderBuilder} instance
     */
    ServiceProviderBuilder addSingleton(Type type, Class<?> implementation);

    /**
     * Adds a transient service by its class, which is an implementation of the specified type.
     * Transient implies a dependency resolution policy in which each service request will return a new instance.
     * It is guaranteed that the implementation of the policy is thread-safe.
     *
     * @param type           the specified type, must be non-null
     * @param implementation the specified implementation class, must extend the type type and be non-null
     * @return this {@link ServiceProviderBuilder} instance
     */
    ServiceProviderBuilder addTransient(Type type, Class<?> implementation);

    default <T> ServiceProviderBuilder addService(JType<T> type,
                                                  Class<? extends T> implementation,
                                                  ServiceWrapper<T> wrapper) {
        return addService(type.getType(), implementation, wrapper);
    }

    default <T> ServiceProviderBuilder addSingleton(JType<T> type, Class<? extends T> implementation) {
        return addSingleton(type.getType(), implementation);
    }

    default <T> ServiceProviderBuilder addTransient(JType<T> type, Class<? extends T> implementation) {
        return addTransient(type.getType(), implementation);
    }

    /**
     * Adds a service by its class, which is an implementation of the specified class.
     *
     * @param type           the specified class, must be non-null
     * @param implementation the specified implementation class, must extend the service type and be non-null
     * @param wrapper        the wrapper function that will be applied to the created instantiator, must be non-null
     * @param <T>            the service type
     * @return this {@link ServiceProviderBuilder} instance
     */
    <T> ServiceProviderBuilder addService(Class<T> type, Class<? extends T> implementation, ServiceWrapper<T> wrapper);

    /**
     * Adds a singleton service by its class, which is an implementation of the specified class.
     * Singleton implies a dependency resolution policy in which each service request will return the same instance.
     * It is guaranteed that the implementation of the policy is thread-safe.
     *
     * @param type           the specified class, must be non-null
     * @param implementation the specified implementation class, must extend the service type and be non-null
     * @return this {@link ServiceProviderBuilder} instance
     */
    <T> ServiceProviderBuilder addSingleton(Class<T> type, Class<? extends T> implementation);

    /**
     * Adds a transient service by its class, which is an implementation of the specified class.
     * Transient implies a dependency resolution policy in which each service request will return a new instance.
     * It is guaranteed that the implementation of the policy is thread-safe.
     *
     * @param type           the specified class, must be non-null
     * @param implementation the specified implementation class, must extend the service type and be non-null
     * @return this {@link ServiceProviderBuilder} instance
     */
    <T> ServiceProviderBuilder addTransient(Class<T> type, Class<? extends T> implementation);

    /**
     * Adds a service by its class, that will be used as service type and service implementation at the same time.
     *
     * @param type    the specified class, must be non-null
     * @param wrapper the wrapper function that will be applied to the created instantiator, must be non-null
     * @param <T>     the service type
     * @return this {@link ServiceProviderBuilder} instance
     */
    <T> ServiceProviderBuilder addService(Class<T> type, ServiceWrapper<T> wrapper);

    /**
     * Adds a singleton service by its class,
     * that will be used as service type and service implementation at the same time.
     * Singleton implies a dependency resolution policy in which each service request will return the same instance.
     * It is guaranteed that the implementation of the policy is thread-safe.
     *
     * @param type the specified class, must be non-null
     * @return this {@link ServiceProviderBuilder} instance
     */
    ServiceProviderBuilder addSingleton(Class<?> type);

    /**
     * Adds a transient service by its class,
     * that will be used as service type and service implementation at the same time.
     * Transient implies a dependency resolution policy in which each service request will return a new instance.
     * It is guaranteed that the implementation of the policy is thread-safe.
     *
     * @param type the specified class, must be non-null
     * @return this {@link ServiceProviderBuilder} instance
     */
    ServiceProviderBuilder addTransient(Class<?> type);

    /**
     * Adds a service by its instantiator, which creates instances of the specified type.
     *
     * @param type     the specified type, must be non-null
     * @param supplier the specified instantiator, must be non-null
     * @return this {@link ServiceProviderBuilder} instance
     */
    ServiceProviderBuilder addService(Type type, Function0<?> supplier);

    /**
     * Adds a service by its instance, which will continue to be used unchanged.
     *
     * @param type     the specified type, must be non-null
     * @param instance the specified instance, may be null
     * @return this {@link ServiceProviderBuilder} instance
     */
    default ServiceProviderBuilder addInstance(Type type, Object instance) {
        return addService(type, () -> instance);
    }

    /**
     * Adds a service by its instance, which will continue to be used unchanged.
     *
     * @param type     the specified type, must be non-null
     * @param instance the specified instance, may be null
     * @param <T>      the service type
     * @return this {@link ServiceProviderBuilder} instance
     */
    default <T> ServiceProviderBuilder addInstance(JType<T> type, T instance) {
        return addService(type, () -> instance);
    }

    /**
     * Adds a service by its instance, which will continue to be used unchanged.
     *
     * @param instance the specified instance, must be non-null (to determine service type)
     * @return this {@link ServiceProviderBuilder} instance
     */
    default ServiceProviderBuilder addInstance(Object instance) {
        return addService(instance.getClass(), () -> instance);
    }

    /**
     * Adds a service by its instantiator, which creates instances of the specified type.
     *
     * @param type     the specified type, must be non-null
     * @param supplier the specified instantiator, must be non-null
     * @param <T>      the service type
     * @return this {@link ServiceProviderBuilder} instance
     */
    default <T> ServiceProviderBuilder addService(JType<T> type, Function0<T> supplier) {
        return addService(type.getType(), supplier);
    }

    /**
     * Removes the service that implements the specified type.
     *
     * @param type the specified type, must be non-null
     * @return this {@link ServiceProviderBuilder} instance
     */
    ServiceProviderBuilder removeService(Type type);

    default ServiceProviderBuilder removeService(JType<?> type) {
        return removeService(type.getType());
    }

    /**
     * Builds a ready-to-use {@link ServiceProvider} implementation and resets
     * this {@link ServiceProviderBuilder} instance to its original state,
     * making it ready for reuse.
     * If the build was not completed due to an error, no third-party effects will be applied.
     * Important: if an error occurred while filling in the repository, the changes made will not be undone.
     *
     * @return {@link ServiceProvider} instance
     * @throws TypeNotFoundException if the dependency of the service used has not been resolved (optional)
     * @throws CycleFoundException   if a cyclic dependence is detected (optional)
     */
    ServiceProvider build();
}
