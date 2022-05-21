package io.github.amayaframework.nodes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Interface describing a universal {@link NodeProvider} factory.
 */
public interface NodeProviderFactory {
    /**
     * Combines two provider factories into one.
     *
     * @param left  factory to be executed first
     * @param right factory to be executed second
     * @return the result of the merger
     */
    static NodeProviderFactory combine(NodeProviderFactory left, NodeProviderFactory right) {
        return new NodeProviderFactory() {
            @Override
            public NodeProvider<Class<?>> getTypeProvider() {
                return NodeProvider.combine(left.getTypeProvider(), right.getTypeProvider());
            }

            @Override
            public NodeProvider<Constructor<?>> getConstructorProvider(Class<?> clazz) {
                return NodeProvider.combine(left.getConstructorProvider(clazz), right.getConstructorProvider(clazz));
            }

            @Override
            public NodeProvider<Method> getMethodProvider(Class<?> clazz) {
                return NodeProvider.combine(left.getMethodProvider(clazz), right.getMethodProvider(clazz));
            }

            @Override
            public NodeProvider<Field> getFieldProvider(Class<?> clazz) {
                return NodeProvider.combine(left.getFieldProvider(clazz), right.getFieldProvider(clazz));
            }
        };
    }

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

    /**
     * Combines the current provider factory with the received one.
     *
     * @param next factory to combine
     * @return the result of the merger
     */
    default NodeProviderFactory combine(NodeProviderFactory next) {
        return combine(this, next);
    }
}
