package io.github.amayaframework.di;

import io.github.amayaframework.di.core.HashTypeRepository;
import io.github.amayaframework.di.core.ObjectFactory;
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

final class BuildUtil {
    private BuildUtil() {
    }

    static ObjectFactory buildStub(AbstractServiceProviderBuilder.TypeEntry entry,
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

    static TypeRepository buildRepository(SchemaFactory schemaFactory,
                                          StubFactory stubFactory,
                                          CacheMode mode,
                                          Map<Type, ObjectFactory> roots,
                                          Map<Type, AbstractServiceProviderBuilder.TypeEntry> types,
                                          TypeRepository repo) {
        var map = repo == null ? new HashMap<Type, ObjectFactory>() : null;
        // Add weak types
        var delayed = new LinkedList<StubEntry>();
        for (var entry : types.entrySet()) {
            var typeEntry = entry.getValue();
            // Build schema
            var schema = schemaFactory.create(typeEntry.impl);
            // Build stub
            var stub = buildStub(typeEntry, schema, stubFactory, mode, delayed);
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

    static TypeRepository buildRepository(StubFactory stubFactory,
                                          CacheMode mode,
                                          Map<Type, ClassSchema> schemas,
                                          Map<Type, ObjectFactory> roots,
                                          Map<Type, AbstractServiceProviderBuilder.TypeEntry> types,
                                          TypeRepository repo) {
        var map = repo == null ? new HashMap<Type, ObjectFactory>() : null;
        // Add weak types
        var delayed = new LinkedList<StubEntry>();
        for (var entry : types.entrySet()) {
            var type = entry.getKey();
            var typeEntry = entry.getValue();
            // Build stub
            var stub = buildStub(typeEntry, schemas.get(type), stubFactory, mode, delayed);
            if (repo == null) {
                map.put(type, stub);
            } else {
                repo.put(type, stub);
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
                entry.stub.set(type, found);
            }
        }
        return repo == null ? new HashTypeRepository(map) : repo;
    }
}
