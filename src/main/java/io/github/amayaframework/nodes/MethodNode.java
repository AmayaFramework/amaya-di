package io.github.amayaframework.nodes;

import java.lang.reflect.Method;

public final class MethodNode extends AbstractInjectNode<Method> {
    public MethodNode(Method body) {
        super(body);
        Class<?>[] types = body.getParameterTypes();
        if (types.length != 1) {
            throw new IllegalStateException("The method for the injection must have one parameter");
        }
        this.type = types[0];
    }
}
