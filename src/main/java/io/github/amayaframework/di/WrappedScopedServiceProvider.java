package io.github.amayaframework.di;

import io.github.amayaframework.di.core.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

final class WrappedScopedServiceProvider extends AbstractServiceProvider {
    private final Map<Type, ObjectFactory> scoped;
    private final Map<Type, WrappedEntry> wrapped;

    WrappedScopedServiceProvider(TypeRepository repository,
                                 Map<Type, ObjectFactory> scoped,
                                 Map<Type, WrappedEntry> wrapped) {
        super(repository);
        this.scoped = scoped;
        this.wrapped = wrapped;
    }

    @Override
    public ServiceProvider createScoped() {
        var map = new HashMap<>(scoped);
        for (var entry : wrapped.entrySet()) {
            var wrappedEntry = entry.getValue();
            map.put(entry.getKey(), wrappedEntry.wrapper.wrap(wrappedEntry.factory));
        }
        return new PlainServiceProvider(new ScopedTypeRepository(
                new HashTypeRepository(map),
                repository
        ));
    }
}
