package io.github.amayaframework.di.containers;

import java.lang.reflect.Constructor;
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
    Object get(int key);

    /**
     * Assigns an object to the corresponding key.
     *
     * @param key   required key
     * @param value object to assign
     * @return the previous object associated with the key, or null
     */
    Object put(int key, Object value);

    /**
     * Removes the object associated with the key.
     *
     * @param key required key
     * @return removed object or null
     */
    Object remove(int key);

    /**
     * Returns the object described by the {@link Value} object
     *
     * @param value required value
     * @param <E>   type of required object
     * @return found object or null
     */
    @SuppressWarnings("unchecked")
    default <E> E getValue(Value<E> value) {
        return (E) get(value.hashCode());
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
    default <E> E putValue(Value<E> key, E value) {
        Objects.requireNonNull(value);
        return (E) put(key.hashCode(), value);
    }

    /**
     * Remove the object associated with the specified {@link Value}.
     *
     * @param value required value
     * @param <E>   type of removed object
     * @return removed object or null
     */
    @SuppressWarnings("unchecked")
    default <E> E removeValue(Value<E> value) {
        return (E) remove(value.hashCode());
    }

    /**
     * Returns an object of the class, if the object does not exist, instantiates the class.
     *
     * @param clazz singleton class
     * @param <E>   singleton type
     * @return instance of the class
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
     * Deletes an object of the class, if it exists.
     *
     * @param clazz singleton class
     * @param <E>   singleton type
     * @return deleted object or null
     */
    @SuppressWarnings("unchecked")
    default <E> E removeSingleton(Class<E> clazz) {
        return (E) remove(clazz.hashCode());
    }
}