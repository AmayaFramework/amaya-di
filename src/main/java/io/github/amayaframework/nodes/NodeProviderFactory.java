package io.github.amayaframework.nodes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Interface describing a universal {@link NodeProvider} factory.
 */
public interface NodeProviderFactory {
    /**
     * Creates a provider for types.
     *
     * @return the resulting provider
     */
    NodeProvider<Class<?>> getTypeProvider();

    /**
     * Creates a provider of constructors belonging to a specific class.
     *
     * @param clazz the class that owns the required nodes
     * @return the resulting provider
     */
    NodeProvider<Constructor<?>> getConstructorProvider(Class<?> clazz);

    /**
     * Creates a provider of methods belonging to a specific class.
     *
     * @param clazz the class that owns the required nodes
     * @return the resulting provider
     */
    NodeProvider<Method> getMethodProvider(Class<?> clazz);

    /**
     * Creates a provider of fields belonging to a specific class.
     *
     * @param clazz the class that owns the required nodes
     * @return the resulting provider
     */
    NodeProvider<Field> getFieldProvider(Class<?> clazz);
}
