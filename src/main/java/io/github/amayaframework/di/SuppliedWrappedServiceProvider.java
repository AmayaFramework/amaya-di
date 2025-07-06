package io.github.amayaframework.di;

import io.github.amayaframework.di.core.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Supplier;

final class SuppliedWrappedServiceProvider extends AbstractServiceProvider {
    private final Map<Type, WrappedEntry> wrapped;
    private final Supplier<TypeRepository> supplier;

    SuppliedWrappedServiceProvider(TypeRepository repository,
                                   Map<Type, WrappedEntry> wrapped,
                                   Supplier<TypeRepository> supplier) {
        super(repository);
        this.wrapped = wrapped;
        this.supplier = supplier;
    }

    @Override
    public ServiceProvider createScoped() {
        var supplied = supplier.get();
        for (var entry : wrapped.entrySet()) {
            var wrappedEntry = entry.getValue();
            supplied.put(entry.getKey(), wrappedEntry.wrapper.wrap(wrappedEntry.factory));
        }
        return new SuppliedPlainServiceProvider(new ScopedTypeRepository(supplied, repository), supplier);
    }
}
