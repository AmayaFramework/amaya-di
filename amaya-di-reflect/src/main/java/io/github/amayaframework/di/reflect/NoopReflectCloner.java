package io.github.amayaframework.di.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * TODO
 */
public final class NoopReflectCloner implements ReflectCloner {

    @Override
    public Constructor<?> clone(Constructor<?> constructor) {
        return constructor;
    }

    @Override
    public Method clone(Method method) {
        return method;
    }

    @Override
    public Field clone(Field field) {
        return field;
    }
}
