package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

public class PlainScopedProviderBuilder extends AbstractScopedProviderBuilder {

    public PlainScopedProviderBuilder(SchemaFactory schemaFactory, StubFactory stubFactory, CacheMode cacheMode) {
        super(schemaFactory, stubFactory, cacheMode);
    }

    @Override
    protected ServiceProvider doBuild() throws Throwable {
        return null;
    }
}
