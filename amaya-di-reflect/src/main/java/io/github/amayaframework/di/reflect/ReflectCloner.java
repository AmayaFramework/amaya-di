package io.github.amayaframework.di.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * An interface describing an abstract cloner of member entities from the Reflection API.
 * The need to clone them arises from the existence of the
 * {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)} API,
 * which modifies the internal state of the instance of the member entity.
 */
public interface ReflectCloner {

    /**
     * Create instance of given {@link Constructor}.
     *
     * @param constructor the constructor to be cloned
     * @return the instance of given {@link Constructor}
     */
    Constructor<?> clone(Constructor<?> constructor);

    /**
     * Create instance of given {@link Method}.
     *
     * @param method the method to be cloned
     * @return the instance of given {@link Method}
     */
    Method clone(Method method);

    /**
     * Create instance of given {@link Field}.
     *
     * @param field the field to be cloned
     * @return the instance of given {@link Field}
     */
    Field clone(Field field);
}
