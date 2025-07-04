package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

public class PlainProviderBuilder extends AbstractServiceProviderBuilder<ServiceProviderBuilder> {

    public PlainProviderBuilder(SchemaFactory schemaFactory, StubFactory stubFactory, CacheMode cacheMode) {
        super(schemaFactory, stubFactory, cacheMode);
    }

    @Override
    protected ServiceProvider doBuild() {
        var schemaFactory = getSchemaFactory();
        var stubFactory = getStubFactory();
        var mode = getCacheMode();
        var repository = BuildUtil.buildRepository(schemaFactory, stubFactory, mode, roots, types, this.repository);
        return new PlainServiceProvider(repository);
    }
}
