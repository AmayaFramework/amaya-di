package io.github.amayaframework.di.transformers;

import com.github.romanqed.jeflect.lambdas.AsmUtil;
import io.github.amayaframework.di.containers.Container;
import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.containers.Value;
import io.github.amayaframework.di.types.*;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

import static com.github.romanqed.jeflect.lambdas.AsmUtil.EMPTY_DESCRIPTOR;
import static com.github.romanqed.jeflect.lambdas.AsmUtil.INIT;

class AsmClassVisitor extends ClassVisitor {
    private static final String DESCRIPTOR = AsmUtil.formatDescriptor("L" + AsmUtil.OBJECT + ";", "I");
    private static final String METHOD_NAME = "get";
    private final InjectType type;
    private final SubTypeFactory factory;
    private final ProviderType provider;

    protected AsmClassVisitor(ClassVisitor visitor, InjectType type, SubTypeFactory factory, ProviderType provider) {
        super(Opcodes.ASM8, visitor);
        this.type = type;
        this.factory = factory;
        this.provider = provider;
    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String descriptor,
                                     String signature,
                                     String[] exceptions) {
        MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (Objects.equals(name, INIT) && Objects.equals(descriptor, EMPTY_DESCRIPTOR)) {
            return new AsmMethodVisitor(visitor);
        }
        return visitor;
    }

    class AsmMethodVisitor extends MethodVisitor {
        private final Type owner;
        private final Collection<InjectField> fields;
        private final Collection<InjectMethod> methods;

        AsmMethodVisitor(MethodVisitor visitor) {
            super(Opcodes.ASM8, visitor);
            this.owner = Type.getType(type.getTarget());
            this.fields = type.getFields();
            this.methods = type.getMethods();
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
            Class<?> subType = factory.getSubType(member.getClazz());
            // Prototype
            if (policy == InjectPolicy.PROTOTYPE) {
                String typeName = Type.getInternalName(subType);
                super.visitTypeInsn(Opcodes.NEW, typeName);
                super.visitInsn(Opcodes.DUP);
                super.visitMethodInsn(Opcodes.INVOKESPECIAL, typeName, INIT, EMPTY_DESCRIPTOR, false);
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
            int hashcode;
            if (policy == InjectPolicy.SINGLETON) {
                hashcode = subType.hashCode();
                try {
                    Container container = (Container) method.invoke(null);
                    container.getSingleton(subType);
                } catch (Exception e) {
                    throw new IllegalStateException("Unable to get container from provider due to", e);
                }
            } else {
                hashcode = Value.hashcode(member.getValue(), subType);
            }
            super.visitLdcInsn(hashcode);
            Class<?> container = method.getReturnType();
            // Invoke necessary method from container
            boolean isInterface = container.isInterface();
            super.visitMethodInsn(isInterface ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL,
                    Type.getInternalName(container),
                    METHOD_NAME,
                    DESCRIPTOR,
                    isInterface);
            // Cast to expected type
            super.visitTypeInsn(Opcodes.CHECKCAST, member.getType().getInternalName());
        }
    }
}
