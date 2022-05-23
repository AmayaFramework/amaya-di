package io.github.amayaframework.di.types;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface InjectMemberFactory {
    InjectField getField(Field field);

    InjectMethod getMethod(Method method);

    InjectConstructor getConstructor(Constructor<?> constructor);
}
