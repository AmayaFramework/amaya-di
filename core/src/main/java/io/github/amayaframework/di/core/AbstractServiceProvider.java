package io.github.amayaframework.di.core;

import com.github.romanqed.jtype.JType;

import java.lang.reflect.Type;

public abstract class AbstractServiceProvider implements ServiceProvider {
    protected TypeRepository repository;

    protected AbstractServiceProvider(TypeRepository repository) {
        this.repository = repository;
    }

    @Override
    public TypeRepository getRepository() {
        return repository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Type type) {
        if (type == null) {
            return null;
        }
        var factory = repository.get(type);
        if (factory == null) {
            return null;
        }
        return (T) factory.create(repository);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        if (type == null) {
            return null;
        }
        var factory = repository.get(type);
        if (factory == null) {
            return null;
        }
        return (T) factory.create(repository);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(JType<T> type) {
        if (type == null) {
            return null;
        }
        var factory = repository.get(type.getType());
        if (factory == null) {
            return null;
        }
        return (T) factory.create(repository);
    }
}
