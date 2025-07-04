package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jtype.JType;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeRepository;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;

public interface ScopedProviderBuilder extends ServiceProviderBuilder {

    // ===

    // Scoped no-body add methods (a "promise" to the resolver that scopes will have implementations)

    ScopedProviderBuilder addScoped(Type type);

    ScopedProviderBuilder addScoped(JType<?> type);

    // ===

    // Scoped add methods (the dependency will only exist inside the scope)

    ScopedProviderBuilder addScoped(Type type, ObjectFactory factory);

    ScopedProviderBuilder addScoped(JType<?> type, ObjectFactory factory);

    // ===

    // Scoped add methods with wrapper

    ScopedProviderBuilder addScoped(Type type, ObjectFactory factory, ServiceWrapper wrapper);

    ScopedProviderBuilder addScoped(JType<?> type, ObjectFactory factory, ServiceWrapper wrapper);

    // ===

    // Scoped remove methods

    ScopedProviderBuilder removeScoped(Type type);

    ScopedProviderBuilder removeScoped(JType<?> type);

    // ===

    // Scoped and stubbed add methods with wrapper (the ObjectFactory implementation will be created by StubFactory)

    ScopedProviderBuilder addScoped(Type type, Class<?> impl, ServiceWrapper wrapper);

    <T> ScopedProviderBuilder addScoped(Class<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    <T> ScopedProviderBuilder addScoped(JType<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    ScopedProviderBuilder addScoped(Class<?> impl, ServiceWrapper wrapper);

    // ===

    // Scoped and stubbed transient add methods (the ObjectFactory implementation will be created by StubFactory)

    ScopedProviderBuilder addScopedTransient(Type type, Class<?> impl);

    <T> ScopedProviderBuilder addScopedTransient(Class<T> type, Class<? extends T> impl);

    <T> ScopedProviderBuilder addScopedTransient(JType<T> type, Class<? extends T> impl);

    ScopedProviderBuilder addScopedTransient(Class<?> impl);

    // ===

    // Scoped and stubbed singleton add methods (the ObjectFactory implementation will be created by StubFactory)

    ScopedProviderBuilder addScopedSingleton(Type type, Class<?> impl);

    <T> ScopedProviderBuilder addScopedSingleton(Class<T> type, Class<? extends T> impl);

    <T> ScopedProviderBuilder addScopedSingleton(JType<T> type, Class<? extends T> impl);

    ScopedProviderBuilder addScopedSingleton(Class<?> impl);

    // ===

    @Override
    ScopedProviderBuilder withSchemaFactory(SchemaFactory factory);

    @Override
    ScopedProviderBuilder withStubFactory(StubFactory factory);

    @Override
    ScopedProviderBuilder withCacheMode(CacheMode mode);

    @Override
    ScopedProviderBuilder withRepository(TypeRepository repository);

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
    <T> ScopedProviderBuilder add(Class<T> type, Function0<T> provider);

    @Override
    <T> ScopedProviderBuilder add(JType<T> type, Function0<T> provider);

    @Override
    ScopedProviderBuilder addInstance(Type type, Object instance);

    @Override
    <T> ScopedProviderBuilder addInstance(Class<T> type, T instance);

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
