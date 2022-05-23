package io.github.amayaframework.di.annotations;

import java.lang.annotation.*;

/**
 * <p>An annotation indicating the target for the injection.</p>
 * <p>Allows you to specify the injection policy.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Documented
public @interface Inject {
    InjectPolicy value() default InjectPolicy.PROTOTYPE;
}
