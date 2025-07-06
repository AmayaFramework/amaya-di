package io.github.amayaframework.di;

import io.github.amayaframework.di.core.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Supplier;

final class SuppliedWrappedScopedServiceProvider extends AbstractServiceProvider {
    private final Map<Type, ObjectFactory> scoped;
    private final Map<Type, WrappedEntry> wrapped;
    private final Supplier<TypeRepository> supplier;

    SuppliedWrappedScopedServiceProvider(TypeRepository repository,
                                         Map<Type, ObjectFactory> scoped,
                                         Map<Type, WrappedEntry> wrapped,
                                         Supplier<TypeRepository> supplier) {
        super(repository);
        this.scoped = scoped;
        this.wrapped = wrapped;
        this.supplier = supplier;
    }

    @Override
    public ServiceProvider createScoped() {
        var supplied = supplier.get();
        supplied.putAll(scoped);
        for (var entry : wrapped.entrySet()) {
            var wrappedEntry = entry.getValue();
            supplied.put(entry.getKey(), wrappedEntry.wrapper.wrap(wrappedEntry.factory));
        }
        return new SuppliedPlainServiceProvider(new ScopedTypeRepository(supplied, repository), supplier);
    }
}
