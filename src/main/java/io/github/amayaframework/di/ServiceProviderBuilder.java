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

public interface ServiceProviderBuilder {

    ServiceProviderBuilder withSchemaFactory(SchemaFactory factory);

    ServiceProviderBuilder withStubFactory(StubFactory factory);

    ServiceProviderBuilder withCacheMode(CacheMode mode);

    ServiceProviderBuilder withRepository(TypeRepository repository);

    ServiceProviderBuilder withRepository(Supplier<TypeRepository> supplier);

    // Plain add methods

    ServiceProviderBuilder add(Type type, ObjectFactory factory);

    ServiceProviderBuilder add(JType<?> type, ObjectFactory factory);

    // ===

    // Common remove methods

    ServiceProviderBuilder remove(Type type);

    ServiceProviderBuilder remove(JType<?> type);

    // ===

    // Function0 wrapped add methods

    ServiceProviderBuilder add(Type type, Function0<?> provider);

    <T> ServiceProviderBuilder add(Class<T> type, Function0<T> provider);

    <T> ServiceProviderBuilder add(JType<T> type, Function0<T> provider);

    // ===

    // Instance add methods

    ServiceProviderBuilder addInstance(Type type, Object instance);

    <T> ServiceProviderBuilder addInstance(Class<T> type, T instance);

    <T> ServiceProviderBuilder addInstance(JType<T> type, T instance);

    ServiceProviderBuilder addInstance(Object instance);

    // ===

    // Stubbed add methods with wrapper (the ObjectFactory implementation will be created by StubFactory)

    ServiceProviderBuilder add(Type type, Class<?> impl, ServiceWrapper wrapper);

    <T> ServiceProviderBuilder add(Class<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    <T> ServiceProviderBuilder add(JType<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    ServiceProviderBuilder add(Class<?> impl, ServiceWrapper wrapper);

    // ===

    // Stubbed transient add methods (the ObjectFactory implementation will be created by StubFactory)

    ServiceProviderBuilder addTransient(Type type, Class<?> impl);

    <T> ServiceProviderBuilder addTransient(Class<T> type, Class<? extends T> impl);

    <T> ServiceProviderBuilder addTransient(JType<T> type, Class<? extends T> impl);

    ServiceProviderBuilder addTransient(Class<?> impl);

    // ===

    // Stubbed singleton add methods (the ObjectFactory implementation will be created by StubFactory)

    ServiceProviderBuilder addSingleton(Type type, Class<?> impl);

    <T> ServiceProviderBuilder addSingleton(Class<T> type, Class<? extends T> impl);

    <T> ServiceProviderBuilder addSingleton(JType<T> type, Class<? extends T> impl);

    ServiceProviderBuilder addSingleton(Class<?> impl);

    // ===

    ServiceProvider build();
}
