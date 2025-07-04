package io.github.amayaframework.di;

import io.github.amayaframework.di.core.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

final class ScopedServiceProvider extends AbstractServiceProvider {
    private final Map<Type, ObjectFactory> scoped;

    ScopedServiceProvider(TypeRepository repository, Map<Type, ObjectFactory> scoped) {
        super(repository);
        this.scoped = scoped;
    }

    @Override
    public ServiceProvider createScoped() {
        return new PlainServiceProvider(new ScopedTypeRepository(
                new HashTypeRepository(new HashMap<>(scoped)),
                repository
        ));
    }
}
