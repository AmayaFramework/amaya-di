package io.github.amayaframework.di;

import io.github.amayaframework.di.core.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Supplier;

final class SuppliedScopedServiceProvider extends AbstractServiceProvider {
    private final Map<Type, ObjectFactory> scoped;
    private final Supplier<TypeRepository> supplier;

    SuppliedScopedServiceProvider(TypeRepository repository,
                                  Map<Type, ObjectFactory> scoped,
                                  Supplier<TypeRepository> supplier) {
        super(repository);
        this.scoped = scoped;
        this.supplier = supplier;
    }

    @Override
    public ServiceProvider createScoped() {
        var supplied = supplier.get();
        supplied.putAll(scoped);
        return new SuppliedPlainServiceProvider(new ScopedTypeRepository(supplied, repository), supplier);
    }
}
