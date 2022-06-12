package io.github.amayaframework.di.types;

import java.lang.reflect.Field;

public final class InjectField extends InjectMember {
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
