package io.github.amayaframework.di;

import java.util.HashSet;
import java.util.Set;

public final class Service {
    private final Class<?> clazz;
    private final Set<MethodScheme> methodSchemes;
    private final Set<FieldScheme> fieldSchemes;
    private ConstructorScheme constructorScheme;

    public Service(Class<?> clazz) {
        this.clazz = clazz;
        this.methodSchemes = new HashSet<>();
        this.fieldSchemes = new HashSet<>();
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public ConstructorScheme getConstructorScheme() {
        return constructorScheme;
    }

    public void setConstructorScheme(ConstructorScheme scheme) {
        this.constructorScheme = scheme;
    }

    public Set<FieldScheme> getFieldSchemes() {
        return fieldSchemes;
    }

    public Set<MethodScheme> getMethodSchemes() {
        return methodSchemes;
    }
}
