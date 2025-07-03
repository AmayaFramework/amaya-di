package io.github.amayaframework.di.asm;

import com.github.romanqed.jeflect.loader.DefineClassLoader;
import com.github.romanqed.jeflect.loader.DefineLoader;
import com.github.romanqed.jeflect.loader.DefineObjectFactory;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.schema.ClassSchema;
import io.github.amayaframework.di.schema.ExecutableSchema;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A factory that creates instantiators based on proxy classes generated on the fly.
 */
public final class AsmStubFactory implements StubFactory {
    private static final String OBJECT_FACTORY = "io/github/amayaframework/di/core/ObjectFactory";
    private static final String CACHED_OBJECT_FACTORY = "io/github/amayaframework/di/stub/CachedObjectFactory";
    private static final String TYPE_PROVIDER = "io/github/amayaframework/di/core/TypeProvider";
    private static final String CREATE_METHOD_DESCRIPTOR =
            "(Lio/github/amayaframework/di/core/TypeProvider;)Ljava/lang/Object;";
    private static final String GET_METHOD_DESCRIPTOR =
            "(Ljava/lang/reflect/Type;)Lio/github/amayaframework/di/core/ObjectFactory;";
    private static final String SET_METHOD_DESCRIPTOR =
            "(Ljava/lang/reflect/Type;Lio/github/amayaframework/di/core/ObjectFactory;)V";
    private static final String TYPE = "java/lang/reflect/Type";
    private static final String TYPE_DESCRIPTOR = "Ljava/lang/reflect/Type;";
    private static final String OBJECT_FACTORY_DESCRIPTOR = "Lio/github/amayaframework/di/core/ObjectFactory;";

    private static final String STUB = "Stub";
    private static final String PARTIAL_STUB = "PartialStub";
    private static final String CACHED_STUB = "CachedStub";

    private final com.github.romanqed.jeflect.loader.ObjectFactory<ObjectFactory> factory;

