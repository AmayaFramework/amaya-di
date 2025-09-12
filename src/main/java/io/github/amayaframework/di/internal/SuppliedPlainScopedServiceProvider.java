package io.github.amayaframework.di.internal;

import io.github.amayaframework.di.core.AbstractCloseableScopedProvider;
import io.github.amayaframework.di.core.ScopedRepository;
import io.github.amayaframework.di.core.ScopedServiceProvider;
import io.github.amayaframework.di.core.ScopedTypeRepository;

import java.util.function.Supplier;

final class SuppliedPlainScopedServiceProvider extends AbstractCloseableScopedProvider {
    private final Supplier<ScopedRepository> supplier;

    SuppliedPlainScopedServiceProvider(ScopedRepository repository, Supplier<ScopedRepository> supplier) {
        super(repository);
        this.supplier = supplier;
    }

    @Override
    public ScopedServiceProvider createScoped() {
        return new SuppliedPlainScopedServiceProvider(new ScopedTypeRepository(supplier.get(), repository), supplier);
    }
}
