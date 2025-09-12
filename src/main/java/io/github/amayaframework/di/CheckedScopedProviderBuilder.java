package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.internal.PlainServiceProvider;
import io.github.amayaframework.di.internal.SuppliedPlainServiceProvider;
import io.github.amayaframework.di.schema.ClassSchema;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A {@link ScopedProviderBuilder} implementation that performs validation checks
 * before building scoped-aware {@link ServiceProvider}.
 * <p>
 * This builder supports scoped bindings and performs additional validation, such as:
 * <ul>
 *     <li>Checking for unresolved dependencies (if enabled)</li>
 *     <li>Validating cyclic dependencies between scoped services (if enabled)</li>
 * </ul>
 * The builder supports both root-level services and scoped-only services.
 * If no scoped services are registered, it will fall back to a plain service provider.
 */
public class CheckedScopedProviderBuilder extends AbstractScopedProviderBuilder<ScopedProviderBuilder> {
    private final int checks;

    /**
     * Constructs a new {@code CheckedScopedProviderBuilder}.
     *
     * @param schemaFactory the factory used to generate injection schemas
     * @param stubFactory   the factory used to generate object factories from schemas
     * @param cacheMode     the default caching strategy for services
     * @param checks        a bitmask indicating which build-time validations to apply
     *                      (see {@link BuilderChecks})
     */
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

    /**
     * Determines whether the given type can be resolved during schema validation.
     *
     * @param type the type to check
     * @return {@code true} if the type can be resolved; {@code false} otherwise
     */
    protected boolean canResolve(Type type) {
        if (repository != null && repository.canProvide(type)) {
            return true;
        }
        return hasRoot(type) || hasType(type);
    }

    /**
     * Determines whether the given scoped type can be resolved during schema validation.
     *
     * @param type the type to check
     * @return {@code true} if the type can be resolved; {@code false} otherwise
     */
    protected boolean canResolveScoped(Type type) {
        return hasPromised(type) || hasScopedRoot(type) || hasScopedType(type) || hasWrapped(type) || canResolve(type);
    }

    /**
     * Builds class schemas for all scoped types declared via {@code addScoped()} and related methods.
     *
     * @param factory the schema factory to use
     * @return a map from type to its corresponding {@link ClassSchema}, for all scoped types with implementation classes
     */
    protected Map<Type, ClassSchema> buildScopedSchemas(SchemaFactory factory) {
        var ret = new HashMap<Type, ClassSchema>();
        if (scopedTypes != null && !scopedTypes.isEmpty()) {
            for (var entry : scopedTypes.entrySet()) {
                ret.put(entry.getKey(), factory.create(entry.getValue()));
            }
        }
        if (wrapped == null || wrapped.isEmpty()) {
            return ret;
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
        var required = BuildUtil.needFactories(this);
        var schemaFactory = getSchemaFactory(required);
        var stubFactory = getStubFactory(required);
        var mode = getCacheMode();
        var schemas = BuildUtil.buildSchemas(schemaFactory, types);
        BuildUtil.doChecks(checks, schemas, this::canResolve);
        var scopedSchemas = buildScopedSchemas(schemaFactory);
        if (checkEnabled(BuilderChecks.VALIDATE_MISSING_TYPES)) {
            BuildUtil.checkMissingTypes(scopedSchemas, this::canResolveScoped, true);
        }
        if (checkEnabled(BuilderChecks.VALIDATE_CYCLES)) {
            checkCycles(schemas, scopedSchemas);
        }
        // noinspection DuplicatedCode
        var repository = getRepository();
        var provider = (SchemaProvider) (t, impl) -> schemaFactory.create(impl);
        buildRepository(repository, provider, stubFactory, mode);
        if (noScoped()) {
            return scopedRepositorySupplier == null
                    ? new PlainServiceProvider(repository)
                    : new SuppliedPlainServiceProvider(repository, scopedRepositorySupplier);
        }
        return BuildUtil.buildScopedProvider(this, provider, stubFactory, repository, mode);
    }
}
