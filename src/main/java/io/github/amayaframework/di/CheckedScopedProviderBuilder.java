package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

public class CheckedScopedProviderBuilder extends AbstractScopedProviderBuilder {
    private final int checks;

    public CheckedScopedProviderBuilder(SchemaFactory schemaFactory,
                                        StubFactory stubFactory,
                                        CacheMode cacheMode,
                                        int checks) {
        super(schemaFactory, stubFactory, cacheMode);
        this.checks = checks;
    }

    private boolean checkEnabled(int check) {
        return BuilderChecks.checkEnabled(checks, check);
    }

    @Override
    protected ServiceProvider doBuild() {
        return null;
    }
}
