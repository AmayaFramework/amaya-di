package io.github.amayaframework.nodes;

import org.atteo.classindex.ClassIndex;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

/**
 * {@link NodeProviderFactory} implementation, searching for classes using the ClassIndex library functionality.
 */
public final class IndexProviderFactory extends AbstractNodeProviderFactory {
    private final Class<? extends Annotation> annotation;

    public IndexProviderFactory(Class<? extends Annotation> annotation) {
        this.annotation = Objects.requireNonNull(annotation);
    }

    @Override
    protected Iterable<Class<?>> getTypes() {
        return ClassIndex.getAnnotated(annotation);
    }

    @Override
    protected Iterable<Method> getMethods(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredMethods());
    }

    @Override
    protected Iterable<Constructor<?>> getConstructors(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredConstructors());
    }

    @Override
    protected Iterable<Field> getFields(Class<?> clazz) {
        return Arrays.asList(clazz.getDeclaredFields());
    }

    @Override
    protected <E> Collection<E> createCollection() {
        return new LinkedList<>();
    }
}
