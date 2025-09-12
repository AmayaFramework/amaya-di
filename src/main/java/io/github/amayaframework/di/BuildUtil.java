package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.core.TypeRepository;
import io.github.amayaframework.di.internal.*;
import io.github.amayaframework.di.schema.ClassSchema;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

final class BuildUtil {
    private BuildUtil() {
    }

    static Map<Type, ClassSchema> buildSchemas(SchemaFactory factory,
                                               Map<Type, AbstractServiceProviderBuilder.TypeEntry> types) {
        var ret = new HashMap<Type, ClassSchema>();
        if (types == null || types.isEmpty()) {
            return ret;
        }
        for (var entry : types.entrySet()) {
            ret.put(entry.getKey(), factory.create(entry.getValue().impl));
        }
        return ret;
    }

    static void checkMissingTypes(Map<Type, ClassSchema> schemas, Predicate<Type> canResolve, boolean scoped) {
        for (var schema : schemas.values()) {
            var types = schema.getTypes();
            for (var type : types) {
                if (canResolve.test(type)) {
                    continue;
                }
                throw new TypeNotFoundException(type, scoped);
            }
        }
    }

    static void checkCycles(Map<Type, GraphNode> graph, boolean scoped) {
        // Find for strongly connected components
        var components = TarjanUtil.findSCC(graph.values());
        if (components.isEmpty()) {
            return;
        }
        if (components.size() == 1) {
            throw new CycleFoundException(components.get(0), scoped);
        }
        throw new CyclesFoundException(components, scoped);
    }

    static void addEdge(Map<Type, GraphNode> graph, Type from, Type to) {
        var fromNode = graph.computeIfAbsent(from, GraphNode::new);
        var toNode = graph.computeIfAbsent(to, GraphNode::new);
        if (fromNode.adjacents == null) {
            fromNode.adjacents = new LinkedList<>();
        }
        fromNode.adjacents.add(toNode);
    }

    static Map<Type, GraphNode> buildGraph(Map<Type, ClassSchema> schemas) {
        var ret = new HashMap<Type, GraphNode>();
        for (var entry : schemas.entrySet()) {
            var type = entry.getKey();
            var types = entry.getValue().getTypes();
            for (var e : types) {
                if (type.equals(e)) {
                    throw new CycleFoundException(List.of(e));
                }
                addEdge(ret, type, e);
            }
        }
        return ret;
    }

    static void doChecks(int checks, Map<Type, ClassSchema> schemas, Predicate<Type> canResolve) {
        if (checks == BuilderChecks.NO_CHECKS) {
            return;
        }
        if (BuilderChecks.checkEnabled(checks, BuilderChecks.VALIDATE_MISSING_TYPES)) {
            checkMissingTypes(schemas, canResolve, false);
        }
        if (BuilderChecks.checkEnabled(checks, BuilderChecks.VALIDATE_CYCLES)) {
            checkCycles(buildGraph(schemas), false);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    static ServiceProvider buildScopedProvider(AbstractScopedProviderBuilder builder,
                                               SchemaProvider schemaProvider,
                                               StubFactory stubFactory,
                                               TypeRepository repository,
                                               CacheMode mode) {
        var delayed = new LinkedList<StubEntry>();
        // Build scoped provider
        var scoped = builder.buildScoped(schemaProvider, stubFactory, delayed, mode);
        var wrapped = builder.wrapped;
        if (wrapped == null || wrapped.isEmpty()) {
            builder.handleDelayed(delayed, scoped, repository);
            return builder.scopedRepositorySupplier == null
                    ? new MapScopedServiceProvider(repository, scoped)
                    : new SuppliedMapScopedServiceProvider(repository, scoped, builder.scopedRepositorySupplier);
        }
        // Add wrapped types
        var entries = builder.buildWrapped(schemaProvider, stubFactory, delayed, mode);
        builder.handleDelayed(delayed, scoped, repository);
        if (scoped.isEmpty()) {
            return builder.scopedRepositorySupplier == null
                    ? new WrappedServiceProvider(repository, entries)
                    : new SuppliedWrappedServiceProvider(repository, entries, builder.scopedRepositorySupplier);
        }
        return builder.scopedRepositorySupplier == null
                ? new WrappedMapServiceProvider(repository, scoped, entries)
                : new SuppliedWrappedMapServiceProvider(repository, scoped, entries, builder.scopedRepositorySupplier);
    }

    static <T extends ScopedProviderBuilder> boolean needFactories(AbstractScopedProviderBuilder<T> builder) {
        var types = builder.types;
        if (types != null && !types.isEmpty()) {
            return true;
        }
        var scopedTypes = builder.scopedTypes;
        if (scopedTypes != null && !scopedTypes.isEmpty()) {
            return true;
        }
        var wrapped = builder.wrapped;
        if (wrapped == null || wrapped.isEmpty()) {
            return false;
        }
        for (var entry : wrapped.entrySet()) {
            if (entry.getValue().impl != null) {
                return true;
            }
        }
        return false;
    }
}
