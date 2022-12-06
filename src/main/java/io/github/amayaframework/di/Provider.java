package io.github.amayaframework.di;

import io.github.amayaframework.di.containers.Value;
import io.github.amayaframework.di.containers.*;

/**
 * A class representing the default provider. Uses a lazy singleton.
 */
public final class Provider {

    @ContainerProvider
    public static Container getContainer() {
        return Holder.CONTAINER;
    }

    /**
     * Returns the object described by the {@link Value} object
     *
     * @param value required value
     * @param <E>   type of required object
     * @return found object or null
     */
    public static <E> E get(Value<E> value) {
        return Holder.CONTAINER.get(value);
    }

    /**
     * Assigns an object of the same type to the specified {@link io.github.amayaframework.di.containers.Value}.
     *
     * @param key   required value
     * @param value object to assign, cannot be null
     * @param <E>   type of assigned object
     * @return the previous object associated with the key, or null
     */
    public static <E> E put(Value<E> key, E value) {
        return Holder.CONTAINER.put(key, value);
    }

    /**
     * Remove the object associated with the specified {@link io.github.amayaframework.di.containers.Value}.
     *
     * @param value required value
     * @param <E>   type of removed object
     * @return removed object or null
     */
    public static <E> E remove(Value<E> value) {
        return Holder.CONTAINER.remove(value);
    }

    public static <E> E get(Class<? extends E> clazz) {
        return Holder.CONTAINER.get(clazz);
    }

    public static Object put(Object value) {
        return Holder.CONTAINER.put(value);
    }

    public static <E, V extends E> E put(Class<E> type, String name, V value) {
        return Holder.CONTAINER.put(type, name, value);
    }

    public static <E> E put(String name, E value) {
        return Holder.CONTAINER.put(name, value);
    }

    public static <E> E remove(Class<? extends E> clazz) {
        return Holder.CONTAINER.remove(clazz);
    }

    public static <C extends E, E> E put(Class<E> clazz, C value) {
        return Holder.CONTAINER.put(clazz, value);
    }

    @LockProvider
    public static Object getLock() {
        return Holder.LOCK;
    }

    private static class Holder {
        private static final Container CONTAINER = new MapContainer();
        private static final Object LOCK = new Object();
    }
}
