package io.github.amayaframework.di.containers;

public interface Container {
    Object getValue(int key);

    @SuppressWarnings("unchecked")
    default <E> E getValue(Value<E> value) {
        return (E) getValue(value.hashCode());
    }

    <E> E setValue(Value<E> field, E value);

    <E> E removeValue(Value<E> value);

    Object getSingleton(int key);

    @SuppressWarnings("unchecked")
    default <E> E getSingleton(Class<E> clazz) {
        return (E) getSingleton(clazz.hashCode());
    }

    <E> E setSingleton(Class<E> clazz);

    <E> E removeSingleton(Class<E> clazz);
}
