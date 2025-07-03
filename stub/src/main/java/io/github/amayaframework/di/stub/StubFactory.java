package io.github.amayaframework.di.stub;

import io.github.amayaframework.di.core.ObjectFactory;
import io.github.amayaframework.di.schema.ClassSchema;

/**
 * A factory interface for creating {@link ObjectFactory} instances
 * from class schemas.
 * <br>
 * Typically used to generate dependency-injection stubs for types
 * at runtime using metadata extracted from {@link ClassSchema}.
 */
public interface StubFactory {

    /**
     * Creates a new factory for the given class schema.
     * <p>
     * If {@code cached} is {@code false}, a plain {@link ObjectFactory} is returned.
     * If {@code cached} is {@code true}, a {@link CachedObjectFactory} is returned instead,
     * enabling internal caching of sub-factories and optimizations for repeated resolution.
     *
     * @param schema the class schema, must be non-null
     * @param mode   the caching mode to apply when building the factory; controls whether and how
     *               dependency sub-factories are cached internally
     * @return an {@link ObjectFactory} or {@link CachedObjectFactory}, depending on {@code cached}
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
