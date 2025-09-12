package io.github.amayaframework.di.internal;

import io.github.amayaframework.di.WrappedEntry;
import io.github.amayaframework.di.core.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class WrappedMapServiceProvider extends AbstractCloseableProvider {
    private final Map<Type, ObjectFactory> scoped;
    private final WrappedEntry[] wrapped;

    public WrappedMapServiceProvider(TypeRepository repository, Map<Type, ObjectFactory> scoped, WrappedEntry[] wrapped) {
        super(repository);
        this.scoped = scoped;
        this.wrapped = wrapped;
    }

    @Override
    public ScopedServiceProvider createScoped() {
        var map = new HashMap<>(scoped);
        for (var entry : wrapped) {
            map.put(entry.type, entry.wrap());
        }
        return new PlainScopedServiceProvider(new ScopedTypeRepository(new HashTypeRepository(map), repository));
    }
}
