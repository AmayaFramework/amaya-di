package io.github.amayaframework.di.transformers;

import com.github.romanqed.jeflect.lambdas.AsmUtil;
import io.github.amayaframework.di.annotations.InjectPolicy;
import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.types.*;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

import static com.github.romanqed.jeflect.lambdas.AsmUtil.EMPTY_DESCRIPTOR;
import static com.github.romanqed.jeflect.lambdas.AsmUtil.INIT;

class AsmMethodVisitor extends MethodVisitor {
    private static final String DESCRIPTOR = AsmUtil.formatDescriptor("L" + AsmUtil.OBJECT + ";", "I");
    private static final String VALUE = "getValue";
    private static final String SINGLETON = "getSingleton";
    private final Type owner;
    private final Collection<InjectField> fields;
    private final Collection<InjectMethod> methods;
    private final SubTypeFactory factory;
    private final ProviderType provider;

    AsmMethodVisitor(MethodVisitor visitor, InjectType type, SubTypeFactory factory, ProviderType provider) {
        super(Opcodes.ASM8, visitor);
        this.owner = Type.getType(type.getTarget());
        this.fields = type.getFields();
        this.methods = type.getMethods();
        this.factory = factory;
        this.provider = provider;
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN) {
            fields.forEach(this::setField);
            methods.forEach(this::callMethod);
        }
        super.visitInsn(opcode);
    }

    private void setField(InjectField field) {
        super.visitVarInsn(Opcodes.ALOAD, 0);
        // Body
        loadArgument(field);
        // Set field
        super.visitFieldInsn(Opcodes.PUTFIELD,
                owner.getInternalName(),
                field.getName(),
                field.getType().getDescriptor());
    }

    private void callMethod(InjectMethod method) {
        super.visitVarInsn(Opcodes.ALOAD, 0);
        // Body
        loadArgument(method);
        // Invoke method
        Class<?> methodOwner = method.getMethod().getDeclaringClass();
        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                Type.getType(methodOwner).getInternalName(),
                method.getName(),
                method.getMethodType().getDescriptor(),
                methodOwner.isInterface());
    }

    private void loadArgument(InjectMember member) {
        InjectPolicy policy = member.getPolicy();
        // Prototype
        if (policy == InjectPolicy.PROTOTYPE) {
            Type subType = Type.getType(factory.getSubType(member.getClazz()));
            super.visitTypeInsn(Opcodes.NEW, subType.getInternalName());
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL,
                    subType.getInternalName(),
                    INIT,
                    EMPTY_DESCRIPTOR,
                    false);
            return;
        }
        // Singleton and field
        // Load container from provider
        Method method = provider.getMethod();
        super.visitMethodInsn(Opcodes.INVOKESTATIC,
                Type.getInternalName(provider.getType()),
                method.getName(),
                Type.getMethodDescriptor(method),
                false);
        super.visitLdcInsn(Objects.hash(member.getValue(), member.getClazz()));
        Class<?> container = method.getReturnType();
        // Invoke necessary method from container
        boolean isInterface = container.isInterface();
        super.visitMethodInsn(isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL,
                Type.getInternalName(container),
                policy == InjectPolicy.SINGLETON ? SINGLETON : VALUE,
                DESCRIPTOR,
                isInterface);
        // Cast to expected type
        super.visitTypeInsn(Opcodes.CHECKCAST, member.getType().getInternalName());
    }
}
