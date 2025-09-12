package io.github.amayaframework.di.internal;

import io.github.amayaframework.di.core.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Supplier;

public final class SuppliedMapScopedServiceProvider extends AbstractCloseableProvider {
    private final Map<Type, ObjectFactory> scoped;
    private final Supplier<ScopedRepository> supplier;

    public SuppliedMapScopedServiceProvider(TypeRepository repository,
                                     Map<Type, ObjectFactory> scoped,
                                     Supplier<ScopedRepository> supplier) {
        super(repository);
        this.scoped = scoped;
        this.supplier = supplier;
    }

    @Override
    public ScopedServiceProvider createScoped() {
        var supplied = supplier.get();
        supplied.putAll(scoped);
        return new SuppliedPlainScopedServiceProvider(new ScopedTypeRepository(supplied, repository), supplier);
    }
}
