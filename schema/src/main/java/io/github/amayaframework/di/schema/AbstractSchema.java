package io.github.amayaframework.di.schema;

import java.util.Objects;

abstract class AbstractSchema<T> implements Schema<T> {
    protected final T target;

    protected AbstractSchema(T target) {
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public T getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractSchema)) return false;
        var that = (AbstractSchema<?>) o;
        return target.equals(that.target);
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }
}
