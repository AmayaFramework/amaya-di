package io.github.amayaframework.di.transformers;

import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.types.InjectType;
import io.github.amayaframework.di.types.SubTypeFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Objects;

class AsmClassFileTransformer implements ClassFileTransformer {
    private final InjectType type;
    private final SubTypeFactory factory;
    private final ProviderType provider;

    AsmClassFileTransformer(InjectType type, SubTypeFactory factory, ProviderType provider) {
        this.type = type;
        this.factory = factory;
        this.provider = provider;
    }

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        if (!Objects.equals(classBeingRedefined, type.getTarget())) {
            return classfileBuffer;
        }
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
        int flags = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
        ClassVisitor visitor = new AsmClassVisitor(writer, type, factory, provider);
        reader.accept(visitor, flags);
        return writer.toByteArray();
    }
}
