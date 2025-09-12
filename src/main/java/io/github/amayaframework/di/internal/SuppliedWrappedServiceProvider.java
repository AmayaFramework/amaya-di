package io.github.amayaframework.di.internal;

import io.github.amayaframework.di.WrappedEntry;
import io.github.amayaframework.di.core.*;

import java.util.function.Supplier;

public final class SuppliedWrappedServiceProvider extends AbstractCloseableProvider {
    private final WrappedEntry[] wrapped;
    private final Supplier<ScopedRepository> supplier;

    public SuppliedWrappedServiceProvider(TypeRepository repository,
                                   WrappedEntry[] wrapped,
                                   Supplier<ScopedRepository> supplier) {
        super(repository);
        this.wrapped = wrapped;
        this.supplier = supplier;
    }

    @Override
    public ScopedServiceProvider createScoped() {
        var supplied = supplier.get();
        for (var entry : wrapped) {
            supplied.put(entry.type, entry.wrap());
        }
        return new SuppliedPlainScopedServiceProvider(new ScopedTypeRepository(supplied, repository), supplier);
    }
}
