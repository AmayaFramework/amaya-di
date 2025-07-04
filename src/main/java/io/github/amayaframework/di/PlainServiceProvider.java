package io.github.amayaframework.di;

import io.github.amayaframework.di.core.*;

final class PlainServiceProvider extends AbstractServiceProvider {

    PlainServiceProvider(TypeRepository repository) {
        super(repository);
    }

    @Override
    public ServiceProvider createScoped() {
        var provided = new HashTypeRepository();
        var scoped = new ScopedTypeRepository(provided, repository);
        return new PlainServiceProvider(scoped);
    }
}
