package io.github.amayaframework.di.scheme;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * An interface describing an abstract type processor
 * that performs their processing for subsequent use in the injection scheme.
 */
public interface TypeProcessor {

    /**
     * Processes the type and returns an implementation ready for use in the injection scheme.
     *
     * @param type    the specified type, must be non-null
     * @param element the annotated element that the type belongs to
     * @return the {@link Type} instance
     */
    Type process(Type type, AnnotatedElement element);
}
