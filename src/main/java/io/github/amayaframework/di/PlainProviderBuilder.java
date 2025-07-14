package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

/**
 * A basic implementation of {@link ServiceProviderBuilder} that builds a service provider
 * without performing any validation or dependency analysis.
 * <p>
 * This builder simply constructs object factories using the registered type bindings and the provided
 * {@link SchemaFactory}, then wires them into a final {@link ServiceProvider}. It does not detect
 * missing or cyclic dependencies.
 * <p>
 * Use this builder when performance or minimal overhead is preferred and dependency integrity
 * is ensured externally.
 */
public class PlainProviderBuilder extends AbstractServiceProviderBuilder<ServiceProviderBuilder> {

    /**
     * Constructs a new instance of {@code PlainProviderBuilder}.
     *
     * @param schemaFactory the factory used to create injection schemas
     * @param stubFactory   the factory used to produce stub object factories from schemas
     * @param cacheMode     the default caching mode to use for generated factories
     */
    public PlainProviderBuilder(SchemaFactory schemaFactory, StubFactory stubFactory, CacheMode cacheMode) {
        super(schemaFactory, stubFactory, cacheMode);
    }

    @Override
    protected ServiceProvider doBuild() {
        var required = !types.isEmpty();
        var schemaFactory = getSchemaFactory(required);
        var stubFactory = getStubFactory(required);
        var mode = getCacheMode();
        var repository = getRepository();
        var provider = required ? (SchemaProvider) (t, impl) -> schemaFactory.create(impl) : null;
        buildRepository(repository, provider, stubFactory, mode);
        if (repositorySupplier != null) {
            return new SuppliedPlainServiceProvider(repository, repositorySupplier);
        }
        return new PlainServiceProvider(repository);
    }
}
