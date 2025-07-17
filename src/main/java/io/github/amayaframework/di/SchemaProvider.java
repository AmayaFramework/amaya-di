package io.github.amayaframework.di;

import io.github.amayaframework.di.schema.ClassSchema;

import java.lang.reflect.Type;

/**
 * Represents a factory interface for obtaining {@link ClassSchema} instances for service implementations.
 * <p>
 * A {@code SchemaProvider} is used internally by the DI container during the build process
 * to generate schemas for services based on their declared implementation classes.
 * It allows for custom logic in how schemas are produced, potentially based on additional metadata,
 * type information, or pre-processing.
 * <p>
 * This abstraction is useful for decoupling schema generation from the rest of the building logic.
 */
@FunctionalInterface
public interface SchemaProvider {

    /**
     * Returns a {@link ClassSchema} representing the injection model for the specified implementation class.
     *
     * @param type the public service type (interface or abstract/base type)
     * @param impl the concrete implementation class to generate a schema for
     * @return a {@link ClassSchema} describing how to instantiate and inject dependencies into {@code impl}
     */
    ClassSchema get(Type type, Class<?> impl);
}
