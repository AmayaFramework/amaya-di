package io.github.amayaframework.di.transformers;

import io.github.amayaframework.di.containers.Container;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import static com.github.romanqed.jeflect.lambdas.AsmUtil.EMPTY_DESCRIPTOR;
import static com.github.romanqed.jeflect.lambdas.AsmUtil.INIT;

final class Util {
    static final String GET_DESCRIPTOR = makeGetDescriptor();
    static final String GET_METHOD_NAME = "get";
    static final String PUT_DESCRIPTOR = makePutDescriptor();
    static final String PUT_METHOD_NAME = "put";
    static final Type OBJECT_TYPE = Type.getType(Object.class);
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
}
