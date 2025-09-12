package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jtype.JType;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.ScopedRepository;
import io.github.amayaframework.di.core.TypeRepository;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * TODO
 */
public interface ScopedProviderConfigurer extends ServiceProviderConfigurer {

    /**
     * Registers a promised scoped dependency, which must be provided externally
     * at the moment of scope creation.
     * <p>
     * This method does not define the implementation or factory for the given type.
     * It only declares that this type will be required in a future scope.
     *
     * @param type the type to register as promised
     * @return this builder instance
     */
    ScopedProviderConfigurer addScoped(Type type);

    /**
     * Registers a promised scoped dependency, which must be provided externally
     * at the moment of scope creation.
     * <p>
     * This method does not define the implementation or factory for the given type.
     * It only declares that this type will be required in a future scope.
     *
     * @param type the type to register as promised
     * @return this builder instance
     */
    ScopedProviderConfigurer addScoped(JType<?> type);

    /**
     * Registers a scoped type with a factory to create instances inside scopes.
     * The factory is used every time a new scope is created.
     * If the factory is null, the type is registered as promised.
     *
     * @param type    the type to register as scoped
     * @param factory the factory to create scoped instances, or null for promised
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer addScoped(Type type, ObjectFactory factory);

    /**
     * Registers a scoped type with a factory to create instances inside scopes.
     * The factory is used every time a new scope is created.
     * If the factory is null, the type is registered as promised.
     *
     * @param type    the type to register as scoped
     * @param factory the factory to create scoped instances, or null for promised
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer addScoped(JType<?> type, ObjectFactory factory);

    /**
     * Registers a scoped type with a factory and a service wrapper.
     * The wrapper will be applied to the factory for each new scope,
     * allowing modification or proxying of instances inside scopes.
     *
     * @param type    the type to register as scoped
     * @param factory the factory to create scoped instances
     * @param wrapper the wrapper to apply for each scope creation
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer addScoped(Type type, ObjectFactory factory, ServiceWrapper wrapper);

    /**
     * Registers a scoped type with a factory and a service wrapper.
     * The wrapper will be applied to the factory for each new scope,
     * allowing modification or proxying of instances inside scopes.
     *
     * @param type    the type to register as scoped
     * @param factory the factory to create scoped instances
     * @param wrapper the wrapper to apply for each scope creation
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer addScoped(JType<?> type, ObjectFactory factory, ServiceWrapper wrapper);

    /**
     * Registers a scoped type with a provider function to create instances inside scopes.
     *
     * @param type     the type to register as scoped
     * @param provider the function to provide service instances
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer addScoped(Type type, Function0<?> provider);

    /**
     * Registers a scoped type with a provider function to create instances inside scopes.
     *
     * @param type     the type to register as scoped
     * @param provider the function to provide service instances
     * @param <T>      the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderConfigurer addScoped(JType<T> type, Function0<T> provider);

    /**
     * Registers a scoped type with a provider function and a service wrapper.
     * The wrapper will be applied to the instances produced by the provider
     * for each new scope.
     *
     * @param type     the type to register as scoped
     * @param provider the function to provide service instances
     * @param wrapper  the wrapper to apply for each scope creation
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer addScoped(Type type, Function0<?> provider, ServiceWrapper wrapper);

    /**
     * Registers a scoped type with a provider function and a service wrapper.
     * The wrapper will be applied to the instances produced by the provider
     * for each new scope.
     *
     * @param type     the type to register as scoped
     * @param provider the function to provide service instances
     * @param wrapper  the wrapper to apply for each scope creation
     * @param <T>      the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderConfigurer addScoped(JType<T> type, Function0<T> provider, ServiceWrapper wrapper);

    /**
     * Registers a scoped type with a specific instance.
     * The same instance will be provided inside all scopes.
     *
     * @param type     the type to register as scoped
     * @param instance the instance to provide for the scoped type
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer addScopedInstance(Type type, Object instance);

    /**
     * Registers a scoped type with a specific instance.
     * The same instance will be provided inside all scopes.
     *
     * @param type     the type to register as scoped
     * @param instance the instance to provide for the scoped type
     * @param <T>      the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderConfigurer addScopedInstance(JType<T> type, T instance);

    /**
     * Registers an instance as scoped by its concrete class.
     * The same instance will be provided inside all scopes.
     *
     * @param instance the instance to register as scoped
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer addScopedInstance(Object instance);

    /**
     * Removes the specified scoped type registration,
     * whether promised, with factory, or wrapped.
     *
     * @param type the type to remove from scoped registrations
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer removeScoped(Type type);

    /**
     * Removes the specified scoped type registration,
     * whether promised, with factory, or wrapped.
     *
     * @param type the type to remove from scoped registrations
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer removeScoped(JType<?> type);

    /**
     * Registers a scoped type by interface/class with an implementation class and a service wrapper.
     * The wrapper will be applied for every new scope.
     *
     * @param type    the interface or supertype to register as scoped
     * @param impl    the implementation class to instantiate inside scopes
     * @param wrapper the wrapper to apply on the implementation factory per scope
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer addScoped(Type type, Class<?> impl, ServiceWrapper wrapper);

    /**
     * Registers a scoped type by interface/class with an implementation class and a service wrapper.
     * The wrapper will be applied for every new scope.
     *
     * @param type    the interface or supertype to register as scoped
     * @param impl    the implementation class to instantiate inside scopes
     * @param wrapper the wrapper to apply on the implementation factory per scope
     * @param <T>     the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderConfigurer addScoped(Class<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    /**
     * Registers a scoped type by generic type with an implementation class and a service wrapper.
     * The wrapper will be applied for every new scope.
     *
     * @param type    the generic type to register as scoped
     * @param impl    the implementation class to instantiate inside scopes
     * @param wrapper the wrapper to apply on the implementation factory per scope
     * @param <T>     the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderConfigurer addScoped(JType<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    /**
     * Registers a scoped implementation class with a service wrapper.
     * The wrapper will be applied for every new scope.
     *
     * @param impl    the implementation class to register as scoped
     * @param wrapper the wrapper to apply on the implementation factory per scope
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer addScoped(Class<?> impl, ServiceWrapper wrapper);

    /**
     * Registers a scoped transient type with implementation.
     * Equivalent to registering without a wrapper.
     *
     * @param type the type to register as scoped transient
     * @param impl the implementation class
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer addScopedTransient(Type type, Class<?> impl);

    /**
     * Registers a scoped transient type with implementation.
     * Equivalent to registering without a wrapper.
     *
     * @param type the type to register as scoped transient
     * @param impl the implementation class
     * @param <T>  the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderConfigurer addScopedTransient(Class<T> type, Class<? extends T> impl);

    /**
     * Registers a scoped transient generic type with implementation.
     * Equivalent to registering without a wrapper.
     *
     * @param type the generic type to register as scoped transient
     * @param impl the implementation class
     * @param <T>  the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderConfigurer addScopedTransient(JType<T> type, Class<? extends T> impl);

    /**
     * Registers a scoped transient implementation class.
     * Equivalent to registering without a wrapper.
     *
     * @param impl the implementation class to register as scoped transient
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer addScopedTransient(Class<?> impl);

    /**
     * Registers a scoped singleton type with implementation and wraps it.
     *
     * @param type the type to register as scoped singleton
     * @param impl the implementation class
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer addScopedSingleton(Type type, Class<?> impl);

    /**
     * Registers a scoped singleton type with implementation and wraps it.
     *
     * @param type the type to register as scoped singleton
     * @param impl the implementation class
     * @param <T>  the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderConfigurer addScopedSingleton(Class<T> type, Class<? extends T> impl);


    /**
     * Registers a scoped singleton generic type with implementation and wraps it.
     *
     * @param type the generic type to register as scoped singleton
     * @param impl the implementation class
     * @param <T>  the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderConfigurer addScopedSingleton(JType<T> type, Class<? extends T> impl);

    /**
     * Registers a scoped singleton implementation class and wraps it.
     *
     * @param impl the implementation class to register as scoped singleton
     * @return this builder instance for chaining
     */
    ScopedProviderConfigurer addScopedSingleton(Class<?> impl);
    
