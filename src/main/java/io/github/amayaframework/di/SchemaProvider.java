package io.github.amayaframework.di;

import io.github.amayaframework.di.schema.ClassSchema;

import java.lang.reflect.Type;

@FunctionalInterface
public interface SchemaProvider {

    ClassSchema get(Type type, Class<?> impl);
}
