package io.github.amayaframework.nodes;

import java.util.List;

/**
 * Interface describing a universal node factory.
 */
public interface NodeFactory {
    /**
     * Creates a provider for types.
     *
     * @return the resulting list
     */
    List<? extends Node<?>> getTypes();

    /**
     * Creates a provider of constructors belonging to a specific class.
     *
     * @param clazz the class that owns the required nodes
     * @return the resulting list
     */
    List<? extends Node<?>> getConstructors(Class<?> clazz);

    /**
     * Creates a provider of methods belonging to a specific class.
     *
     * @param clazz the class that owns the required nodes
     * @return the resulting list
     */
    List<? extends Node<?>> getMethods(Class<?> clazz);

    /**
     * Creates a provider of fields belonging to a specific class.
     *
     * @param clazz the class that owns the required nodes
     * @return the resulting list
     */
    List<? extends Node<?>> getFields(Class<?> clazz);
}
