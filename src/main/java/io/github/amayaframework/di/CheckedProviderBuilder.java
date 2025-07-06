package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;

public class CheckedProviderBuilder extends AbstractServiceProviderBuilder<ServiceProviderBuilder> {
    private final int checks;

    public CheckedProviderBuilder(SchemaFactory schemaFactory,
                                  StubFactory stubFactory,
                                  CacheMode cacheMode,
                                  int checks) {
        super(schemaFactory, stubFactory, cacheMode);
        this.checks = checks;
    }

    protected boolean canResolve(Type type) {
        if (repository != null && repository.canProvide(type)) {
            return true;
        }
        return roots.containsKey(type) || types.containsKey(type);
    }

    @Override
    protected ServiceProvider doBuild() {
        var schemaFactory = getSchemaFactory();
        var schemas = BuildUtil.buildSchemas(schemaFactory, types);
        BuildUtil.doChecks(checks, schemas, this::canResolve);
        var stubFactory = getStubFactory();
        var mode = getCacheMode();
        var repository = getRepository();
        buildRepository(repository, stubFactory, (type, v) -> schemas.get(type), mode);
        if (repositorySupplier != null) {
            return new SuppliedPlainServiceProvider(repository, repositorySupplier);
        }
        return new PlainServiceProvider(repository);
    }
}
