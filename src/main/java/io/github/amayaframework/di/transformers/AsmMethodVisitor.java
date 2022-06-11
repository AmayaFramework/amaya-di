package io.github.amayaframework.di.transformers;

import com.github.romanqed.jeflect.lambdas.AsmUtil;
import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.containers.Value;
import io.github.amayaframework.di.types.*;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class AsmMethodVisitor extends LocalVariablesSorter {
    private final SubTypeFactory factory;
    private final ProviderType provider;
    private final Type owner;
    private final Collection<InjectField> fields;
    private final Collection<InjectMethod> methods;
    private final Map<Class<?>, Integer> singletons;
    private int container = -1;

    AsmMethodVisitor(InjectType type,
                     SubTypeFactory factory,
                     ProviderType provider,
                     int access,
                     MethodVisitor visitor,
                     String descriptor) {
        super(Opcodes.ASM8, access, descriptor, visitor);
        this.factory = factory;
        this.provider = provider;
        this.owner = Type.getType(type.getTarget());
        this.fields = type.getFields();
        this.methods = type.getMethods();
        this.singletons = new HashMap<>();
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN) {
            fields.forEach(this::setField);
            methods.forEach(this::callMethod);
        }
        mv.visitInsn(opcode);
    }

    private Class<?> loadContainer() {
        Method method = provider.getMethod();
        if (container < 0) {
            container = newLocal(Util.CONTAINER_TYPE);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    Type.getInternalName(provider.getType()),
                    method.getName(),
                    Type.getMethodDescriptor(method),
                    false);
            mv.visitVarInsn(Opcodes.ASTORE, container);
        }
        mv.visitVarInsn(Opcodes.ALOAD, container);
        return method.getReturnType();
    }

    private void getFromContainer(int hashCode) {
        Class<?> container = loadContainer();
        mv.visitLdcInsn(hashCode);
        AsmUtil.packPrimitive(mv, Type.INT_TYPE);
        // Invoke get method from container
        boolean isInterface = container.isInterface();
        mv.visitMethodInsn(isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(container),
                Util.GET_METHOD_NAME,
                Util.GET_DESCRIPTOR,
                isInterface);
    }

    private void putToContainer(int hashCode, int varIndex) {
        Class<?> container = loadContainer();
        mv.visitLdcInsn(hashCode);
        AsmUtil.packPrimitive(mv, Type.INT_TYPE);
        mv.visitVarInsn(Opcodes.ALOAD, varIndex);
        // Invoke put method from container
        boolean isInterface = container.isInterface();
        mv.visitMethodInsn(isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(container),
                Util.PUT_METHOD_NAME,
                Util.PUT_DESCRIPTOR,
                isInterface);
    }

    private void setField(InjectField field) {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        // Body
        loadArgument(field);
        // Set field
        mv.visitFieldInsn(Opcodes.PUTFIELD,
                owner.getInternalName(),
                field.getName(),
                field.getType().getDescriptor());
    }

    private void callMethod(InjectMethod method) {
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        // Body
        loadArgument(method);
        // Invoke method
        Class<?> methodOwner = method.getMethod().getDeclaringClass();
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getType(methodOwner).getInternalName(),
                method.getName(),
                method.getMethodType().getDescriptor(),
                methodOwner.isInterface());
    }

    private void loadSingleton(Class<?> subType) {
        Integer varIndex = singletons.get(subType);
        if (varIndex == null) {
            // Create variable
            varIndex = newLocal(Util.OBJECT_TYPE);
            singletons.put(subType, varIndex);
            // Get from container
            getFromContainer(subType.hashCode());
            // Save to variable
            mv.visitVarInsn(Opcodes.ASTORE, varIndex);
            // if var == null
            mv.visitVarInsn(Opcodes.ALOAD, varIndex);
            Label label = new Label();
            mv.visitJumpInsn(Opcodes.IFNONNULL, label);
            Util.newObject(mv, Type.getInternalName(subType));
            mv.visitVarInsn(Opcodes.ASTORE, varIndex);
            putToContainer(subType.hashCode(), varIndex);
            mv.visitInsn(Opcodes.POP);
            mv.visitLabel(label);
        }
        // Load from variable
        mv.visitVarInsn(Opcodes.ALOAD, varIndex);
    }

    private void loadArgument(InjectMember member) {
        InjectPolicy policy = member.getPolicy();
        Class<?> subType = factory.getSubType(member.getClazz());
        // Prototype
        if (policy == InjectPolicy.PROTOTYPE) {
            String typeName = Type.getInternalName(subType);
            Util.newObject(mv, typeName);
            return;
        }
        // Singleton or value
        if (policy == InjectPolicy.SINGLETON) {
            loadSingleton(subType);
        } else {
            getFromContainer(Value.hashCode(member.getValue(), subType));
        }
        // Cast to expected type
        mv.visitTypeInsn(Opcodes.CHECKCAST, member.getType().getInternalName());
    }
}
