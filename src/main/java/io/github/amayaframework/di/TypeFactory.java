package io.github.amayaframework.di;

/**
 * An interface describing a factory looking for subtypes for types.
 */
public interface TypeFactory {
    /**
     * Searches for a subtype for the passed class.
     *
     * @param clazz the class for which the subtype will be searched
     * @return the found subtype or the class itself, if the subtypes were not found
     */
    Class<?> getSubType(Class<?> clazz);

    /**
     * Allows you to associate a class with its subtype.
     *
     * @param type    the class that is the main type
     * @param subType a class that is a subtype
     * @param <E>     type of parent class
     * @return previous subtype or null if the relationship did not exist
     */
    <E> Class<? extends E> setSubType(Class<E> type, Class<? extends E> subType);
}
