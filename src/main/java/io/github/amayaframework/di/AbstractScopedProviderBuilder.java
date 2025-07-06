package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jtype.JType;
import io.github.amayaframework.di.core.LazyObjectFactory;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.TypeRepository;
import io.github.amayaframework.di.schema.ClassSchema;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.CachedObjectFactory;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;
import java.util.*;

public abstract class AbstractScopedProviderBuilder<B extends ScopedProviderBuilder>
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
    @SuppressWarnings("unchecked")
    public B addScoped(Type type) {
        Objects.requireNonNull(type);
        scopedRoots.remove(type);
        scopedTypes.remove(type);
        wrapped.remove(type);
        promised.add(type);
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B addScoped(Type type, ObjectFactory factory) {
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
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B addScoped(Type type, ObjectFactory factory, ServiceWrapper wrapper) {
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
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B addScoped(Type type, Function0<?> provider) {
        // noinspection DuplicatedCode
        Objects.requireNonNull(type);
        Objects.requireNonNull(provider);
        promised.remove(type);
        scopedTypes.remove(type);
        wrapped.remove(type);
        scopedRoots.put(type, v -> provider.invoke());
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B addScoped(Type type, Function0<?> provider, ServiceWrapper wrapper) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(provider);
        promised.remove(type);
        scopedTypes.remove(type);
        if (wrapper == null) {
            wrapped.remove(type);
            scopedRoots.put(type, v -> provider.invoke());
        } else {
            scopedRoots.remove(type);
            wrapped.put(type, new ScopedTypeEntry(v -> provider.invoke(), wrapper));
        }
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B addScopedInstance(Type type, Object instance) {
        Objects.requireNonNull(type);
        promised.remove(type);
        scopedTypes.remove(type);
        wrapped.remove(type);
        scopedRoots.put(type, v -> instance);
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B removeScoped(Type type) {
        promised.remove(type);
        scopedRoots.remove(type);
        scopedTypes.remove(type);
        wrapped.remove(type);
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B addScoped(Type type, Class<?> impl, ServiceWrapper wrapper) {
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
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> B addScoped(Class<T> type, Class<? extends T> impl, ServiceWrapper wrapper) {
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
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> B addScoped(JType<T> type, Class<? extends T> impl, ServiceWrapper wrapper) {
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
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B addScoped(Class<?> impl, ServiceWrapper wrapper) {
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
        return (B) this;
    }

    // Proxy scoped methods

    @Override
    public B addScoped(JType<?> type) {
        return addScoped(type.getType());
    }

    @Override
    public B addScoped(JType<?> type, ObjectFactory factory) {
        return addScoped(type.getType(), factory);
    }

    @Override
    public B addScoped(JType<?> type, ObjectFactory factory, ServiceWrapper wrapper) {
        return addScoped(type.getType(), factory, wrapper);
    }

    @Override
    public <T> B addScoped(JType<T> type, Function0<T> provider) {
        return addScoped(type.getType(), provider);
    }

    @Override
    public <T> B addScoped(JType<T> type, Function0<T> provider, ServiceWrapper wrapper) {
        return addScoped(type.getType(), provider, wrapper);
    }

    @Override
    public <T> B addScopedInstance(JType<T> type, T instance) {
        return addScopedInstance(type.getType(), instance);
    }

    @Override
    public B addScopedInstance(Object instance) {
        return addScopedInstance(instance.getClass(), instance);
    }

    @Override
    public B removeScoped(JType<?> type) {
        return removeScoped(type.getType());
    }

    @Override
    public B addScopedTransient(Type type, Class<?> impl) {
        return addScoped(type, impl, null);
    }

    @Override
    public <T> B addScopedTransient(Class<T> type, Class<? extends T> impl) {
        return addScoped(type, impl, null);
    }

    @Override
    public <T> B addScopedTransient(JType<T> type, Class<? extends T> impl) {
        return addScoped(type, impl, null);
    }

    @Override
    public B addScopedTransient(Class<?> impl) {
        return addScoped(impl, (ServiceWrapper) null);
    }

    @Override
    public B addScopedSingleton(Type type, Class<?> impl) {
        return addScoped(type, impl, LazyObjectFactory::new);
    }

    @Override
    public <T> B addScopedSingleton(Class<T> type, Class<? extends T> impl) {
        return addScoped(type, impl, LazyObjectFactory::new);
    }

    @Override
    public <T> B addScopedSingleton(JType<T> type, Class<? extends T> impl) {
        return addScoped(type, impl, LazyObjectFactory::new);
    }

    @Override
    public B addScopedSingleton(Class<?> impl) {
        return addScoped(impl, LazyObjectFactory::new);
    }

    // Utility methods

    protected CacheMode deduceCacheMode(ClassSchema schema, CacheMode mode, Set<Type> cached) {
        // Max safe cache mode is CacheMode.PARTIAL
        var types = schema.getTypes();
        for (var type : types) {
            // Skip all overwritten types
            if (promised.contains(type)
                    || scopedRoots.containsKey(type)
                    || scopedTypes.containsKey(type)
                    || wrapped.containsKey(type)) {
                mode = CacheMode.PARTIAL;
                continue;
            }
            cached.add(type);
        }
        return cached.isEmpty() ? CacheMode.NONE : mode;
    }

    @Override
    protected ObjectFactory buildStub(TypeEntry entry,
                                      ClassSchema schema,
                                      StubFactory factory,
                                      CacheMode mode,
                                      List<StubEntry> delayed) {
        // If we have wrapper, then just build stub with given cache mode
        if (entry.wrapper != null) {
            var stub = factory.create(schema, mode);
            if (stub instanceof CachedObjectFactory) {
                delayed.add(new StubEntry(schema.getTypes(), (CachedObjectFactory) stub));
            }
            return entry.wrapper.wrap(stub);
        }
        // Otherwise, we must deduce cache mode
        // If type depends on type redefined by scope, maximum cache mode is partial
        if (mode == CacheMode.NONE) {
            return factory.create(schema, CacheMode.NONE);
        }
        var cached = new HashSet<Type>();
        var deduced = deduceCacheMode(schema, mode, cached);
        var stub = factory.create(schema, deduced);
        if (stub instanceof CachedObjectFactory) {
            delayed.add(new StubEntry(cached, (CachedObjectFactory) stub));
        }
        return stub;
    }

    protected CacheMode deduceCacheMode(ClassSchema schema, CacheMode mode) {
        // Max safe cache mode is CacheMode.PARTIAL
        if (mode != CacheMode.FULL) {
            return mode;
        }
        var types = schema.getTypes();
        for (var type : types) {
            if (promised.contains(type) || wrapped.containsKey(type)) {
                return CacheMode.PARTIAL;
            }
        }
        return CacheMode.FULL;
    }

    protected boolean noScoped() {
        return promised.isEmpty() && scopedRoots.isEmpty() && scopedTypes.isEmpty() && wrapped.isEmpty();
    }

    @SuppressWarnings("unchecked")
    protected Map<Type, ObjectFactory> buildScoped(SchemaProvider schemaProvider,
                                                   StubFactory stubFactory,
                                                   List<StubEntry> delayed,
                                                   CacheMode mode) {
        if (scopedRoots.isEmpty() && scopedTypes.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        // Add scoped weak types
        var ret = new HashMap<Type, ObjectFactory>();
        for (var entry : scopedTypes.entrySet()) {
            var type = entry.getKey();
            // Build schema
            var schema = schemaProvider.get(type, entry.getValue());
            // Build stub and check if it is cached
            var deduced = deduceCacheMode(schema, mode);
            var stub = stubFactory.create(schema, deduced);
            if (stub instanceof CachedObjectFactory) {
                delayed.add(new StubEntry(schema.getTypes(), (CachedObjectFactory) stub));
            }
            ret.put(type, stub);
        }
        // Add root types
        ret.putAll(scopedRoots);
        return ret;
    }

    @SuppressWarnings("unchecked")
    protected Map<Type, WrappedEntry> buildWrapped(SchemaProvider schemaProvider,
                                                   StubFactory stubFactory,
                                                   List<StubEntry> delayed,
                                                   CacheMode mode) {
        if (wrapped.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        var ret = new HashMap<Type, WrappedEntry>();
        for (var wrappedEntry : wrapped.entrySet()) {
            var type = wrappedEntry.getKey();
            var entry = wrappedEntry.getValue();
            // Handle wrapped root type
            if (entry.impl == null) {
                ret.put(type, new WrappedEntry(entry.factory, entry.wrapper));
                continue;
            }
            // Build schema
            var schema = schemaProvider.get(type, entry.impl);
            // Build stub and check it
            var deduced = deduceCacheMode(schema, mode);
            var stub = stubFactory.create(schema, deduced);
            if (stub instanceof CachedObjectFactory) {
                delayed.add(new StubEntry(schema.getTypes(), (CachedObjectFactory) stub));
            }
            ret.put(type, new WrappedEntry(stub, entry.wrapper));
        }
        return ret;
    }

    protected ObjectFactory findType(Type type, Map<Type, ObjectFactory> scoped, TypeRepository repository) {
        if (promised.contains(type)) {
            return null;
        }
        if (wrapped.containsKey(type)) {
            return null;
        }
        var ret = scoped.get(type);
        if (ret != null) {
            return ret;
        }
        return repository.get(type);
    }

    protected void handleDelayed(List<StubEntry> delayed, Map<Type, ObjectFactory> scoped, TypeRepository repository) {
        for (var entry : delayed) {
            for (var type : entry.types) {
                entry.stub.set(type, findType(type, scoped, repository));
            }
        }
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
