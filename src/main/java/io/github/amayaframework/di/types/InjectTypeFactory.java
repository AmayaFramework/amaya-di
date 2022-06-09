package io.github.amayaframework.di.types;

import java.util.Collection;

public interface InjectTypeFactory {
    Collection<InjectType> getInjectTypes();

    InjectType getInjectType(Class<?> clazz);
}
