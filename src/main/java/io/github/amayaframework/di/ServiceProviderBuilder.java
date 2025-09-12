package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jtype.JType;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.ScopedRepository;
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
public interface ServiceProviderBuilder extends ServiceProviderConfigurer {

    @Override
    void reset();

    @Override
    ServiceProviderBuilder withSchemaFactory(SchemaFactory factory);

    @Override
    ServiceProviderBuilder withStubFactory(StubFactory factory);

    @Override
    ServiceProviderBuilder withCacheMode(CacheMode mode);

    @Override
    ServiceProviderBuilder withRepository(TypeRepository repository);

    @Override
    ServiceProviderBuilder withRepository(Supplier<TypeRepository> supplier);

    @Override
    ServiceProviderBuilder withScopedRepository(Supplier<ScopedRepository> supplier);

    @Override
    ServiceProviderBuilder add(Type type, ObjectFactory factory);

    @Override
    ServiceProviderBuilder add(JType<?> type, ObjectFactory factory);

    @Override
    ServiceProviderBuilder remove(Type type);

    @Override
    ServiceProviderBuilder add(Type type, Function0<?> provider);

    @Override
    ServiceProviderBuilder remove(JType<?> type);

    @Override
    <T> ServiceProviderBuilder add(JType<T> type, Function0<T> provider);

    @Override
    ServiceProviderBuilder addInstance(Type type, Object instance);

    @Override
    <T> ServiceProviderBuilder addInstance(JType<T> type, T instance);

    @Override
    <T> ServiceProviderBuilder add(Class<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    @Override
    ServiceProviderBuilder addInstance(Object instance);

    @Override
    ServiceProviderBuilder add(Type type, Class<?> impl, ServiceWrapper wrapper);

    @Override
    <T> ServiceProviderBuilder add(JType<T> type, Class<? extends T> impl, ServiceWrapper wrapper);

    @Override
    ServiceProviderBuilder add(Class<?> impl, ServiceWrapper wrapper);

    @Override
    ServiceProviderBuilder addTransient(Type type, Class<?> impl);

    @Override
    <T> ServiceProviderBuilder addTransient(Class<T> type, Class<? extends T> impl);

    @Override
    <T> ServiceProviderBuilder addTransient(JType<T> type, Class<? extends T> impl);

    @Override
    ServiceProviderBuilder addTransient(Class<?> impl);

    @Override
    ServiceProviderBuilder addSingleton(Type type, Class<?> impl);

    @Override
    <T> ServiceProviderBuilder addSingleton(Class<T> type, Class<? extends T> impl);

    @Override
    <T> ServiceProviderBuilder addSingleton(JType<T> type, Class<? extends T> impl);

    @Override
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
