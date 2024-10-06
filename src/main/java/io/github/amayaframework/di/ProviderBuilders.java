package io.github.amayaframework.di;

import io.github.amayaframework.di.scheme.ReflectionSchemeFactory;
import io.github.amayaframework.di.scheme.SchemeFactory;
import io.github.amayaframework.di.stub.BytecodeStubFactory;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.annotation.Annotation;

/**
 * A utility class that provides methods for creating all {@link ServiceProviderBuilder}
 * and {@link ManualProviderBuilder} implementations included in the current version of the framework.
 */
public final class ProviderBuilders {
    private static final SchemeFactory REFLECTION_FACTORY = new ReflectionSchemeFactory(Inject.class);
    private static final StubFactory BYTECODE_FACTORY = new BytecodeStubFactory();

    private ProviderBuilders() {
    }

    /**
     * Creates {@link CheckedProviderBuilder} with the specified scheme and stub factories.
     *
     * @param schemeFactory the specified scheme factory, must be non-null
     * @param stubFactory   the specified stub factory, must be non-null
     * @return the {@link ServiceProviderBuilder} instance
     */
    public static ServiceProviderBuilder createChecked(SchemeFactory schemeFactory, StubFactory stubFactory) {
        return new CheckedProviderBuilder(schemeFactory, stubFactory);
    }

    /**
     * Creates {@link CheckedProviderBuilder} instance
     * with {@link ReflectionSchemeFactory} and {@link BytecodeStubFactory}, using the specified annotation as marker.
     *
     * @param annotation the specified annotation, must be non-null
     * @return the {@link ServiceProviderBuilder} instance
     */
    public static ServiceProviderBuilder createChecked(Class<? extends Annotation> annotation) {
        return createChecked(new ReflectionSchemeFactory(annotation), BYTECODE_FACTORY);
    }

    /**
     * Creates {@link CheckedProviderBuilder} instance
     * with {@link ReflectionSchemeFactory} and {@link BytecodeStubFactory}, using {@link Inject} annotation as marker.
     *
     * @return the {@link ServiceProviderBuilder} instance
     */
    public static ServiceProviderBuilder createChecked() {
        return createChecked(REFLECTION_FACTORY, BYTECODE_FACTORY);
    }

    /**
     * Creates {@link ManualCheckedProviderBuilder} with the specified scheme and stub factories.
     *
     * @param schemeFactory the specified scheme factory, must be non-null
     * @param stubFactory   the specified stub factory, must be non-null
     * @return the {@link ManualProviderBuilder} instance
     */
    public static ManualProviderBuilder createManual(SchemeFactory schemeFactory, StubFactory stubFactory) {
        return new ManualCheckedProviderBuilder(schemeFactory, stubFactory);
    }

    /**
     * Creates {@link ManualCheckedProviderBuilder} instance
     * with {@link ReflectionSchemeFactory} and {@link BytecodeStubFactory}, using the specified annotation as marker.
     *
     * @param annotation the specified annotation, must be non-null
     * @return the {@link ManualProviderBuilder} instance
     */
    public static ManualProviderBuilder createManual(Class<? extends Annotation> annotation) {
        return createManual(new ReflectionSchemeFactory(annotation), BYTECODE_FACTORY);
    }

    /**
     * Creates {@link ManualCheckedProviderBuilder} instance
     * with {@link ReflectionSchemeFactory} and {@link BytecodeStubFactory}, using {@link Inject} annotation as marker.
     *
     * @return the {@link ManualProviderBuilder} instance
     */
    public static ManualProviderBuilder createManual() {
        return createManual(REFLECTION_FACTORY, BYTECODE_FACTORY);
    }
}
