package io.github.amayaframework.di;

import io.github.amayaframework.di.constructors.ConstructorFactory;
import io.github.amayaframework.di.containers.ProviderType;
import io.github.amayaframework.di.transformers.Transformer;
import org.atteo.classindex.ClassIndex;

import java.lang.instrument.UnmodifiableClassException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.StreamSupport;

final class IndexDI implements DI {
    private final ProviderType provider;
    private final ConstructorFactory constructorFactory;
    private final Transformer transformer;
    private final Map<Class<?>, Callable<?>> prepared;
    private final Object lock;

    IndexDI(ProviderType provider, ConstructorFactory constructorFactory, Transformer transformer) {
        this.provider = provider;
        this.constructorFactory = constructorFactory;
        this.transformer = transformer;
        this.prepared = new HashMap<>();
        this.lock = new Object();
    }

    private void checkClass(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Inject.class)) {
            throw new InjectError("the class does not have an Inject annotation");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> Callable<E> prepare(Class<E> clazz) {
        synchronized (lock) {
            Callable<E> ret = (Callable<E>) prepared.get(clazz);
            if (ret != null) {
                return ret;
            }
            checkClass(clazz);
            try {
                ret = constructorFactory.getConstructor(clazz, provider);
            } catch (Throwable e) {
                throw new InjectError(clazz, e);
            }
            prepared.put(clazz, ret);
            return ret;
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
        Iterable<Class<?>> found = ClassIndex.getAnnotated(AutoTransform.class);
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
