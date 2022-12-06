package io.github.amayaframework.di.containers;

import java.util.Objects;

/**
 * A class describing the concept of "value": a name-type pair.
 *
 * @param <T> class type
 */
public final class Value<T> {
    private final String name;
    private final Class<T> type;

    public Value(String name, Class<T> type) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
    }

    public static int hashCode(String name, Class<?> type) {
        return Objects.hash(name, type);
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return hashCode(name, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Value)) {
            return false;
        }
        Value<?> value = (Value<?>) obj;
        return value.name.equals(name) && value.type == type;
    }
}
