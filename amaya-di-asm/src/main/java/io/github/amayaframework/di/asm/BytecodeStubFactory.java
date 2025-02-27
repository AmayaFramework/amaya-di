package io.github.amayaframework.di.asm;

import com.github.romanqed.jeflect.loader.DefineClassLoader;
import com.github.romanqed.jeflect.loader.DefineLoader;
import com.github.romanqed.jeflect.loader.DefineObjectFactory;
import com.github.romanqed.jeflect.loader.ObjectFactory;
import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jtype.TypeUtil;
import io.github.amayaframework.di.scheme.ClassScheme;
import io.github.amayaframework.di.stub.StubFactory;
import io.github.amayaframework.di.stub.TypeProvider;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A factory that creates instantiators based on proxy classes generated on the fly.
 */
public final class BytecodeStubFactory implements StubFactory {
    private static final String STUB = "Stub";
    private static final Type FUNCTION0 = Type.getType(Function0.class);
    private static final Method INVOKE = Exceptions.suppress(() -> Function0.class.getDeclaredMethod("invoke"));
    private static final String OBJECT_NAME = Type.getInternalName(Object.class);

    private final ObjectFactory<Function0<?>> factory;

    /**
     * Constructs {@link BytecodeStubFactory} with the specified {@link ObjectFactory} instance,
     * which will be used to load and instantiate the bytecode of proxy classes.
     *
     * @param factory the specified factory, must be non-null
     */
    public BytecodeStubFactory(ObjectFactory<Function0<?>> factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    /**
     * Constructs {@link BytecodeStubFactory} with the {@link DefineObjectFactory} using
     * the specified {@link DefineLoader}.
     *
     * @param loader the specified loader, must be non-null
     */
    public BytecodeStubFactory(DefineLoader loader) {
        this.factory = new DefineObjectFactory<>(loader);
    }

    /**
     * Constructs {@link BytecodeStubFactory} with {@link DefineClassLoader}.
     */
    public BytecodeStubFactory() {
        this(new DefineClassLoader());
    }

    private static void generateConstructor(ClassWriter writer, String name, String[] order) {
        var visitor = writer.visitMethod(
                Opcodes.ACC_PUBLIC,
                "<init>",
                "([" + FUNCTION0.getDescriptor() + ")V",
                null,
                null
        );
        visitor.visitCode();
        // Call parent constructor
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                OBJECT_NAME,
                "<init>",
                "()V",
                false
        );
        // Put type providers to fields
        for (var i = 0; i < order.length; ++i) {
            // Load this-ref and array-ref
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitVarInsn(Opcodes.ALOAD, 1);
            // Push index on stack
            AsmUtil.pushInt(visitor, i);
            // Load provider from array and put to field
            visitor.visitInsn(Opcodes.AALOAD);
            visitor.visitFieldInsn(
                    Opcodes.PUTFIELD,
                    name,
                    order[i],
                    FUNCTION0.getDescriptor()
            );
        }
        visitor.visitInsn(Opcodes.RETURN);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private static void loadType(MethodVisitor visitor, String name, String field, java.lang.reflect.Type type) {
        // Load this-ref
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        // Load field
        visitor.visitFieldInsn(
                Opcodes.GETFIELD,
                name,
                field,
                FUNCTION0.getDescriptor()
        );
        // Get type implementation
        visitor.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                FUNCTION0.getInternalName(),
                INVOKE.getName(),
                Type.getMethodDescriptor(INVOKE),
                true
        );
        // Cast implementation to given type
        AsmUtil.castReference(visitor, TypeUtil.getRawType(type));
    }

    private static void processExecutable(MethodVisitor visitor,
                                          String name,
                                          java.lang.reflect.Type[] parameters,
                                          Map<java.lang.reflect.Type, String> fields) {
        for (var parameter : parameters) {
            var field = fields.get(parameter);
            loadType(visitor, name, field, parameter);
        }
    }

    private static void generateInvokeMethod(ClassWriter writer,
                                             String name,
                                             ClassScheme scheme,
                                             Map<java.lang.reflect.Type, String> types) {
        // Prepare schemes
        var constructor = scheme.getConstructorScheme();
        var fields = scheme.getFieldSchemes();
        var methods = scheme.getMethodSchemes();
        var raw = scheme.getTarget();
        // Declare method signature
        var visitor = writer.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                INVOKE.getName(),
                Type.getMethodDescriptor(INVOKE),
                null,
                new String[]{Type.getInternalName(Throwable.class)}
        );
        visitor.visitCode();
        // Create instance of target class
        visitor.visitTypeInsn(Opcodes.NEW, Type.getInternalName(raw));
        visitor.visitInsn(Opcodes.DUP);
        // Invoke constructor by scheme
        processExecutable(visitor, name, constructor.getMapping(), types);
        AsmUtil.invoke(visitor, constructor.getTarget());
        // Process field schemes
        for (var field : fields) {
            // ref.<field> = (Type) this.<type>.invoke();
            visitor.visitInsn(Opcodes.DUP);
            var target = field.getTarget();
            var type = field.getType();
            var source = types.get(type);
            loadType(visitor, name, source, type);
            visitor.visitFieldInsn(
                    Opcodes.PUTFIELD,
                    Type.getInternalName(raw),
                    target.getName(),
                    Type.getDescriptor(target.getType())
            );
        }
        // Process method schemes
        for (var method : methods) {
            visitor.visitInsn(Opcodes.DUP);
            processExecutable(visitor, name, method.getMapping(), types);
            AsmUtil.invoke(visitor, method.getTarget());
        }
        // Return constructed instance
        visitor.visitInsn(Opcodes.ARETURN);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private static byte[] generate(String name, ClassScheme scheme, Set<java.lang.reflect.Type> types) {
        // Init writer
        var writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        // Declare class
        writer.visit(
                Opcodes.V11,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                name,
                null,
                OBJECT_NAME,
                new String[]{FUNCTION0.getInternalName()}
        );
        // Generate type-field mapping
        var mapping = Mapping.of(types);
        // Declare fields
        for (var field : mapping.fields.keySet()) {
            writer.visitField(
                    Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                    field,
                    FUNCTION0.getDescriptor(),
                    null,
                    null
            );
        }
        // Generate constructor
        generateConstructor(writer, name, mapping.order);
        // Generate invoke method
        generateInvokeMethod(writer, name, scheme, mapping.types);
        // Close writer
        writer.visitEnd();
        // Return bytecode
        return writer.toByteArray();
    }

    private static Function0<?> instantiate(Class<?> clazz,
                                            Set<java.lang.reflect.Type> types,
                                            TypeProvider provider) throws Throwable {
        var constructor = clazz.getDeclaredConstructor(Function0[].class);
        if (types.isEmpty()) {
            return (Function0<?>) constructor.newInstance((Object) null);
        }
        var arguments = new Function0<?>[types.size()];
        var count = 0;
        for (var type : types) {
            arguments[count++] = Objects.requireNonNull(provider.apply(type));
        }
        return (Function0<?>) constructor.newInstance((Object) arguments);
    }

    @Override
    public Function0<?> create(ClassScheme scheme, TypeProvider provider) {
        Objects.requireNonNull(scheme);
        Objects.requireNonNull(provider);
        var target = scheme.getTarget();
        var name = target.getName() + STUB;
        var types = scheme.getTypes();
        return factory.create(
                name,
                () -> generate(Type.getInternalName(target) + STUB, scheme, types),
                clazz -> instantiate(clazz, types, provider)
        );
    }
}
