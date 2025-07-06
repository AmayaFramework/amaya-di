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

/**
 * Abstract base implementation of {@link ScopedProviderBuilder} that
 * supports registration and management of scoped service types.
 * <p>
 * Scoped services exist only inside a scope. Types can be registered
 * as promised (declared but not implemented at build time), or with
 * factories, providers, instances, and wrappers.
 * <p>
 * Wrappers registered for scoped types will be applied to the factory
 * creating instances every time a new scope is created.
 *
 * @param <B> the concrete builder type extending this abstract builder
 */
public abstract class AbstractScopedProviderBuilder<B extends ScopedProviderBuilder>
        extends AbstractServiceProviderBuilder<ScopedProviderBuilder>
        implements ScopedProviderBuilder {

    /**
     * Set of promised scoped types.
     * These types are declared as present, but implementations are expected
     * to be provided externally when creating actual scopes.
     */
    protected Set<Type> promised;

    /**
     * Map of scoped root types with their associated factories.
     * Factories create instances inside each new scope.
     */
    protected Map<Type, ObjectFactory> scopedRoots;

    /**
     * Map of scoped types registered by class without wrappers.
     * Used to track implementations registered as scoped types.
     */
    protected Map<Type, Class<?>> scopedTypes;

    /**
     * Map of scoped types registered with wrappers.
     * The wrapper is applied to the factory every time a new scope is created.
     */
    protected Map<Type, ScopedTypeEntry> wrapped;

    /**
     * Constructs a new AbstractScopedProviderBuilder.
     *
     * @param schemaFactory the schema factory to build type schemas
     * @param stubFactory   the stub factory to create stub object factories
     * @param cacheMode     the cache mode used for created object factories
     */
    protected AbstractScopedProviderBuilder(SchemaFactory schemaFactory, StubFactory stubFactory, CacheMode cacheMode) {
        super(schemaFactory, stubFactory, cacheMode);
    }

    /**
     * Resets the builder state, clearing all registered scoped types,
     * promised types, and wrappers.
     */
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

    /**
     * Determines the appropriate cache mode for the given {@link ClassSchema} based on
     * the initial mode, considering scoped and wrapped types, while also collecting
     * types that should be cached into the provided set.
     *
     * <p>For each type in the schema:
     * - If it is a promised, scoped root, scoped type, or wrapped type,
     * the cache mode is set to {@link CacheMode#PARTIAL} and the type is skipped from caching.
     * - Otherwise, the type is added to the {@code cached} set to be cached.</p>
     *
     * <p>If after processing all types the {@code cached} set is empty,
     * the method returns {@link CacheMode#NONE} indicating no caching is needed.
     * Otherwise, it returns the (possibly downgraded) cache mode.</p>
     *
     * @param schema the {@link ClassSchema} containing types to check
     * @param mode   the initial cache mode to consider and potentially downgrade
     * @param cached a {@link Set} to collect types that should be cached
     * @return the deduced cache mode, possibly {@link CacheMode#NONE} if no types are cached
     */
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

    /**
     * Determines the appropriate cache mode for the given {@link ClassSchema} based on
     * the provided initial cache mode and the presence of promised or wrapped types.
     *
     * <p>If the initial mode is not {@link CacheMode#FULL}, it is returned unchanged.
     * Otherwise, if any type in the schema is either a promised type or wrapped type,
     * the cache mode is downgraded to {@link CacheMode#PARTIAL}.
     * If no such types are found, the cache mode remains {@link CacheMode#FULL}.</p>
     *
     * @param schema the {@link ClassSchema} containing types to check
     * @param mode   the initial cache mode to consider
     * @return the deduced cache mode, potentially downgraded to {@link CacheMode#PARTIAL}
     */
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

    /**
     * Checks whether any scoped types are registered,
     * including promised, rooted, typed or wrapped.
     *
     * @return true if no scoped types are registered, false otherwise
     */
    protected boolean noScoped() {
        return promised.isEmpty() && scopedRoots.isEmpty() && scopedTypes.isEmpty() && wrapped.isEmpty();
    }

    /**
     * Builds a map of scoped object factories for all registered scoped types
     * (both those registered by class and root scoped factories).
     * <p>
     * For each scoped type, its schema is obtained from the provided schema provider;
     * then a stub factory is used to create an object factory with the appropriate cache mode.
     * If the created stub supports caching, it is added to the delayed list for later resolution
     * of its dependencies.
     *
     * @param schemaProvider the provider to get schemas for types and implementations
     * @param stubFactory    the factory to create stub object factories from schemas
     * @param delayed        a list to accumulate stub entries that require delayed dependency wiring
     * @param mode           the desired cache mode for created factories
     * @return a map from scoped types to their corresponding object factories
     */
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

    /**
     * Builds a map of wrapped scoped object factories.
     * <p>
     * For each wrapped scoped type, if it has an implementation class,
     * its schema is fetched and a stub factory is created with appropriate caching.
     * The resulting stub or factory is paired with its wrapper in a WrappedEntry.
     * <p>
     * These wrapped entries will have their wrapper applied when scopes are created,
     * ensuring wrapper logic is executed per scope.
     *
     * @param schemaProvider the provider to get schemas for types and implementations
     * @param stubFactory    the factory to create stub object factories from schemas
     * @param delayed        a list to accumulate stub entries that require delayed dependency wiring
     * @param mode           the desired cache mode for created factories
     * @return a map from scoped types to their wrapped entries containing factory and wrapper
     */
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

    /**
     * Finds an object factory for the specified type among scoped factories
     * or the provided type repository.
     * Returns null if the type is promised or wrapped.
     *
     * @param type       the type to find
     * @param scoped     map of scoped object factories
     * @param repository the type repository for fallback lookup
     * @return the factory instance or null if not found or promised/wrapped
     */
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

    /**
     * Resolves delayed stub entries by wiring their dependencies after all
     * scoped and repository factories have been created.
     * <p>
     * For each delayed stub, all types it depends on are resolved via
     * {@link #findType(Type, Map, TypeRepository)} from scoped factories
     * and the repository, and then injected into the stub.
     *
     * @param delayed    the list of stub entries that require delayed wiring
     * @param scoped     map of scoped object factories available for dependency resolution
     * @param repository the type repository for fallback dependency lookup
     */
    protected void handleDelayed(List<StubEntry> delayed, Map<Type, ObjectFactory> scoped, TypeRepository repository) {
        for (var entry : delayed) {
            for (var type : entry.types) {
                entry.stub.set(type, findType(type, scoped, repository));
            }
        }
    }

    /**
     * Internal container class for storing scoped type entries
     * that have an implementation or a factory with an associated wrapper.
     */
    protected static final class ScopedTypeEntry {
        /**
         * Implementation class of the scoped type, or null if using factory
         */
        protected Class<?> impl;

        /**
         * Factory to create instances of the scoped type, or null if using impl
         */
        protected ObjectFactory factory;

        /**
         * Wrapper to apply on factory for each scope creation
         */
        protected ServiceWrapper wrapper;

        /**
         * Creates a scoped type entry wrapping an implementation class.
         *
         * @param impl    the implementation class
         * @param wrapper the service wrapper to apply
         */
        protected ScopedTypeEntry(Class<?> impl, ServiceWrapper wrapper) {
            this.impl = impl;
            this.factory = null;
            this.wrapper = wrapper;
        }

        /**
         * Creates a scoped type entry wrapping a factory.
         *
         * @param factory the object factory
         * @param wrapper the service wrapper to apply
         */
        protected ScopedTypeEntry(ObjectFactory factory, ServiceWrapper wrapper) {
            this.factory = factory;
            this.impl = null;
            this.wrapper = wrapper;
        }
    }
}
