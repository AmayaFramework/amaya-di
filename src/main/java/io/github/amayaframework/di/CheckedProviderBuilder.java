package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.jfunc.Function0;
import io.github.amayaframework.di.graph.GraphUtil;
import io.github.amayaframework.di.graph.HashGraph;
import io.github.amayaframework.di.scheme.ClassScheme;
import io.github.amayaframework.di.scheme.ReflectionSchemeFactory;
import io.github.amayaframework.di.scheme.SchemeFactory;
import io.github.amayaframework.di.stub.BytecodeStubFactory;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class CheckedProviderBuilder extends AbstractProviderBuilder {
    private final SchemeFactory schemeFactory;
    private final StubFactory stubFactory;

    public CheckedProviderBuilder(SchemeFactory schemeFactory, StubFactory stubFactory) {
        this.schemeFactory = Objects.requireNonNull(schemeFactory);
        this.stubFactory = Objects.requireNonNull(stubFactory);
    }

    public static ServiceProviderBuilder create(Class<? extends Annotation> annotation) {
        return new CheckedProviderBuilder(new ReflectionSchemeFactory(annotation), new BytecodeStubFactory());
    }

    public static ServiceProviderBuilder create() {
        return create(Inject.class);
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
                    throw new CycleFoundException(List.of(e));
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
        // Prepare weak artifacts
        var provider = new LazyProvider(repository);
        for (var entry : any.entrySet()) {
            var artifact = entry.getKey();
            var scheme = schemes.get(entry.getValue().implementation);
            var wrapper = entry.getValue().wrapper;
            provider.provide(artifact, () -> (Function0<Object>) wrapper.invoke(stubFactory.create(scheme, provider)));
        }
        // Validate dependencies
        for (var scheme : schemes.values()) {
            var artifacts = scheme.getArtifacts();
            for (var artifact : artifacts) {
                if (repository.contains(artifact)) {
                    continue;
                }
                if (strong.containsKey(artifact)) {
                    continue;
                }
                if (provider.body.containsKey(artifact)) {
                    continue;
                }
                throw new ArtifactNotFoundException(artifact);
            }
        }
        // Add strong artifacts
        strong.forEach(repository::add);
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
}
