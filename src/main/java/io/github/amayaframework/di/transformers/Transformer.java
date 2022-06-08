package io.github.amayaframework.di.transformers;

import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.types.InjectType;

import java.lang.instrument.UnmodifiableClassException;
import java.util.Collection;
import java.util.Collections;

public interface Transformer {
    default void transform(InjectType type, ProviderType provider) throws UnmodifiableClassException {
        transform(Collections.singletonList(type), provider);
    }

    void transform(Collection<InjectType> types, ProviderType provider) throws UnmodifiableClassException;
}
