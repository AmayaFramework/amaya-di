package io.github.amayaframework.di.internal;

import io.github.amayaframework.di.WrappedEntry;
import io.github.amayaframework.di.core.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Supplier;

public final class SuppliedWrappedMapServiceProvider extends AbstractCloseableProvider {
    private final Map<Type, ObjectFactory> scoped;
    private final WrappedEntry[] wrapped;
    private final Supplier<ScopedRepository> supplier;

    public SuppliedWrappedMapServiceProvider(TypeRepository repository,
                                      Map<Type, ObjectFactory> scoped,
                                      WrappedEntry[] wrapped,
                                      Supplier<ScopedRepository> supplier) {
        super(repository);
        this.scoped = scoped;
        this.wrapped = wrapped;
        this.supplier = supplier;
    }

    @Override
    public ScopedServiceProvider createScoped() {
        var supplied = supplier.get();
        supplied.putAll(scoped);
        for (var entry : wrapped) {
            supplied.put(entry.type, entry.wrap());
        }
        return new SuppliedPlainScopedServiceProvider(new ScopedTypeRepository(supplied, repository), supplier);
    }
}
