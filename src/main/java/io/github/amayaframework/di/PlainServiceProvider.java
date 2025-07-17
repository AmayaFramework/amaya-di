package io.github.amayaframework.di;

import io.github.amayaframework.di.core.*;

final class PlainServiceProvider extends AbstractServiceProvider {

    PlainServiceProvider(TypeRepository repository) {
        super(repository);
    }

    @Override
    public ServiceProvider createScoped() {
        return new PlainServiceProvider(new ScopedTypeRepository(new HashTypeRepository(), repository));
    }
}
