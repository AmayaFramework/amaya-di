package io.github.amayaframework.di.internal;

import io.github.amayaframework.di.WrappedEntry;
import io.github.amayaframework.di.core.*;

import java.lang.reflect.Type;
import java.util.HashMap;

public final class WrappedServiceProvider extends AbstractCloseableProvider {
    private final WrappedEntry[] wrapped;

    public WrappedServiceProvider(TypeRepository repository, WrappedEntry[] wrapped) {
        super(repository);
        this.wrapped = wrapped;
    }

    @Override
    public ScopedServiceProvider createScoped() {
        var map = new HashMap<Type, ObjectFactory>();
        for (var entry : wrapped) {
            map.put(entry.type, entry.wrap());
        }
        return new PlainScopedServiceProvider(new ScopedTypeRepository(new HashTypeRepository(map), repository));
    }
}
