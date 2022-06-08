package io.github.amayaframework.di.transformers;

import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.types.InjectType;
import io.github.amayaframework.di.types.SubTypeFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.*;

public class AsmTransformer implements Transformer {
    private final Instrumentation instrumentation;
    private final SubTypeFactory factory;

    public AsmTransformer(Instrumentation instrumentation, SubTypeFactory factory) {
        this.instrumentation = Objects.requireNonNull(instrumentation);
        this.factory = Objects.requireNonNull(factory);
    }

    private static boolean hasEmptyInit(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(e -> e.getParameterCount() == 0)
                .count() == 1;
    }

    @Override
    public void transform(Collection<InjectType> types, ProviderType provider) throws UnmodifiableClassException {
        List<ClassFileTransformer> transformers = new LinkedList<>();
        Class<?>[] classes = new Class<?>[types.size()];
        int index = 0;
        for (InjectType type : types) {
            Class<?> clazz = type.getTarget();
            if (!hasEmptyInit(clazz)) {
                throw new IllegalStateException("Empty constructor not found");
            }
            classes[index++] = clazz;
            ClassFileTransformer toAdd = new AsmClassFileTransformer(type, factory, provider);
            instrumentation.addTransformer(toAdd, true);
            transformers.add(toAdd);
        }
        instrumentation.retransformClasses(classes);
        for (ClassFileTransformer transformer : transformers) {
            instrumentation.removeTransformer(transformer);
        }
    }
}
