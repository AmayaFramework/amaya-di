package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Function1;
import com.github.romanqed.jfunc.LazyFunction0;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractProviderBuilder implements ServiceProviderBuilder {
    protected Map<Artifact, Function0<Object>> strong;
    protected Map<Artifact, Entry> any;
    protected Repository repository;

    protected AbstractProviderBuilder() {
        this.reset();
    }

    protected void reset() {
        this.strong = new HashMap<>();
        this.any = new HashMap<>();
        this.repository = null;
    }

    @Override
    public ServiceProviderBuilder setRepository(Repository repository) {
        this.repository = repository;
        return this;
    }

    @Override
    public <T> ServiceProviderBuilder addService(Artifact artifact,
                                                 Class<? extends T> implementation,
                                                 Function1<Function0<T>, Function0<T>> wrapper) {
        // Non-null checks
        Objects.requireNonNull(artifact);
        Objects.requireNonNull(implementation);
        Objects.requireNonNull(wrapper);
        // Check if the implementation is a child class of an artifact type
        var parent = artifact.getType();
        if (!parent.isAssignableFrom(implementation)) {
            throw new IllegalArgumentException("The implementation is not a child class of the artifact type");
        }
        any.put(artifact, Entry.of(implementation, wrapper));
        return this;
    }

    @Override
    public ServiceProviderBuilder addSingleton(Artifact artifact, Class<?> implementation) {
        return addService(artifact, implementation, LazyFunction0::new);
    }

    @Override
    public ServiceProviderBuilder addTransient(Artifact artifact, Class<?> implementation) {
        return addService(artifact, implementation, Function1.identity());
    }

    @Override
    public <T> ServiceProviderBuilder addService(Class<T> type,
                                                 Class<? extends T> implementation,
                                                 Function1<Function0<T>, Function0<T>> wrapper) {
        return addService(new Artifact(type), implementation, wrapper);
    }

    @Override
    public <T> ServiceProviderBuilder addSingleton(Class<T> type, Class<? extends T> implementation) {
        return addService(type, implementation, LazyFunction0::new);
    }

    @Override
    public <T> ServiceProviderBuilder addTransient(Class<T> type, Class<? extends T> implementation) {
        return addService(type, implementation, Function1.identity());
    }

    @Override
    public <T> ServiceProviderBuilder addService(Class<T> type, Function1<Function0<T>, Function0<T>> wrapper) {
        return addService(new Artifact(type), type, wrapper);
    }

    @Override
    public ServiceProviderBuilder addSingleton(Class<?> type) {
        return addService(type, LazyFunction0::new);
    }

    @Override
    public ServiceProviderBuilder addTransient(Class<?> type) {
        return addService(type, Function1.identity());
    }

    @Override
    @SuppressWarnings("unchecked")
    public ServiceProviderBuilder addService(Artifact artifact, Function0<?> supplier) {
        Objects.requireNonNull(artifact);
        Objects.requireNonNull(supplier);
        strong.put(artifact, (Function0<Object>) supplier);
        return this;
    }

    @Override
    public <T> ServiceProviderBuilder addService(Class<T> type, Function0<T> supplier) {
        return addService(new Artifact(type), supplier);
    }

    @Override
    public ServiceProviderBuilder removeService(Artifact artifact) {
        strong.remove(artifact);
        any.remove(artifact);
        return this;
    }

    @Override
    public ServiceProviderBuilder removeService(Class<?> type) {
        return removeService(new Artifact(type));
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
