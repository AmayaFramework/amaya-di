package io.github.amayaframework.di.scheme;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Set;

/**
 * A scheme that defines the correspondence between artifact and class field.
 */
public final class FieldScheme extends AbstractScheme<Field> {
    final Type type;

    //    /**
//     * Constructs field scheme for specified field and artifact.
//     *
//     * @param target   the specified field, must be non-null
//     * @param type the specified artifact, must be non-null
//     */
    public FieldScheme(Field target, Type type) {
        super(target);
        this.type = Objects.requireNonNull(type);
    }

    //    /**
//     * Returns the artifact associated with this field.
//     *
//     * @return the artifact associated with this field
//     */
    public Type getType() {
        return type;
    }

    @Override
    public Set<Type> getTypes() {
        return Set.of(type);
    }

    @Override
    public String toString() {
        return "FieldScheme{" +
                "type=" + type +
                ", target=" + target +
                '}';
    }
}
