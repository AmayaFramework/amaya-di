package io.github.amayaframework.di.transformers;

import io.github.amayaframework.di.containers.ProviderType;

import java.lang.instrument.UnmodifiableClassException;

public interface Transformer {
    default void transform(Class<?> clazz, ProviderType provider) throws UnmodifiableClassException {
        transform(new Class<?>[]{clazz}, provider);
    }

    void transform(Class<?>[] classes, ProviderType provider) throws UnmodifiableClassException;
}
