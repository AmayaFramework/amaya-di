package io.github.amayaframework.di;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jgraph.Graph;
import com.github.romanqed.jgraph.GraphUtil;
import com.github.romanqed.jgraph.HashGraph;
import io.github.amayaframework.di.scheme.ClassScheme;
import io.github.amayaframework.di.scheme.SchemeFactory;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;
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
    public static final int VALIDATE_CYCLES = 0b01;
    public static final int VALIDATE_MISSING_TYPES = 0b10;
    public static final int VALIDATE_ALL = VALIDATE_CYCLES | VALIDATE_MISSING_TYPES;

    private final SchemeFactory schemeFactory;
    private final StubFactory stubFactory;
    private final int checks;

    /**
     * Constructs {@link CheckedProviderBuilder} instance with the specified scheme, stub factories and check set.
     *
     * @param schemeFactory the specified scheme factory, must be non-null
     * @param stubFactory   the specified stub factory, must be non-null
     * @param checks        the specified set of applied checks
     */
    public CheckedProviderBuilder(SchemeFactory schemeFactory, StubFactory stubFactory, int checks) {
        this.schemeFactory = Objects.requireNonNull(schemeFactory);
        this.stubFactory = Objects.requireNonNull(stubFactory);
        this.checks = checks;
    }

    /**
     * Constructs {@link CheckedProviderBuilder} instance with the specified scheme and stub factories.
     * Enables all available checks {@link CheckedProviderBuilder#VALIDATE_ALL}.
     *
     * @param schemeFactory the specified scheme factory, must be non-null
     * @param stubFactory   the specified stub factory, must be non-null
     */
    public CheckedProviderBuilder(SchemeFactory schemeFactory, StubFactory stubFactory) {
        this(schemeFactory, stubFactory, VALIDATE_ALL);
    }

    protected Graph<Type> makeGraph(Map<Class<?>, ClassScheme> schemes) {
        var ret = new HashGraph<Type>();
        for (var entry : any.entrySet()) {
            var type = entry.getKey();
            var types = schemes.get(entry.getValue().implementation).getTypes();
            types.forEach(e -> {
                if (type.equals(e)) {
                    throw new CycleFoundException(List.of(e));
                }
                ret.addEdge(type, e);
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
    protected void buildTypes(Map<Class<?>, ClassScheme> schemes, LazyProvider provider) {
        for (var entry : any.entrySet()) {
            var type = entry.getKey();
            var value = entry.getValue();
            var scheme = schemes.get(value.implementation);
            var wrapper = value.wrapper;
            provider.add(type, () -> (Function0<Object>) wrapper.invoke(stubFactory.create(scheme, provider)));
        }
    }

    private boolean checkEnabled(int check) {
        return (checks & check) != 0;
    }

    protected void checkCycles(Map<Class<?>, ClassScheme> schemes) {
        // Build dependency graph
        var graph = makeGraph(schemes);
        // Find for strongly connected components
        var components = GraphUtil.findSCC(graph);
        for (var component : components) {
            if (component.size() > 1) {
                throw new CycleFoundException(component);
            }
        }
    }

    protected void checkMissingTypes(Map<Class<?>, ClassScheme> schemes, ServiceRepository repository) {
        for (var scheme : schemes.values()) {
            var types = scheme.getTypes();
            for (var type : types) {
                if (repository.contains(type)) {
                    continue;
                }
                if (canResolve(type)) {
                    continue;
                }
                throw new TypeNotFoundException(type);
            }
        }
    }

    @Override
    protected ServiceProvider checkedBuild() {
        // Build class schemes
        var schemes = makeSchemes();
        // Validate cyclic dependencies
        if (checkEnabled(VALIDATE_CYCLES)) {
            checkCycles(schemes);
        }
        // Build repository
        var repository = Objects.requireNonNullElse(this.repository, new RepositoryImpl());
        // Validate missing types
        if (checkEnabled(VALIDATE_MISSING_TYPES)) {
            checkMissingTypes(schemes, repository);
        }
        // Prepare weak types
        var provider = new LazyProvider(repository);
        buildTypes(schemes, provider);
        // Add strong types
        strong.forEach(repository::add);
        // Fire all delayed stub creations
        provider.commit();
        return new ServiceProviderImpl(repository);
    }
}
