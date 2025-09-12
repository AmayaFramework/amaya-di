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
 * Fluent builder for constructing a {@link ServiceProvider}.
 * <p>
 * Extends {@link ServiceProviderConfigurer}, so all configuration methods return
 * {@link ServiceProviderBuilder} for chaining. Supports registering services via raw
 * {@link ObjectFactory} instances, functional providers, prebuilt instances, or
 * implementation classes (with optional {@link ServiceWrapper}) and lifetime helpers
 * (transient/singleton).
 * <p>
 * Behavior highlights:
 * <ul>
 *   <li>If you register implementation classes, a {@link StubFactory} must be supplied;
 *       otherwise only direct factories/instances can be built.</li>
 *   <li>Singleton helpers use lazy instantiation. If a produced value implements the
 *       DI-core close contract ({@link io.github.amayaframework.di.core.Closeable}),
 *       it will be closed when the owning provider/scope is closed.</li>
 *   <li>If {@link #withScopedRepository(Supplier)} is provided, the resulting
 *       {@link ServiceProvider} will support {@code createScoped()} even for “plain”
 *       (non-scoped) builders.</li>
 *   <li>The builder is not thread-safe. Reuse across threads requires external synchronization.</li>
 * </ul>
 * <p>
 * Each call to {@link #build()} produces a new provider and then resets the internal
 * builder state so the instance can be reused for a different configuration.
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
     * Builds a new {@link ServiceProvider} from the current configuration and resets the builder.
     * <p>
     * The returned provider may support {@code createScoped()} if the underlying builder is scoped
     * or a {@link ScopedRepository} supplier was provided via {@link #withScopedRepository(Supplier)}.
     * <p>
     * If an error occurs during the build process, the builder is still reset.
     *
     * @return a newly constructed {@link ServiceProvider}
     * @throws RuntimeException if the configuration cannot be built
     * @throws Error            if a fatal error occurs during build
     */
    ServiceProvider build();
}
