package io.github.amayaframework.di;

import com.github.romanqed.jgraph.Graph;
import com.github.romanqed.jgraph.GraphUtil;
import com.github.romanqed.jgraph.HashGraph;
import io.github.amayaframework.di.core.ServiceProvider;
import io.github.amayaframework.di.schema.ClassSchema;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckedProviderBuilder extends AbstractServiceProviderBuilder<ServiceProviderBuilder> {
    private final int checks;

    public CheckedProviderBuilder(SchemaFactory schemaFactory,
                                  StubFactory stubFactory,
                                  CacheMode cacheMode,
                                  int checks) {
        super(schemaFactory, stubFactory, cacheMode);
        this.checks = checks;
    }

    private boolean checkEnabled(int check) {
        return BuilderChecks.checkEnabled(checks, check);
    }

    private Map<Type, ClassSchema> buildSchemas() {
        var factory = getSchemaFactory();
        var ret = new HashMap<Type, ClassSchema>();
        for (var entry : types.entrySet()) {
            ret.put(entry.getKey(), factory.create(entry.getValue().impl));
        }
        return ret;
    }

    private boolean canResolve(Type type) {
        if (repository != null && repository.canProvide(type)) {
            return true;
        }
        return roots.containsKey(type) || types.containsKey(type);
    }

    private void checkMissingTypes(Map<Type, ClassSchema> schemas) {
        for (var schema : schemas.values()) {
            var types = schema.getTypes();
            for (var type : types) {
                if (canResolve(type)) {
                    continue;
                }
                throw new TypeNotFoundException(type);
            }
        }
    }

    private Graph<Type> makeGraph(Map<Type, ClassSchema> schemas) {
        var ret = new HashGraph<Type>();
        for (var entry : schemas.entrySet()) {
            var type = entry.getKey();
            var types = entry.getValue().getTypes();
            for (var e : types) {
                if (type.equals(e)) {
                    throw new CycleFoundException(List.of(e));
                }
                ret.addEdge(type, e);
            }
        }
        return ret;
    }

    private void checkCycles(Map<Type, ClassSchema> schemas) {
        // Build dependency graph
        var graph = makeGraph(schemas);
        // Find for strongly connected components
        var components = GraphUtil.findSCC(graph);
        for (var component : components) {
            if (component.size() > 1) {
                throw new CycleFoundException(component);
            }
        }
    }

    @Override
    protected ServiceProvider doBuild() {
        var schemas = buildSchemas();
        if (checkEnabled(BuilderChecks.VALIDATE_MISSING_TYPES)) {
            checkMissingTypes(schemas);
        }
        if (checkEnabled(BuilderChecks.VALIDATE_CYCLES)) {
            checkCycles(schemas);
        }
        var stubFactory = getStubFactory();
        var mode = getCacheMode();
        var repository = BuildUtil.buildRepository(stubFactory, mode, schemas, roots, types, this.repository);
        return new PlainServiceProvider(repository);
    }
}
