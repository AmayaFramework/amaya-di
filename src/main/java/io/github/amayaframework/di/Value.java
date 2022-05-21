package io.github.amayaframework.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An annotation that allows you to specify the name of the injected value when using {@link InjectPolicy#VALUE}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
    String value();
}
