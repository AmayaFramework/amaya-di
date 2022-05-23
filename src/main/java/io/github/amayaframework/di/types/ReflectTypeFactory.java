package io.github.amayaframework.di.types;

import io.github.amayaframework.di.annotations.DirectInject;
import io.github.amayaframework.di.annotations.Inject;
import io.github.amayaframework.di.annotations.InjectPolicy;
import io.github.amayaframework.di.annotations.Value;
import org.atteo.classindex.ClassIndex;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class ReflectTypeFactory implements InjectTypeFactory {

    private Data extractData(AnnotatedElement element, String name) {
        Inject inject = element.getAnnotation(Inject.class);
        if (inject == null) {
            throw new IllegalStateException("Inject annotation not found");
        }
        InjectPolicy policy = inject.value();
        Value value = element.getAnnotation(Value.class);
        String found = value == null ? name : value.value();
        if (found == null && policy == InjectPolicy.VALUE) {
            throw new IllegalStateException("Value not specified for VALUE policy");
        }
        return new Data(policy, found);
    }

    private void checkParameters(Executable executable) {
        Class<?>[] types = executable.getParameterTypes();
        if (types.length != 1) {
            throw new IllegalStateException("The executable target for the injection must have one parameter");
        }
    }

    @Override
    public InjectField getField(Field field) {
        Data data = extractData(field, field.getName());
        return new InjectField(field, data.policy, data.value);
    }

    @Override
    public InjectMethod getMethod(Method method) {
        checkParameters(method);
        if (Modifier.isStatic(method.getModifiers())) {
            throw new IllegalStateException("Can't inject values into static method");
        }
        Data data = extractData(method, null);
        return new InjectMethod(method, data.policy, data.value);
    }

    @Override
    public InjectConstructor getConstructor(Constructor<?> constructor) {
        checkParameters(constructor);
        Data data = extractData(constructor, null);
        return new InjectConstructor(constructor, data.policy, data.value);
    }

    @Override
    public Collection<InjectType> getInjectTypes() {
        Iterable<Class<?>> found = ClassIndex.getAnnotated(DirectInject.class);
        List<InjectType> ret = new LinkedList<>();
        for (Class<?> clazz : found) {
            ret.add(getInjectType(clazz));
        }
        return ret;
    }

    @Override
    public InjectType getInjectType(Class<?> clazz) {
        List<InjectField> fields = new LinkedList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                fields.add(getField(field));
            }
        }
        List<InjectMethod> methods = new LinkedList<>();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(Inject.class)) {
                methods.add(getMethod(method));
            }
        }
        Constructor<?> found = null;
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (!constructor.isAnnotationPresent(Inject.class)) {
                continue;
            }
            if (found != null) {
                throw new IllegalStateException("Found more than 1 constructor for injection");
            }
            found = constructor;
        }
        InjectConstructor injectConstructor = found == null ? null : getConstructor(found);
        return new InjectType() {

            @Override
            public Collection<InjectField> getFields() {
                return fields;
            }

            @Override
            public Collection<InjectMethod> getMethods() {
                return methods;
            }

            @Override
            public InjectConstructor getConstructor() {
                return injectConstructor;
            }

            @Override
            public Class<?> getTarget() {
                return clazz;
            }
        };
    }

    private static class Data {
        final InjectPolicy policy;
        final String value;

        private Data(InjectPolicy policy, String value) {
            this.policy = policy;
            this.value = value;
        }
    }
}
