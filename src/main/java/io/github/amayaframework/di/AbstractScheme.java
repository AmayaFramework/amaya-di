package io.github.amayaframework.di;

import java.lang.reflect.Member;

abstract class AbstractScheme<T extends Member> implements Scheme<T> {
    protected final T target;

    protected AbstractScheme(T target) {
        this.target = target;
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
