package io.github.amayaframework.di.scheme;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class ClassScheme extends AbstractScheme<Class<?>> {
    private final Set<MethodScheme> methodSchemes;
    private final Set<FieldScheme> fieldSchemes;
    private final ConstructorScheme constructorScheme;
    private final Set<Artifact> artifacts;

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

    public ConstructorScheme getConstructorScheme() {
        return constructorScheme;
    }

    public Set<FieldScheme> getFieldSchemes() {
        return fieldSchemes;
    }

    public Set<MethodScheme> getMethodSchemes() {
        return methodSchemes;
    }

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
                ", artifacts=" + artifacts +
                ", target=" + target +
                '}';
    }
}
