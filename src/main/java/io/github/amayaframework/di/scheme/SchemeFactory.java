package io.github.amayaframework.di.scheme;

/**
 * An interface describing an abstract factory that creates schemes for the specified classes.
 */
public interface SchemeFactory {

    /**
     * Creates a scheme for the specified class.
     *
     * @param clazz the specified class, must be non-null
     * @return the created scheme
     * @throws IllegalTypeException   if a type has been detected that cannot be used as an artifact (optional)
     * @throws IllegalMemberException if a class member has been detected that cannot be used for injection (optional)
     * @throws IllegalClassException  if it is not possible to build a schema for the specified class (optional)
     */
    ClassScheme create(Class<?> clazz);
}
