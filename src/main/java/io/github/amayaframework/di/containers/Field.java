package io.github.amayaframework.di.containers;

import java.util.Objects;

public class Field<T> {
    private final String name;
    private final Class<?> type;

    public Field(String name, Class<?> type) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
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
        return Objects.hash(name, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Field)) {
            return false;
        }
        Field<?> field = (Field<?>) obj;
        return field.name.equals(name) && field.type == type;
    }
}
