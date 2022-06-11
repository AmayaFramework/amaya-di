package io.github.amayaframework.di;

import com.github.romanqed.jeflect.meta.MetaFactory;
import io.github.amayaframework.di.constructors.ConstructorFactory;
import io.github.amayaframework.di.constructors.MetaConstructorFactory;
import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.transformers.AsmTransformer;
import io.github.amayaframework.di.transformers.Transformer;
import io.github.amayaframework.di.types.IndexTypeFactory;
import io.github.amayaframework.di.types.InjectTypeFactory;
import io.github.amayaframework.di.types.ReflectTypeFactory;
import io.github.amayaframework.di.types.SubTypeFactory;
import net.bytebuddy.agent.ByteBuddyAgent;

import java.lang.instrument.Instrumentation;
import java.util.Objects;
import java.util.Optional;

public class DIBuilder {
    private static final DIBuilder DEFAULT_BUILDER = new DIBuilder();
    private static final ProviderType PROVIDER_TYPE = ProviderType.fromClass(ContainerProvider.class);
    private ProviderType provider;
    private Instrumentation instrumentation;
    private SubTypeFactory subTypeFactory;
    private InjectTypeFactory injectTypeFactory;
    private MetaFactory metaFactory;
    private boolean autoTransform;

    public DIBuilder() {
        resetValues();
    }

    public static DI createDefault() {
        return DEFAULT_BUILDER.build();
    }

    private void resetValues() {
        this.provider = null;
        this.instrumentation = null;
        this.subTypeFactory = null;
        this.injectTypeFactory = null;
        this.metaFactory = null;
        this.autoTransform = true;
    }

    public Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public DIBuilder setInstrumentation(Instrumentation instrumentation) {
        this.instrumentation = Objects.requireNonNull(instrumentation);
        return this;
    }

    public ProviderType getProvider() {
        return provider;
    }

    public DIBuilder setProvider(Class<?> provider) {
        this.provider = ProviderType.fromClass(provider);
        return this;
    }

    public SubTypeFactory getSubTypeFactory() {
        return subTypeFactory;
    }

    public DIBuilder setSubTypeFactory(SubTypeFactory factory) {
        this.subTypeFactory = Objects.requireNonNull(factory);
        return this;
    }

    public InjectTypeFactory getInjectTypeFactory() {
        return injectTypeFactory;
    }

    public DIBuilder setInjectTypeFactory(InjectTypeFactory factory) {
        this.injectTypeFactory = Objects.requireNonNull(factory);
        return this;
    }

    public MetaFactory getMetaFactory() {
        return metaFactory;
    }

    public DIBuilder setMetaFactory(MetaFactory factory) {
        this.metaFactory = Objects.requireNonNull(factory);
        return this;
    }

    public boolean isAutoTransform() {
        return autoTransform;
    }

    public DIBuilder setAutoTransform(boolean autoTransform) {
        this.autoTransform = autoTransform;
        return this;
    }

    public DI build() {
        provider = Optional.ofNullable(provider).orElse(PROVIDER_TYPE);
        instrumentation = Optional.ofNullable(instrumentation).orElse(ByteBuddyAgent.install());
        subTypeFactory = Optional.ofNullable(subTypeFactory).orElse(new IndexTypeFactory());
        injectTypeFactory = Optional.ofNullable(injectTypeFactory).orElse(new ReflectTypeFactory());
        metaFactory = Optional.ofNullable(metaFactory).orElse(new MetaFactory());
        Transformer transformer = new AsmTransformer(instrumentation, injectTypeFactory, subTypeFactory);
        ConstructorFactory factory = new MetaConstructorFactory(metaFactory, injectTypeFactory, subTypeFactory);
        DI ret = new IndexDI(provider, factory, transformer);
        if (autoTransform) {
            ret.transform();
        }
        resetValues();
        return ret;
    }
}
