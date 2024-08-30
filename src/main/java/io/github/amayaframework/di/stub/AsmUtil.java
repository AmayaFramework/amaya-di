package io.github.amayaframework.di.stub;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class AsmUtil {

    private AsmUtil() {
    }

    public static void pushInt(MethodVisitor visitor, int value) {
        if (value >= -1 && value <= 5) {
            visitor.visitInsn(Opcodes.ICONST_M1 + value + 1);
            return;
        }
        if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            visitor.visitIntInsn(Opcodes.BIPUSH, value);
            return;
        }
        if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            visitor.visitIntInsn(Opcodes.SIPUSH, value);
            return;
        }
        visitor.visitLdcInsn(value);
    }

    public static void invoke(MethodVisitor visitor, Method method) {
        var owner = method.getDeclaringClass();
        var isInterface = owner.isInterface();
        var opcode = Modifier.isStatic(method.getModifiers()) ?
                Opcodes.INVOKESTATIC
                : (isInterface ?
                Opcodes.INVOKEINTERFACE
                : Opcodes.INVOKEVIRTUAL);
        visitor.visitMethodInsn(
                opcode,
                Type.getInternalName(owner),
                method.getName(),
                Type.getMethodDescriptor(method),
                isInterface
        );
    }

    public static void invoke(MethodVisitor visitor, Constructor<?> constructor) {
        visitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                Type.getInternalName(constructor.getDeclaringClass()),
                "<init>",
                Type.getConstructorDescriptor(constructor),
                false
        );
    }
}
