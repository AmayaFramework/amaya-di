package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.internal.PlainServiceProvider;
import io.github.amayaframework.di.internal.SuppliedPlainServiceProvider;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

/**
 * A basic implementation of {@link ScopedProviderBuilder} that builds scoped-aware
 * {@link ServiceProvider} without performing dependency validation.
 * <p>
 * This builder supports registration of scoped services and constructs a {@link ServiceProvider}
 * capable of creating scopes at runtime. It does not perform resolution checks or graph analysis.
 * <p>
 * If no scoped services are registered, it will fall back to a plain service provider.
 */
public class PlainScopedProviderBuilder extends AbstractScopedProviderBuilder<ScopedProviderBuilder> {

    /**
     * Constructs a new {@code PlainScopedProviderBuilder}.
     *
     * @param schemaFactory the factory responsible for producing injection schemas
     * @param stubFactory   the factory used to generate object factories from schemas
     * @param cacheMode     the default caching strategy for factories
     */
    public PlainScopedProviderBuilder(SchemaFactory schemaFactory, StubFactory stubFactory, CacheMode cacheMode) {
        super(schemaFactory, stubFactory, cacheMode);
    }

    @Override
    protected ServiceProvider doBuild() {
        var required = BuildUtil.needFactories(this);
        var schemaFactory = getSchemaFactory(required);
        var stubFactory = getStubFactory(required);
        var mode = getCacheMode();
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
