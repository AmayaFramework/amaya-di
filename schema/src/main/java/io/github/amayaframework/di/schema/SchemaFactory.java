package io.github.amayaframework.di.schema;

/**
 * A factory interface for producing {@link ClassSchema} instances based on Java class metadata.
 */
public interface SchemaFactory {

    /**
     * Creates a schema for the specified class.
     *
     * @param clazz the specified class, must be non-null
     * @return the created schema
     * @throws IllegalMemberException if a class member has been detected that cannot be used for injection (optional)
     * @throws IllegalClassException  if it is not possible to build a schema for the specified class (optional)
     */
    ClassSchema create(Class<?> clazz);
}
