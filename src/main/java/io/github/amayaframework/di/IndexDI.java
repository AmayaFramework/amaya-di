package io.github.amayaframework.di;

import io.github.amayaframework.di.constructors.ConstructorFactory;
import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.transformers.Transformer;
import org.atteo.classindex.ClassIndex;

import java.lang.instrument.UnmodifiableClassException;
import java.util.concurrent.Callable;
import java.util.stream.StreamSupport;

public class IndexDI implements DI {
    private final ProviderType provider;
    private final ConstructorFactory constructorFactory;
    private final Transformer transformer;

    IndexDI(ProviderType provider, ConstructorFactory constructorFactory, Transformer transformer) {
        this.provider = provider;
        this.constructorFactory = constructorFactory;
        this.transformer = transformer;
    }

    private void checkClass(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Inject.class)) {
            throw new InjectError("the class does not have an Inject annotation");
        }
    }

    @Override
    public <E> Callable<E> prepare(Class<E> clazz) {
        checkClass(clazz);
        try {
            return constructorFactory.getConstructor(clazz, provider);
        } catch (Throwable e) {
            throw new InjectError(clazz, e);
        }
    }

    @Override
    public void transform(Class<?> clazz) {
        checkClass(clazz);
        try {
            transformer.transform(clazz, provider);
        } catch (UnmodifiableClassException e) {
            throw new InjectError(clazz, e);
        }
    }

    @Override
    public Class<?>[] transform() {
        Iterable<Class<?>> found = ClassIndex.getAnnotated(Autowire.class);
        Class<?>[] classes = StreamSupport.stream(found.spliterator(), false).toArray(Class<?>[]::new);
        if (classes.length == 0) {
            return classes;
        }
        for (Class<?> clazz : classes) {
            checkClass(clazz);
            try {
                transformer.transform(clazz, provider);
            } catch (UnmodifiableClassException e) {
                throw new InjectError(clazz, e);
            }
        }
        return classes;
    }
}
