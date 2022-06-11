package io.github.amayaframework.di.constructors;

/**
 * Internal interface for packaging some methods used inside the {@link ConstructorFactory}
 */
public interface Producer {
    Object produce(Object value) throws Exception;
}