    // api fixes


    @Override
    ScopedProviderConfigurer withSchemaFactory(SchemaFactory factory);

    @Override
    ScopedProviderConfigurer withStubFactory(StubFactory factory);

    @Override
    ScopedProviderConfigurer withCacheMode(CacheMode mode);

    @Override
    ScopedProviderConfigurer withRepository(TypeRepository repository);

    @Override
    ScopedProviderConfigurer withRepository(Supplier<TypeRepository> supplier);

    @Override
    ScopedProviderConfigurer withScopedRepository(Supplier<ScopedRepository> supplier);

    @Override
    ScopedProviderConfigurer add(Type type, ObjectFactory factory);

    @Override
    ScopedProviderConfigurer add(JType<?> type, ObjectFactory factory);

    @Override
    ScopedProviderConfigurer remove(Type type);

    @Override
    ScopedProviderConfigurer remove(JType<?> type);

    @Override
    ScopedProviderConfigurer add(Type type, Function0<?> provider);

    @Override
    <T> ScopedProviderConfigurer add(JType<T> type, Function0<T> provider);

    @Override
    ScopedProviderConfigurer addInstance(Type type, Object instance);

    @Override
    <T> ScopedProviderConfigurer addInstance(JType<T> type, T instance);

    @Override
    ScopedProviderConfigurer addInstance(Object instance);

    @Override
    ScopedProviderConfigurer add(Type type, Class<?> impl, ServiceWrapper wrapper);

    @Override
    <T> ScopedProviderConfigurer add(Class<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    @Override
    <T> ScopedProviderConfigurer add(JType<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    @Override
    ScopedProviderConfigurer add(Class<?> impl, ServiceWrapper wrapper);

    @Override
    ScopedProviderConfigurer addTransient(Type type, Class<?> impl);

    @Override
    <T> ScopedProviderConfigurer addTransient(Class<T> type, Class<? extends T> impl);

    @Override
    <T> ScopedProviderConfigurer addTransient(JType<T> type, Class<? extends T> impl);

    @Override
    ScopedProviderConfigurer addTransient(Class<?> impl);

    @Override
    ScopedProviderConfigurer addSingleton(Type type, Class<?> impl);

    @Override
    <T> ScopedProviderConfigurer addSingleton(Class<T> type, Class<? extends T> impl);

    @Override
    <T> ScopedProviderConfigurer addSingleton(JType<T> type, Class<? extends T> impl);

    @Override
    ScopedProviderConfigurer addSingleton(Class<?> impl);
}
