package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;

/**
 * A {@link ServiceProviderBuilder} implementation that performs validation checks
 * before constructing a plain (non-scoped) {@link ServiceProvider}.
 * <p>
 * This builder allows adding singleton and transient services, either via explicit factories,
 * implementation types, or instances. Before finalizing the build, it performs various
 * validation checks (configurable via a bitmask passed at construction time) to ensure that
 * the resulting provider is consistent and all required dependencies are satisfied.
 * <p>
 * The supported validations include:
 * <ul>
 *     <li>Dependency resolution checks – ensures that all dependencies declared in service schemas
 *         can be resolved by the current set of bindings</li>
 *     <li>Cycle detection – ensures that there are no circular dependencies between services</li>
 * </ul>
 * <p>
 * This builder does not support scoped services. To register scoped bindings,
 * use {@link ScopedProviderBuilder} or its checked variant {@link CheckedScopedProviderBuilder}.
 */
public class CheckedProviderBuilder extends AbstractServiceProviderBuilder<ServiceProviderBuilder> {
    private final int checks;

    /**
     * Constructs a new instance of {@code CheckedProviderBuilder}.
     *
     * @param schemaFactory the factory used to create injection schemas
     * @param stubFactory   the factory used to produce stub object factories from schemas
     * @param cacheMode     the default caching mode to use for generated factories
     * @param checks        the number of validation passes to perform during build
     */
    public CheckedProviderBuilder(SchemaFactory schemaFactory,
                                  StubFactory stubFactory,
                                  CacheMode cacheMode,
                                  int checks) {
        super(schemaFactory, stubFactory, cacheMode);
        this.checks = checks;
    }

    /**
     * Determines whether the given type can be resolved during schema validation.
     * <p>
     * The method checks whether the type is either provided by the current repository
     * or is registered as a root or weak binding in this builder.
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

    @Override
    protected ServiceProvider doBuild() {
        var required = types != null && !types.isEmpty();
        var schemaFactory = getSchemaFactory(required);
        var schemas = BuildUtil.buildSchemas(schemaFactory, types);
        BuildUtil.doChecks(checks, schemas, this::canResolve);
        var stubFactory = getStubFactory(required);
        var mode = getCacheMode();
        var repository = getRepository();
        var provider = required ? (SchemaProvider) (type, v) -> schemas.get(type) : null;
        buildRepository(repository, provider, stubFactory, mode);
        if (scopedRepositorySupplier != null) {
            return new SuppliedPlainServiceProvider(repository, scopedRepositorySupplier);
        }
        return new PlainServiceProvider(repository);
    }
}
