package io.github.amayaframework.di.stub;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.schema.ClassSchema;

/**
 * A factory interface for creating {@link ObjectFactory} instances
 * based on metadata extracted from {@link ClassSchema}.
 * <p>
 * Typically used to generate runtime injection stubs for user-defined types.
 */
@FunctionalInterface
public interface StubFactory {

    /**
     * Creates a new factory for the given class schema using the specified caching mode.
     * <p>
     * If {@code mode} is {@link CacheMode#NONE}, a plain {@link ObjectFactory} is returned.
     * Otherwise, a {@link CachedObjectFactory} is returned with internal caching behavior
     * based on the selected {@link CacheMode}.
     *
     * @param schema the class schema describing the target class and its dependencies, must be non-null
     * @param mode   the caching strategy to use for dependency resolution
     * @return an {@link ObjectFactory} or {@link CachedObjectFactory}, depending on the caching mode
     */
    ObjectFactory create(ClassSchema schema, CacheMode mode);

    /**
     * Creates a non-cached factory for the specified schema.
     * <p>
     * Equivalent to {@code create(schema, false)}.
     *
     * @param schema the class schema, must be non-null
     * @return a basic {@link ObjectFactory} instance without internal caching
     */
    default ObjectFactory create(ClassSchema schema) {
        return create(schema, CacheMode.NONE);
    }
}
