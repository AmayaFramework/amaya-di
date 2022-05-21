package io.github.amayaframework.di;

import java.lang.annotation.*;

/**
 * <p>An annotation that allows you to specify the parameter in which the value will be embedded.</p>
 * <p>Used when injecting into a method or constructor.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
public @interface Required {
}
