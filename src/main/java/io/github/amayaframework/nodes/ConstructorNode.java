package io.github.amayaframework.nodes;

import java.lang.reflect.Constructor;

public final class ConstructorNode extends AbstractInjectNode<Constructor<?>> {
    public ConstructorNode(Constructor<?> body) {
        super(body);
        Class<?>[] types = body.getParameterTypes();
        if (types.length != 1) {
            throw new IllegalStateException("The constructor for the injection must have one parameter");
        }
        this.type = types[0];
    }
}
