package io.github.amayaframework.di.containers;

import java.util.Objects;

public class Value<T> {
    private final String name;
    private final Class<?> type;

    public Value(String name, Class<?> type) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
    }

    public static int hashcode(String name, Class<?> type) {
        return Objects.hash(name, type);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return hashcode(name, type);
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
