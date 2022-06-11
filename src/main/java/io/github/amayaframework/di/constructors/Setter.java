package io.github.amayaframework.di.constructors;

/**
 * Internal interface for packaging some methods used inside the {@link ConstructorFactory}
 */
public interface Setter {
    void set(Object owner, Object value) throws Exception;
}
