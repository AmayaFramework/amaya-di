package io.github.amayaframework.di.types;

import io.github.amayaframework.di.DirectInject;
import io.github.amayaframework.di.Inject;
import io.github.amayaframework.di.InjectPolicy;
import io.github.amayaframework.di.Value;
import org.atteo.classindex.ClassIndex;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class ReflectTypeFactory implements InjectTypeFactory {
    private static final Predicate<AnnotatedElement> FILTER = element -> element.isAnnotationPresent(Inject.class);

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
        if (Modifier.isAbstract(clazz.getModifiers())) {
            throw new IllegalStateException(String.format("The %s type cannot be abstract", clazz.getName()));
        }
        // Find fields
        List<InjectField> fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(FILTER)
                .map(this::getField)
                .collect(Collectors.toList());
        // Find methods
        List<InjectMethod> methods = Arrays.stream(clazz.getMethods())
                .filter(FILTER)
                .map(this::getMethod)
                .collect(Collectors.toList());
        // Find constructors
        List<InjectConstructor> constructors = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(FILTER)
                .map(this::getConstructor)
                .collect(Collectors.toList());
        return new InjectType(clazz, fields, methods, constructors);
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
