package io.github.amayaframework.di.transformers;

import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.types.InjectType;
import io.github.amayaframework.di.types.InjectTypeFactory;
import io.github.amayaframework.di.types.SubTypeFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.Objects;

/**
 * An implementation of {@link Transformer} that uses the Asm library to edit the bytecode of classes.
 */
public class AsmTransformer implements Transformer {
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

    private static boolean hasEmptyInit(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(e -> e.getParameterCount() == 0)
                .count() == 1;
    }

    @Override
    public void transform(Class<?> clazz, ProviderType provider) throws UnmodifiableClassException {
        if (!hasEmptyInit(clazz)) {
            throw new IllegalStateException("Empty constructor not found");
        }
        InjectType type = injectFactory.getInjectType(clazz);
        ClassFileTransformer transformer = new AsmClassFileTransformer(type, typeFactory, provider);
        instrumentation.addTransformer(transformer, true);
        instrumentation.retransformClasses(clazz);
        instrumentation.removeTransformer(transformer);
    }
}
