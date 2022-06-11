package io.github.amayaframework.di.constructors;

@FunctionalInterface
public interface Setter {
    void set(Object owner, Object value) throws Exception;
}
