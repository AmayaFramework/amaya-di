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
    private final Map<Class<?>, Integer> instances;
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
        this.instances = new HashMap<>();
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN) {
            fields.forEach(this::setField);
            methods.forEach(this::callMethod);
        }
        mv.visitInsn(opcode);
    }

    private void loadContainer() {
        if (container >= 0) {
            return;
        }
        Method method = provider.getContainerMethod();
        container = newLocal(Util.CONTAINER_TYPE);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                Type.getInternalName(provider.getType()),
                method.getName(),
                Type.getMethodDescriptor(method),
                false);
        mv.visitVarInsn(Opcodes.ASTORE, container);
    }

    private void getFromContainer(int hashCode) {
        Class<?> containerType = provider.getContainerMethod().getReturnType();
        mv.visitVarInsn(Opcodes.ALOAD, container);
        mv.visitLdcInsn(hashCode);
        AsmUtil.packPrimitive(mv, Type.INT_TYPE);
        // Invoke get method from container
        boolean isInterface = containerType.isInterface();
        mv.visitMethodInsn(isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(containerType),
                Util.GET_METHOD_NAME,
                Util.GET_DESCRIPTOR,
                isInterface);
    }

    private void putToContainer(int hashCode, int varIndex) {
        Class<?> containerType = provider.getContainerMethod().getReturnType();
        mv.visitVarInsn(Opcodes.ALOAD, container);
        mv.visitLdcInsn(hashCode);
        AsmUtil.packPrimitive(mv, Type.INT_TYPE);
        mv.visitVarInsn(Opcodes.ALOAD, varIndex);
        // Invoke put method from container
        boolean isInterface = containerType.isInterface();
        mv.visitMethodInsn(isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(containerType),
                Util.PUT_METHOD_NAME,
                Util.PUT_DESCRIPTOR,
                isInterface);
    }

    private void setField(InjectField field) {
        // Body
        loadArgument(field, () -> mv.visitVarInsn(Opcodes.ALOAD, 0));
        // Set field
        mv.visitFieldInsn(Opcodes.PUTFIELD,
                owner.getInternalName(),
                field.getName(),
                field.getType().getDescriptor());
    }

    private void callMethod(InjectMethod method) {
        // Body
        loadArgument(method, () -> mv.visitVarInsn(Opcodes.ALOAD, 0));
        // Invoke method
        Class<?> methodOwner = method.getMethod().getDeclaringClass();
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getType(methodOwner).getInternalName(),
                method.getName(),
                method.getMethodType().getDescriptor(),
                methodOwner.isInterface());
    }

    private int initSingleton(Class<?> subType) {
        // Create variable
        int ret = newLocal(Util.OBJECT_TYPE);
        // Get lock from provider
        Method lockMethod = provider.getLockMethod();
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                Type.getInternalName(provider.getType()),
                lockMethod.getName(),
                Type.getMethodDescriptor(lockMethod),
                false);
        Util.synchronizedCall(this, () -> {
            getFromContainer(subType.hashCode());
            // Save to variable
            mv.visitVarInsn(Opcodes.ASTORE, ret);
            // if var == null
            mv.visitVarInsn(Opcodes.ALOAD, ret);
            Label label = new Label();
            mv.visitJumpInsn(Opcodes.IFNONNULL, label);
            Util.newObject(mv, Type.getInternalName(subType));
            mv.visitVarInsn(Opcodes.ASTORE, ret);
            loadContainer();
            putToContainer(subType.hashCode(), ret);
            mv.visitInsn(Opcodes.POP);
            mv.visitLabel(label);
        });
        instances.put(subType, ret);
        return ret;
    }

    private void loadArgument(InjectMember member, Runnable loader) {
        InjectPolicy policy = member.getPolicy();
        Class<?> subType = factory.getSubType(member.getClazz());
        // Prototype
        if (policy == InjectPolicy.PROTOTYPE) {
            loader.run();
            String typeName = Type.getInternalName(subType);
            Util.newObject(mv, typeName);
            return;
        }
        // Singleton or value
        if (policy == InjectPolicy.SINGLETON) {
            loadContainer();
            Integer varIndex = instances.get(subType);
            if (varIndex == null) {
                varIndex = initSingleton(subType);
            }
            loader.run();
            // Load from variable
            mv.visitVarInsn(Opcodes.ALOAD, varIndex);
        } else {
            loadContainer();
            loader.run();
            getFromContainer(Value.hashCode(member.getValue(), subType));
        }
        // Cast to expected type
        mv.visitTypeInsn(Opcodes.CHECKCAST, member.getType().getInternalName());
    }
}
