package io.github.amayaframework.di;

import java.lang.reflect.Constructor;
import java.util.List;

public final class ConstructorScheme extends AbstractScheme<Constructor<?>> {
    private final List<Dependency> dependencies;

    public ConstructorScheme(Constructor<?> target, List<Dependency> dependencies) {
        super(target);
        this.dependencies = dependencies;
    }

    @Override
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    @Override
    public void accept(SchemeVisitor visitor) {
        visitor.visit(this);
    }
}
