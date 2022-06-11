package io.github.amayaframework.di.types;

public interface InjectTypeFactory {
    InjectType getInjectType(Class<?> clazz);
}
