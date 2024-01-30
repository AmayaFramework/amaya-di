package io.github.amayaframework.di.scheme;

import java.util.Objects;

abstract class AbstractScheme<T> implements Scheme<T> {
    protected final T target;

    protected AbstractScheme(T target) {
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public T getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractScheme)) return false;
        var that = (AbstractScheme<?>) o;
        return target.equals(that.target);
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }
}
