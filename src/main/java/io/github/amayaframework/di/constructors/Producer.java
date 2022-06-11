package io.github.amayaframework.di.constructors;

public interface Producer {
    Object produce(Object value) throws Exception;
}
