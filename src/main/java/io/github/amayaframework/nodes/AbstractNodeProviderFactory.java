package io.github.amayaframework.nodes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

public abstract class AbstractNodeProviderFactory implements NodeProviderFactory {
    protected abstract Iterable<Class<?>> getTypes();

    protected abstract Iterable<Method> getMethods(Class<?> clazz);

    protected abstract Iterable<Constructor<?>> getConstructors(Class<?> clazz);

    protected abstract Iterable<Field> getFields(Class<?> clazz);

    protected abstract <E> Collection<E> createCollection();

    @Override
    public NodeProvider<Class<?>> getTypeProvider() {
        return predicate -> {
            Collection<Class<?>> ret = createCollection();
            for (Class<?> clazz : getTypes()) {
                if (predicate.test(clazz)) {
                    ret.add(clazz);
                }
            }
            return ret;
        };
    }

    @Override
    public NodeProvider<Constructor<?>> getConstructorProvider(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        return predicate -> {
            Collection<Constructor<?>> ret = createCollection();
            for (Constructor<?> constructor : getConstructors(clazz)) {
                if (predicate.test(constructor)) {
                    ret.add(constructor);
                }
            }
            return ret;
        };
    }

    @Override
    public NodeProvider<Method> getMethodProvider(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        return predicate -> {
            Collection<Method> ret = createCollection();
            for (Method method : getMethods(clazz)) {
                if (predicate.test(method)) {
                    ret.add(method);
                }
            }
            return ret;
        };
    }

    @Override
    public NodeProvider<Field> getFieldProvider(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        return predicate -> {
            Collection<Field> ret = createCollection();
            for (Field field : getFields(clazz)) {
                if (predicate.test(field)) {
                    ret.add(field);
                }
            }
            return ret;
        };
    }
}
