package io.github.amayaframework.di.internal;

import io.github.amayaframework.di.core.*;

public final class PlainScopedServiceProvider extends AbstractCloseableScopedProvider {
    public PlainScopedServiceProvider(ScopedRepository repository) {
        super(repository);
    }

    @Override
    public ScopedServiceProvider createScoped() {
        return new PlainScopedServiceProvider(new ScopedTypeRepository(new HashTypeRepository(), repository));
    }
}
