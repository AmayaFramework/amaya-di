package io.github.amayaframework.di.scheme;

import io.github.amayaframework.di.Artifact;

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
    private final Set<Artifact> artifacts;

    /**
     * Constructs class scheme for specified class and schemes for its members.
     *
     * @param clazz             the specified class, must be non-null
     * @param constructorScheme the constructor scheme, must be non-null
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
        this.artifacts = Collections.unmodifiableSet(collectArtifacts());
    }

    private Set<Artifact> collectArtifacts() {
        var ret = new HashSet<Artifact>();
        if (constructorScheme != null) {
            ret.addAll(constructorScheme.artifacts);
        }
        for (var scheme : fieldSchemes) {
            ret.add(scheme.artifact);
        }
        for (var scheme : methodSchemes) {
            ret.addAll(scheme.artifacts);
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
    public Set<Artifact> getArtifacts() {
        return artifacts;
    }

    @Override
    public String toString() {
        return "ClassScheme{" +
                "methodSchemes=" + methodSchemes +
                ", fieldSchemes=" + fieldSchemes +
                ", constructorScheme=" + constructorScheme +
                ", target=" + target +
                '}';
    }
}
