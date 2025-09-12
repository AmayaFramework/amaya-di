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
 * A builder for creating a scope-aware {@link io.github.amayaframework.di.core.ServiceProvider}.
 * <p>
 * In addition to all root (non-scoped) registrations available via {@link ServiceProviderBuilder},
 * this builder lets you declare and configure <em>scoped</em> services — dependencies that can be
 * resolved only inside scopes created from the resulting provider.
 * <p>
 * Supported scoped patterns:
 * <ul>
 *   <li><b>Promised types</b> – declared as required for a scope, but must be supplied externally
 *       at scope creation time via a {@link ScopedRepository}. Promised types are not resolvable at the root.</li>
 *   <li><b>Scoped factories</b> – factories stored in the builder and copied into each new scope.</li>
 *   <li><b>Wrapped scoped factories</b> – a factory plus {@link ServiceWrapper}; the wrapper is applied
 *       per scope (not at build time), enabling per-scope decoration/proxying.</li>
 *   <li><b>Scoped transient</b> – a new instance is created on every resolve within a scope.</li>
 *   <li><b>Scoped singleton</b> – one lazily-created instance <em>per scope</em>; if the produced value
 *       implements {@link io.github.amayaframework.di.core.Closeable}, it will be closed when the scope closes.</li>
 *   <li><b>Scoped instance</b> – a fixed instance exposed to all scopes (shared across scopes).</li>
 * </ul>
 * Wrappers registered for scoped types are applied each time a new scope is created.
 * <p>
 * Thread-safety: this builder is not thread-safe. A successful {@link #build()} resets internal state,
 * just like {@link ServiceProviderBuilder#build()}.
 *
 * @see ServiceProviderBuilder
 * @see ScopedProviderConfigurer
 * @see io.github.amayaframework.di.core.ServiceProvider
 * @see ScopedRepository
 * @see io.github.amayaframework.di.core.Closeable
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
