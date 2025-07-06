package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jtype.JType;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeRepository;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;
import java.util.function.Supplier;

/**
 * A builder for creating service providers with support for scoped services.
 * <p>
 * Scoped services are services that exist only within a scope.
 * They are accessible exclusively inside the scope they belong to.
 * <p>
 * This builder supports registering both actual scoped service implementations
 * and "promised" scoped types — types declared as required but expected to be
 * provided externally when the scope is created.
 * <p>
 * All wrapped types registered with a {@link ServiceWrapper} will have their wrapper
 * applied each time a new scope is created.
 * <p>
 * This interface extends {@link ServiceProviderBuilder}, so it supports
 * all service registrations available there as well.
 *
 * @see ServiceProviderBuilder
 */
public interface ScopedProviderBuilder extends ServiceProviderBuilder {

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
    ScopedProviderBuilder addScoped(Type type);

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
    ScopedProviderBuilder addScoped(JType<?> type);

    /**
     * Registers a scoped type with a factory to create instances inside scopes.
     * The factory is used every time a new scope is created.
     * If the factory is null, the type is registered as promised.
     *
     * @param type    the type to register as scoped
     * @param factory the factory to create scoped instances, or null for promised
     * @return this builder instance for chaining
     */
    ScopedProviderBuilder addScoped(Type type, ObjectFactory factory);

    /**
     * Registers a scoped type with a factory to create instances inside scopes.
     * The factory is used every time a new scope is created.
     * If the factory is null, the type is registered as promised.
     *
     * @param type    the type to register as scoped
     * @param factory the factory to create scoped instances, or null for promised
     * @return this builder instance for chaining
     */
    ScopedProviderBuilder addScoped(JType<?> type, ObjectFactory factory);

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
    ScopedProviderBuilder addScoped(Type type, ObjectFactory factory, ServiceWrapper wrapper);

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
    ScopedProviderBuilder addScoped(JType<?> type, ObjectFactory factory, ServiceWrapper wrapper);

    /**
     * Registers a scoped type with a provider function to create instances inside scopes.
     *
     * @param type     the type to register as scoped
     * @param provider the function to provide service instances
     * @return this builder instance for chaining
     */
    ScopedProviderBuilder addScoped(Type type, Function0<?> provider);

    /**
     * Registers a scoped type with a provider function to create instances inside scopes.
     *
     * @param type     the type to register as scoped
     * @param provider the function to provide service instances
     * @param <T>      the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderBuilder addScoped(JType<T> type, Function0<T> provider);

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
    ScopedProviderBuilder addScoped(Type type, Function0<?> provider, ServiceWrapper wrapper);

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
    <T> ScopedProviderBuilder addScoped(JType<T> type, Function0<T> provider, ServiceWrapper wrapper);

    /**
     * Registers a scoped type with a specific instance.
     * The same instance will be provided inside all scopes.
     *
     * @param type     the type to register as scoped
     * @param instance the instance to provide for the scoped type
     * @return this builder instance for chaining
     */
    ScopedProviderBuilder addScopedInstance(Type type, Object instance);

    /**
     * Registers a scoped type with a specific instance.
     * The same instance will be provided inside all scopes.
     *
     * @param type     the type to register as scoped
     * @param instance the instance to provide for the scoped type
     * @param <T>      the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderBuilder addScopedInstance(JType<T> type, T instance);

    /**
     * Registers an instance as scoped by its concrete class.
     * The same instance will be provided inside all scopes.
     *
     * @param instance the instance to register as scoped
     * @return this builder instance for chaining
     */
    ScopedProviderBuilder addScopedInstance(Object instance);

    /**
     * Removes the specified scoped type registration,
     * whether promised, with factory, or wrapped.
     *
     * @param type the type to remove from scoped registrations
     * @return this builder instance for chaining
     */
    ScopedProviderBuilder removeScoped(Type type);

    /**
     * Removes the specified scoped type registration,
     * whether promised, with factory, or wrapped.
     *
     * @param type the type to remove from scoped registrations
     * @return this builder instance for chaining
     */
    ScopedProviderBuilder removeScoped(JType<?> type);

