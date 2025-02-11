package io.github.amayaframework.di.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

final class AsmUtil {
    static final Map<Class<?>, Class<?>> PRIMITIVES = Map.of(
            boolean.class, Boolean.class,
            char.class, Character.class,
            byte.class, Byte.class,
            short.class, Short.class,
            int.class, Integer.class,
            float.class, Float.class,
            long.class, Long.class,
            double.class, Double.class
    );
    static final Map<Class<?>, String> PRIMITIVE_METHODS = Map.of(
            boolean.class, "booleanValue",
            char.class, "charValue",
            byte.class, "byteValue",
            short.class, "shortValue",
            int.class, "intValue",
            float.class, "floatValue",
            long.class, "longValue",
            double.class, "doubleValue"
    );

    private AsmUtil() {
    }

    static void castReference(MethodVisitor visitor, Class<?> clazz) {
        if (clazz == Object.class) {
            return;
        }
        if (!clazz.isPrimitive()) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, Type.getInternalName(clazz));
            return;
        }
        var wrap = Type.getInternalName(PRIMITIVES.get(clazz));
        visitor.visitTypeInsn(Opcodes.CHECKCAST, wrap);
        var method = PRIMITIVE_METHODS.get(clazz);
        visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                wrap,
                method,
                "()" + Type.getDescriptor(clazz),
                false
        );
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
