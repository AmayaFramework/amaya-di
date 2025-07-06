package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.schema.ClassSchema;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CheckedScopedProviderBuilder extends AbstractScopedProviderBuilder<ScopedProviderBuilder> {
    private final int checks;

    public CheckedScopedProviderBuilder(SchemaFactory schemaFactory,
                                        StubFactory stubFactory,
                                        CacheMode cacheMode,
                                        int checks) {
        super(schemaFactory, stubFactory, cacheMode);
        this.checks = checks;
    }

    private static boolean notOverrides(Set<Type> base, Set<Type> scoped) {
        for (var type : base) {
            if (scoped.contains(type)) {
                return false;
            }
        }
        return true;
    }

    private static void checkCycles(Map<Type, ClassSchema> base, Map<Type, ClassSchema> scoped) {
        if (notOverrides(base.keySet(), scoped.keySet())) {
            BuildUtil.checkCycles(BuildUtil.buildGraph(scoped), true);
            return;
        }
        var merged = new HashMap<>(base);
        merged.putAll(scoped);
        BuildUtil.checkCycles(BuildUtil.buildGraph(merged), true);
    }

    private boolean checkEnabled(int check) {
        return BuilderChecks.checkEnabled(checks, check);
    }

    private boolean canResolve(Type type) {
        if (repository != null && repository.canProvide(type)) {
            return true;
        }
        return roots.containsKey(type) || types.containsKey(type);
    }

    private boolean canResolveScoped(Type type) {
        return promised.contains(type)
                || scopedRoots.containsKey(type)
                || scopedTypes.containsKey(type)
                || wrapped.containsKey(type)
                || canResolve(type);
    }

    private Map<Type, ClassSchema> buildScopedSchemas(SchemaFactory factory) {
        var ret = new HashMap<Type, ClassSchema>();
        for (var entry : scopedTypes.entrySet()) {
            ret.put(entry.getKey(), factory.create(entry.getValue()));
        }
        for (var entry : wrapped.entrySet()) {
            var wrappedEntry = entry.getValue();
            if (wrappedEntry.impl == null) {
                continue;
            }
            ret.put(entry.getKey(), factory.create(wrappedEntry.impl));
        }
        return ret;
    }

    @Override
    protected ServiceProvider doBuild() {
        var schemaFactory = getSchemaFactory();
        var stubFactory = getStubFactory();
        var mode = getCacheMode();
        var schemas = BuildUtil.buildSchemas(schemaFactory, types);
        BuildUtil.doChecks(checks, schemas, this::canResolve);
        var scopedSchemas = buildScopedSchemas(schemaFactory);
        if (checkEnabled(BuilderChecks.VALIDATE_MISSING_TYPES)) {
            BuildUtil.checkMissingTypes(scopedSchemas, this::canResolveScoped);
        }
        if (checkEnabled(BuilderChecks.VALIDATE_CYCLES)) {
            checkCycles(schemas, scopedSchemas);
        }
        // noinspection DuplicatedCode
        var repository = getRepository();
        var provider = (SchemaProvider) (t, impl) -> schemaFactory.create(impl);
        buildRepository(repository, provider, stubFactory, mode);
        if (noScoped()) {
            return repositorySupplier == null
                    ? new PlainServiceProvider(repository)
                    : new SuppliedPlainServiceProvider(repository, repositorySupplier);
        }
        return BuildUtil.buildScopedProvider(this, provider, stubFactory, repository, mode);
    }
}
