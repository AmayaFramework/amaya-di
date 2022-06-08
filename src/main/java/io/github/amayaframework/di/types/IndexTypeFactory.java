package io.github.amayaframework.di.types;

import io.github.amayaframework.di.Source;
import org.atteo.classindex.ClassIndex;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class IndexTypeFactory implements SubTypeFactory {
    private final List<Class<?>> sources;
    private final Map<Class<?>, Class<?>> types;

    public IndexTypeFactory() {
        this.sources = findSubTypes();
        this.types = new ConcurrentHashMap<>();
    }

    private static List<Class<?>> findSubTypes() {
        Iterable<Class<?>> found = ClassIndex.getAnnotated(Source.class);
        List<Class<?>> ret = new LinkedList<>();
        for (Class<?> clazz : found) {
            if (Modifier.isAbstract(clazz.getModifiers())) {
                throw new IllegalStateException(String.format("Source %s cannot be abstract", clazz.getName()));
            }
            ret.add(clazz);
        }
        return ret;
    }

    private Class<?> findSubType(Class<?> clazz) {
        List<Class<?>> classes = sources
                .stream()
                .filter(clazz::isAssignableFrom)
                .collect(Collectors.toList());
        if (classes.isEmpty()) {
            return null;
        }
        if (classes.size() != 1) {
            throw new IllegalStateException("Several sub-types found");
        }
        return classes.get(0);
    }

    @Override
    public Class<?> getSubType(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        Class<?> ret = types.get(clazz);
        if (ret != null) {
            return ret;
        }
        ret = findSubType(clazz);
        if (ret != null) {
            types.put(clazz, ret);
            return ret;
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            throw new IllegalStateException("The type is abstract and has no subtypes");
        }
        return clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Class<? extends E> setSubType(Class<E> type, Class<? extends E> subType) {
        if (!type.isAssignableFrom(subType)) {
            throw new IllegalStateException("A subtype class is not an inheritor of a class");
        }
        return (Class<? extends E>) types.put(type, subType);
    }
}
