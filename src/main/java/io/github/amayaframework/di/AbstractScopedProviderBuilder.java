package io.github.amayaframework.di;

import com.github.romanqed.jtype.JType;
import io.github.amayaframework.di.core.LazyObjectFactory;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;
import java.util.*;

public abstract class AbstractScopedProviderBuilder
        extends AbstractServiceProviderBuilder<ScopedProviderBuilder>
        implements ScopedProviderBuilder {
    // Promised root types
    protected Set<Type> promised;
    // Scoped root types
    protected Map<Type, ObjectFactory> scopedRoots;
    // Non-wrapped scoped types
    protected Map<Type, Class<?>> scopedTypes;
    // Wrapped scoped types
    protected Map<Type, ScopedTypeEntry> wrapped;

    protected AbstractScopedProviderBuilder(SchemaFactory schemaFactory, StubFactory stubFactory, CacheMode cacheMode) {
        super(schemaFactory, stubFactory, cacheMode);
    }

    @Override
    protected void reset() {
        super.reset();
        // Reset type maps
        this.promised = new HashSet<>();
        this.scopedRoots = new HashMap<>();
        this.scopedTypes = new HashMap<>();
        this.wrapped = new HashMap<>();
    }

    // Base scoped methods

    @Override
    public ScopedProviderBuilder addScoped(Type type) {
        Objects.requireNonNull(type);
        scopedRoots.remove(type);
        scopedTypes.remove(type);
        wrapped.remove(type);
        promised.add(type);
        return this;
    }

    @Override
    public ScopedProviderBuilder addScoped(Type type, ObjectFactory factory) {
        Objects.requireNonNull(type);
        scopedTypes.remove(type);
        wrapped.remove(type);
        if (factory == null) {
            scopedRoots.remove(type);
            promised.add(type);
        } else {
            promised.remove(type);
            scopedRoots.put(type, factory);
        }
        return this;
    }

    @Override
    public ScopedProviderBuilder addScoped(Type type, ObjectFactory factory, ServiceWrapper wrapper) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(factory);
        promised.remove(type);
        // noinspection DuplicatedCode
        scopedTypes.remove(type);
        if (wrapper == null) {
            wrapped.remove(type);
            scopedRoots.put(type, factory);
        } else {
            scopedRoots.remove(type);
            wrapped.put(type, new ScopedTypeEntry(factory, wrapper));
        }
        return this;
    }

    @Override
    public ScopedProviderBuilder removeScoped(Type type) {
        promised.remove(type);
        scopedRoots.remove(type);
        scopedTypes.remove(type);
        wrapped.remove(type);
        return this;
    }

    @Override
    public ScopedProviderBuilder addScoped(Type type, Class<?> impl, ServiceWrapper wrapper) {
        // noinspection DuplicatedCode
        Objects.requireNonNull(type);
        Objects.requireNonNull(impl);
        checkInheritance(type, impl);
        promised.remove(type);
        scopedRoots.remove(type);
        if (wrapper == null) {
            wrapped.remove(type);
            scopedTypes.put(type, impl);
        } else {
            scopedTypes.remove(type);
            wrapped.put(type, new ScopedTypeEntry(impl, wrapper));
        }
        return this;
    }

    @Override
    public <T> ScopedProviderBuilder addScoped(Class<T> type, Class<? extends T> impl, ServiceWrapper wrapper) {
        // noinspection DuplicatedCode
        Objects.requireNonNull(type);
        Objects.requireNonNull(impl);
        checkInheritance(type, impl);
        promised.remove(type);
        scopedRoots.remove(type);
        if (wrapper == null) {
            wrapped.remove(type);
            scopedTypes.put(type, impl);
        } else {
            scopedTypes.remove(type);
            wrapped.put(type, new ScopedTypeEntry(impl, wrapper));
        }
        return this;
    }

    @Override
    public <T> ScopedProviderBuilder addScoped(JType<T> type, Class<? extends T> impl, ServiceWrapper wrapper) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(impl);
        checkInheritance(type.getRawType(), impl);
        var complex = type.getType();
        promised.remove(complex);
        // noinspection DuplicatedCode
        scopedRoots.remove(complex);
        if (wrapper == null) {
            wrapped.remove(complex);
            scopedTypes.put(complex, impl);
        } else {
            scopedTypes.remove(complex);
            wrapped.put(complex, new ScopedTypeEntry(impl, wrapper));
        }
        return this;
    }

    @Override
    public ScopedProviderBuilder addScoped(Class<?> impl, ServiceWrapper wrapper) {
        // noinspection DuplicatedCode
        Objects.requireNonNull(impl);
        promised.remove(impl);
        scopedRoots.remove(impl);
        if (wrapper == null) {
            wrapped.remove(impl);
            scopedTypes.put(impl, impl);
        } else {
            scopedTypes.remove(impl);
            wrapped.put(impl, new ScopedTypeEntry(impl, wrapper));
        }
        return this;
    }

    // Proxy scoped methods


    @Override
    public ScopedProviderBuilder addScoped(JType<?> type) {
        return addScoped(type.getType());
    }

    @Override
    public ScopedProviderBuilder addScoped(JType<?> type, ObjectFactory factory) {
        return addScoped(type.getType(), factory);
    }

    @Override
    public ScopedProviderBuilder addScoped(JType<?> type, ObjectFactory factory, ServiceWrapper wrapper) {
        return addScoped(type.getType(), factory, wrapper);
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

    protected static final class ScopedTypeEntry {
        Class<?> impl;
        ObjectFactory factory;
        ServiceWrapper wrapper;

        protected ScopedTypeEntry(Class<?> impl, ServiceWrapper wrapper) {
            this.impl = impl;
            this.factory = null;
            this.wrapper = wrapper;
        }

        protected ScopedTypeEntry(ObjectFactory factory, ServiceWrapper wrapper) {
            this.factory = factory;
            this.impl = null;
            this.wrapper = wrapper;
        }
    }
}
