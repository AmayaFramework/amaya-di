package io.github.amayaframework.di.schema;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A schema that describes a class and its dependency injection metadata.
 * <p>
 * It aggregates constructor, field, and method schemas to describe all
 * injectable members of the class.
 */
public final class ClassSchema extends AbstractSchema<Class<?>> {
    private final Set<MethodSchema> methodSchemas;
    private final Set<FieldSchema> fieldSchemas;
    private final ConstructorSchema constructorSchema;
    private Set<Type> types;

    /**
     * Constructs class schema for specified class and schemas for its members.
     *
     * @param clazz             the specified class, must be non-null
     * @param constructorSchema the constructor schema, may be null
     * @param fieldSchemas      the set of field schemas, must be non-null
     * @param methodSchemas     the set of method schemas, must be non-null
     */
    public ClassSchema(Class<?> clazz,
                       ConstructorSchema constructorSchema,
                       Set<FieldSchema> fieldSchemas,
                       Set<MethodSchema> methodSchemas) {
        super(clazz);
        this.constructorSchema = Objects.requireNonNull(constructorSchema);
        this.fieldSchemas = Collections.unmodifiableSet(Objects.requireNonNull(fieldSchemas));
        this.methodSchemas = Collections.unmodifiableSet(Objects.requireNonNull(methodSchemas));
    }

    /**
     * Returns the constructor schema describing how to create an instance of the class.
     *
     * @return non-null {@link ConstructorSchema} for the class
     */
    public ConstructorSchema constructorSchema() {
        return constructorSchema;
    }

    /**
     * Returns an unmodifiable set of {@link FieldSchema} describing the injectable fields.
     *
     * @return non-null, immutable set of field schemas
     */
    public Set<FieldSchema> fieldSchemas() {
        return fieldSchemas;
    }

    /**
     * Returns an unmodifiable set of {@link MethodSchema} describing the injectable methods.
     *
     * @return non-null, immutable set of method schemas
     */
    public Set<MethodSchema> methodSchemas() {
        return methodSchemas;
    }

    private Set<Type> collectTypes() {
        var ret = new HashSet<>(constructorSchema.types);
        for (var schema : fieldSchemas) {
            ret.add(schema.type);
        }
        for (var schema : methodSchemas) {
            ret.addAll(schema.types);
        }
        return ret;
    }

    /**
     * Returns all types that are required by this class via constructor,
     * field, and method injection. Computed once and then cached.
     *
     * @return non-null, immutable set of required {@link Type}s
     */
    @Override
    public Set<Type> types() {
        if (types == null) {
            types = Collections.unmodifiableSet(collectTypes());
        }
        return types;
    }

    @Override
    public String toString() {
        return "ClassSchema{" + target + "}";
    }
}
