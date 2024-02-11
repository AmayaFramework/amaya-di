package io.github.amayaframework.di;

import java.lang.annotation.*;

/**
 * An annotation that is used as a default marker when building a
 * {@link io.github.amayaframework.di.scheme.ClassScheme}.
 * It can only be applied to constructors, fields, and methods.
 * It is inherited.
 * <br>
 * Simple usage example:
 * <pre>
 *     class MyApp {
 *         // Some internal ctor
 *         public MyApp() {...}
 *
 *         &#64;Inject
 *         public MyApp(Service1 service1) {...}
 *
 *         &#64;Inject
 *         public Service2 service2;
 *
 *         &#64;Inject
 *         public void setService3(Service3 dep) {...}
 *     }
 * </pre>
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD})
public @interface Inject {
}
