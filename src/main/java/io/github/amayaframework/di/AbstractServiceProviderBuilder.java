package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jtype.IllegalTypeException;
import com.github.romanqed.jtype.JType;
import com.github.romanqed.jtype.TypeUtil;
import io.github.amayaframework.di.core.*;
import io.github.amayaframework.di.schema.ClassSchema;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.CachedObjectFactory;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class AbstractServiceProviderBuilder<B extends ServiceProviderBuilder> implements ServiceProviderBuilder {
    // Defaults
    protected final SchemaFactory defaultSchemaFactory;
    protected final StubFactory defaultStubFactory;
    protected final CacheMode defaultCacheMode;

    // Overriding factories
    protected SchemaFactory schemaFactory;
    protected StubFactory stubFactory;

    // Cache mode
    protected CacheMode cacheMode;

    // Repository
    protected Supplier<TypeRepository> repositorySupplier;
    protected TypeRepository repository;

    // Root types
    protected Map<Type, ObjectFactory> roots;
    // Other types
    protected Map<Type, TypeEntry> types;

    protected AbstractServiceProviderBuilder(SchemaFactory schemaFactory, StubFactory stubFactory, CacheMode cacheMode) {
        this.defaultSchemaFactory = schemaFactory;
        this.defaultStubFactory = stubFactory;
        this.defaultCacheMode = Objects.requireNonNull(cacheMode);
        this.reset();
    }

    protected static void checkInheritance(Class<?> type, Class<?> impl) {
        if (!type.isAssignableFrom(impl)) {
            throw new IllegalTypeException("The implementation is not a child class of the type " + type, impl);
        }
    }

    protected static void checkInheritance(Type type, Class<?> impl) {
        checkInheritance(TypeUtil.getRawType(type), impl);
    }

    // Reset function
    protected void reset() {
        // Reset factories
        this.schemaFactory = null;
        this.stubFactory = null;
        // Reset cache mode
        this.cacheMode = null;
        // Reset repository
        this.repository = null;
        this.repositorySupplier = null;
        // Reset type maps
        this.roots = new HashMap<>();
        this.types = new HashMap<>();
    }

    // Inner factory getters
    protected SchemaFactory getSchemaFactory() {
        if (schemaFactory != null) {
            return schemaFactory;
        }
        if (defaultSchemaFactory == null) {
            throw new IllegalStateException("No SchemaFactory is set or available by default");
        }
        return defaultSchemaFactory;
    }

    protected StubFactory getStubFactory() {
        if (stubFactory != null) {
            return stubFactory;
        }
        if (defaultStubFactory == null) {
            throw new IllegalStateException("No StubFactory is set or available by default");
        }
        return defaultStubFactory;
    }

    // Inner cache mode getter
    protected CacheMode getCacheMode() {
        if (cacheMode != null) {
            return cacheMode;
        }
        return defaultCacheMode;
    }

    // Inner repository getters
    protected TypeRepository getRepository() {
        if (repository != null) {
            return repository;
        }
        if (repositorySupplier != null) {
            return repositorySupplier.get();
        }
        return new HashTypeRepository();
    }

    @Override
    @SuppressWarnings("unchecked")
    public B withSchemaFactory(SchemaFactory factory) {
        this.schemaFactory = factory;
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B withStubFactory(StubFactory factory) {
        this.stubFactory = factory;
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B withCacheMode(CacheMode mode) {
        this.cacheMode = mode;
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B withRepository(TypeRepository repository) {
        this.repositorySupplier = null;
        this.repository = repository;
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B withRepository(Supplier<TypeRepository> supplier) {
        this.repository = null;
        this.repositorySupplier = supplier;
        return (B) this;
    }

    // Base methods

    @Override
    @SuppressWarnings("unchecked")
    public B add(Type type, ObjectFactory factory) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(factory);
        types.remove(type);
        roots.put(type, factory);
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B remove(Type type) {
        types.remove(type);
        roots.remove(type);
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B add(Type type, Function0<?> provider) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(provider);
        types.remove(type);
        roots.put(type, v -> provider.invoke());
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> B add(Class<T> type, Function0<T> provider) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(provider);
        types.remove(type);
        roots.put(type, v -> provider.invoke());
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B addInstance(Type type, Object instance) {
        Objects.requireNonNull(type);
        types.remove(type);
        roots.put(type, v -> instance);
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> B addInstance(Class<T> type, T instance) {
        Objects.requireNonNull(type);
        types.remove(type);
        roots.put(type, v -> instance);
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B add(Type type, Class<?> impl, ServiceWrapper wrapper) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(impl);
        checkInheritance(type, impl);
        roots.remove(type);
        types.put(type, new TypeEntry(impl, wrapper));
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> B add(Class<T> type, Class<? extends T> impl, ServiceWrapper wrapper) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(impl);
        checkInheritance(type, impl);
        roots.remove(type);
        types.put(type, new TypeEntry(impl, wrapper));
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> B add(JType<T> type, Class<? extends T> impl, ServiceWrapper wrapper) {
        // noinspection DuplicatedCode
        Objects.requireNonNull(type);
        Objects.requireNonNull(impl);
        checkInheritance(type.getRawType(), impl);
        var complex = type.getType();
        roots.remove(complex);
        types.put(complex, new TypeEntry(impl, wrapper));
        return (B) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public B add(Class<?> impl, ServiceWrapper wrapper) {
        Objects.requireNonNull(impl);
        roots.remove(impl);
        types.put(impl, new TypeEntry(impl, wrapper));
        return (B) this;
    }

    // Proxy methods

    @Override
    public B add(JType<?> type, ObjectFactory factory) {
        return add(type.getType(), factory);
    }

    @Override
    public B remove(JType<?> type) {
        return remove(type.getType());
    }

    @Override
    public <T> B add(JType<T> type, Function0<T> provider) {
        return add(type.getType(), provider);
    }

    @Override
    public <T> B addInstance(JType<T> type, T instance) {
        return addInstance(type.getType(), instance);
    }

    @Override
    public B addInstance(Object instance) {
        return addInstance(instance.getClass(), instance);
    }

    @Override
    public B addTransient(Type type, Class<?> impl) {
        return add(type, impl, null);
    }

    @Override
    public <T> B addTransient(Class<T> type, Class<? extends T> impl) {
        return add(type, impl, null);
    }

    @Override
    public <T> B addTransient(JType<T> type, Class<? extends T> impl) {
        return add(type, impl, null);
    }

    @Override
    public B addTransient(Class<?> impl) {
        return add(impl, (ServiceWrapper) null);
    }

    @Override
    public B addSingleton(Type type, Class<?> impl) {
        return add(type, impl, LazyObjectFactory::new);
    }

    @Override
    public <T> B addSingleton(Class<T> type, Class<? extends T> impl) {
        return add(type, impl, LazyObjectFactory::new);
    }

    @Override
    public <T> B addSingleton(JType<T> type, Class<? extends T> impl) {
        return add(type, impl, LazyObjectFactory::new);
    }

    @Override
    public B addSingleton(Class<?> impl) {
        return add(impl, LazyObjectFactory::new);
    }

    // Utility methods

    protected ObjectFactory buildStub(TypeEntry entry,
                                      ClassSchema schema,
                                      StubFactory factory,
                                      CacheMode mode,
                                      List<StubEntry> delayed) {
        // Build stub and check if it is cached
        var stub = factory.create(schema, mode);
        if (stub instanceof CachedObjectFactory) {
            delayed.add(new StubEntry(schema.getTypes(), (CachedObjectFactory) stub));
        }
        // Apply wrapper
        if (entry.wrapper != null) {
            stub = entry.wrapper.wrap(stub);
        }
        return stub;
    }

    protected void buildRepository(TypeRepository repository,
                                   StubFactory stubFactory,
                                   BiFunction<Type, Class<?>, ClassSchema> schemaProvider,
                                   CacheMode mode) {
        // Add weak types
        var delayed = new LinkedList<StubEntry>();
        for (var entry : types.entrySet()) {
            var type = entry.getKey();
            var typeEntry = entry.getValue();
            // Build schema
            var schema = schemaProvider.apply(type, typeEntry.impl);
            // Build stub
            var stub = buildStub(typeEntry, schema, stubFactory, mode, delayed);
            repository.put(type, stub);
        }
        // Add root types
        roots.forEach(repository::put);
        // Handle delayed cached stubs
        for (var entry : delayed) {
            for (var type : entry.types) {
                var found = repository.get(type);
                if (found == null) {
                    throw new TypeNotFoundException(type);
                }
                entry.stub.set(type, found);
            }
        }
    }

    // Build methods

    protected abstract ServiceProvider doBuild() throws Throwable;

    @Override
    public ServiceProvider build() {
        try {
            return doBuild();
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            this.reset();
        }
    }

    protected static final class TypeEntry {
        Class<?> impl;
        ServiceWrapper wrapper;

        protected TypeEntry(Class<?> impl, ServiceWrapper wrapper) {
            this.impl = impl;
            this.wrapper = wrapper;
        }
    }
}
