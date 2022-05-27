package io.github.amayaframework.di.containers;

public interface Container {
    Object getField(Integer key);

    @SuppressWarnings("unchecked")
    default <E> E getField(Field<E> field) {
        return (E) getField(field.hashCode());
    }

    <E> E setField(Field<E> field, E value);

    <E> E removeField(Field<E> field);

    Object getSingleton(Integer key);

    @SuppressWarnings("unchecked")
    default <E> E getSingleton(Class<E> clazz) {
        return (E) getSingleton(clazz.hashCode());
    }

    <E> E setSingleton(Class<E> clazz);

    <E> E removeSingleton(Class<E> clazz);
}
