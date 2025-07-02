package io.github.amayaframework.di.core;

public final class LazyObjectFactory implements ObjectFactory {
    private final ObjectFactory body;
    private final Object lock;
    private volatile Object value;

    public LazyObjectFactory(ObjectFactory body) {
        this.body = body;
        this.lock = new Object();
    }

    @Override
    public Object create(TypeProvider provider) {
        if (value == null) {
            synchronized (lock) {
                if (value == null) {
                    value = body.create(provider);
                }
            }
        }
        return value;
    }
}
