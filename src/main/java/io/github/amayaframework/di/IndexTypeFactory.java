package io.github.amayaframework.di;

import io.github.amayaframework.nodes.NodeProvider;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class IndexTypeFactory implements TypeFactory {
    private final Collection<Class<?>> sources;
    private final Map<Class<?>, Class<?>> types;

    public IndexTypeFactory(NodeProvider<Class<?>> provider) {
        Objects.requireNonNull(provider);
        this.sources = provider.get();
        this.types = new ConcurrentHashMap<>();
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
