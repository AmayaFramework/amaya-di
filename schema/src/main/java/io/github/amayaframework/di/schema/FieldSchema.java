package io.github.amayaframework.di.schema;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;

/**
 * A scheme that defines the correspondence between type and class field.
 */
public final class FieldSchema extends AbstractSchema<Field> {
    final Type type;

    /**
     * Constructs field scheme for specified field and type.
     *
     * @param target the specified field, must be non-null
     * @param type   the specified type, must be non-null
     */
    public FieldSchema(Field target, Type type) {
        super(target);
        this.type = Objects.requireNonNull(type);
    }

    /**
     * Returns the type associated with this field.
     *
     * @return the type associated with this field
     */
    public Type getType() {
        return type;
    }

    @Override
    public Set<Type> getTypes() {
        return Set.of(type);
    }

    @Override
    public String toString() {
        return "FieldSchema{" +
                "type=" + type +
                ", target=" + target +
                '}';
    }
}
