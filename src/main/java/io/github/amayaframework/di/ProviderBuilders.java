package io.github.amayaframework.di;

import io.github.amayaframework.di.schema.ReflectSchemaFactory;
import io.github.amayaframework.di.schema.SchemaFactory;
import io.github.amayaframework.di.stub.CacheMode;
import io.github.amayaframework.di.stub.StubFactory;

public final class ProviderBuilders {
    public static final SchemaFactory SCHEMA_FACTORY = new ReflectSchemaFactory(Inject.class);
    public static final CacheMode CACHE_MODE = CacheMode.FULL;

    private ProviderBuilders() {
    }

    // Base methods

    public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {
        return new CheckedProviderBuilder(schemaFactory, stubFactory, CACHE_MODE, checks);
    }

    public static ServiceProviderBuilder create(int checks) {
        return new CheckedProviderBuilder(SCHEMA_FACTORY, null, CACHE_MODE, checks);
    }

    public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory) {
        return new PlainProviderBuilder(schemaFactory, stubFactory, CACHE_MODE);
    }

    public static ServiceProviderBuilder create() {
        return new PlainProviderBuilder(SCHEMA_FACTORY, null, CACHE_MODE);
    }

    public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {
        return new CheckedScopedProviderBuilder(schemaFactory, stubFactory, CACHE_MODE, checks);
    }

    public static ScopedProviderBuilder createScoped(int checks) {
        return new CheckedScopedProviderBuilder(SCHEMA_FACTORY, null, CACHE_MODE, checks);
    }

    public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory) {
        return new PlainScopedProviderBuilder(schemaFactory, stubFactory, CACHE_MODE);
    }

    public static ScopedProviderBuilder createScoped() {
        return new PlainScopedProviderBuilder(SCHEMA_FACTORY, null, CACHE_MODE);
    }

    // Proxy methods

    public static ServiceProviderBuilder create(StubFactory stubFactory, int checks) {
        return create(SCHEMA_FACTORY, stubFactory, checks);
    }

    public static ServiceProviderBuilder create(StubFactory stubFactory) {
        return create(SCHEMA_FACTORY, stubFactory);
    }

    public static ScopedProviderBuilder createScoped(StubFactory stubFactory, int checks) {
        return createScoped(SCHEMA_FACTORY, stubFactory, checks);
    }

    public static ScopedProviderBuilder createScoped(StubFactory stubFactory) {
        return createScoped(SCHEMA_FACTORY, stubFactory);
    }
}
