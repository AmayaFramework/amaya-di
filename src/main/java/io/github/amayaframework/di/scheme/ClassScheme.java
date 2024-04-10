package io.github.amayaframework.di.scheme;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A scheme that defines the correspondence between set of artifacts and class.
 */
public final class ClassScheme extends AbstractScheme<Class<?>> {
    private final Set<MethodScheme> methodSchemes;
    private final Set<FieldScheme> fieldSchemes;
    private final ConstructorScheme constructorScheme;
    private final Set<Type> types;

    /**
     * Constructs class scheme for specified class and schemes for its members.
     *
     * @param clazz             the specified class, must be non-null
     * @param constructorScheme the constructor scheme, may be null
     * @param fieldSchemes      the set of field schemes, must be non-null
     * @param methodSchemes     the set of method schemes, must be non-null
     */
    public ClassScheme(Class<?> clazz,
                       ConstructorScheme constructorScheme,
                       Set<FieldScheme> fieldSchemes,
                       Set<MethodScheme> methodSchemes) {
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
    public ConstructorScheme getConstructorScheme() {
        return constructorScheme;
    }

    /**
     * Returns the set of field schemes for class fields.
     *
     * @return the set of field schemes
     */
    public Set<FieldScheme> getFieldSchemes() {
        return fieldSchemes;
    }

    /**
     * Returns the set of method schemes for class methods.
     *
     * @return the set of method schemes
     */
    public Set<MethodScheme> getMethodSchemes() {
        return methodSchemes;
    }

    /**
     * Returns all artifacts that class members depend on.
     *
     * @return the set of artifacts
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
