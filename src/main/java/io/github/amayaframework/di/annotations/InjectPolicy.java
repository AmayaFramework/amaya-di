package io.github.amayaframework.di.annotations;

/**
 * An enumeration containing supported injection policies.
 */
public enum InjectPolicy {
    /**
     * The injected object will be instantiated once and then used in all subsequent injections.
     */
    SINGLETON,

    /**
     * The injected object will be instantiated every time.
     */
    PROTOTYPE,

    /**
     * The injected object will be searched in the shared object storage
     * by the name of the target or by the name specified using the {@link Value} annotation.
     */
    VALUE
}
