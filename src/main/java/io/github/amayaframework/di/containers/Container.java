package io.github.amayaframework.di.containers;

import java.util.Objects;

/**
 * A container containing key-related data.
 */
public interface Container {
    /**
     * Returns a stored object corresponding to an integer key.
     *
     * @param key required key
     * @return found object or null
     */
    Object get(Integer key);

    /**
     * Assigns an object to the corresponding key.
     *
     * @param key   required key
     * @param value object to assign
     * @return the previous object associated with the key, or null
     */
    Object put(Integer key, Object value);

    /**
     * Removes the object associated with the key.
     *
     * @param key required key
     * @return removed object or null
     */
    Object remove(Integer key);

    /**
     * Returns the object described by the {@link Value} object
     *
     * @param value required value
     * @param <E>   type of required object
     * @return found object or null
     */
    @SuppressWarnings("unchecked")
    default <E> E get(Value<E> value) {
        return (E) get(value.hashCode());
    }

    default <E> E get(Class<? extends E> clazz) {
        return get(new Value<>("", clazz));
    }

    /**
     * Assigns an object of the same type to the specified {@link Value}.
     *
     * @param key   required value
     * @param value object to assign, cannot be null
     * @param <E>   type of assigned object
     * @return the previous object associated with the key, or null
     */
    @SuppressWarnings("unchecked")
    default <E> E put(Value<? extends E> key, E value) {
        Objects.requireNonNull(value);
        return (E) put(key.hashCode(), value);
    }

    @SuppressWarnings("unchecked")
    default <E> E put(String name, E value) {
        return (E) put(new Value<>(name, value.getClass()), value);
    }

    default <C extends E, E> E put(Class<E> clazz, C value) {
        return put(new Value<>("", clazz), value);
    }

    default Object put(Object value) {
        Objects.requireNonNull(value);
        Class<?> clazz = value.getClass();
        return put(new Value<>("", clazz), value);
    }

    default <E, V extends E> E put(Class<E> type, String name, V value) {
        return put(new Value<>(name, type), value);
    }

    /**
     * Remove the object associated with the specified {@link Value}.
     *
     * @param value required value
     * @param <E>   type of removed object
     * @return removed object or null
     */
    @SuppressWarnings("unchecked")
    default <E> E remove(Value<E> value) {
        return (E) remove(value.hashCode());
    }

    default <E> E remove(Class<? extends E> clazz) {
        return remove(new Value<>("", clazz));
    }
}
