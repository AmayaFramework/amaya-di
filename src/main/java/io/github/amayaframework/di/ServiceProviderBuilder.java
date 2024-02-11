package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Function1;

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
     * Adds a service by its class, which is an implementation of the specified artifact.
     *
     * @param artifact       the specified artifact, must be non-null
     * @param implementation the specified implementation class, must extend the artifact type and be non-null
     * @param wrapper        the wrapper function that will be applied to the created instantiator, must be non-null
     * @param <T>            the service type
     * @return this {@link ServiceProviderBuilder} instance
     */
    <T> ServiceProviderBuilder addService(Artifact artifact,
                                          Class<? extends T> implementation,
                                          Function1<Function0<T>, Function0<T>> wrapper);

    /**
     * Adds a singleton service by its class, which is an implementation of the specified artifact.
     * Singleton implies a dependency resolution policy in which each service request will return the same instance.
     * It is guaranteed that the implementation of the policy is thread-safe.
     *
     * @param artifact       the specified artifact, must be non-null
     * @param implementation the specified implementation class, must extend the artifact type and be non-null
     * @return this {@link ServiceProviderBuilder} instance
     */
    ServiceProviderBuilder addSingleton(Artifact artifact, Class<?> implementation);

    /**
     * Adds a transient service by its class, which is an implementation of the specified artifact.
     * Transient implies a dependency resolution policy in which each service request will return a new instance.
     * It is guaranteed that the implementation of the policy is thread-safe.
     *
     * @param artifact       the specified artifact, must be non-null
     * @param implementation the specified implementation class, must extend the artifact type and be non-null
     * @return this {@link ServiceProviderBuilder} instance
     */
    ServiceProviderBuilder addTransient(Artifact artifact, Class<?> implementation);

    /**
     * Adds a service by its class, which is an implementation of the specified class.
     *
     * @param type           the specified class, must be non-null
     * @param implementation the specified implementation class, must extend the service type and be non-null
     * @param wrapper        the wrapper function that will be applied to the created instantiator, must be non-null
     * @param <T>            the service type
     * @return this {@link ServiceProviderBuilder} instance
     */
    <T> ServiceProviderBuilder addService(Class<T> type,
                                          Class<? extends T> implementation,
                                          Function1<Function0<T>, Function0<T>> wrapper);

    /**
     * Adds a singleton service by its class, which is an implementation of the specified class.
     * Singleton implies a dependency resolution policy in which each service request will return the same instance.
     * It is guaranteed that the implementation of the policy is thread-safe.
     *
     * @param type           the specified class, must be non-null
     * @param implementation the specified implementation class, must extend the service type and be non-null
     * @param <T>            the service type
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
     * @param <T>            the service type
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
    <T> ServiceProviderBuilder addService(Class<T> type, Function1<Function0<T>, Function0<T>> wrapper);

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
     * Adds a service by its instantiator, which creates instances of the specified artifact.
     *
     * @param artifact the specified artifact, must be non-null
     * @param supplier the specified instantiator, must be non-null
     * @return this {@link ServiceProviderBuilder} instance
     */
    ServiceProviderBuilder addService(Artifact artifact, Function0<?> supplier);

    /**
     * Adds a service by its instantiator, which creates instances of the specified class.
     *
     * @param type     the specified class, must be non-null
     * @param supplier the specified instantiator, must be non-null
     * @param <T>      the service type
     * @return this {@link ServiceProviderBuilder} instance
     */
    <T> ServiceProviderBuilder addService(Class<T> type, Function0<T> supplier);

    /**
     * Removes the service that implements the specified artifact.
     *
     * @param artifact the specified artifact, must be non-null
     * @return this {@link ServiceProviderBuilder} instance
     */
    ServiceProviderBuilder removeService(Artifact artifact);

    /**
     * Removes the service that implements the specified type.
     *
     * @param type the specified type, must be non-null
     * @return this {@link ServiceProviderBuilder} instance
     */
    ServiceProviderBuilder removeService(Class<?> type);

    /**
     * Builds a ready-to-use {@link ServiceProvider} implementation and resets
     * this {@link ServiceProviderBuilder} instance to its original state,
     * making it ready for reuse.
     * If the build was not completed due to an error, no third-party effects will be applied.
     *
     * @return {@link ServiceProvider} instance
     * @throws ArtifactNotFoundException if the dependency of the service used has not been resolved (optional)
     * @throws CycleFoundException       if a cyclic dependence is detected (optional)
     */
    ServiceProvider build();
}
