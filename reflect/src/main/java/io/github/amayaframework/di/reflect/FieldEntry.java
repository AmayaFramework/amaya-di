package io.github.amayaframework.di.reflect;

import io.github.amayaframework.di.core.ObjectFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

final class FieldEntry {
    final Field field;
    final Type type;
    ObjectFactory factory;

    FieldEntry(Field field, Type type) {
        this.field = field;
        this.type = type;
    }

    FieldEntry(Field field) {
        this.field = field;
        this.type = null;
    }
}
