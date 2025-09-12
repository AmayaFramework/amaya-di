package io.github.amayaframework.di;

import io.github.amayaframework.di.core.*;

final class WrapUtil {

    static ObjectFactory wrapInstance(Object instance) {
        if (instance instanceof Closeable) {
            return new InstanceFactory((Closeable) instance);
        }
        return v -> instance;
    }

    static ServiceWrapper wrapLazy(Class<?> target) {
        if (Closeable.class.isAssignableFrom(target)) {
            return LazyCloseableObjectFactory::new;
        }
        return LazyObjectFactory::new;
    }

    private static final class InstanceFactory implements CloseableObjectFactory {
        private final Closeable instance;

        InstanceFactory(Closeable instance) {
            this.instance = instance;
        }

        @Override
        public Object create(TypeProvider provider) {
            return instance;
        }

        @Override
        public void close() {
            instance.close();
        }
    }
}
