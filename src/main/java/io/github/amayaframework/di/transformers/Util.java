package io.github.amayaframework.di.transformers;

import io.github.amayaframework.di.containers.Container;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import static com.github.romanqed.jeflect.lambdas.AsmUtil.EMPTY_DESCRIPTOR;
import static com.github.romanqed.jeflect.lambdas.AsmUtil.INIT;

final class Util {
    static final String GET_DESCRIPTOR = makeGetDescriptor();
    static final String GET_METHOD_NAME = "get";
    static final String PUT_DESCRIPTOR = makePutDescriptor();
    static final String PUT_METHOD_NAME = "put";
    static final Type OBJECT_TYPE = Type.getType(Object.class);
    static final Type THROWABLE_TYPE = Type.getType(Throwable.class);
    static final Type CONTAINER_TYPE = Type.getType(Container.class);

    private static String makePutDescriptor() {
        try {
            return Type.getMethodDescriptor(Container.class.getMethod("put", Integer.class, Object.class));
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("It is impossible to find get method for extracting the descriptor");
        }
    }

    private static String makeGetDescriptor() {
        try {
            return Type.getMethodDescriptor(Container.class.getMethod("get", Integer.class));
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("It is impossible to find get method for extracting the descriptor");
        }
    }

    static void newObject(MethodVisitor visitor, String typeName) {
        visitor.visitTypeInsn(Opcodes.NEW, typeName);
        visitor.visitInsn(Opcodes.DUP);
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, typeName, INIT, EMPTY_DESCRIPTOR, false);
    }

    static void synchronizedCall(LocalVariablesSorter visitor, Runnable body) {
        // Body labels
        Label bodyStartLabel = new Label();
        Label bodyEndLabel = new Label();
        // Handle labels
        Label handleLabel = new Label();
        Label throwLabel = new Label();
        // try-catches
        visitor.visitTryCatchBlock(bodyStartLabel, bodyEndLabel, handleLabel, null);
        visitor.visitTryCatchBlock(handleLabel, throwLabel, handleLabel, null);
        // Save lock to variable
        int varIndex = visitor.newLocal(Util.OBJECT_TYPE);
        visitor.visitInsn(Opcodes.DUP);
        visitor.visitVarInsn(Opcodes.ASTORE, varIndex);
        // Enter
        visitor.visitInsn(Opcodes.MONITORENTER);
        visitor.visitLabel(bodyStartLabel);
        // Generate body
        body.run();
        visitor.visitVarInsn(Opcodes.ALOAD, varIndex);
        visitor.visitInsn(Opcodes.MONITOREXIT);
        visitor.visitLabel(bodyEndLabel);
        Label gotoLabel = new Label();
        visitor.visitJumpInsn(Opcodes.GOTO, gotoLabel);
        visitor.visitLabel(handleLabel);
        visitor.visitVarInsn(Opcodes.ASTORE, varIndex + 1);
        visitor.visitVarInsn(Opcodes.ALOAD, varIndex);
        visitor.visitInsn(Opcodes.MONITOREXIT);
        visitor.visitLabel(throwLabel);
        visitor.visitVarInsn(Opcodes.ALOAD, varIndex + 1);
        visitor.visitInsn(Opcodes.ATHROW);
        visitor.visitLabel(gotoLabel);
    }
}
