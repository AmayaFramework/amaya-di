package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

public class PlainScopedProviderBuilder extends AbstractScopedProviderBuilder<ScopedProviderBuilder> {

    public PlainScopedProviderBuilder(SchemaFactory schemaFactory, StubFactory stubFactory, CacheMode cacheMode) {
        super(schemaFactory, stubFactory, cacheMode);
    }

    @Override
    protected ServiceProvider doBuild() {
        var schemaFactory = getSchemaFactory();
        var stubFactory = getStubFactory();
        var mode = getCacheMode();
        var repository = getRepository();
        // noinspection DuplicatedCode
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
