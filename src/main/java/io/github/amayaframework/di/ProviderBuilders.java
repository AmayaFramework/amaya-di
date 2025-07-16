package io.github.amayaframework.di;

import io.github.amayaframework.di.schema.ReflectSchemaFactory;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

/**
 * Utility class providing factory methods to create instances of {@link ServiceProviderBuilder}
 * and {@link ScopedProviderBuilder} with various configurations.
 *
 * <p>It offers preconfigured defaults for schema factory, cache mode, and validation checks,
 * as well as overloaded methods allowing customization of schema factories, stub factories,
 * and validation levels. The class covers both plain and checked builders, including scoped variants.</p>
 *
 * <p>This class serves as the main entry point for getting builder instances used to configure
 * and construct service providers within the DI framework.</p>
 */
public final class ProviderBuilders {
    /**
     * The default schema factory used for introspecting injection points.
     */
    public static final SchemaFactory SCHEMA_FACTORY = new ReflectSchemaFactory(Inject.class);

    /**
     * The default cache mode applied to built providers.
     */
    public static final CacheMode CACHE_MODE = CacheMode.FULL;

    private ProviderBuilders() {
    }

    // Base methods

    /**
     * Creates a {@link ServiceProviderBuilder} with the given schema factory, stub factory, and validation checks.
     *
     * @param schemaFactory the schema factory to use for dependency schemas
     * @param stubFactory   the stub factory to use for generating stubs (maybe null)
     * @param checks        bitmask of validation checks to apply during build (see {@link BuilderChecks})
     * @return a new instance of {@link ServiceProviderBuilder} with checking enabled
     */
    public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {
        if (checks == BuilderChecks.NO_CHECKS) {
            return new PlainProviderBuilder(schemaFactory, stubFactory, CACHE_MODE);
        }
        return new CheckedProviderBuilder(schemaFactory, stubFactory, CACHE_MODE, checks);
    }

    /**
     * Creates a {@link ServiceProviderBuilder} with the default schema factory,
     * no stub factory, and specified validation checks.
     *
     * @param checks bitmask of validation checks to apply during build
     * @return a new instance of {@link ServiceProviderBuilder} with checking enabled
     */
    public static ServiceProviderBuilder create(int checks) {
        if (checks == BuilderChecks.NO_CHECKS) {
            return new PlainProviderBuilder(SCHEMA_FACTORY, null, CACHE_MODE);
        }
        return new CheckedProviderBuilder(SCHEMA_FACTORY, null, CACHE_MODE, checks);
    }

