package io.github.amayaframework.di.asm;

import com.github.romanqed.jeflect.loader.DefineClassLoader;
import com.github.romanqed.jeflect.loader.DefineLoader;
import com.github.romanqed.jeflect.loader.DefineObjectFactory;
import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.schema.ClassSchema;
import io.github.amayaframework.di.schema.ExecutableSchema;
import io.github.amayaframework.di.stub.StubFactory;
import org.objectweb.asm.*;

import java.util.LinkedList;
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
                                          BiConsumer<MethodVisitor, java.lang.reflect.Type> loader) {
        var types = schema.getTarget().getParameterTypes();
        var mapping = schema.getMapping();
        for (var i = 0; i < types.length; ++i) {
            loader.accept(visitor, mapping[i]);
            AsmUtil.castReference(visitor, types[i]);
        }
    }

    private static void generateCreateMethod(ClassWriter writer,
                                             ClassSchema schema,
                                             BiConsumer<MethodVisitor, java.lang.reflect.Type> loader) {
        var target = schema.getTarget();
        // Declare method signature
        var visitor = writer.visitMethod(
                Opcodes.ACC_PUBLIC,
                "create",
                CREATE_METHOD_DESCRIPTOR,
                null,
                new String[]{Type.getInternalName(Throwable.class)}
        );
        visitor.visitCode();
        // {
        // Create instance of target class
        visitor.visitTypeInsn(Opcodes.NEW, Type.getInternalName(target));
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
                    Type.getInternalName(target),
                    field.getName(),
                    Type.getDescriptor(type)
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
            visitor.visitFieldInsn(
                    Opcodes.PUTFIELD,
                    name,
                    Integer.toString(i),
                    TYPE_DESCRIPTOR
            );
        }
        visitor.visitInsn(Opcodes.RETURN);
        // }
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private static void loadTypeFromRepository(MethodVisitor visitor,
                                               String name,
                                               java.lang.reflect.Type type,
                                               Map<java.lang.reflect.Type, String> map) {
        // Load repository
        visitor.visitVarInsn(Opcodes.ALOAD, 1);
        // Push type
        if (type.getClass() == Class.class) {
            visitor.visitLdcInsn(Type.getType((Class<?>) type));
        } else {
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitFieldInsn(
                    Opcodes.GETFIELD,
                    name,
                    map.get(type),
                    TYPE_DESCRIPTOR
            );
        }
        // Lookup in TypeProvider
        visitor.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                TYPE_PROVIDER,
                "get",
                GET_METHOD_DESCRIPTOR,
                true
        );
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

    private static byte[] generateResolved(String name, ClassSchema schema, Map<java.lang.reflect.Type, String> map) {
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
        generateCreateMethod(writer, schema, (visitor, type) ->
                loadTypeFromRepository(visitor, name, type, map)
        );
        // Get bytecode
        writer.visitEnd();
        return writer.toByteArray();
    }

    private static void setObjectFactoryField(MethodVisitor visitor, String name, String field) {
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitVarInsn(Opcodes.ALOAD, 2);
        visitor.visitFieldInsn(
                Opcodes.PUTFIELD,
                name,
                field,
                OBJECT_FACTORY_DESCRIPTOR
        );
    }

    private static void compareAndSet(MethodVisitor visitor, String name, Class<?> clazz, String field) {
        // Prepare out label
        var out = new Label();
        // Compare ldc == type
        visitor.visitLdcInsn(Type.getType(clazz));
        visitor.visitVarInsn(Opcodes.ALOAD, 1);
        visitor.visitJumpInsn(Opcodes.IF_ACMPNE, out);
        setObjectFactoryField(visitor, name, field);
        visitor.visitInsn(Opcodes.RETURN);
        // Set out label
        visitor.visitLabel(out);
    }

    private static void compareAndSet(MethodVisitor visitor, String name, java.lang.reflect.Type type, String field) {
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

    private static void generateSetMethod(ClassWriter writer, String name, Map<java.lang.reflect.Type, String> map) {
        var visitor = writer.visitMethod(
                Opcodes.ACC_PUBLIC,
                "set",
                SET_METHOD_DESCRIPTOR,
                null,
                null
        );
        visitor.visitCode();
        // {
        var first = new LinkedList<Map.Entry<java.lang.reflect.Type, String>>();
        var last = new LinkedList<Map.Entry<java.lang.reflect.Type, String>>();
        for (var entry : map.entrySet()) {
            if (entry.getKey().getClass() == Class.class) {
                first.add(entry);
            } else {
                last.add(entry);
            }
        }
        for (var entry : first) {
            compareAndSet(visitor, name, (Class<?>) entry.getKey(), entry.getValue());
        }
        if (!last.isEmpty()) {
            // Prepare type name
            visitor.visitVarInsn(Opcodes.ALOAD, 1);
            visitor.visitMethodInsn(
                    Opcodes.INVOKEINTERFACE,
                    TYPE,
                    "getTypeName",
                    "()Ljava/lang/String;",
                    true
            );
            var iterator = last.iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                if (iterator.hasNext()) {
                    visitor.visitInsn(Opcodes.DUP);
                }
                compareAndSet(visitor, name, entry.getKey(), entry.getValue());
            }
        }
        visitor.visitInsn(Opcodes.RETURN);
        // }
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private static void loadTypeFromField(MethodVisitor visitor,
                                          String name,
                                          java.lang.reflect.Type type,
                                          Map<java.lang.reflect.Type, String> map) {
        // Get factory from field
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(
                Opcodes.GETFIELD,
                name,
                map.get(type),
                OBJECT_FACTORY_DESCRIPTOR
        );
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

    private static byte[] generateCached(String name, ClassSchema schema, Map<java.lang.reflect.Type, String> map) {
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
        // Declare fields
        for (var field : map.values()) {
            writer.visitField(
                    Opcodes.ACC_PRIVATE,
                    field,
                    OBJECT_FACTORY_DESCRIPTOR,
                    null,
                    null
            );
        }
        // Generate constructor
        AsmUtil.createEmptyConstructor(writer);
        // Generate set() method
        generateSetMethod(writer, name, map);
        // Generate create() method
        generateCreateMethod(writer, schema, (visitor, type) ->
                loadTypeFromField(visitor, name, type, map)
        );
        // Get bytecode
        writer.visitEnd();
        return writer.toByteArray();
    }

    @Override
    public ObjectFactory create(ClassSchema schema, boolean cached) {
        Objects.requireNonNull(schema);
        var target = schema.getTarget();
        var types = schema.getTypes();
        if (cached) {
            var name = target.getCanonicalName().replace('.', '#') + CACHED_STUB;
            return factory.create(name, () -> generateCached(name, schema, MapUtil.ofAll(types)));
        }
        var name = target.getCanonicalName().replace('.', '#') + STUB;
        var map = MapUtil.ofComplex(types);
        return factory.create(name, () -> generateResolved(name, schema, map), clazz -> {
            if (map.isEmpty()) {
                return (ObjectFactory) clazz
                        .getDeclaredConstructor((Class<?>[]) null)
                        .newInstance((Object[]) null);
            }
            var parameters = map.keySet().toArray(new java.lang.reflect.Type[0]);
            return (ObjectFactory) clazz
                    .getDeclaredConstructor(java.lang.reflect.Type[].class)
                    .newInstance((Object) parameters);
        });
    }
}
