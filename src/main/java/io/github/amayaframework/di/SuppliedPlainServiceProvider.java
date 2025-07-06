package io.github.amayaframework.di;

import io.github.amayaframework.di.core.*;

import java.util.function.Supplier;

final class SuppliedPlainServiceProvider extends AbstractServiceProvider {
    private final Supplier<TypeRepository> supplier;

    SuppliedPlainServiceProvider(TypeRepository repository, Supplier<TypeRepository> supplier) {
        super(repository);
        this.supplier = supplier;
    }

    @Override
    public ServiceProvider createScoped() {
        return new SuppliedPlainServiceProvider(new ScopedTypeRepository(supplier.get(), repository), supplier);
    }
}
