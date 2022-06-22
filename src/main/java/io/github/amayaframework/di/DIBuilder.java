package io.github.amayaframework.di;

import com.github.romanqed.jeflect.meta.MetaFactory;
import io.github.amayaframework.di.constructors.ConstructorFactory;
import io.github.amayaframework.di.constructors.MetaConstructorFactory;
import io.github.amayaframework.di.containers.Container;
import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.transformers.AsmTransformer;
import io.github.amayaframework.di.transformers.Transformer;
import io.github.amayaframework.di.types.IndexTypeFactory;
import io.github.amayaframework.di.types.InjectTypeFactory;
import io.github.amayaframework.di.types.ReflectTypeFactory;
import io.github.amayaframework.di.types.SubTypeFactory;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.Optional;

/**
 * Built-in builder for DI, using {@link MetaFactory} and ClassIndex to search for annotated classes.
 */
public class DIBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DIBuilder.class);
    private static final DIBuilder DEFAULT_BUILDER = new DIBuilder();
    private static final ProviderType PROVIDER_TYPE = ProviderType.fromClass(Provider.class);
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

    /**
     * Sets the instrumentation to be used for class transformation.
     *
     * @param instrumentation instance {@link Instrumentation}, obtained by loading javaagent
     * @return {@link DIBuilder} instance
     */
    public DIBuilder setInstrumentation(Instrumentation instrumentation) {
        this.instrumentation = Objects.requireNonNull(instrumentation);
        return this;
    }

    public ProviderType getProvider() {
        return provider;
    }

    /**
     * Sets the provider class that provides the {@link Container} instance.
     *
     * @param provider the provider class must contain 2 static getters that return {@link Container} and lock instances
     * @return {@link DIBuilder} instance
     */
    public DIBuilder setProvider(Class<?> provider) {
        this.provider = ProviderType.fromClass(provider);
        return this;
    }

    public SubTypeFactory getSubTypeFactory() {
        return subTypeFactory;
    }

    /**
     * Sets the factory that supplies subtypes.
     *
     * @param factory {@link SubTypeFactory} instance
     * @return {@link DIBuilder} instance
     */
    public DIBuilder setSubTypeFactory(SubTypeFactory factory) {
        this.subTypeFactory = Objects.requireNonNull(factory);
        return this;
    }

    public InjectTypeFactory getInjectTypeFactory() {
        return injectTypeFactory;
    }

    /**
     * Specifies a factory that generates information for the subsequent injection of dependencies.
     *
     * @param factory {@link InjectTypeFactory} instance
     * @return {@link DIBuilder} instance
     */
    public DIBuilder setInjectTypeFactory(InjectTypeFactory factory) {
        this.injectTypeFactory = Objects.requireNonNull(factory);
        return this;
    }

    public MetaFactory getMetaFactory() {
        return metaFactory;
    }

    /**
     * Specifies a factory that generates meta-lambdas for manual injection.
     *
     * @param factory the instance of the {@link MetaFactory}
     *                must contain a correctly initialized {@link MethodHandles.Lookup}
     * @return {@link DIBuilder} instance
     */
    public DIBuilder setMetaFactory(MetaFactory factory) {
        this.metaFactory = Objects.requireNonNull(factory);
        return this;
    }

    public boolean isAutoTransform() {
        return autoTransform;
    }

    /**
     * Sets a flag indicating that all classes annotated with {@link AutoTransform}
     * will be found and transformed when creating {@link DI}.
     *
     * @param autoTransform boolean flag, if it is necessary to process classes - true, if not - false.
     * @return {@link DIBuilder} instance
     */
    public DIBuilder setAutoTransform(boolean autoTransform) {
        this.autoTransform = autoTransform;
        return this;
    }

    private void logData() {
        String message = "DI successfully created\n" +
                "Auto transform: " + autoTransform + '\n' +
                "Container provider: " + provider.getType() + '\n' +
                "Sub-type factory: " + subTypeFactory.getClass() + '\n' +
                "Inject type factory: " + injectTypeFactory.getClass();
        LOGGER.debug(message);
    }

    /**
     * @return {@link DI} resulting instance
     */
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
        logData();
        resetValues();
        return ret;
    }
}
