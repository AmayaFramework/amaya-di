package io.github.amayaframework.di.types;

import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;

public final class InjectConstructor extends InjectMember {
    private final Constructor<?> constructor;
    private final Type constructorType;

    InjectConstructor(Constructor<?> member, InjectPolicy policy, String value) {
        super(member, member.getParameterTypes()[0], policy, value);
        this.constructor = member;
        this.constructorType = Type.getType(member);
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public Type getConstructorType() {
        return constructorType;
    }
}
