package io.github.amayaframework.di;

import java.lang.annotation.*;

/**
 * An annotation that should be present in any class in which dependencies are injected.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Inject {
}
