package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import io.github.amayaframework.di.graph.Graph;
import io.github.amayaframework.di.graph.GraphUtil;
import io.github.amayaframework.di.graph.HashGraph;
import io.github.amayaframework.di.scheme.ClassScheme;
import io.github.amayaframework.di.scheme.SchemeFactory;
import io.github.amayaframework.di.stub.StubFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link ServiceProviderBuilder} implementation that performs static analysis of the collected set of services.
 * Checks for all required dependencies and analyzes the dependency graph for cycles.
 * Creates {@link ServiceProvider} according to the transaction principle, that is, until the build is successfully
 * completed, no side effects will be applied.
 */
public class CheckedProviderBuilder extends AbstractProviderBuilder {
    private final SchemeFactory schemeFactory;
    private final StubFactory stubFactory;

    /**
     * Constructs {@link CheckedProviderBuilder} instance with the specified scheme and stub factories.
     *
     * @param schemeFactory the specified scheme factory, must be non-null
     * @param stubFactory   the specified stub factory, must be non-null
     */
    public CheckedProviderBuilder(SchemeFactory schemeFactory, StubFactory stubFactory) {
        this.schemeFactory = Objects.requireNonNull(schemeFactory);
        this.stubFactory = Objects.requireNonNull(stubFactory);
    }

    protected Graph<Artifact> makeGraph(Map<Class<?>, ClassScheme> schemes) {
        var ret = new HashGraph<Artifact>();
        for (var entry : any.entrySet()) {
            var artifact = entry.getKey();
            var artifacts = schemes.get(entry.getValue().implementation).getArtifacts();
            artifacts.forEach(e -> {
                if (artifact.equals(e)) {
                    throw new CycleFoundException(List.of(e));
                }
                ret.addEdge(artifact, e);
            });
        }
        return ret;
    }

    protected Map<Class<?>, ClassScheme> makeSchemes() {
        var ret = new HashMap<Class<?>, ClassScheme>();
        for (var entry : any.values()) {
            var type = entry.implementation;
            var scheme = schemeFactory.create(type);
            ret.put(type, scheme);
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    protected void buildArtifacts(Map<Class<?>, ClassScheme> schemes, LazyProvider provider) {
        for (var entry : any.entrySet()) {
            var artifact = entry.getKey();
            var value = entry.getValue();
            var scheme = schemes.get(value.implementation);
            var wrapper = value.wrapper;
            provider.add(artifact, () -> (Function0<Object>) wrapper.invoke(stubFactory.create(scheme, provider)));
        }
    }

    @Override
    protected ServiceProvider checkedBuild() {
        // Build class schemes
        var schemes = makeSchemes();
        // Build dependency graph
        var graph = makeGraph(schemes);
        // Find for strongly connected components
        var components = GraphUtil.findStronglyConnectedComponents(graph);
        for (var component : components) {
            if (component.size() > 1) {
                throw new CycleFoundException(component);
            }
        }
        // Build repository
        var repository = Objects.requireNonNullElse(this.repository, new RepositoryImpl());
        // Validate missing artifacts
        for (var scheme : schemes.values()) {
            var artifacts = scheme.getArtifacts();
            for (var artifact : artifacts) {
                if (repository.contains(artifact)) {
                    continue;
                }
                if (resolve(artifact)) {
                    continue;
                }
                throw new ArtifactNotFoundException(artifact);
            }
        }
        // Prepare weak artifacts
        var provider = new LazyProvider(repository);
        buildArtifacts(schemes, provider);
        // Add strong artifacts
        strong.forEach(repository::add);
        // Fire all delayed stub creations
        provider.commit();
        return new ServiceProviderImpl(repository);
    }
}
