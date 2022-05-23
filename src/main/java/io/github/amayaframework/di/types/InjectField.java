package io.github.amayaframework.di.types;

import io.github.amayaframework.di.annotations.InjectPolicy;

import java.lang.reflect.Field;

public class InjectField extends InjectMember {
    private final Field field;
    private final String name;

    public InjectField(Field field, InjectPolicy policy, String value) {
        super(field, field.getType(), policy, value);
        this.field = field;
        this.name = field.getName();
    }

    public Field getField() {
        return field;
    }

    public String getName() {
        return name;
    }
}
