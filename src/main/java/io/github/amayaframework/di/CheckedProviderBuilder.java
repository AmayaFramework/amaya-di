package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Function1;
import com.github.romanqed.jfunc.LazyFunction0;
import io.github.amayaframework.di.graph.GraphUtil;
import io.github.amayaframework.di.graph.HashGraph;
import io.github.amayaframework.di.scheme.ClassScheme;
import io.github.amayaframework.di.scheme.ReflectionSchemeFactory;
import io.github.amayaframework.di.scheme.SchemeFactory;
import io.github.amayaframework.di.stub.BytecodeStubFactory;
import io.github.amayaframework.di.stub.StubFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public final class CheckedProviderBuilder implements ServiceProviderBuilder {
    private final SchemeFactory schemeFactory;
    private final StubFactory stubFactory;
    private Map<Artifact, Function0<Object>> strong;
    private Map<Artifact, Entry> any;
    private Repository repository;

    public CheckedProviderBuilder(SchemeFactory schemeFactory, StubFactory stubFactory) {
        this.schemeFactory = Objects.requireNonNull(schemeFactory);
        this.stubFactory = Objects.requireNonNull(stubFactory);
        this.reset();
    }

    public static ServiceProviderBuilder createDefault() {
        return new CheckedProviderBuilder(new ReflectionSchemeFactory(Inject.class), new BytecodeStubFactory());
    }

    private void reset() {
        this.strong = new HashMap<>();
        this.any = new HashMap<>();
        this.repository = null;
    }

    @Override
    public ServiceProviderBuilder setRepository(Repository repository) {
        this.repository = Objects.requireNonNull(repository);
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
    public <T> ServiceProviderBuilder addSingleton(Artifact artifact, Class<? extends T> implementation) {
        return addService(artifact, implementation, LazyFunction0::new);
    }

    @Override
    public <T> ServiceProviderBuilder addTransient(Artifact artifact, Class<? extends T> implementation) {
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
    public <T> ServiceProviderBuilder addSingleton(Class<T> type) {
        return addService(type, LazyFunction0::new);
    }

    @Override
    public <T> ServiceProviderBuilder addTransient(Class<T> type) {
        return addService(type, Function1.identity());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ServiceProviderBuilder addService(Artifact artifact, Function0<T> supplier) {
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

    @Override
    @SuppressWarnings("unchecked")
    public ServiceProvider build() {
        // Build class schemes
        var schemes = new HashMap<Class<?>, ClassScheme>();
        for (var entry : any.values()) {
            var type = entry.implementation;
            var scheme = schemeFactory.create(type);
            schemes.put(type, scheme);
        }
        // Build dependency graph
        var graph = new HashGraph<Artifact>();
        for (var entry : any.entrySet()) {
            var artifact = entry.getKey();
            var artifacts = schemes.get(entry.getValue().implementation).getArtifacts();
            artifacts.forEach(e -> {
                if (artifact.equals(e)) {
                    throw new CycleFoundException(Set.of(e));
                }
                graph.addEdge(artifact, e);
            });
        }
        // Find strongly connected components
        var components = GraphUtil.findStronglyConnectedComponents(graph);
        for (var component : components) {
            if (component.size() > 1) {
                throw new CycleFoundException(component);
            }
        }
        // Build repository
        var repository = Objects.requireNonNullElse(this.repository, new HashRepository());
        // Add strong artifacts
        strong.forEach(repository::add);
        // Prepare weak artifacts
        var provider = new LazyProvider(repository);
        for (var entry : any.entrySet()) {
            var artifact = entry.getKey();
            var scheme = schemes.get(entry.getValue().implementation);
            var wrapper = entry.getValue().wrapper;
            provider.provide(artifact, () -> (Function0<Object>) wrapper.invoke(stubFactory.create(scheme, provider)));
        }
        // Fire all delayed stub creations
        for (var entry : provider.body.entrySet()) {
            var artifact = entry.getKey();
            var supplier = Exceptions.suppress(entry.getValue());
            repository.add(artifact, supplier);
        }
        this.reset();
        return new PlainServiceProvider(repository);
    }

    private static final class LazyProvider implements Function<Artifact, Function0<Object>> {
        private final Map<Artifact, Function0<Function0<Object>>> body;
        private final Repository repository;

        private LazyProvider(Repository repository) {
            this.body = new HashMap<>();
            this.repository = repository;
        }

        private void provide(Artifact artifact, Function0<Function0<Object>> provided) {
            body.put(artifact, provided);
        }

        @Override
        public Function0<Object> apply(Artifact artifact) {
            var ret = repository.get(artifact);
            if (ret != null) {
                return ret;
            }
            var provided = body.get(artifact);
            if (provided == null) {
                return null;
            }
            var function = Exceptions.suppress(provided);
            repository.add(artifact, function);
            // It is important to request the artifact again from the repository so that it can apply the wrapper.
            return repository.get(artifact);
        }
    }

    private static final class Entry {
        Class<?> implementation;
        Function1<Function0<?>, Function0<?>> wrapper;

        @SuppressWarnings("unchecked")
        private Entry(Class<?> implementation, Function1<?, ?> wrapper) {
            this.implementation = implementation;
            this.wrapper = (Function1<Function0<?>, Function0<?>>) wrapper;
        }

        private static <T> Entry of(Class<? extends T> implementation, Function1<Function0<T>, Function0<T>> wrapper) {
            return new Entry(implementation, wrapper);
        }
    }
}
