package io.github.amayaframework.di;

import java.lang.reflect.Field;
import java.util.List;

public final class FieldScheme extends AbstractScheme<Field> {
    private final Dependency dependency;

    public FieldScheme(Field target, Dependency dependency) {
        super(target);
        this.dependency = dependency;
    }

    public Dependency getDependency() {
        return dependency;
    }

    @Override
    public List<Dependency> getDependencies() {
        return List.of(dependency);
    }

    @Override
    public void accept(SchemeVisitor visitor) {
        visitor.visit(this);
    }
}