    /**
     * Creates a plain {@link ServiceProviderBuilder} without validation checks,
     * using the provided schema factory and stub factory.
     *
     * @param schemaFactory the schema factory to use
     * @param stubFactory   the stub factory to use (maybe null)
     * @return a new instance of {@link ServiceProviderBuilder} without validation
     */
    public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory) {
        return new PlainProviderBuilder(schemaFactory, stubFactory, CACHE_MODE);
    }

    /**
     * Creates a plain {@link ServiceProviderBuilder} with default schema factory,
     * no stub factory, and no validation.
     *
     * @return a new instance of {@link ServiceProviderBuilder} without validation
     */
    public static ServiceProviderBuilder create() {
        return new PlainProviderBuilder(SCHEMA_FACTORY, null, CACHE_MODE);
    }

    /**
     * Creates a checked {@link ServiceProviderBuilder} with all validation checks enabled,
     * default schema factory, and no stub factory.
     *
     * @return a new instance of {@link ServiceProviderBuilder} with full validation
     */
    public static ServiceProviderBuilder createChecked() {
        return new CheckedProviderBuilder(SCHEMA_FACTORY, null, CACHE_MODE, BuilderChecks.VALIDATE_ALL);
    }

    /**
     * Creates a scoped {@link ScopedProviderBuilder} with specified schema factory,
     * stub factory, and validation checks.
     *
     * @param schemaFactory the schema factory to use
     * @param stubFactory   the stub factory to use (maybe null)
     * @param checks        bitmask of validation checks to apply
     * @return a new instance of {@link ScopedProviderBuilder} with checking enabled
     */
    public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {
        if (checks == BuilderChecks.NO_CHECKS) {
            return new PlainScopedProviderBuilder(schemaFactory, stubFactory, CACHE_MODE);
        }
        return new CheckedScopedProviderBuilder(schemaFactory, stubFactory, CACHE_MODE, checks);
    }

    /**
     * Creates a scoped {@link ScopedProviderBuilder} with default schema factory,
     * no stub factory, and specified validation checks.
     *
     * @param checks bitmask of validation checks to apply
     * @return a new instance of {@link ScopedProviderBuilder} with checking enabled
     */
    public static ScopedProviderBuilder createScoped(int checks) {
        if (checks == BuilderChecks.NO_CHECKS) {
            return new PlainScopedProviderBuilder(SCHEMA_FACTORY, null, CACHE_MODE);
        }
        return new CheckedScopedProviderBuilder(SCHEMA_FACTORY, null, CACHE_MODE, checks);
    }

    /**
     * Creates a plain scoped {@link ScopedProviderBuilder} without validation,
     * using the specified schema factory and stub factory.
     *
     * @param schemaFactory the schema factory to use
     * @param stubFactory   the stub factory to use (maybe null)
     * @return a new instance of {@link ScopedProviderBuilder} without validation
     */
    public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory) {
        return new PlainScopedProviderBuilder(schemaFactory, stubFactory, CACHE_MODE);
    }

    /**
     * Creates a plain scoped {@link ScopedProviderBuilder} with default schema factory,
     * no stub factory, and no validation.
     *
     * @return a new instance of {@link ScopedProviderBuilder} without validation
     */
    public static ScopedProviderBuilder createScoped() {
        return new PlainScopedProviderBuilder(SCHEMA_FACTORY, null, CACHE_MODE);
    }


    /**
     * Creates a checked scoped {@link ScopedProviderBuilder} with all validation checks enabled,
     * default schema factory, and no stub factory.
     *
     * @return a new instance of {@link ScopedProviderBuilder} with full validation
     */
    public static ScopedProviderBuilder createCheckedScoped() {
        return new CheckedScopedProviderBuilder(SCHEMA_FACTORY, null, CACHE_MODE, BuilderChecks.VALIDATE_ALL);
    }

    // Proxy methods for convenience when only stub factory or checks are specified

    /**
     * Creates a {@link ServiceProviderBuilder} with the given stub factory and validation checks,
     * using the default schema factory.
     *
     * @param stubFactory the stub factory to use (maybe null)
     * @param checks      bitmask of validation checks to apply
     * @return a new instance of {@link ServiceProviderBuilder} with checking enabled
     */
    public static ServiceProviderBuilder create(StubFactory stubFactory, int checks) {
        return create(SCHEMA_FACTORY, stubFactory, checks);
    }

    /**
     * Creates a plain {@link ServiceProviderBuilder} with the given stub factory,
     * default schema factory, and no validation.
     *
     * @param stubFactory the stub factory to use (maybe null)
     * @return a new instance of {@link ServiceProviderBuilder} without validation
     */
    public static ServiceProviderBuilder create(StubFactory stubFactory) {
        return create(SCHEMA_FACTORY, stubFactory);
    }

    /**
     * Creates a checked {@link ServiceProviderBuilder} with all validation checks enabled,
     * default schema factory and the given stub factory.
     *
     * @param stubFactory the stub factory to use (maybe null)
     * @return a new instance of {@link ServiceProviderBuilder} with full validation
     */
    public static ServiceProviderBuilder createChecked(StubFactory stubFactory) {
        return create(SCHEMA_FACTORY, stubFactory, BuilderChecks.VALIDATE_ALL);
    }

    /**
     * Creates a scoped {@link ScopedProviderBuilder} with the given stub factory and validation checks,
     * using the default schema factory.
     *
     * @param stubFactory the stub factory to use (maybe null)
     * @param checks      bitmask of validation checks to apply
     * @return a new instance of {@link ScopedProviderBuilder} with checking enabled
     */
    public static ScopedProviderBuilder createScoped(StubFactory stubFactory, int checks) {
        return createScoped(SCHEMA_FACTORY, stubFactory, checks);
    }

    /**
     * Creates a plain scoped {@link ScopedProviderBuilder} with the given stub factory,
     * default schema factory, and no validation.
     *
     * @param stubFactory the stub factory to use (maybe null)
     * @return a new instance of {@link ScopedProviderBuilder} without validation
     */
    public static ScopedProviderBuilder createScoped(StubFactory stubFactory) {
        return createScoped(SCHEMA_FACTORY, stubFactory);
    }

    /**
     * Creates a checked scoped {@link ScopedProviderBuilder} with all validation checks enabled,
     * default schema factory and the given stub factory.
     *
     * @param stubFactory the stub factory to use (maybe null)
     * @return a new instance of {@link ScopedProviderBuilder} with full validation
     */
    public static ScopedProviderBuilder createCheckedScoped(StubFactory stubFactory) {
        return createScoped(SCHEMA_FACTORY, stubFactory, BuilderChecks.VALIDATE_ALL);
    }
}
