package io.github.amayaframework.di;

import io.github.amayaframework.di.reflect.ReflectStubFactory;
import io.github.amayaframework.di.stub.StubFactory;
import org.junit.jupiter.api.Test;

public class ReflectScopedProviderBuilderTest extends ScopedProviderBuilderTest {
    private static final StubFactory FACTORY = new ReflectStubFactory();

    @Test
    public void testSingleService() {
        testSingleService(() -> ProviderBuilders.createScoped(FACTORY));
        testSingleService(() -> ProviderBuilders.createScoped(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testTwoServices() {
        testTwoServices(() -> ProviderBuilders.createScoped(FACTORY));
        testTwoServices(() -> ProviderBuilders.createScoped(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testComplexServices() {
        testComplexServices(() -> ProviderBuilders.createScoped(FACTORY));
        testComplexServices(() -> ProviderBuilders.createScoped(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testApp() {
        testApp(() -> ProviderBuilders.createScoped(FACTORY));
        testApp(() -> ProviderBuilders.createScoped(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testMissingType() {
        testMissingType(() -> ProviderBuilders.createScoped(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testCycle() {
        testCycle(() -> ProviderBuilders.createScoped(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testScopeOverride() {
        testScopeOverride(() -> ProviderBuilders.createScoped(FACTORY));
        testScopeOverride(() -> ProviderBuilders.createScoped(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testScopeOverridesConst() {
        testScopeOverridesConst(() -> ProviderBuilders.createScoped(FACTORY));
        testScopeOverridesConst(() -> ProviderBuilders.createScoped(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testScopeOverridesService() {
        testScopeOverridesService(() -> ProviderBuilders.createScoped(FACTORY));
        testScopeOverridesService(() -> ProviderBuilders.createScoped(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testComplexScopeOverridesService() {
        testComplexScopeOverridesService(() -> ProviderBuilders.createScoped(FACTORY));
        testComplexScopeOverridesService(() -> ProviderBuilders.createScoped(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testPromisedType() {
        testPromisedType(() -> ProviderBuilders.createScoped(FACTORY));
        testPromisedType(() -> ProviderBuilders.createScoped(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testScopedTypeNotFound() {
        testScopedTypeNotFound(() -> ProviderBuilders.createScoped(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testScopedCycle() {
        testScopedCycle(() -> ProviderBuilders.createScoped(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testCrossCycle() {
        testCrossCycle(() -> ProviderBuilders.createScoped(FACTORY, BuilderChecks.VALIDATE_ALL));
    }
}
