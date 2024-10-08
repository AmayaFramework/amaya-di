package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Exceptions;

import java.lang.reflect.Type;

final class ServiceProviderImpl implements ServiceProvider {
    private final ServiceRepository repository;

    ServiceProviderImpl(ServiceRepository repository) {
        this.repository = repository;
    }

    @Override
    public ServiceRepository getRepository() {
        return repository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Type type) {
        var supplier = repository.get(type);
        if (supplier == null) {
            return null;
        }
        return (T) Exceptions.suppress(supplier);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        var supplier = repository.get(type);
        if (supplier == null) {
            return null;
        }
        return (T) Exceptions.suppress(supplier);
    }
}
