package io.github.amayaframework.di.transformers;

import io.github.amayaframework.di.types.InjectType;

import java.lang.instrument.UnmodifiableClassException;
import java.util.Collection;
import java.util.Collections;

public interface Transformer {
    default void transform(InjectType type) throws UnmodifiableClassException {
        transform(Collections.singletonList(type));
    }

    void transform(Collection<InjectType> types) throws UnmodifiableClassException;
}
