package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.jfunc.Function0;

final class PlainServiceProvider implements ServiceProvider {
    private final Repository repository;

    PlainServiceProvider(Repository repository) {
        this.repository = repository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Function0<T> get(Artifact artifact) {
        return (Function0<T>) repository.get(artifact);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Function0<T> get(Class<T> type, Class<?>... generics) {
        return (Function0<T>) repository.get(new Artifact(type, generics));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Function0<T> get(Class<T> type) {
        return (Function0<T>) repository.get(new Artifact(type));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T instantiate(Artifact artifact) {
        var supplier = get(artifact);
        if (supplier == null) {
            return null;
        }
        return (T) Exceptions.suppress(supplier);
    }

    @Override
    public <T> T instantiate(Class<T> type, Class<?>... generics) {
        var supplier = get(type, generics);
        if (supplier == null) {
            return null;
        }
        return Exceptions.suppress(supplier);
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
