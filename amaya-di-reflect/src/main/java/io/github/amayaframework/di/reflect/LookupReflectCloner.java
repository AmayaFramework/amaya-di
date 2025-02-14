package io.github.amayaframework.di.reflect;

import java.lang.reflect.*;

/**
 * TODO
 */
public final class LookupReflectCloner implements ReflectCloner {

    private static boolean isPublic(Member object) {
        return Modifier.isPublic(object.getModifiers());
    }

    @Override
    public Constructor<?> clone(Constructor<?> constructor) {
        var owner = constructor.getDeclaringClass();
        var constructors = isPublic(constructor) ? owner.getConstructors() : owner.getDeclaredConstructors();
        for (var entry : constructors) {
            if (constructor.equals(entry)) {
                return entry;
            }
        }
        throw new IllegalStateException("Cannot found given constructor, the JVM may be corrupted");
    }

    @Override
    public Method clone(Method method) {
        var owner = method.getDeclaringClass();
        var methods = isPublic(method) ? owner.getMethods() : owner.getDeclaredMethods();
        for (var entry : methods) {
            if (method.equals(entry)) {
                return entry;
            }
        }
        throw new IllegalStateException("Cannot found given method, the JVM may be corrupted");
    }

    @Override
    public Field clone(Field field) {
        var owner = field.getDeclaringClass();
        var fields = isPublic(field) ? owner.getFields() : owner.getDeclaredFields();
        for (var entry : fields) {
            if (field.equals(entry)) {
                return entry;
            }
        }
        throw new IllegalStateException("Cannot found given field, the JVM may be corrupted");
    }
}
