package io.github.amayaframework.di.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * TODO
 */
public interface ReflectCloner {

    /**
     * TODO
     *
     * @param constructor
     * @return
     */
    Constructor<?> clone(Constructor<?> constructor);

    /**
     * TODO
     *
     * @param method
     * @return
     */
    Method clone(Method method);

    /**
     * TODO
     *
     * @param field
     * @return
     */
    Field clone(Field field);
}
