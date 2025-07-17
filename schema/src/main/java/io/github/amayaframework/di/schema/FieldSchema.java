package io.github.amayaframework.di.schema;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;

/**
 * A schema that describes a field and the type required to inject into it.
 */
public final class FieldSchema extends AbstractSchema<Field> {
    final Type type;

    /**
     * Constructs field schema for specified field and type.
     *
     * @param target the specified field, must be non-null
     * @param type   the specified type, must be non-null
     */
    public FieldSchema(Field target, Type type) {
        super(target);
        this.type = Objects.requireNonNull(type);
    }

    /**
     * Returns the {@link Type} to be injected into the target field.
     *
     * @return non-null type of the field dependency
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns a set containing exactly this field's injection type.
     *
     * @return non-null, single-element immutable set of types
     */
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
