package io.github.amayaframework.di.types;

import io.github.amayaframework.di.Prototype;
import io.github.amayaframework.di.Singleton;
import io.github.amayaframework.di.Value;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An enumeration containing supported injection policies.
 */
public enum InjectPolicy {
    /**
     * The injected object will be instantiated once and then used in all subsequent injections.
     */
    SINGLETON(Singleton.class),

    /**
     * The injected object will be instantiated every time.
     */
    PROTOTYPE(Prototype.class),

    /**
     * The injected object will be searched in the shared object storage
     * by the name of the target or by the name specified using the {@link Value} annotation.
     */
    VALUE(Value.class);

    private static final Map<Class<? extends Annotation>, InjectPolicy> children = toMap();

    final Class<? extends Annotation> annotation;

    InjectPolicy(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
    }

    private static Map<Class<? extends Annotation>, InjectPolicy> toMap() {
        Map<Class<? extends Annotation>, InjectPolicy> ret = new HashMap<>();
        for (InjectPolicy policy : InjectPolicy.values()) {
            ret.put(policy.annotation, policy);
        }
        return Collections.unmodifiableMap(ret);
    }

    public static InjectPolicy fromAnnotation(Annotation annotation) {
        return children.get(annotation.annotationType());
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }
}
