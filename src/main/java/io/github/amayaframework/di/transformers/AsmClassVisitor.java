package io.github.amayaframework.di.transformers;

import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.types.InjectType;
import io.github.amayaframework.di.types.SubTypeFactory;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Objects;

import static com.github.romanqed.jeflect.AsmUtil.INIT;

class AsmClassVisitor extends ClassVisitor {
    private final InjectType type;
    private final SubTypeFactory factory;
    private final ProviderType provider;

    protected AsmClassVisitor(ClassVisitor visitor, InjectType type, SubTypeFactory factory, ProviderType provider) {
        super(Opcodes.ASM8, visitor);
        this.type = type;
        this.factory = factory;
        this.provider = provider;
    }

    @Override
    public MethodVisitor visitMethod(int access,
                                     String name,
                                     String descriptor,
                                     String signature,
                                     String[] exceptions) {
        MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (Objects.equals(name, INIT)) {
            return new AsmMethodVisitor(type, factory, provider, access, visitor, descriptor);
        }
        return visitor;
    }
}
