package io.github.amayaframework.di.internal;

import io.github.amayaframework.di.core.*;

import java.util.function.Supplier;

public final class SuppliedPlainServiceProvider extends AbstractCloseableProvider {
    private final Supplier<ScopedRepository> supplier;

    public SuppliedPlainServiceProvider(TypeRepository repository, Supplier<ScopedRepository> supplier) {
        super(repository);
        this.supplier = supplier;
    }

    @Override
    public ScopedServiceProvider createScoped() {
        return new SuppliedPlainScopedServiceProvider(new ScopedTypeRepository(supplier.get(), repository), supplier);
    }
}