    /**
     * Constructs {@link AsmStubFactory} with the specified {@link ObjectFactory} instance,
     * which will be used to load and instantiate the bytecode of proxy classes.
     *
     * @param factory the specified factory, must be non-null
     */
    public AsmStubFactory(com.github.romanqed.jeflect.loader.ObjectFactory<ObjectFactory> factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    /**
     * Constructs {@link AsmStubFactory} with the {@link DefineObjectFactory} using
     * the specified {@link DefineLoader}.
     *
     * @param loader the specified loader, must be non-null
     */
    public AsmStubFactory(DefineLoader loader) {
        this.factory = new DefineObjectFactory<>(loader);
    }

    /**
     * Constructs {@link AsmStubFactory} with {@link DefineClassLoader}.
     */
    public AsmStubFactory() {
        this.factory = new DefineObjectFactory<>(new DefineClassLoader());
    }

    private static void processExecutable(MethodVisitor visitor,
                                          ExecutableSchema<?> schema,
                                          BiConsumer<MethodVisitor, Type> loader) {
        var target = schema.getTarget();
        var offset = Modifier.isStatic(target.getModifiers()) ? 1 : 0;
        var types = target.getParameterTypes();
        var mapping = schema.getMapping();
        for (var i = 0; i < mapping.length; ++i) {
            loader.accept(visitor, mapping[i]);
            AsmUtil.castReference(visitor, types[i + offset]);
        }
    }

    private static void generateCreateMethod(ClassWriter writer,
                                             ClassSchema schema,
                                             BiConsumer<MethodVisitor, Type> loader) {
        var target = schema.getTarget();
        // Declare method signature
        var visitor = writer.visitMethod(
                Opcodes.ACC_PUBLIC,
                "create",
                CREATE_METHOD_DESCRIPTOR,
                null,
                new String[]{org.objectweb.asm.Type.getInternalName(Throwable.class)}
        );
        visitor.visitCode();
        // {
        // Create instance of target class
        visitor.visitTypeInsn(Opcodes.NEW, org.objectweb.asm.Type.getInternalName(target));
        visitor.visitInsn(Opcodes.DUP);
        // Invoke constructor by schema
        // var v = new Type(arg1, arg2, arg3, ...);
        var constructor = schema.getConstructorSchema();
        processExecutable(visitor, constructor, loader);
        AsmUtil.invoke(visitor, constructor.getTarget());
        // Process field schemas
        var fields = schema.getFieldSchemas();
        for (var fieldSchema : fields) {
            // ref.<field> = (Type) instance;
            visitor.visitInsn(Opcodes.DUP);
            loader.accept(visitor, fieldSchema.getType());
            var field = fieldSchema.getTarget();
            var type = field.getType();
            AsmUtil.castReference(visitor, type);
            visitor.visitFieldInsn(
                    Opcodes.PUTFIELD,
                    org.objectweb.asm.Type.getInternalName(target),
                    field.getName(),
                    org.objectweb.asm.Type.getDescriptor(type)
            );
        }
        // Process method schemas
        var methods = schema.getMethodSchemas();
        for (var method : methods) {
            visitor.visitInsn(Opcodes.DUP);
            processExecutable(visitor, method, loader);
            AsmUtil.invoke(visitor, method.getTarget());
        }
        // Return constructed instance
        visitor.visitInsn(Opcodes.ARETURN);
        // }
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private static void generateConstructor(ClassWriter writer, String name, int count) {
        var visitor = writer.visitMethod(
                Opcodes.ACC_PUBLIC,
                AsmUtil.INIT,
                "([Ljava/lang/reflect/Type;)V",
                null,
                null
        );
        visitor.visitCode();
        // {
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                AsmUtil.OBJECT_NAME,
                AsmUtil.INIT,
                AsmUtil.EMPTY_DESCRIPTOR,
                false
        );
        for (var i = 0; i < count; ++i) {
            // Load this-ref and array-ref
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitVarInsn(Opcodes.ALOAD, 1);
            // Push index on stack
            AsmUtil.pushInt(visitor, i);
            // Load type from array and put to field
            visitor.visitInsn(Opcodes.AALOAD);
            visitor.visitFieldInsn(Opcodes.PUTFIELD, name, Integer.toString(i), TYPE_DESCRIPTOR);
        }
        visitor.visitInsn(Opcodes.RETURN);
        // }
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private static void invokeCreate(MethodVisitor visitor) {
        // Invoke ObjectFactory#create(TypeProvider)
        visitor.visitVarInsn(Opcodes.ALOAD, 1);
        visitor.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                OBJECT_FACTORY,
                "create",
                CREATE_METHOD_DESCRIPTOR,
                true
        );
    }

    private static void lookupType(MethodVisitor visitor, String name, Type type, Map<Type, String> map) {
        // Load provider
        visitor.visitVarInsn(Opcodes.ALOAD, 1);
        // Push type
        if (type.getClass() == Class.class) {
            visitor.visitLdcInsn(org.objectweb.asm.Type.getType((Class<?>) type));
        } else {
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitFieldInsn(Opcodes.GETFIELD, name, map.get(type), TYPE_DESCRIPTOR);
        }
        // Lookup in TypeProvider
        visitor.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                TYPE_PROVIDER,
                "get",
                GET_METHOD_DESCRIPTOR,
                true
        );
    }

    private static byte[] generateResolved(String name, ClassSchema schema, Map<Type, String> map) {
        var writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        // Declare class
        writer.visit(
                Opcodes.V11,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                name,
                null,
                AsmUtil.OBJECT_NAME,
                new String[]{OBJECT_FACTORY}
        );
        // Declare fields
        for (var field : map.values()) {
            writer.visitField(
                    Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                    field,
                    TYPE_DESCRIPTOR,
                    null,
                    null
            );
        }
        // Generate constructor
        var count = map.size();
        if (count == 0) {
            AsmUtil.createEmptyConstructor(writer);
        } else {
            generateConstructor(writer, name, count);
        }
        // Generate create() method
        generateCreateMethod(writer, schema, (visitor, type) -> {
            lookupType(visitor, name, type, map);
            invokeCreate(visitor);
        });
        // Get bytecode
        writer.visitEnd();
        return writer.toByteArray();
    }

