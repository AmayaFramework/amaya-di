package io.github.amayaframework.di;

import java.lang.annotation.*;

/**
 * Annotation indicating that the injected object will be searched in the shared object storage
 * by the name of the target or by the name specified using this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Documented
public @interface Value {
    String value() default "";
}
