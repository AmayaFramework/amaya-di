package io.github.amayaframework.di.core;

import com.github.romanqed.jtype.JType;

import java.lang.reflect.Type;

/**
 * A base implementation of {@link ServiceProvider} that uses a {@link TypeRepository}
 * to resolve services.
 */
public abstract class AbstractServiceProvider implements ServiceProvider {
    /**
     * The internal repository used to resolve services.
     */
    protected TypeRepository repository;

    /**
     * Constructs a new instance with the specified type repository.
     *
     * @param repository the repository used to resolve services, must be non-null
     */
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
