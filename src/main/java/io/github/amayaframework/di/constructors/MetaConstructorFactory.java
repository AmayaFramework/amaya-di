package io.github.amayaframework.di.constructors;

import com.github.romanqed.jeflect.ReflectUtil;
import com.github.romanqed.jeflect.meta.LambdaType;
import com.github.romanqed.jeflect.meta.MetaFactory;
import io.github.amayaframework.di.containers.Container;
import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.containers.Value;
import io.github.amayaframework.di.types.*;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Callable;

public class MetaConstructorFactory implements ConstructorFactory {
    private static final LambdaType<Setter> SETTER = LambdaType.fromClass(Setter.class);
    private static final LambdaType<Producer> PRODUCER = LambdaType.fromClass(Producer.class);
    private static final LambdaType<ContainerAccessor> ACCESSOR = LambdaType.fromClass(ContainerAccessor.class);
    private final MetaFactory metaFactory;
    private final InjectTypeFactory injectFactory;
    private final SubTypeFactory typeFactory;
    private final ProviderType provider;

    public MetaConstructorFactory(MetaFactory metaFactory,
                                  InjectTypeFactory injectFactory,
                                  SubTypeFactory typeFactory,
                                  ProviderType provider) {
        this.metaFactory = Objects.requireNonNull(metaFactory);
        this.injectFactory = Objects.requireNonNull(injectFactory);
        this.typeFactory = Objects.requireNonNull(typeFactory);
        this.provider = provider;
    }

    private Callable<?> getDependency(InjectMember member) throws Throwable {
        InjectPolicy policy = member.getPolicy();
        Class<?> subType = typeFactory.getSubType(member.getClazz());
        // Prototype
        if (policy == InjectPolicy.PROTOTYPE) {
            return getConstructor(subType);
        }
        ContainerAccessor accessor = metaFactory.packLambdaMethod(ACCESSOR, provider.getMethod());
        // Singleton
        if (policy == InjectPolicy.SINGLETON) {
            int hashCode = subType.hashCode();
            Callable<?> constructor = getConstructor(subType);
            return () -> {
                Container container = accessor.get();
                Object singleton = container.get(hashCode);
                if (singleton == null) {
                    singleton = constructor.call();
                    container.put(hashCode, singleton);
                }
                return singleton;
            };
        }
        int hashCode = Value.hashcode(member.getValue(), subType);
        return () -> {
            Container container = accessor.get();
            return container.get(hashCode);
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Callable<E> getConstructor(Class<E> clazz) throws Throwable {
        InjectType type = injectFactory.getInjectType(clazz);
        if (type == null) {
            return metaFactory.packLambdaConstructor(ReflectUtil.CALLABLE, clazz.getDeclaredConstructor());
        }
        // Prepare setters
        Collection<InjectMethod> methods = type.getMethods();
        int size = methods.size();
        Setter[] setters = new Setter[size];
        Callable<?>[] dependencies = new Callable<?>[size];
        int index = 0;
        for (InjectMethod method : methods) {
            setters[index] = metaFactory.packLambdaMethod(SETTER, method.getMethod());
            dependencies[index] = getDependency(method);
            ++index;
        }
        // Prepare constructor
        Callable<?> init;
        InjectConstructor constructor = type.getConstructor();
        if (constructor == null) {
            init = metaFactory.packLambdaConstructor(ReflectUtil.CALLABLE, clazz.getDeclaredConstructor());
        } else {
            Producer producer = metaFactory.packLambdaConstructor(PRODUCER, constructor.getConstructor());
            Callable<?> dependency = getDependency(constructor);
            init = () -> producer.produce(dependency.call());
        }
        return () -> {
            Object ret = init.call();
            for (int i = 0; i < size; ++i) {
                setters[i].set(ret, dependencies[i].call());
            }
            return (E) ret;
        };
    }
}
