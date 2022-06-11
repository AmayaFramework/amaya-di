package io.github.amayaframework.di;

import java.lang.annotation.*;

/**
 * Annotation indicating that the injected object will be instantiated once and then used in all subsequent injections.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Documented
public @interface Prototype {
}
