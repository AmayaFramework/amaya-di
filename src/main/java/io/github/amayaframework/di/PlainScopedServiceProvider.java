package io.github.amayaframework.di;

import io.github.amayaframework.di.core.*;

final class PlainScopedServiceProvider extends AbstractCloseableScopedProvider {
    PlainScopedServiceProvider(ScopedRepository repository) {
        super(repository);
    }

    @Override
    public ScopedServiceProvider createScoped() {
        return new PlainScopedServiceProvider(new ScopedTypeRepository(new HashTypeRepository(), repository));
    }
}
