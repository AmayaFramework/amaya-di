package io.github.amayaframework.di.transformers;

import com.github.romanqed.jeflect.transformers.BytecodeTransformer;
import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.types.InjectType;
import io.github.amayaframework.di.types.InjectTypeFactory;
import io.github.amayaframework.di.types.SubTypeFactory;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An implementation of {@link Transformer} that uses the Asm library to edit the bytecode of classes.
 */
public final class AsmTransformer implements Transformer {
    private static final String CLASSFILE_POSTFIX = ".class";
    private final Instrumentation instrumentation;
    private final InjectTypeFactory injectFactory;
    private final SubTypeFactory typeFactory;

    public AsmTransformer(Instrumentation instrumentation,
                          InjectTypeFactory injectFactory,
                          SubTypeFactory typeFactory) {
        this.instrumentation = Objects.requireNonNull(instrumentation);
        this.injectFactory = Objects.requireNonNull(injectFactory);
        this.typeFactory = Objects.requireNonNull(typeFactory);
    }

    private static InputStream loadClassBody(Class<?> clazz) {
        return clazz.getResourceAsStream(clazz.getName() + CLASSFILE_POSTFIX);
    }

    private static void close(InputStream stream) {
        try {
            stream.close();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot close stream due to", e);
        }
    }

    private ClassDefinition process(Class<?> clazz, InputStream body, ProviderType provider) {
        try {
            ClassReader reader = new ClassReader(body);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
            InjectType type = injectFactory.getInjectType(clazz);
            ClassVisitor visitor = new AsmClassVisitor(writer, type, typeFactory, provider);
            reader.accept(visitor, ClassReader.SKIP_FRAMES);
            return new ClassDefinition(clazz, writer.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read class file due to", e);
        } finally {
            close(body);
        }
    }

    private void retransform(List<Class<?>> classes, ProviderType provider) throws UnmodifiableClassException {
        Map<String, Function<ClassVisitor, ClassVisitor>> found = new HashMap<>();
        classes.forEach(clazz -> {
            String name = clazz.getName().replace('.', '/');
            found.put(name, visitor -> {
                InjectType type = injectFactory.getInjectType(clazz);
                return new AsmClassVisitor(visitor, type, typeFactory, provider);
            });
        });
        BytecodeTransformer transformer = new BytecodeTransformer(
                ClassReader.SKIP_FRAMES,
                ClassWriter.COMPUTE_FRAMES,
                found::get);
        instrumentation.addTransformer(transformer, true);
        instrumentation.retransformClasses(classes.toArray(new Class[0]));
        instrumentation.removeTransformer(transformer);
        transformer.validate();
    }

    private void redefine(List<Class<?>> classes, ProviderType provider) throws UnmodifiableClassException {
        if (!instrumentation.isRedefineClassesSupported()) {
            return;
        }
        List<ClassDefinition> found = new LinkedList<>();
        classes.removeIf(clazz -> {
            InputStream body = loadClassBody(clazz);
            if (body == null) {
                return false;
            }
            found.add(process(clazz, body, provider));
            close(body);
            return true;
        });
        try {
            instrumentation.redefineClasses(found.toArray(new ClassDefinition[0]));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class for redefine not found", e);
        }
    }

    @Override
    public void transform(Class<?>[] classes, ProviderType provider) throws UnmodifiableClassException {
        if (classes.length == 0) {
            throw new IllegalArgumentException("Cannot transform empty class sequence");
        }
        if (!instrumentation.isRetransformClassesSupported()) {
            throw new IllegalStateException("Given instrumentation does not support retransform");
        }
        List<Class<?>> toModify = Arrays.stream(classes).collect(Collectors.toList());
        redefine(toModify, provider);
        if (toModify.isEmpty()) {
            return;
        }
        retransform(toModify, provider);
    }
}
