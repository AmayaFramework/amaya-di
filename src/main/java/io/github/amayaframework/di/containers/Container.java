package io.github.amayaframework.di.containers;

import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 *
 */
public interface Container {
    /**
     * @param key
     * @return
     */
    Object get(int key);

    /**
     * @param key
     * @param value
     * @return
     */
    Object put(int key, Object value);

    /**
     * @param key
     * @return
     */
    Object remove(int key);

    /**
     * @param value
     * @param <E>
     * @return
     */
    @SuppressWarnings("unchecked")
    default <E> E getValue(Value<E> value) {
        return (E) get(value.hashCode());
    }

    /**
     * @param key
     * @param value
     * @param <E>
     * @return
     */
    @SuppressWarnings("unchecked")
    default <E> E putValue(Value<E> key, E value) {
        Objects.requireNonNull(value);
        return (E) put(key.hashCode(), value);
    }

    /**
     * @param value
     * @param <E>
     * @return
     */
    @SuppressWarnings("unchecked")
    default <E> E removeValue(Value<E> value) {
        return (E) remove(value.hashCode());
    }

    /**
     * @param clazz
     * @param <E>
     * @return
     */
    @SuppressWarnings("unchecked")
    default <E> E getSingleton(Class<E> clazz) {
        Objects.requireNonNull(clazz);
        int hashcode = clazz.hashCode();
        E ret = (E) get(hashcode);
        if (ret != null) {
            return ret;
        }
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            ret = (E) constructor.newInstance();
            put(hashcode, ret);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to instantiate singleton due to", e);
        }
        return ret;
    }

    /**
     * @param clazz
     * @param <E>
     * @return
     */
    @SuppressWarnings("unchecked")
    default <E> E removeSingleton(Class<E> clazz) {
        return (E) remove(clazz.hashCode());
    }
}
