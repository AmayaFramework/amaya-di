package io.github.amayaframework.di.types;

/**
 * An interface describing a mechanism that analyzes a class suitable for injection.
 */
public interface InjectTypeFactory {
    /**
     * Analyzes the class and returns a structure describing its internal parts prepared for injection.
     *
     * @param clazz class for analysis
     * @return the resulting object of {@link InjectType}
     */
    InjectType getInjectType(Class<?> clazz);
}
