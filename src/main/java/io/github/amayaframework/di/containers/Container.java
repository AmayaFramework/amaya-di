package io.github.amayaframework.di.containers;

import java.util.Objects;

public interface Container {
    Object get(int key);

    Object put(int key, Object value);

    Object remove(int key);

    @SuppressWarnings("unchecked")
    default <E> E getValue(Value<E> value) {
        return (E) get(value.hashCode());
    }

    @SuppressWarnings("unchecked")
    default <E> E putValue(Value<E> key, E value) {
        Objects.requireNonNull(value);
        return (E) put(key.hashCode(), value);
    }

    @SuppressWarnings("unchecked")
    default <E> E removeValue(Value<E> value) {
        return (E) remove(value.hashCode());
    }

    @SuppressWarnings("unchecked")
    default <E> E getSingleton(Class<E> clazz) {
        return (E) get(clazz.hashCode());
    }

    @SuppressWarnings("unchecked")
    default <E> E putSingleton(Class<E> clazz) {
        try {
            return (E) put(clazz.hashCode(), clazz.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            throw new IllegalStateException("It is not possible to instantiate a class due to", e);
        }
    }

    @SuppressWarnings("unchecked")
    default <E> E removeSingleton(Class<E> clazz) {
        return (E) remove(clazz.hashCode());
    }
}
