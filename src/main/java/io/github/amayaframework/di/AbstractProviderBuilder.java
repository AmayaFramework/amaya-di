package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Function1;
import com.github.romanqed.jfunc.LazyFunction0;
import com.github.romanqed.jtype.TypeUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A class that provides a skeletal implementation of the {@link ServiceProviderBuilder},
 * containing implementations of all methods, structures for subsequent analysis,
 * and a reset mechanism to the initial state.
 */
public abstract class AbstractProviderBuilder implements ServiceProviderBuilder {

    /**
     * The map contains "strong" services, that is, they definitely do not have dependencies.
     */
    protected Map<Type, Function0<Object>> strong;

    /**
     * A map containing "weak" services, that is, having dependencies that need to be resolved.
     */
    protected Map<Type, Entry> any;

    /**
     * The {@link ServiceRepository} instance used, may be null
     */
    protected ServiceRepository repository;

    /**
     * Constructs {@link AbstractProviderBuilder}, reset to the initial state.
     * Does not contain any services, the repository used is set to null.
     */
    protected AbstractProviderBuilder() {
        this.reset();
    }

    /**
     * Resets the contents of this {@link AbstractProviderBuilder} instance.
     */
    protected void reset() {
        this.strong = new HashMap<>();
        this.any = new HashMap<>();
        this.repository = null;
    }

    /**
     * Determines whether a type implementation exists.
     *
     * @param type the specified type
     * @return true, if exists, false otherwise
     */
    protected boolean canResolve(Type type) {
        return strong.containsKey(type) || any.containsKey(type);
    }

    @Override
    public ServiceProviderBuilder setRepository(ServiceRepository repository) {
        this.repository = repository;
        return this;
    }

    @Override
    public <T> ServiceProviderBuilder addService(Type type,
                                                 Class<? extends T> implementation,
                                                 ServiceWrapper<T> wrapper) {
        // Non-null checks
        Objects.requireNonNull(type);
        Objects.requireNonNull(implementation);
        Objects.requireNonNull(wrapper);
        // Check if the implementation is a child class of a given type
        var parent = TypeUtil.getRawType(type);
        if (!parent.isAssignableFrom(implementation)) {
            throw new IllegalArgumentException("The implementation is not a child class of the type type");
        }
        strong.remove(type);
        any.put(type, Entry.of(implementation, wrapper));
        return this;
    }

    @Override
    public ServiceProviderBuilder addSingleton(Type type, Class<?> implementation) {
        return addService(type, implementation, LazyFunction0::new);
    }

    @Override
    public ServiceProviderBuilder addTransient(Type type, Class<?> implementation) {
        return addService(type, implementation, s -> s);
    }

    @Override
    public <T> ServiceProviderBuilder addService(Class<T> type,
                                                 Class<? extends T> implementation,
                                                 ServiceWrapper<T> wrapper) {
        // Non-null checks
        Objects.requireNonNull(type);
        Objects.requireNonNull(implementation);
        Objects.requireNonNull(wrapper);
        // Check if the implementation is a child class of a given type
        if (!type.isAssignableFrom(implementation)) {
            throw new IllegalArgumentException("The implementation is not a child class of the type type");
        }
        strong.remove(type);
        any.put(type, Entry.of(implementation, wrapper));
        return this;
    }

    @Override
    public <T> ServiceProviderBuilder addSingleton(Class<T> type, Class<? extends T> implementation) {
        return addService(type, implementation, LazyFunction0::new);
    }

    @Override
    public <T> ServiceProviderBuilder addTransient(Class<T> type, Class<? extends T> implementation) {
        return addService(type, implementation, s -> s);
    }

    @Override
    public <T> ServiceProviderBuilder addService(Class<T> type, ServiceWrapper<T> wrapper) {
        return addService(type, type, wrapper);
    }

    @Override
    public ServiceProviderBuilder addSingleton(Class<?> type) {
        return addService(type, LazyFunction0::new);
    }

    @Override
    public ServiceProviderBuilder addTransient(Class<?> type) {
        return addService(type, s -> s);
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServiceProviderBuilder addService(Type type, Function0<?> supplier) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(supplier);
        any.remove(type);
        strong.put(type, (Function0<Object>) supplier);
        return this;
    }

    @Override
    public ServiceProviderBuilder removeService(Type type) {
        Objects.requireNonNull(type);
        strong.remove(type);
        any.remove(type);
        return this;
    }

    /**
     * Builds a ready-to-use {@link ServiceProvider} implementation.
     * Override this method instead of {@link ServiceProviderBuilder#build()},
     * so as not to worry about resetting the builder to its original state.
     * Can handle checked exceptions.
     *
     * @return {@link ServiceProvider} instance
     * @throws Throwable if any problems occurs
     */
    protected abstract ServiceProvider checkedBuild() throws Throwable;

    @Override
    public ServiceProvider build() {
        try {
            var ret = checkedBuild();
            reset();
            return ret;
        } catch (Error | RuntimeException e) {
            reset();
            throw e;
        } catch (Throwable e) {
            reset();
            throw new RuntimeException(e);
        }
    }

    protected static final class Entry {
        Class<?> implementation;
        Function1<Function0<?>, Function0<?>> wrapper;

        @SuppressWarnings("unchecked")
        Entry(Class<?> implementation, Function1<?, ?> wrapper) {
            this.implementation = implementation;
            this.wrapper = (Function1<Function0<?>, Function0<?>>) wrapper;
        }

        static <T> Entry of(Class<? extends T> implementation, Function1<Function0<T>, Function0<T>> wrapper) {
            return new Entry(implementation, wrapper);
        }
    }
}
