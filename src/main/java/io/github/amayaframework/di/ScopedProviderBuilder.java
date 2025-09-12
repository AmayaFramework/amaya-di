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
public interface ScopedProviderBuilder extends ScopedProviderConfigurer, ServiceProviderBuilder {

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
    ScopedProviderBuilder withScopedRepository(Supplier<ScopedRepository> supplier);

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

    @Override
    ScopedProviderBuilder addScoped(Type type);

    @Override
    ScopedProviderBuilder addScoped(JType<?> type);

    @Override
    ScopedProviderBuilder addScoped(Type type, ObjectFactory factory);

    @Override
    ScopedProviderBuilder addScoped(JType<?> type, ObjectFactory factory);

    @Override
    ScopedProviderBuilder addScoped(Type type, ObjectFactory factory, ServiceWrapper wrapper);

    @Override
    ScopedProviderBuilder addScoped(JType<?> type, ObjectFactory factory, ServiceWrapper wrapper);

    @Override
    ScopedProviderBuilder addScoped(Type type, Function0<?> provider);

    @Override
    <T> ScopedProviderBuilder addScoped(JType<T> type, Function0<T> provider);

    @Override
    ScopedProviderBuilder addScoped(Type type, Function0<?> provider, ServiceWrapper wrapper);

    @Override
    <T> ScopedProviderBuilder addScoped(JType<T> type, Function0<T> provider, ServiceWrapper wrapper);

    @Override
    ScopedProviderBuilder addScopedInstance(Type type, Object instance);

    @Override
    <T> ScopedProviderBuilder addScopedInstance(JType<T> type, T instance);

    @Override
    ScopedProviderBuilder addScopedInstance(Object instance);

    @Override
    ScopedProviderBuilder removeScoped(Type type);

    @Override
    ScopedProviderBuilder removeScoped(JType<?> type);

    @Override
    ScopedProviderBuilder addScoped(Type type, Class<?> impl, ServiceWrapper wrapper);

    @Override
    <T> ScopedProviderBuilder addScoped(Class<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    @Override
    <T> ScopedProviderBuilder addScoped(JType<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    @Override
    ScopedProviderBuilder addScoped(Class<?> impl, ServiceWrapper wrapper);

    @Override
    ScopedProviderBuilder addScopedTransient(Type type, Class<?> impl);

    @Override
    <T> ScopedProviderBuilder addScopedTransient(Class<T> type, Class<? extends T> impl);

    @Override
    <T> ScopedProviderBuilder addScopedTransient(JType<T> type, Class<? extends T> impl);

    @Override
    ScopedProviderBuilder addScopedTransient(Class<?> impl);

    @Override
    ScopedProviderBuilder addScopedSingleton(Type type, Class<?> impl);

    @Override
    <T> ScopedProviderBuilder addScopedSingleton(Class<T> type, Class<? extends T> impl);

    @Override
    <T> ScopedProviderBuilder addScopedSingleton(JType<T> type, Class<? extends T> impl);

    @Override
    ScopedProviderBuilder addScopedSingleton(Class<?> impl);
}
