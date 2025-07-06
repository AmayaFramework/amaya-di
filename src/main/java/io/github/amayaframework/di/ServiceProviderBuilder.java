package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jtype.JType;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.core.TypeRepository;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * A builder interface for constructing a {@link ServiceProvider}.
 * <p>
 * Allows configuring repositories, schema/stub factories, and registering
 * services (factories, instances, implementations) with optional scoping.
 * <p>
 * Each call to {@link #build()} resets the internal builder state.
 */
public interface ServiceProviderBuilder {

    /**
     * Specifies the {@link SchemaFactory} to use for generating injection metadata.
     *
     * @param factory the schema factory to use
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder withSchemaFactory(SchemaFactory factory);

    /**
     * Specifies the {@link StubFactory} to use for generating {@link ObjectFactory} stubs.
     *
     * @param factory the stub factory to use
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder withStubFactory(StubFactory factory);

    /**
     * Specifies the {@link CacheMode} used during stub generation.
     *
     * @param mode the caching mode to apply
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder withCacheMode(CacheMode mode);

    /**
     * Sets the {@link TypeRepository} to store built object factories.
     *
     * @param repository the repository instance
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder withRepository(TypeRepository repository);

    /**
     * Sets a lazy provider of the {@link TypeRepository}.
     *
     * @param supplier the supplier that provides the repository
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder withRepository(Supplier<TypeRepository> supplier);

    /**
     * Registers a raw object factory directly for the specified type.
     *
     * @param type    the target type
     * @param factory the object factory to use
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder add(Type type, ObjectFactory factory);

    /**
     * Registers a raw object factory using {@link JType}.
     *
     * @param type    the target type
     * @param factory the object factory to use
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder add(JType<?> type, ObjectFactory factory);

    /**
     * Removes any previously registered factory or service binding for the specified type.
     *
     * @param type the type to remove
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder remove(Type type);

    /**
     * Removes any binding for the given {@link JType}.
     *
     * @param type the type to remove
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder remove(JType<?> type);

    /**
     * Registers a functional provider for the specified type.
     *
     * @param type     the target type
     * @param provider the function that produces an instance
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder add(Type type, Function0<?> provider);

    /**
     * Registers a typed functional provider using {@link JType}.
     *
     * @param type     the target type
     * @param provider the function that produces an instance
     * @param <T>      the service type
     * @return this builder instance for chaining
     */
    <T> ServiceProviderBuilder add(JType<T> type, Function0<T> provider);

    /**
     * Registers a prebuilt service instance.
     *
     * @param type     the service type
     * @param instance the service instance
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder addInstance(Type type, Object instance);


    /**
     * Registers a typed service instance using {@link JType}.
     *
     * @param type     the target type
     * @param instance the instance to register
     * @param <T>      the service type
     * @return this builder instance for chaining
     */
    <T> ServiceProviderBuilder addInstance(JType<T> type, T instance);

    /**
     * Registers an untyped service instance using its runtime class.
     *
     * @param instance the instance to register
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder addInstance(Object instance);

    /**
     * Registers a concrete implementation with a {@link ServiceWrapper} for the given type.
     *
     * @param type    the service interface or base type
     * @param impl    the implementation class
     * @param wrapper the wrapper to apply to the factory
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder add(Type type, Class<?> impl, ServiceWrapper wrapper);

    /**
     * Registers a typed implementation with a wrapper.
     *
     * @param type    the base type
     * @param impl    the implementation class
     * @param wrapper the factory wrapper
     * @param <T>     the service type
     * @return this builder instance for chaining
     */
    <T> ServiceProviderBuilder add(Class<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    /**
     * Registers a typed implementation via {@link JType} with a wrapper.
     *
     * @param type    the typed service
     * @param impl    the implementation class
     * @param wrapper the wrapper to apply
     * @param <T>     the service type
     * @return this builder instance for chaining
     */
    <T> ServiceProviderBuilder add(JType<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    /**
     * Registers a self-bound implementation with a wrapper.
     *
     * @param impl    the implementation class
     * @param wrapper the factory wrapper
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder add(Class<?> impl, ServiceWrapper wrapper);

    /**
     * Registers a transient binding (new instance created per request).
     *
     * @param type the service type
     * @param impl the implementation class
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder addTransient(Type type, Class<?> impl);

    /**
     * Registers a typed transient binding.
     *
     * @param type the service type
     * @param impl the implementation class
     * @param <T>  the service type
     * @return this builder instance for chaining
     */
    <T> ServiceProviderBuilder addTransient(Class<T> type, Class<? extends T> impl);

    /**
     * Registers a typed transient binding using {@link JType}.
     *
     * @param type the service type
     * @param impl the implementation class
     * @param <T>  the service type
     * @return this builder instance for chaining
     */
    <T> ServiceProviderBuilder addTransient(JType<T> type, Class<? extends T> impl);

    /**
     * Registers a self-bound transient binding.
     *
     * @param impl the implementation class
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder addTransient(Class<?> impl);

    /**
     * Registers a singleton binding (single instance reused).
     *
     * @param type the service type
     * @param impl the implementation class
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder addSingleton(Type type, Class<?> impl);

    /**
     * Registers a typed singleton binding.
     *
     * @param type the service type
     * @param impl the implementation class
     * @param <T>  the service type
     * @return this builder instance for chaining
     */
    <T> ServiceProviderBuilder addSingleton(Class<T> type, Class<? extends T> impl);

    /**
     * Registers a typed singleton binding using {@link JType}.
     *
     * @param type the service type
     * @param impl the implementation class
     * @param <T>  the service type
     * @return this builder instance for chaining
     */
    <T> ServiceProviderBuilder addSingleton(JType<T> type, Class<? extends T> impl);

    /**
     * Registers a self-bound singleton binding.
     *
     * @param impl the implementation class
     * @return this builder instance for chaining
     */
    ServiceProviderBuilder addSingleton(Class<?> impl);

    /**
     * Builds a new {@link ServiceProvider} and resets the builder.
     * <p>
     * If an error occurs during the build process, the builder is still reset.
     *
     * @return a newly constructed {@link ServiceProvider}
     * @throws RuntimeException if the build process fails
     * @throws Error            if the build process fails
     */
    ServiceProvider build();
}
