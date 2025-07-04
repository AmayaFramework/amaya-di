package io.github.amayaframework.di;

import io.github.amayaframework.di.scheme.ReflectionSchemeFactory;
import io.github.amayaframework.di.scheme.SchemeFactory;
import io.github.amayaframework.di.stub.StubFactory;

import java.lang.annotation.Annotation;

/**
 * A utility class that provides methods for creating all {@link ServiceProviderBuilder}
 * and {@link ManualProviderBuilder} implementations included in the current version of the framework.
 */
public final class ProviderBuilders {
    public static final SchemeFactory REFLECTION_FACTORY = new ReflectionSchemeFactory(Inject.class);

    private ProviderBuilders() {
    }

    /**
     * Creates {@link CheckedProviderBuilder} with the specified scheme, stub factories and check set.
     *
     * @param schemeFactory the specified scheme factory, must be non-null
     * @param stubFactory   the specified stub factory, must be non-null
     * @param checks        the specified set of applied checks
     * @return the {@link ServiceProviderBuilder} instance
     */
    public static ServiceProviderBuilder createChecked(
            SchemeFactory schemeFactory,
            StubFactory stubFactory,
            int checks) {
        return new CheckedProviderBuilder(schemeFactory, stubFactory, checks);
    }

    /**
     * Creates {@link CheckedProviderBuilder} with the specified scheme and stub factories.
     * Enables all available checks {@link CheckedProviderBuilder#VALIDATE_ALL}.
     *
     * @param schemeFactory the specified scheme factory, must be non-null
     * @param stubFactory   the specified stub factory, must be non-null
     * @return the {@link ServiceProviderBuilder} instance
     */
    public static ServiceProviderBuilder createChecked(SchemeFactory schemeFactory, StubFactory stubFactory) {
        return new CheckedProviderBuilder(schemeFactory, stubFactory);
    }

    /**
     * Creates {@link CheckedProviderBuilder} instance with the specified stub factory,
     * {@link ReflectionSchemeFactory}, using the specified annotation as marker and check set.
     *
     * @param annotation the specified annotation, must be non-null
     * @param factory    the specified stub factory, must be non-null
     * @param checks     the specified set of applied checks
     * @return the {@link ServiceProviderBuilder} instance
     */
    public static ServiceProviderBuilder createChecked(
            Class<? extends Annotation> annotation,
            StubFactory factory,
            int checks) {
        return createChecked(new ReflectionSchemeFactory(annotation), factory, checks);
    }

    /**
     * Creates {@link CheckedProviderBuilder} instance with the specified stub factory and
     * {@link ReflectionSchemeFactory}, using the specified annotation as marker.
     * Enables all available checks {@link CheckedProviderBuilder#VALIDATE_ALL}.
     *
     * @param annotation the specified annotation, must be non-null
     * @param factory    the specified stub factory, must be non-null
     * @return the {@link ServiceProviderBuilder} instance
     */
    public static ServiceProviderBuilder createChecked(Class<? extends Annotation> annotation, StubFactory factory) {
        return createChecked(new ReflectionSchemeFactory(annotation), factory);
    }

    /**
     * Creates {@link CheckedProviderBuilder} instance with the specified stub factory,
     * {@link ReflectionSchemeFactory}, using {@link Inject} annotation as marker and check set.
     *
     * @param factory the specified stub factory, must be non-null
     * @param checks  the specified set of applied checks
     * @return the {@link ServiceProviderBuilder} instance
     */
    public static ServiceProviderBuilder createChecked(StubFactory factory, int checks) {
        return createChecked(REFLECTION_FACTORY, factory, checks);
    }

    /**
     * Creates {@link CheckedProviderBuilder} instance with the specified stub factory and
     * {@link ReflectionSchemeFactory}, using {@link Inject} annotation as marker.
     * Enables all available checks {@link CheckedProviderBuilder#VALIDATE_ALL}.
     *
     * @param factory the specified stub factory, must be non-null
     * @return the {@link ServiceProviderBuilder} instance
     */
    public static ServiceProviderBuilder createChecked(StubFactory factory) {
        return createChecked(REFLECTION_FACTORY, factory);
    }

    /**
     * Creates {@link ManualCheckedProviderBuilder} with the specified scheme, stub factories and check set.
     *
     * @param schemeFactory the specified scheme factory, must be non-null
     * @param stubFactory   the specified stub factory, must be non-null
     * @param checks        the specified set of applied checks
     * @return the {@link ManualProviderBuilder} instance
     */
    public static ManualProviderBuilder createManual(SchemeFactory schemeFactory, StubFactory stubFactory, int checks) {
        return new ManualCheckedProviderBuilder(schemeFactory, stubFactory, checks);
    }

    /**
     * Creates {@link ManualCheckedProviderBuilder} with the specified scheme and stub factories.
     * Enables all available checks {@link CheckedProviderBuilder#VALIDATE_ALL}.
     *
     * @param schemeFactory the specified scheme factory, must be non-null
     * @param stubFactory   the specified stub factory, must be non-null
     * @return the {@link ManualProviderBuilder} instance
     */
    public static ManualProviderBuilder createManual(SchemeFactory schemeFactory, StubFactory stubFactory) {
        return new ManualCheckedProviderBuilder(schemeFactory, stubFactory);
    }

    /**
     * Creates {@link ManualCheckedProviderBuilder} instance with the specified stub factory,
     * {@link ReflectionSchemeFactory}, using the specified annotation as marker and check set.
     *
     * @param annotation the specified annotation, must be non-null
     * @param factory    the specified stub factory, must be non-null
     * @param checks     the specified set of applied checks
     * @return the {@link ServiceProviderBuilder} instance
     */
    public static ManualProviderBuilder createManual(
            Class<? extends Annotation> annotation,
            StubFactory factory,
            int checks) {
        return createManual(new ReflectionSchemeFactory(annotation), factory, checks);
    }

    /**
     * Creates {@link ManualCheckedProviderBuilder} instance with the specified stub factory and
     * {@link ReflectionSchemeFactory}, using the specified annotation as marker.
     * Enables all available checks {@link CheckedProviderBuilder#VALIDATE_ALL}.
     *
     * @param annotation the specified annotation, must be non-null
     * @param factory    the specified stub factory, must be non-null
     * @return the {@link ServiceProviderBuilder} instance
     */
    public static ManualProviderBuilder createManual(Class<? extends Annotation> annotation, StubFactory factory) {
        return createManual(new ReflectionSchemeFactory(annotation), factory);
    }

    /**
     * Creates {@link ManualCheckedProviderBuilder} instance with the specified stub factory,
     * {@link ReflectionSchemeFactory}, using {@link Inject} annotation as marker and check set.
     *
     * @param factory the specified stub factory, must be non-null
     * @param checks  the specified set of applied checks
     * @return the {@link ServiceProviderBuilder} instance
     */
    public static ManualProviderBuilder createManual(StubFactory factory, int checks) {
        return createManual(REFLECTION_FACTORY, factory, checks);
    }

    /**
     * Creates {@link ManualCheckedProviderBuilder} instance with the specified stub factory and
     * {@link ReflectionSchemeFactory}, using {@link Inject} annotation as marker.
     * Enables all available checks {@link CheckedProviderBuilder#VALIDATE_ALL}.
     *
     * @param factory the specified stub factory, must be non-null
     * @return the {@link ServiceProviderBuilder} instance
     */
    public static ManualProviderBuilder createManual(StubFactory factory) {
        return createManual(REFLECTION_FACTORY, factory);
    }
}
