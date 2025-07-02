package io.github.amayaframework.di.reflect;

import com.github.romanqed.jfunc.Function0;

import java.lang.reflect.Field;

@SuppressWarnings("rawtypes")
final class FieldEntry {
    final Field field;
    final Function0 provider;

    FieldEntry(Field field, Function0 provider) {
        this.field = field;
        this.provider = provider;
    }
}
