package io.github.amayaframework.di.schema;

/**
 * An interface describing an abstract factory that creates scheme for the specified class.
 */
public interface SchemaFactory {

    /**
     * Creates a scheme for the specified class.
     *
     * @param clazz the specified class, must be non-null
     * @return the created scheme
     * @throws IllegalMemberException if a class member has been detected that cannot be used for injection (optional)
     * @throws IllegalClassException  if it is not possible to build a scheme for the specified class (optional)
     */
    ClassSchema create(Class<?> clazz);
}
