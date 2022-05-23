package io.github.amayaframework.di.transformers;

import io.github.amayaframework.di.annotations.InjectPolicy;
import io.github.amayaframework.di.types.*;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Collection;

import static io.github.amayaframework.di.transformers.Constants.*;

public class AsmMethodVisitor extends MethodVisitor {
    private final Type owner;
    private final Collection<InjectField> fields;
    private final Collection<InjectMethod> methods;
    private final SubTypeFactory factory;

    AsmMethodVisitor(MethodVisitor visitor, InjectType type, SubTypeFactory factory) {
        super(API, visitor);
        this.owner = Type.getType(type.getTarget());
        this.fields = type.getFields();
        this.methods = type.getMethods();
        this.factory = factory;
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
        // TODO
        Type subType = Type.getType(factory.getSubType(member.getClazz()));
        if (member.getPolicy() == InjectPolicy.PROTOTYPE) {
            super.visitTypeInsn(Opcodes.NEW, subType.getInternalName());
            super.visitInsn(Opcodes.DUP);
            super.visitMethodInsn(Opcodes.INVOKESPECIAL,
                    subType.getInternalName(),
                    INIT,
                    EMPTY_DESCRIPTOR,
                    false);
        }
    }
}
