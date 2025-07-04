package io.github.amayaframework.di;

import io.github.amayaframework.di.core.HashTypeRepository;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.core.TypeRepository;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.CachedObjectFactory;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;

public class PlainProviderBuilder extends AbstractServiceProviderBuilder<ServiceProviderBuilder> {

    public PlainProviderBuilder(SchemaFactory schemaFactory, StubFactory stubFactory, CacheMode cacheMode) {
        super(schemaFactory, stubFactory, cacheMode);
    }

    private TypeRepository buildRepository(TypeRepository repo) throws Throwable {
        var schemaFactory = getSchemaFactory();
        var stubFactory = getStubFactory();
        var mode = getCacheMode();
        var map = repo == null ? new HashMap<Type, ObjectFactory>() : null;
        // Add weak types
        var delayed = new LinkedList<StubEntry>();
        for (var entry : types.entrySet()) {
            var typeEntry = entry.getValue();
            // Build schema
            var schema = schemaFactory.create(typeEntry.impl);
            // Build stub and check if it is cached
            var stub = stubFactory.create(schema, mode);
            if (stub instanceof CachedObjectFactory) {
                delayed.add(new StubEntry(schema.getTypes(), (CachedObjectFactory) stub));
            }
            // Apply wrapper
            if (typeEntry.wrapper != null) {
                stub = typeEntry.wrapper.invoke(stub);
            }
            if (repo == null) {
                map.put(entry.getKey(), stub);
            } else {
                repo.put(entry.getKey(), stub);
            }
        }
        // Add root types
        if (repo == null) {
            map.putAll(roots);
        } else {
            roots.forEach(repo::put);
        }
        // Handle delayed cached stubs
        for (var entry : delayed) {
            for (var type : entry.types) {
                var found = repo == null ? map.get(type) : repo.get(type);
                if (found == null) {
                    throw new TypeNotFoundException(type);
                }
                entry.stub.set(type, found);
            }
        }
        return repo == null ? new HashTypeRepository(map) : repo;
    }

    @Override
    protected ServiceProvider doBuild() throws Throwable {
        var repository = buildRepository(this.repository);
        return new PlainServiceProvider(repository);
    }
}