    private static void setObjectFactoryField(MethodVisitor visitor, String name, String field) {
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitVarInsn(Opcodes.ALOAD, 2);
        visitor.visitFieldInsn(Opcodes.PUTFIELD, name, field, OBJECT_FACTORY_DESCRIPTOR);
    }

    private static void compareAndSet(MethodVisitor visitor, String name, Class<?> clazz, String field) {
        // Prepare out label
        var out = new Label();
        // Compare type == ldc
        visitor.visitLdcInsn(org.objectweb.asm.Type.getType(clazz));
        visitor.visitJumpInsn(Opcodes.IF_ACMPNE, out);
        setObjectFactoryField(visitor, name, field);
        visitor.visitInsn(Opcodes.RETURN);
        // Set out label
        visitor.visitLabel(out);
    }

    private static void compareAndSet(MethodVisitor visitor, String name, Type type, String field) {
        // Prepare out label
        var out = new Label();
        // Compare type.getTypeName().equals("ldc")
        visitor.visitLdcInsn(type.getTypeName());
        visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/String",
                "equals",
                "(Ljava/lang/Object;)Z",
                false
        );
        visitor.visitJumpInsn(Opcodes.IFEQ, out);
        setObjectFactoryField(visitor, name, field);
        visitor.visitInsn(Opcodes.RETURN);
        // Set out label
        visitor.visitLabel(out);
    }

    private static void compareAndSet(MethodVisitor visitor, String name, String type, String field) {
        // Prepare out label
        var out = new Label();
        // Compare type.equals(this.type)
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, name, type, TYPE_DESCRIPTOR);
        visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/Object",
                "equals",
                "(Ljava/lang/Object;)Z",
                false
        );
        visitor.visitJumpInsn(Opcodes.IFEQ, out);
        setObjectFactoryField(visitor, name, field);
        // Set out label
        visitor.visitLabel(out);
    }

    private static void compareComplexTypes(MethodVisitor visitor,
                                            String name,
                                            List<Map.Entry<Type, String>> types,
                                            Map<Type, String> map) {
        if (types.isEmpty()) {
            return;
        }
        if (map != null) {
            visitor.visitVarInsn(Opcodes.ALOAD, 1);
        } else {
            visitor.visitVarInsn(Opcodes.ALOAD, 1);
            visitor.visitMethodInsn(
                    Opcodes.INVOKEINTERFACE,
                    TYPE,
                    "getTypeName",
                    "()Ljava/lang/String;",
                    true
            );
        }
        var iterator = types.iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (iterator.hasNext()) {
                visitor.visitInsn(Opcodes.DUP);
            }
            if (map == null) {
                compareAndSet(visitor, name, entry.getKey(), entry.getValue());
            } else {
                var typeField = map.get(entry.getKey());
                compareAndSet(visitor, name, typeField, entry.getValue());
            }
        }
    }

    private static void generateSetMethod(ClassWriter writer,
                                          String name,
                                          Map<Type, String> fields,
                                          Map<Type, String> types) {
        var visitor = writer.visitMethod(
                Opcodes.ACC_PUBLIC,
                "set",
                SET_METHOD_DESCRIPTOR,
                null,
                null
        );
        visitor.visitCode();
        // {
        var first = new LinkedList<Map.Entry<Type, String>>();
        var last = new LinkedList<Map.Entry<Type, String>>();
        for (var entry : fields.entrySet()) {
            if (entry.getKey().getClass() == Class.class) {
                first.add(entry);
            } else {
                last.add(entry);
            }
        }
        visitor.visitVarInsn(Opcodes.ALOAD, 1);
        var iterator = first.iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (iterator.hasNext()) {
                visitor.visitInsn(Opcodes.DUP);
            }
            compareAndSet(visitor, name, (Class<?>) entry.getKey(), entry.getValue());
        }
        compareComplexTypes(visitor, name, last, types);
        visitor.visitInsn(Opcodes.RETURN);
        // }
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private static void loadTypeFromField(MethodVisitor visitor, String name, String field) {
        // Get factory from field
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, name, field, OBJECT_FACTORY_DESCRIPTOR);
        invokeCreate(visitor);
    }

    private static void lazyLoadType(MethodVisitor visitor,
                                     String name,
                                     Type type,
                                     String field,
                                     Map<Type, String> types) {
        // field == null ? provider.get(type) : field
        // Load field to check it
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, name, field, OBJECT_FACTORY_DESCRIPTOR);
        visitor.visitInsn(Opcodes.DUP);
        // Compare field with null
        var nonNull = new Label();
        visitor.visitJumpInsn(Opcodes.IFNONNULL, nonNull);
        // Load type from provider
        visitor.visitInsn(Opcodes.POP);
        lookupType(visitor, name, type, types);
        // Visit label
        visitor.visitLabel(nonNull);
        invokeCreate(visitor);
    }

    private static byte[] generateCached(String name,
                                         ClassSchema schema,
                                         Map<Type, String> fields,
                                         Map<Type, String> types) {
        var writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        // Declare class
        writer.visit(
                Opcodes.V11,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                name,
                null,
                AsmUtil.OBJECT_NAME,
                new String[]{CACHED_OBJECT_FACTORY}
        );
        // Declare fields for types
        if (types != null) {
            for (var field : types.values()) {
                writer.visitField(
                        Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                        field,
                        TYPE_DESCRIPTOR,
                        null,
                        null
                );
            }
        }
        // Declare fields for factories
        for (var field : fields.values()) {
            writer.visitField(Opcodes.ACC_PRIVATE, field, OBJECT_FACTORY_DESCRIPTOR, null, null);
        }
        // Generate constructor
        var count = types == null ? 0 : types.size();
        if (count == 0) {
            AsmUtil.createEmptyConstructor(writer);
        } else {
            generateConstructor(writer, name, count);
        }
        // Generate set() method
        generateSetMethod(writer, name, fields, types);
        // Generate create() method
        if (types == null) {
            generateCreateMethod(writer, schema, (visitor, type) ->
                    loadTypeFromField(visitor, name, fields.get(type))
            );
        } else {
            generateCreateMethod(writer, schema, (visitor, type) ->
                    lazyLoadType(visitor, name, type, fields.get(type), types)
            );
        }
        // Get bytecode
        writer.visitEnd();
        return writer.toByteArray();
    }

    private static String getName(Class<?> clazz, String postfix) {
        var name = clazz.getCanonicalName().replace('.', '#');
        return name + postfix;
    }

    private static ObjectFactory instantiate(Class<?> clazz, Map<Type, String> map) throws Throwable {
        if (map.isEmpty()) {
            return (ObjectFactory) clazz
                    .getDeclaredConstructor((Class<?>[]) null)
                    .newInstance((Object[]) null);
        }
        var parameters = map.keySet().toArray(new Type[0]);
        return (ObjectFactory) clazz
                .getDeclaredConstructor(Type[].class)
                .newInstance((Object) parameters);
    }

    private ObjectFactory createFull(ClassSchema schema) {
        var name = getName(schema.getTarget(), CACHED_STUB);
        return factory.create(name, () ->
                generateCached(name, schema, MapUtil.ofAll(schema.getTypes(), ""), null)
        );
    }

    private ObjectFactory createPartial(ClassSchema schema) {
        var name = getName(schema.getTarget(), PARTIAL_STUB);
        var types = MapUtil.ofComplex(schema.getTypes());
        return factory.create(
                name,
                () -> generateCached(name, schema, MapUtil.ofAll(schema.getTypes(), "f"), types),
                clazz -> instantiate(clazz, types)
        );
    }

    private ObjectFactory createNone(ClassSchema schema) {
        var name = getName(schema.getTarget(), STUB);
        var map = MapUtil.ofComplex(schema.getTypes());
        return factory.create(
                name,
                () -> generateResolved(name, schema, map),
                clazz -> instantiate(clazz, map)
        );
    }

    @Override
    public ObjectFactory create(ClassSchema schema, CacheMode mode) {
        Objects.requireNonNull(schema);
        if (mode == CacheMode.FULL) {
            return createFull(schema);
        }
        if (mode == CacheMode.PARTIAL) {
            return createPartial(schema);
        }
        return createNone(schema);
    }
}
