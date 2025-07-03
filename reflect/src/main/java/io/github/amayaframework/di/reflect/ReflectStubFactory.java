package io.github.amayaframework.di.reflect;

import com.github.romanqed.jeflect.cloner.NoopReflectCloner;
import com.github.romanqed.jeflect.cloner.ReflectCloner;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.schema.ClassSchema;
import io.github.amayaframework.di.schema.ConstructorSchema;
import io.github.amayaframework.di.schema.FieldSchema;
import io.github.amayaframework.di.schema.MethodSchema;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A factory that creates instantiators using member accessors from the Reflection API.
 */
public final class ReflectStubFactory implements StubFactory {
    private final ReflectCloner cloner;

    /**
     * Constructs a {@link ReflectStubFactory} instance with the specified {@link ReflectCloner} instance,
     * which will be used to clone reflective entities from {@link ClassSchema}.
     *
     * @param cloner the {@link ReflectCloner} instance, must be non-null
     */
    public ReflectStubFactory(ReflectCloner cloner) {
        this.cloner = Objects.requireNonNull(cloner);
    }

    /**
     * Constructs a {@link ReflectStubFactory} instance with the {@link NoopReflectCloner}.
     * It is suitable for use in cases where classSchema is used ONLY ONCE,
     * or when {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)} does not affect the result.
     */
    public ReflectStubFactory() {
        this.cloner = new NoopReflectCloner();
    }

    private static void register(Map<Type, Consumer<ObjectFactory>> map, Type type, Consumer<ObjectFactory> updater) {
        var base = map.get(type);
        if (base == null) {
            map.put(type, updater);
            return;
        }
        map.put(type, base.andThen(updater));
    }

    private static ObjectFactory[] prepareFactories(Type[] types, Map<Type, Consumer<ObjectFactory>> updaters) {
        var ret = new ObjectFactory[types.length];
        for (var i = 0; i < ret.length; ++i) {
            register(updaters, types[i], new FactoryUpdater(ret, i));
        }
        return ret;
    }

    private static void register(Map<Type, Consumer<ObjectFactory>> map, Type type, FieldEntry entry) {
        register(map, type, factory -> entry.factory = factory);
    }

    private ObjectFactory wrap(ConstructorSchema schema, CacheMode mode, Map<Type, Consumer<ObjectFactory>> updaters) {
        var mapping = schema.getMapping();
        var target = cloner.clone(schema.getTarget());
        target.setAccessible(true);
        if (mapping.length == 0) {
            return new EmptyConstructorObjectFactory(target);
        }
        // Wrap with no cache
        if (mode == CacheMode.NONE) {
            return new ConstructorObjectFactory(target, mapping);
        }
        // Wrap cached
        var factories = prepareFactories(mapping, updaters);
        if (mode == CacheMode.FULL) {
            return new CachedConstructorObjectFactory(target, factories);
        }
        return new PartialConstructorObjectFactory(target, mapping, factories);
    }

    private MethodInvoker wrap(MethodSchema schema, CacheMode mode, Map<Type, Consumer<ObjectFactory>> updaters) {
        var mapping = schema.getMapping();
        var target = cloner.clone(schema.getTarget());
        target.setAccessible(true);
        var isStatic = Modifier.isStatic(target.getModifiers());
        // Wrap with no cache
        if (mode == CacheMode.NONE) {
            return isStatic ? new StaticMethodInvoker(target, mapping) : new VirtualMethodInvoker(target, mapping);
        }
        // Wrap cached
        var factories = prepareFactories(mapping, updaters);
        if (mode == CacheMode.FULL) {
            return isStatic
                    ? new CachedStaticMethodInvoker(target, factories)
                    : new CachedVirtualMethodInvoker(target, factories);
        }
        return isStatic
                ? new PartialStaticMethodInvoker(target, mapping, factories)
                : new PartialVirtualMethodInvoker(target, mapping, factories);
    }

    private FieldEntry wrap(FieldSchema schema, CacheMode mode, Map<Type, Consumer<ObjectFactory>> updaters) {
        var target = cloner.clone(schema.getTarget());
        target.setAccessible(true);
        // Wrap with no cache
        if (mode == CacheMode.NONE) {
            return new FieldEntry(target, schema.getType());
        }
        // Wrap cached
        var type = schema.getType();
        if (mode == CacheMode.FULL) {
            var ret = new FieldEntry(target);
            register(updaters, type, ret);
            return ret;
        }
        var ret = new FieldEntry(target, type);
        register(updaters, type, ret);
        return ret;
    }

    private MethodInvoker[] wrapMethods(Set<MethodSchema> schemas,
                                        CacheMode mode,
                                        Map<Type, Consumer<ObjectFactory>> updaters) {
        var ret = new MethodInvoker[schemas.size()];
        var i = 0;
        for (var schema : schemas) {
            ret[i++] = wrap(schema, mode, updaters);
        }
        return ret;
    }

    private FieldEntry[] wrapFields(Set<FieldSchema> schemas,
                                    CacheMode mode,
                                    Map<Type, Consumer<ObjectFactory>> updaters) {
        var ret = new FieldEntry[schemas.size()];
        var i = 0;
        for (var schema : schemas) {
            ret[i++] = wrap(schema, mode, updaters);
        }
        return ret;
    }

    private ObjectFactory createFactory(ClassSchema schema,
                                        CacheMode mode,
                                        Map<Type, Consumer<ObjectFactory>> updaters) {
        var constructor = wrap(schema.getConstructorSchema(), mode, updaters);
        var methodSchemas = schema.getMethodSchemas();
        var fieldSchemas = schema.getFieldSchemas();
        if (methodSchemas.isEmpty() && fieldSchemas.isEmpty()) {
            return constructor;
        }
        if (methodSchemas.isEmpty()) {
            var fields = wrapFields(fieldSchemas, mode, updaters);
            if (mode == CacheMode.NONE) {
                return new FieldsObjectFactory(constructor, fields);
            }
            if (mode == CacheMode.FULL) {
                return new CachedFieldsObjectFactory(constructor, fields);
            }
            return new PartialFieldsObjectFactory(constructor, fields);
        }
        if (fieldSchemas.isEmpty()) {
            var methods = wrapMethods(methodSchemas, mode, updaters);
            return new MethodsObjectFactory(constructor, methods);
        }
        var methods = wrapMethods(methodSchemas, mode, updaters);
        var fields = wrapFields(fieldSchemas, mode, updaters);
        if (mode == CacheMode.NONE) {
            return new FullObjectFactory(constructor, methods, fields);
        }
        if (mode == CacheMode.FULL) {
            return new CachedFullObjectFactory(constructor, methods, fields);
        }
        return new PartialFullObjectFactory(constructor, methods, fields);
    }

    @Override
    public ObjectFactory create(ClassSchema schema, CacheMode mode) {
        Objects.requireNonNull(schema);
        if (mode == null || mode == CacheMode.NONE) {
            return createFactory(schema, CacheMode.NONE, null);
        }
        var updaters = new HashMap<Type, Consumer<ObjectFactory>>();
        var factory = createFactory(schema, mode, updaters);
        return new UpdatedObjectFactory(factory, updaters);
    }
}
