package io.github.amayaframework.di.types;

import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;

public class InjectConstructor extends InjectMember {
    private final Type constructorType;

    InjectConstructor(Constructor<?> member, InjectPolicy policy, String value) {
        super(member, member.getParameterTypes()[0], policy, value);
        this.constructorType = Type.getType(member);
    }

    public Type getConstructorType() {
        return constructorType;
    }
}
