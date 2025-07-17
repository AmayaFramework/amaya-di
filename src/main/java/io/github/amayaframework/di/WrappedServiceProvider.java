package io.github.amayaframework.di;

import io.github.amayaframework.di.core.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

final class WrappedServiceProvider extends AbstractServiceProvider {
    private final Map<Type, WrappedEntry> wrapped;

    WrappedServiceProvider(TypeRepository repository, Map<Type, WrappedEntry> wrapped) {
        super(repository);
        this.wrapped = wrapped;
    }

    @Override
    public ServiceProvider createScoped() {
        var map = new HashMap<Type, ObjectFactory>();
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
