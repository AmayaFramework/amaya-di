package io.github.amayaframework.di;

import com.github.romanqed.jtype.JType;
import io.github.amayaframework.di.core.LazyObjectFactory;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractScopedProviderBuilder
        extends AbstractServiceProviderBuilder<ScopedProviderBuilder>
        implements ScopedProviderBuilder {
    // Scoped root types
    protected Map<Type, ObjectFactory> scopedRoots;
    // Other scoped types
    protected Map<Type, TypeEntry> scopedTypes;

    protected AbstractScopedProviderBuilder(SchemaFactory schemaFactory, StubFactory stubFactory, CacheMode cacheMode) {
        super(schemaFactory, stubFactory, cacheMode);
    }

    @Override
    protected void reset() {
        super.reset();
        // Reset type maps
        this.scopedRoots = new HashMap<>();
        this.scopedTypes = new HashMap<>();
    }

    // Base scoped methods

    @Override
    public ScopedProviderBuilder addScoped(Type type, ObjectFactory factory) {
        Objects.requireNonNull(type);
        scopedTypes.remove(type);
        scopedRoots.put(type, factory);
        return this;
    }

    @Override
    public ScopedProviderBuilder removeScoped(Type type) {
        scopedRoots.remove(type);
        scopedTypes.remove(type);
        return this;
    }

    @Override
    public ScopedProviderBuilder addScoped(Type type, Class<?> impl, ServiceWrapper wrapper) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(impl);
        checkInheritance(type, impl);
        scopedRoots.remove(type);
        scopedTypes.put(type, new TypeEntry(impl, wrapper));
        return this;
    }

    @Override
    public <T> ScopedProviderBuilder addScoped(Class<T> type, Class<? extends T> impl, ServiceWrapper wrapper) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(impl);
        checkInheritance(type, impl);
        scopedRoots.remove(type);
        scopedTypes.put(type, new TypeEntry(impl, wrapper));
        return this;
    }

    @Override
    public <T> ScopedProviderBuilder addScoped(JType<T> type, Class<? extends T> impl, ServiceWrapper wrapper) {
        // noinspection DuplicatedCode
        Objects.requireNonNull(type);
        Objects.requireNonNull(impl);
        checkInheritance(type.getRawType(), impl);
        var complex = type.getType();
        scopedRoots.remove(complex);
        scopedTypes.put(complex, new TypeEntry(impl, wrapper));
        return this;
    }

    @Override
    public ScopedProviderBuilder addScoped(Class<?> impl, ServiceWrapper wrapper) {
        Objects.requireNonNull(impl);
        scopedRoots.remove(impl);
        scopedTypes.put(impl, new TypeEntry(impl, wrapper));
        return this;
    }

    // Proxy scoped methods

    @Override
    public ScopedProviderBuilder addScoped(Type type) {
        return addScoped(type, null);
    }

    @Override
    public ScopedProviderBuilder addScoped(JType<?> type) {
        return addScoped(type.getType(), null);
    }

    @Override
    public ScopedProviderBuilder addScoped(JType<?> type, ObjectFactory factory) {
        return addScoped(type.getType(), factory);
    }

    @Override
    public ScopedProviderBuilder removeScoped(JType<?> type) {
        return removeScoped(type.getType());
    }

    @Override
    public ScopedProviderBuilder addScopedTransient(Type type, Class<?> impl) {
        return addScoped(type, impl, null);
    }

    @Override
    public <T> ScopedProviderBuilder addScopedTransient(Class<T> type, Class<? extends T> impl) {
        return addScoped(type, impl, null);
    }

    @Override
    public <T> ScopedProviderBuilder addScopedTransient(JType<T> type, Class<? extends T> impl) {
        return addScoped(type, impl, null);
    }

    @Override
    public ScopedProviderBuilder addScopedTransient(Class<?> impl) {
        return addScoped(impl, (ServiceWrapper) null);
    }

    @Override
    public ScopedProviderBuilder addScopedSingleton(Type type, Class<?> impl) {
        return addScoped(type, impl, LazyObjectFactory::new);
    }

    @Override
    public <T> ScopedProviderBuilder addScopedSingleton(Class<T> type, Class<? extends T> impl) {
        return addScoped(type, impl, LazyObjectFactory::new);
    }

    @Override
    public <T> ScopedProviderBuilder addScopedSingleton(JType<T> type, Class<? extends T> impl) {
        return addScoped(type, impl, LazyObjectFactory::new);
    }

    @Override
    public ScopedProviderBuilder addScopedSingleton(Class<?> impl) {
        return addScoped(impl, LazyObjectFactory::new);
    }
}
