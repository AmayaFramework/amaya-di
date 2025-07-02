package io.github.amayaframework.di.schema;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A scheme that defines the correspondence between set of types and class.
 */
public final class ClassSchema extends AbstractSchema<Class<?>> {
    private final Set<MethodSchema> methodSchemas;
    private final Set<FieldSchema> fieldSchemas;
    private final ConstructorSchema constructorSchema;
    private final Set<Type> types;

    /**
     * Constructs class scheme for specified class and schemes for its members.
     *
     * @param clazz             the specified class, must be non-null
     * @param constructorSchema the constructor scheme, may be null
     * @param fieldSchemas      the set of field schemes, must be non-null
     * @param methodSchemas     the set of method schemes, must be non-null
     */
    public ClassSchema(Class<?> clazz,
                       ConstructorSchema constructorSchema,
                       Set<FieldSchema> fieldSchemas,
                       Set<MethodSchema> methodSchemas) {
        super(clazz);
        this.constructorSchema = Objects.requireNonNull(constructorSchema);
        this.fieldSchemas = Collections.unmodifiableSet(Objects.requireNonNull(fieldSchemas));
        this.methodSchemas = Collections.unmodifiableSet(Objects.requireNonNull(methodSchemas));
        this.types = Collections.unmodifiableSet(collectTypes());
    }

    private Set<Type> collectTypes() {
        var ret = new HashSet<>(constructorSchema.types);
        for (var scheme : fieldSchemas) {
            ret.add(scheme.type);
        }
        for (var scheme : methodSchemas) {
            ret.addAll(scheme.types);
        }
        return ret;
    }

    /**
     * Returns the constructor scheme for class constructor.
     *
     * @return the constructor scheme.
     */
    public ConstructorSchema getConstructorSchema() {
        return constructorSchema;
    }

    /**
     * Returns the set of field schemes for class fields.
     *
     * @return the set of field schemes
     */
    public Set<FieldSchema> getFieldSchemas() {
        return fieldSchemas;
    }

    /**
     * Returns the set of method schemes for class methods.
     *
     * @return the set of method schemes
     */
    public Set<MethodSchema> getMethodSchemas() {
        return methodSchemas;
    }

    /**
     * Returns all types that class members depend on.
     *
     * @return the set of types
     */
    @Override
    public Set<Type> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return "ClassSchema{" + target + "}";
    }
}
