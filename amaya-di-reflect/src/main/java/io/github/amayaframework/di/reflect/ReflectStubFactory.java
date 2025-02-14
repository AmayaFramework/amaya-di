package io.github.amayaframework.di.reflect;

import com.github.romanqed.jfunc.Function0;
import com.github.romanqed.jfunc.Runnable1;
import io.github.amayaframework.di.scheme.ClassScheme;
import io.github.amayaframework.di.scheme.ConstructorScheme;
import io.github.amayaframework.di.scheme.FieldScheme;
import io.github.amayaframework.di.scheme.MethodScheme;
import io.github.amayaframework.di.stub.StubFactory;
import io.github.amayaframework.di.stub.TypeProvider;

import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Set;

/**
 * A factory that creates instantiators using member accessors from the Reflection API.
 */
public final class ReflectStubFactory implements StubFactory {
    private final ReflectCloner cloner;

    /**
     * Constructs a {@link ReflectStubFactory} instance with the specified {@link ReflectCloner} instance,
     * which will be used to clone reflective entities from {@link ClassScheme}.
     *
     * @param cloner the {@link ReflectCloner} instance, must be non-null
     */
    public ReflectStubFactory(ReflectCloner cloner) {
        this.cloner = Objects.requireNonNull(cloner);
    }

    /**
     * Constructs a {@link ReflectStubFactory} instance with the {@link NoopReflectCloner}.
     * Suitable for use with any {@link io.github.amayaframework.di.scheme.SchemeFactory}
     * and {@link io.github.amayaframework.di.ServiceProviderBuilder} implementations
     * that use the {@link ClassScheme} created by the factory ONLY ONCE.
     * Otherwise, after the first build of the {@link io.github.amayaframework.di.ServiceProvider},
     * all the reflective objects in it will be changed by calling
     * {@link java.lang.reflect.AccessibleObject#setAccessible(boolean)}.
     */
    public ReflectStubFactory() {
        this.cloner = new NoopReflectCloner();
    }

    @SuppressWarnings("rawtypes")
    private Function0 wrapConstructor(ConstructorScheme scheme, TypeProvider provider) {
        var mapping = scheme.getMapping();
        var length = mapping.length;
        var target = cloner.clone(scheme.getTarget());
        target.setAccessible(true);
        if (length == 0) {
            return new EmptyConstructorObjectFactory(target);
        }
        var providers = new Function0[length];
        for (var i = 0; i < length; ++i) {
            providers[i] = provider.apply(mapping[i]);
        }
        return new ConstructorObjectFactory(target, providers);
    }

    @SuppressWarnings("rawtypes")
    private Runnable1 wrapMethod(MethodScheme scheme, TypeProvider provider) {
        var mapping = scheme.getMapping();
        var length = mapping.length;
        var target = cloner.clone(scheme.getTarget());
        target.setAccessible(true);
        var providers = new Function0[length];
        for (var i = 0; i < length; ++i) {
            providers[i] = provider.apply(mapping[i]);
        }
        if (Modifier.isStatic(target.getModifiers())) {
            return new StaticMethodInvoker(target, providers);
        }
        return new VirtualMethodInvoker(target, providers);
    }

    private FieldEntry wrapField(FieldScheme scheme, TypeProvider provider) {
        var target = cloner.clone(scheme.getTarget());
        target.setAccessible(true);
        var found = provider.apply(scheme.getType());
        return new FieldEntry(target, found);
    }

    @SuppressWarnings("rawtypes")
    private Runnable1[] wrapMethods(Set<MethodScheme> schemes, TypeProvider provider) {
        var ret = new Runnable1[schemes.size()];
        var i = 0;
        for (var scheme : schemes) {
            ret[i++] = wrapMethod(scheme, provider);
        }
        return ret;
    }

    private FieldEntry[] wrapFields(Set<FieldScheme> schemes, TypeProvider provider) {
        var ret = new FieldEntry[schemes.size()];
        var i = 0;
        for (var scheme : schemes) {
            ret[i++] = wrapField(scheme, provider);
        }
        return ret;
    }

    @Override
    public Function0<?> create(ClassScheme scheme, TypeProvider provider) {
        Objects.requireNonNull(provider);
        var constructor = wrapConstructor(scheme.getConstructorScheme(), provider);
        var methodSchemes = scheme.getMethodSchemes();
        var fieldSchemes = scheme.getFieldSchemes();
        if (methodSchemes.isEmpty() && fieldSchemes.isEmpty()) {
            return constructor;
        }
        if (methodSchemes.isEmpty()) {
            var fields = wrapFields(fieldSchemes, provider);
            return new FieldsObjectFactory(constructor, fields);
        }
        if (fieldSchemes.isEmpty()) {
            var methods = wrapMethods(methodSchemes, provider);
            return new MethodsObjectFactory(constructor, methods);
        }
        var methods = wrapMethods(methodSchemes, provider);
        var fields = wrapFields(fieldSchemes, provider);
        return new FullObjectFactory(constructor, methods, fields);
    }
}
