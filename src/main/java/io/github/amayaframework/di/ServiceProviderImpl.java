package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.jfunc.Function0;

import java.lang.reflect.Type;

final class ServiceProviderImpl implements ServiceProvider {
    private final Repository repository;

    ServiceProviderImpl(Repository repository) {
        this.repository = repository;
    }

    @Override
    public Repository getRepository() {
        return repository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Function0<T> get(Type type) {
        return (Function0<T>) repository.get(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Function0<T> get(Class<T> type) {
        return (Function0<T>) repository.get(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T instantiate(Type type) {
        var supplier = get(type);
        if (supplier == null) {
            return null;
        }
        return (T) Exceptions.suppress(supplier);
    }

    @Override
    public <T> T instantiate(Class<T> type) {
        var supplier = get(type);
        if (supplier == null) {
            return null;
        }
        return Exceptions.suppress(supplier);
    }
}
