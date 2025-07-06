package io.github.amayaframework.di;

import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.core.TypeRepository;
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
        for (var entry : types.entrySet()) {
            ret.put(entry.getKey(), factory.create(entry.getValue().impl));
        }
        return ret;
    }

    static void checkMissingTypes(Map<Type, ClassSchema> schemas, Predicate<Type> canResolve) {
        for (var schema : schemas.values()) {
            var types = schema.getTypes();
            for (var type : types) {
                if (canResolve.test(type)) {
                    continue;
                }
                throw new TypeNotFoundException(type);
            }
        }
    }

    static void checkCycles(Map<Type, GraphNode> graph) {
        // Find for strongly connected components
        var components = TarjanUtil.findSCC(graph.values());
        if (components.isEmpty()) {
            return;
        }
        if (components.size() == 1) {
            throw new CycleFoundException(components.get(0));
        }
        throw new CyclesFoundException(components);
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
            checkMissingTypes(schemas, canResolve);
        }
        if (BuilderChecks.checkEnabled(checks, BuilderChecks.VALIDATE_CYCLES)) {
            checkCycles(buildGraph(schemas));
        }
    }

    static ServiceProvider buildScopedProvider(AbstractScopedProviderBuilder builder,
                                               SchemaFactory schemaFactory,
                                               StubFactory stubFactory,
                                               TypeRepository repository,
                                               CacheMode mode) {
        var delayed = new LinkedList<StubEntry>();
        // Build scoped provider
        var scoped = builder.buildScoped(schemaFactory, stubFactory, delayed, mode);
        if (builder.wrapped.isEmpty()) {
            builder.handleDelayed(delayed, scoped, repository);
            return builder.repositorySupplier == null
                    ? new ScopedServiceProvider(repository, scoped)
                    : new SuppliedScopedServiceProvider(repository, scoped, builder.repositorySupplier);
        }
        // Add wrapped types
        var wrapped = builder.buildWrapped(schemaFactory, stubFactory, delayed, mode);
        builder.handleDelayed(delayed, scoped, repository);
        if (scoped.isEmpty()) {
            return builder.repositorySupplier == null
                    ? new WrappedServiceProvider(repository, wrapped)
                    : new SuppliedWrappedServiceProvider(repository, wrapped, builder.repositorySupplier);
        }
        return builder.repositorySupplier == null
                ? new WrappedScopedServiceProvider(repository, scoped, wrapped)
                : new SuppliedWrappedScopedServiceProvider(repository, scoped, wrapped, builder.repositorySupplier);
    }
}
