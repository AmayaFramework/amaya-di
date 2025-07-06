package io.github.amayaframework.di;

import io.github.amayaframework.di.asm.AsmStubFactory;
import io.github.amayaframework.di.stub.StubFactory;
import org.junit.jupiter.api.Test;

public class AsmServiceProviderBuilderTest extends ServiceProviderBuilderTest {
    private static final StubFactory FACTORY = new AsmStubFactory();

    @Test
    public void testSingleService() {
        testSingleService(() -> ProviderBuilders.create(FACTORY));
        testSingleService(() -> ProviderBuilders.create(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testTwoServices() {
        testTwoServices(() -> ProviderBuilders.create(FACTORY));
        testTwoServices(() -> ProviderBuilders.create(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testComplexServices() {
        testComplexServices(() -> ProviderBuilders.create(FACTORY));
        testComplexServices(() -> ProviderBuilders.create(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testApp() {
        testApp(() -> ProviderBuilders.create(FACTORY));
        testApp(() -> ProviderBuilders.create(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testMissingType() {
        testMissingType(() -> ProviderBuilders.create(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testCycle() {
        testCycle(() -> ProviderBuilders.create(FACTORY, BuilderChecks.VALIDATE_ALL));
    }

    @Test
    public void testScopeOverride() {
        testScopeOverride(() -> ProviderBuilders.create(FACTORY));
        testScopeOverride(() -> ProviderBuilders.create(FACTORY, BuilderChecks.VALIDATE_ALL));
    }
}
