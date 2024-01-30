package io.github.amayaframework.di.stub;

import com.github.romanqed.jeflect.*;
import com.github.romanqed.jfunc.Exceptions;
import com.github.romanqed.jfunc.Function0;
import io.github.amayaframework.di.Repository;
import io.github.amayaframework.di.scheme.Artifact;
import io.github.amayaframework.di.scheme.ClassScheme;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

public final class BytecodeStubFactory implements StubFactory {
    private static final String STUB = "Stub";
    private static final Type FUNCTION0 = Type.getType(Function0.class);
    private static final Method INVOKE = Exceptions.suppress(() -> Function0.class.getDeclaredMethod("invoke"));

    private final ObjectFactory<Function0<?>> factory;

    public BytecodeStubFactory(ObjectFactory<Function0<?>> factory) {
        this.factory = factory;
    }

    public BytecodeStubFactory(DefineLoader loader) {
        this.factory = new DefineObjectFactory<>(loader);
    }

    public BytecodeStubFactory() {
        this(new DefineClassLoader());
    }

    private static void generateConstructor(ClassWriter writer, String name, String[] order) {
        var visitor = writer.visitMethod(
                Opcodes.ACC_PUBLIC,
                AsmUtil.INIT,
                "([" + FUNCTION0.getDescriptor() + ")V",
                null,
                null
        );
        visitor.visitCode();
        // Call parent constructor
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                AsmUtil.OBJECT.getInternalName(),
                AsmUtil.INIT,
                AsmUtil.EMPTY_DESCRIPTOR,
                false
        );
        // Put artifact providers to fields
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

    private static void loadArtifact(MethodVisitor visitor, String name, String field, Artifact artifact) {
        // Load this-ref
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        // Load field
        visitor.visitFieldInsn(
                Opcodes.GETFIELD,
                name,
                field,
                FUNCTION0.getDescriptor()
        );
        // Get artifact implementation
        visitor.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                FUNCTION0.getInternalName(),
                INVOKE.getName(),
                Type.getMethodDescriptor(INVOKE),
                true
        );
        // Cast implementation to artifact type
        visitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(artifact.getType()));
    }

    private static void processExecutable(MethodVisitor visitor,
                                          String name,
                                          Artifact[] parameters,
                                          Map<Artifact, String> fields) {
        for (var parameter : parameters) {
            var field = fields.get(parameter);
            loadArtifact(visitor, name, field, parameter);
        }
    }

    private static void generateInvokeMethod(ClassWriter writer,
                                             String name,
                                             ClassScheme scheme,
                                             Map<Artifact, String> artifacts) {
        // Prepare schemes
        var constructor = scheme.getConstructorScheme();
        var fields = scheme.getFieldSchemes();
        var methods = scheme.getMethodSchemes();
        var type = scheme.getTarget();
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
        visitor.visitTypeInsn(Opcodes.NEW, Type.getInternalName(type));
        visitor.visitInsn(Opcodes.DUP);
        // Invoke constructor by scheme
        processExecutable(visitor, name, constructor.getMapping(), artifacts);
        AsmUtil.invoke(visitor, constructor.getTarget());
        // Process field schemes
        for (var field : fields) {
            // ref.<field> = (ArtifactType) this.<artifact>.invoke();
            visitor.visitInsn(Opcodes.DUP);
            var target = field.getTarget();
            var artifact = field.getArtifact();
            var source = artifacts.get(artifact);
            loadArtifact(visitor, name, source, artifact);
            visitor.visitFieldInsn(
                    Opcodes.PUTFIELD,
                    Type.getInternalName(type),
                    target.getName(),
                    Type.getDescriptor(target.getType())
            );
        }
        // Process method schemes
        for (var method : methods) {
            visitor.visitInsn(Opcodes.DUP);
            processExecutable(visitor, name, method.getMapping(), artifacts);
            AsmUtil.invoke(visitor, method.getTarget());
        }
        // Return constructed instance
        visitor.visitInsn(Opcodes.ARETURN);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private static byte[] generate(String name, ClassScheme scheme, Set<Artifact> artifacts) {
        // Init writer
        var writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        // Declare class
        writer.visit(
                Opcodes.V11,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                name,
                null,
                AsmUtil.OBJECT.getInternalName(),
                new String[]{FUNCTION0.getInternalName()}
        );
        // Generate artifact-field mapping
        var mapping = Mapping.of(artifacts);
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
        generateInvokeMethod(writer, name, scheme, mapping.artifacts);
        // Close writer
        writer.visitEnd();
        // Return bytecode
        return writer.toByteArray();
    }

    @Override
    public Function0<?> create(ClassScheme scheme, Repository repository) {
        var target = scheme.getTarget();
        var name = target.getName() + STUB;
        var artifacts = scheme.getArtifacts();
        return factory.create(
                name,
                () -> {
                    var internal = Type.getInternalName(target) + STUB;
                    return generate(internal, scheme, artifacts);
                },
                clazz -> {
                    var constructor = clazz.getDeclaredConstructor(Function0[].class);
                    var arguments = new Function0<?>[artifacts.size()];
                    var count = 0;
                    for (var artifact : artifacts) {
                        arguments[count++] = repository.get(artifact);
                    }
                    return (Function0<?>) constructor.newInstance(new Object[]{arguments});
                }
        );
    }
}