    /**
     * Registers a scoped type by interface/class with an implementation class and a service wrapper.
     * The wrapper will be applied for every new scope.
     *
     * @param type    the interface or supertype to register as scoped
     * @param impl    the implementation class to instantiate inside scopes
     * @param wrapper the wrapper to apply on the implementation factory per scope
     * @return this builder instance for chaining
     */
    ScopedProviderBuilder addScoped(Type type, Class<?> impl, ServiceWrapper wrapper);

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
    <T> ScopedProviderBuilder addScoped(Class<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

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
    <T> ScopedProviderBuilder addScoped(JType<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    /**
     * Registers a scoped implementation class with a service wrapper.
     * The wrapper will be applied for every new scope.
     *
     * @param impl    the implementation class to register as scoped
     * @param wrapper the wrapper to apply on the implementation factory per scope
     * @return this builder instance for chaining
     */
    ScopedProviderBuilder addScoped(Class<?> impl, ServiceWrapper wrapper);

    /**
     * Registers a scoped transient type with implementation.
     * Equivalent to registering without a wrapper.
     *
     * @param type the type to register as scoped transient
     * @param impl the implementation class
     * @return this builder instance for chaining
     */
    ScopedProviderBuilder addScopedTransient(Type type, Class<?> impl);

    /**
     * Registers a scoped transient type with implementation.
     * Equivalent to registering without a wrapper.
     *
     * @param type the type to register as scoped transient
     * @param impl the implementation class
     * @param <T>  the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderBuilder addScopedTransient(Class<T> type, Class<? extends T> impl);

    /**
     * Registers a scoped transient generic type with implementation.
     * Equivalent to registering without a wrapper.
     *
     * @param type the generic type to register as scoped transient
     * @param impl the implementation class
     * @param <T>  the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderBuilder addScopedTransient(JType<T> type, Class<? extends T> impl);

    /**
     * Registers a scoped transient implementation class.
     * Equivalent to registering without a wrapper.
     *
     * @param impl the implementation class to register as scoped transient
     * @return this builder instance for chaining
     */
    ScopedProviderBuilder addScopedTransient(Class<?> impl);

    /**
     * Registers a scoped singleton type with implementation and wraps it.
     *
     * @param type the type to register as scoped singleton
     * @param impl the implementation class
     * @return this builder instance for chaining
     */
    ScopedProviderBuilder addScopedSingleton(Type type, Class<?> impl);

    /**
     * Registers a scoped singleton type with implementation and wraps it.
     *
     * @param type the type to register as scoped singleton
     * @param impl the implementation class
     * @param <T>  the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderBuilder addScopedSingleton(Class<T> type, Class<? extends T> impl);


    /**
     * Registers a scoped singleton generic type with implementation and wraps it.
     *
     * @param type the generic type to register as scoped singleton
     * @param impl the implementation class
     * @param <T>  the type parameter
     * @return this builder instance for chaining
     */
    <T> ScopedProviderBuilder addScopedSingleton(JType<T> type, Class<? extends T> impl);

    /**
     * Registers a scoped singleton implementation class and wraps it.
     *
     * @param impl the implementation class to register as scoped singleton
     * @return this builder instance for chaining
     */
    ScopedProviderBuilder addScopedSingleton(Class<?> impl);

    // api fixes

    @Override
    ScopedProviderBuilder withSchemaFactory(SchemaFactory factory);

    @Override
    ScopedProviderBuilder withStubFactory(StubFactory factory);

    @Override
    ScopedProviderBuilder withCacheMode(CacheMode mode);

    @Override
    ScopedProviderBuilder withRepository(TypeRepository repository);

    @Override
    ScopedProviderBuilder withRepository(Supplier<TypeRepository> supplier);

    @Override
    ScopedProviderBuilder add(Type type, ObjectFactory factory);

    @Override
    ScopedProviderBuilder add(JType<?> type, ObjectFactory factory);

    @Override
    ScopedProviderBuilder remove(Type type);

    @Override
    ScopedProviderBuilder remove(JType<?> type);

    @Override
    ScopedProviderBuilder add(Type type, Function0<?> provider);

    @Override
    <T> ScopedProviderBuilder add(JType<T> type, Function0<T> provider);

    @Override
    ScopedProviderBuilder addInstance(Type type, Object instance);

    @Override
    <T> ScopedProviderBuilder addInstance(JType<T> type, T instance);

    @Override
    ScopedProviderBuilder addInstance(Object instance);

    @Override
    ScopedProviderBuilder add(Type type, Class<?> impl, ServiceWrapper wrapper);

    @Override
    <T> ScopedProviderBuilder add(Class<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    @Override
    <T> ScopedProviderBuilder add(JType<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    @Override
    ScopedProviderBuilder add(Class<?> impl, ServiceWrapper wrapper);

    @Override
    ScopedProviderBuilder addTransient(Type type, Class<?> impl);

    @Override
    <T> ScopedProviderBuilder addTransient(Class<T> type, Class<? extends T> impl);

    @Override
    <T> ScopedProviderBuilder addTransient(JType<T> type, Class<? extends T> impl);

    @Override
    ScopedProviderBuilder addTransient(Class<?> impl);

    @Override
    ScopedProviderBuilder addSingleton(Type type, Class<?> impl);

    @Override
    <T> ScopedProviderBuilder addSingleton(Class<T> type, Class<? extends T> impl);

    @Override
    <T> ScopedProviderBuilder addSingleton(JType<T> type, Class<? extends T> impl);

    @Override
    ScopedProviderBuilder addSingleton(Class<?> impl);
}
