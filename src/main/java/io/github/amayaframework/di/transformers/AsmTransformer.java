package io.github.amayaframework.di.transformers;

import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.types.InjectTypeFactory;
import io.github.amayaframework.di.types.SubTypeFactory;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
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

    @Override
    public void transform(Class<?>[] classes, ProviderType provider) throws UnmodifiableClassException {
        // TODO
    }
}
