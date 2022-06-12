package io.github.amayaframework.di;

import org.atteo.classindex.IndexAnnotated;

import java.lang.annotation.*;

/**
 * An annotation indicating that the annotated class will be automatically found and registered as a subclass.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@IndexAnnotated
@Documented
public @interface Source {
}
