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
    private final Set<MethodSchema> methodSchemes;
    private final Set<FieldSchema> fieldSchemes;
    private final ConstructorSchema constructorScheme;
    private final Set<Type> types;

    /**
     * Constructs class scheme for specified class and schemes for its members.
     *
     * @param clazz             the specified class, must be non-null
     * @param constructorScheme the constructor scheme, may be null
     * @param fieldSchemes      the set of field schemes, must be non-null
     * @param methodSchemes     the set of method schemes, must be non-null
     */
    public ClassSchema(Class<?> clazz,
                       ConstructorSchema constructorScheme,
                       Set<FieldSchema> fieldSchemes,
                       Set<MethodSchema> methodSchemes) {
        super(clazz);
        this.constructorScheme = Objects.requireNonNull(constructorScheme);
        this.fieldSchemes = Collections.unmodifiableSet(Objects.requireNonNull(fieldSchemes));
        this.methodSchemes = Collections.unmodifiableSet(Objects.requireNonNull(methodSchemes));
        this.types = Collections.unmodifiableSet(collectTypes());
    }

    private Set<Type> collectTypes() {
        var ret = new HashSet<>(constructorScheme.types);
        for (var scheme : fieldSchemes) {
            ret.add(scheme.type);
        }
        for (var scheme : methodSchemes) {
            ret.addAll(scheme.types);
        }
        return ret;
    }

    /**
     * Returns the constructor scheme for class constructor.
     *
     * @return the constructor scheme.
     */
    public ConstructorSchema getConstructorScheme() {
        return constructorScheme;
    }

    /**
     * Returns the set of field schemes for class fields.
     *
     * @return the set of field schemes
     */
    public Set<FieldSchema> getFieldSchemes() {
        return fieldSchemes;
    }

    /**
     * Returns the set of method schemes for class methods.
     *
     * @return the set of method schemes
     */
    public Set<MethodSchema> getMethodSchemes() {
        return methodSchemes;
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
        return "ClassScheme{" + target + "}";
    }
}
