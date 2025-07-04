package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.core.TypeRepository;
import io.github.amayaframework.di.schema.ClassSchema;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.CachedObjectFactory;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlainScopedProviderBuilder extends AbstractScopedProviderBuilder {

    public PlainScopedProviderBuilder(SchemaFactory schemaFactory, StubFactory stubFactory, CacheMode cacheMode) {
        super(schemaFactory, stubFactory, cacheMode);
    }

    private CacheMode deduceCacheMode(ClassSchema schema, CacheMode mode) {
        // Max safe cache mode is CacheMode.PARTIAL
        if (mode != CacheMode.FULL) {
            return mode;
        }
        var types = schema.getTypes();
        for (var type : types) {
            // Check for promised type
            if (promised.contains(type)) {
                return CacheMode.PARTIAL;
            }
            // Check for wrapped type
            if (wrapped.containsKey(type)) {
                return CacheMode.PARTIAL;
            }
        }
        return CacheMode.FULL;
    }

    private ObjectFactory find(Type type, Map<Type, ObjectFactory> scoped, TypeRepository repository) {
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

    private void handleDelayed(List<StubEntry> delayed, Map<Type, ObjectFactory> scoped, TypeRepository repository) {
        for (var entry : delayed) {
            for (var type : entry.types) {
                entry.stub.set(type, find(type, scoped, repository));
            }
        }
    }

    @Override
    protected ServiceProvider doBuild() {
        var schemaFactory = getSchemaFactory();
        var stubFactory = getStubFactory();
        var mode = getCacheMode();
        // Build base repo
        var repository = BuildUtil.buildRepository(schemaFactory, stubFactory, mode, roots, types, this.repository);
        var scoped = new HashMap<Type, ObjectFactory>();
        // Add scoped weak types
        var delayed = new LinkedList<StubEntry>();
        for (var entry : scopedTypes.entrySet()) {
            // Build schema
            var schema = schemaFactory.create(entry.getValue());
            // Build stub and check if it is cached
            var deduced = deduceCacheMode(schema, mode);
            var stub = stubFactory.create(schema, deduced);
            if (stub instanceof CachedObjectFactory) {
                delayed.add(new StubEntry(schema.getTypes(), (CachedObjectFactory) stub));
            }
            scoped.put(entry.getKey(), stub);
        }
        // Add root types
        scoped.putAll(scopedRoots);
        if (wrapped.isEmpty()) {
            handleDelayed(delayed, scoped, repository);
            return new ScopedServiceProvider(repository, scoped);
        }
        // Add wrapped types
        var wrapped = new HashMap<Type, WrappedEntry>();
        for (var wrappedEntry : this.wrapped.entrySet()) {
            var type = wrappedEntry.getKey();
            var entry = wrappedEntry.getValue();
            // Handle wrapped root type
            if (entry.impl == null) {
                wrapped.put(type, new WrappedEntry(entry.factory, entry.wrapper));
                continue;
            }
            // Build schema
            var schema = schemaFactory.create(entry.impl);
            // Build stub and check it
            var deduced = deduceCacheMode(schema, mode);
            var stub = stubFactory.create(schema, deduced);
            if (stub instanceof CachedObjectFactory) {
                delayed.add(new StubEntry(schema.getTypes(), (CachedObjectFactory) stub));
            }
            wrapped.put(type, new WrappedEntry(stub, entry.wrapper));
        }
        handleDelayed(delayed, scoped, repository);
        return new WrappedScopedServiceProvider(repository, scoped, wrapped);
    }
}
