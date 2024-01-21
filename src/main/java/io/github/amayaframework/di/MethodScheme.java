package io.github.amayaframework.di;

import java.lang.reflect.Method;
import java.util.List;

public final class MethodScheme extends AbstractScheme<Method> {
    private final List<Dependency> dependencies;

    public MethodScheme(Method target, List<Dependency> dependencies) {
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
