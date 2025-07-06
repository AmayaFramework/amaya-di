package io.github.amayaframework.di.schema;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * A processor that normalizes Java types (e.g., resolves generics, wildcards, etc.)
 * for consistent use in dependency injection schemas.
 */
public interface TypeProcessor {

    /**
     * Processes the type and returns an implementation ready for use in the injection schema.
     *
     * @param type    the specified type, must be non-null
     * @param element the annotated element that the type belongs to
     * @return the {@link Type} instance
     */
    Type process(Type type, AnnotatedElement element);
}
