package io.github.amayaframework.di;

import io.github.amayaframework.di.core.*;

import java.util.function.Supplier;

final class SuppliedPlainServiceProvider extends AbstractCloseableProvider {
    private final Supplier<ScopedRepository> supplier;

    SuppliedPlainServiceProvider(TypeRepository repository, Supplier<ScopedRepository> supplier) {
        super(repository);
        this.supplier = supplier;
    }

    @Override
    public ScopedServiceProvider createScoped() {
        return new SuppliedPlainScopedServiceProvider(new ScopedTypeRepository(supplier.get(), repository), supplier);
    }
}
