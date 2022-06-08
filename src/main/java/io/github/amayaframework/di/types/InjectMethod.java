package io.github.amayaframework.di.types;

import io.github.amayaframework.di.InjectPolicy;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

public class InjectMethod extends InjectMember {
    private final Method method;
    private final String name;
    private final Type methodType;

    InjectMethod(Method member, InjectPolicy policy, String value) {
        super(member, member.getParameterTypes()[0], policy, value);
        this.method = member;
        this.name = member.getName();
        this.methodType = Type.getType(member);
    }

    public Method getMethod() {
        return method;
    }

    public Type getMethodType() {
        return methodType;
    }

    public String getName() {
        return name;
    }
}
