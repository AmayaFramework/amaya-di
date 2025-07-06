//package io.github.amayaframework.di;
//
//import io.github.amayaframework.di.core.ServiceProvider;
//import io.github.amayaframework.di.schema.ClassSchema;
//import io.github.amayaframework.di.schema.SchemaFactory;
//import io.github.amayaframework.di.stub.CacheMode;
//import io.github.amayaframework.di.stub.StubFactory;
//
//import java.lang.reflect.Type;
//import java.util.HashMap;
//import java.util.Map;
//
//public class CheckedScopedProviderBuilder extends AbstractScopedProviderBuilder {
//    private final int checks;
//
//    public CheckedScopedProviderBuilder(SchemaFactory schemaFactory,
//                                        StubFactory stubFactory,
//                                        CacheMode cacheMode,
//                                        int checks) {
//        super(schemaFactory, stubFactory, cacheMode);
//        this.checks = checks;
//    }
//
//    private boolean canResolve(Type type) {
//        if (repository != null && repository.canProvide(type)) {
//            return true;
//        }
//        return roots.containsKey(type) || types.containsKey(type);
//    }
//
//    private boolean canResolveScoped(Type type) {
//        return promised.contains(type)
//                || scopedRoots.containsKey(type)
//                || scopedTypes.containsKey(type)
//                || wrapped.containsKey(type)
//                || canResolve(type);
//    }
//
//    private Map<Type, ClassSchema> buildScopedSchemas(SchemaFactory factory) {
//        var ret = new HashMap<Type, ClassSchema>();
//        for (var entry : scopedTypes.entrySet()) {
//            ret.put(entry.getKey(), factory.create(entry.getValue()));
//        }
//        for (var entry : wrapped.entrySet()) {
//            var wrappedEntry = entry.getValue();
//            if (wrappedEntry.impl == null) {
//                continue;
//            }
//            ret.put(entry.getKey(), factory.create(wrappedEntry.impl));
//        }
//        return ret;
//    }
//
//    @Override
//    protected ServiceProvider doBuild() {
//        var schemaFactory = getSchemaFactory();
//        var schemas = BuildUtil.buildSchemas(schemaFactory, types);
//        BuildUtil.doChecks(checks, schemas, this::canResolve);
//        var scopedSchemas = buildScopedSchemas(schemaFactory);
//        BuildUtil.doChecks(checks, scopedSchemas, this::canResolveScoped);
//        var stubFactory = getStubFactory();
//        var mode = getCacheMode();
//        // Build base repo
//        var repository = BuildUtil.buildRepository(schemaFactory, stubFactory, mode, roots, types, this.repository);
//        // Build scoped provider
//        return BuildUtil.buildScoped(
//                schemaFactory,
//                stubFactory,
//                mode,
//                this::deduceCacheMode,
//                (type, scoped) -> find(type, scoped, repository),
//                scopedRoots,
//                scopedTypes,
//                wrapped,
//                repository
//        );
//    }
//}
