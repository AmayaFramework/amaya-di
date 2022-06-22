package io.github.amayaframework.di.transformers;

import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.types.InjectType;
import io.github.amayaframework.di.types.SubTypeFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Objects;

class AsmClassFileTransformer implements ClassFileTransformer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsmClassFileTransformer.class);
    private static final int OPTIONS = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
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
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
        ClassVisitor visitor = new AsmClassVisitor(writer, type, factory, provider);
        try {
            reader.accept(visitor, OPTIONS);
            LOGGER.debug("Class " + className + " successfully transformed");
        } catch (Throwable e) {
            LOGGER.error("It is impossible to transform " + className + " due to", e);
            return classfileBuffer;
        }
        return writer.toByteArray();
    }
}
