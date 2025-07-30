package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Exceptions;
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
import java.util.function.Supplier;

/**
 * An abstract implementation of {@link ServiceProviderBuilder} that provides
 * reusable logic for dependency registration and service provider construction.
 *
 * @param <B> the concrete builder type extending this abstract class
 */
public abstract class AbstractServiceProviderBuilder<B extends ServiceProviderBuilder> implements ServiceProviderBuilder {
    // Defaults
    /**
     * The default schema factory used when none is explicitly set.
     */
    protected final SchemaFactory defaultSchemaFactory;

    /**
     * The default stub factory used when none is explicitly set.
     */
    protected final StubFactory defaultStubFactory;

    /**
     * The default cache mode used for stub generation.
     */
    protected final CacheMode defaultCacheMode;

    // Overriding factories

    /**
     * A custom schema factory, overriding the default if provided.
     */
    protected SchemaFactory schemaFactory;

    /**
     * A custom stub factory, overriding the default if provided.
     */
    protected StubFactory stubFactory;

    // Cache mode
    /**
     * An optional override for the stub factory cache mode.
     */
    protected CacheMode cacheMode;

    // Repository
    /**
     * A supplier for lazy creation of the type repository.
     */
    protected Supplier<TypeRepository> repositorySupplier;

    /**
     * A pre-instantiated type repository.
     */
    protected TypeRepository repository;

    // Root types
    /**
     * Map of root type factories directly registered.
     */
    protected Map<Type, ObjectFactory> roots;
    // Other types
    /**
     * Map of implementation bindings to be stub-generated.
     */
    protected Map<Type, TypeEntry> types;

    /**
     * Creates a new builder with default factories and caching mode.
     *
     * @param schemaFactory the default schema factory
     * @param stubFactory   the default stub factory
     * @param cacheMode     the default caching mode
     */
    protected AbstractServiceProviderBuilder(SchemaFactory schemaFactory, StubFactory stubFactory, CacheMode cacheMode) {
        this.defaultSchemaFactory = schemaFactory;
        this.defaultStubFactory = stubFactory;
        this.defaultCacheMode = Objects.requireNonNull(cacheMode);
        this.reset();
    }

    /**
     * Validates that the implementation class is a subtype of the specified type.
     *
     * @param type the parent or interface type
     * @param impl the implementation class
     * @throws IllegalTypeException if the types are incompatible
     */
    protected static void checkInheritance(Class<?> type, Class<?> impl) {
        if (!type.isAssignableFrom(impl)) {
            throw new IllegalTypeException("The implementation is not a child class of the type " + type, impl);
        }
    }

    /**
     * Same as {@link #checkInheritance(Class, Class)} but for generic {@link Type}.
     *
     * @param type the type to check
     * @param impl the implementation class
     * @throws IllegalTypeException if the types are incompatible
     */
    protected static void checkInheritance(Type type, Class<?> impl) {
        checkInheritance(TypeUtil.getRawType(type), impl);
    }

    // Reset function

    /**
     * Resets the builder to its initial state, removing all bindings and overrides.
     */
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

    /**
     * Gets the configured or default {@link SchemaFactory}.
     *
     * @param required determines whether the result is strictly required, or whether it can be null
     * @return a schema factory instance
     * @throws IllegalStateException if none is available
     */
    protected SchemaFactory getSchemaFactory(boolean required) {
        if (schemaFactory != null) {
            return schemaFactory;
        }
        if (required && defaultSchemaFactory == null) {
            throw new IllegalStateException("No SchemaFactory is set or available by default");
        }
        return defaultSchemaFactory;
    }

    /**
     * Gets the configured or default {@link StubFactory}.
     *
     * @param required determines whether the result is strictly required, or whether it can be null
     * @return a stub factory instance
     * @throws IllegalStateException if none is available
     */
    protected StubFactory getStubFactory(boolean required) {
        if (stubFactory != null) {
            return stubFactory;
        }
        if (required && defaultStubFactory == null) {
            throw new IllegalStateException("No StubFactory is set or available by default");
        }
        return defaultStubFactory;
    }

    // Inner cache mode getter

    /**
     * Gets the configured or default {@link CacheMode}.
     *
     * @return a caching mode
     */
    protected CacheMode getCacheMode() {
        if (cacheMode != null) {
            return cacheMode;
        }
        return defaultCacheMode;
    }

    // Inner repository getters

    /**
     * Gets the configured repository or instantiates a new one.
     *
     * @return the type repository
     */
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
    public B addInstance(Type type, Object instance) {
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

    /**
     * Builds a stub factory for the given type entry using schema and caching strategy.
     * If a {@link CachedObjectFactory} is created, it is delayed for post-resolution wiring.
     *
     * @param entry   the type entry representing binding info
     * @param schema  the schema used to generate the factory
     * @param factory the stub factory
     * @param mode    the caching strategy
     * @param delayed list of delayed stubs to be finalized later
     * @return the created object factory
     */
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

    /**
     * Builds the final repository using all added bindings and stubs.
     * Populates it with direct and generated factories.
     *
     * @param repository     the repository to populate
     * @param schemaProvider the schema provider used to resolve class metadata
     * @param stubFactory    the stub factory used to create stubs
     * @param mode           the caching mode
     * @throws TypeNotFoundException if any type required by a stub is not found
     */
    protected void buildRepository(TypeRepository repository,
                                   SchemaProvider schemaProvider,
                                   StubFactory stubFactory,
                                   CacheMode mode) {
        // Add root types
        roots.forEach(repository::put);
        if (schemaProvider == null || stubFactory == null) {
            return;
        }
        // Add weak types
        var delayed = new LinkedList<StubEntry>();
        for (var entry : types.entrySet()) {
            var type = entry.getKey();
            var typeEntry = entry.getValue();
            // Build schema
            var schema = schemaProvider.get(type, typeEntry.impl);
            // Build stub
            var stub = buildStub(typeEntry, schema, stubFactory, mode, delayed);
            repository.put(type, stub);
        }
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

    /**
     * Internal build logic implemented by concrete builders.
     *
     * @return a new service provider
     * @throws Throwable if any error occurs during build
     */
    protected abstract ServiceProvider doBuild() throws Throwable;

    @Override
    public ServiceProvider build() {
        try {
            return doBuild();
        } catch (Throwable e) {
            Exceptions.throwAny(e);
            // Unreachable code to suppress javac error
            return null;
        } finally {
            this.reset();
        }
    }

    /**
     * Represents an implementation binding for a service type.
     * Includes implementation class and optional wrapper.
     */
    protected static final class TypeEntry {
        /**
         * The implementation class
         */
        protected Class<?> impl;
        /**
         * The wrapper to apply to the factory
         */
        protected ServiceWrapper wrapper;

        /**
         * Creates a new binding entry.
         *
         * @param impl    the implementation class
         * @param wrapper the factory wrapper to use
         */
        protected TypeEntry(Class<?> impl, ServiceWrapper wrapper) {
            this.impl = impl;
            this.wrapper = wrapper;
        }
    }
}
