package io.github.amayaframework.di.core;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.jtype.JType;

import java.lang.reflect.Type;

/**
 * A base implementation of {@link ServiceProvider} backed by a {@link TypeRepository}.
 * <br>
 * Provides default resolution logic for {@link #get(Type)}, {@link #get(Class)},
 * and {@link #get(JType)}, delegating lookups to the underlying repository.
 *
 * @param <R> the concrete {@link TypeRepository} type used by this provider
 */
public abstract class AbstractServiceProvider<R extends TypeRepository> implements ServiceProvider {
    /**
     * The repository used to resolve services.
     */
    protected R repository;

    /**
     * Constructs a new service provider with the specified repository.
     *
     * @param repository the repository used to resolve services, must be non-null
     */
    protected AbstractServiceProvider(R repository) {
        this.repository = repository;
    }

    /**
     * Returns the repository backing this provider.
     *
     * @return the {@link TypeRepository} instance
     */
    @Override
    public R repository() {
        return repository;
    }

    /**
     * Resolves a service by its {@link Type}.
     *
     * @param type the service type to resolve, may be {@code null}
     * @param <T>  the expected service type
     * @return the resolved service instance, or {@code null} if not found
     */
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
        try {
            return (T) factory.create(repository);
        } catch (Throwable e) {
            Exceptions.throwAny(e);
            // Unreachable: throwAny always throws
            return null;
        }
    }

    /**
     * Resolves a service by its {@link Class}.
     *
     * @param type the service class to resolve, must be non-null
     * @param <T>  the expected service type
     * @return the resolved service instance, or {@code null} if not found
     */
    @Override
    public <T> T get(Class<T> type) {
        return get((Type) type);
    }

    /**
     * Resolves a service by its {@link JType} (generic type).
     *
     * @param type the generic service type to resolve, must be non-null
     * @param <T>  the expected service type
     * @return the resolved service instance, or {@code null} if not found
     */
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
        try {
            return (T) factory.create(repository);
        } catch (Throwable e) {
            Exceptions.throwAny(e);
            // Unreachable: throwAny always throws
            return null;
        }
    }
}
