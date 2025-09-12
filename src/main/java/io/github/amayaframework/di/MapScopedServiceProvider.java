package io.github.amayaframework.di;

import io.github.amayaframework.di.core.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

final class MapScopedServiceProvider extends AbstractCloseableProvider {
    private final Map<Type, ObjectFactory> scoped;

    MapScopedServiceProvider(TypeRepository repository, Map<Type, ObjectFactory> scoped) {
        super(repository);
        this.scoped = scoped;
    }


    @Override
    public ScopedServiceProvider createScoped() {
        return new PlainScopedServiceProvider(new ScopedTypeRepository(
                new HashTypeRepository(new HashMap<>(scoped)),
                repository
        ));
    }
}
