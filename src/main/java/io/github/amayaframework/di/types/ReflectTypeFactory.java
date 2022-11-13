package io.github.amayaframework.di.types;

import io.github.amayaframework.di.Inject;
import io.github.amayaframework.di.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Implementation {@link InjectTypeFactory} using standard reflection.</p>
 * <p>All classes must be annotated with {@link Inject}.</p>
 */
public final class ReflectTypeFactory implements InjectTypeFactory {
    private Data extractData(AnnotatedElement element) {
        List<Annotation> found = Arrays.stream(element.getDeclaredAnnotations())
                .filter(e -> InjectPolicy.fromAnnotation(e) != null)
                .collect(Collectors.toList());
        if (found.isEmpty()) {
            return null;
        }
        if (found.size() > 1) {
            throw new IllegalStateException("Several inject annotations found");
        }
        Annotation annotation = found.get(0);
        InjectPolicy policy = InjectPolicy.fromAnnotation(annotation);
        String name = null;
        if (policy == InjectPolicy.VALUE) {
            name = ((Value) annotation).value();
        }
        return new Data(policy, name);
    }

    private void checkParameters(Executable executable) {
        Class<?>[] types = executable.getParameterTypes();
        if (types.length != 1) {
            throw new IllegalStateException("The executable target for the injection must have one parameter");
        }
    }

    private List<InjectField> findFields(Field[] fields) {
        List<InjectField> ret = new LinkedList<>();
        for (Field field : fields) {
            Data data = extractData(field);
            if (data == null) {
                continue;
            }
            ret.add(new InjectField(field, data.policy, data.value));
        }
        return ret;
    }

    private List<InjectMethod> findMethods(Method[] methods) {
        List<InjectMethod> ret = new LinkedList<>();
        for (Method method : methods) {
            Data data = extractData(method);
            if (data == null) {
                continue;
            }
            checkParameters(method);
            if (Modifier.isStatic(method.getModifiers())) {
                throw new IllegalStateException("Can't inject values into static method");
            }
            if (method.getReturnType() != void.class) {
                throw new IllegalStateException("The method must to return nothing");
            }
            ret.add(new InjectMethod(method, data.policy, data.value));
        }
        return ret;
    }

    private InjectConstructor findConstructor(Constructor<?>[] constructors) {
        InjectConstructor ret = null;
        for (Constructor<?> constructor : constructors) {
            Data data = extractData(constructor);
            if (data == null) {
                continue;
            }
            checkParameters(constructor);
            if (ret != null) {
                throw new IllegalStateException("Found more than 1 constructor for injection");
            }
            ret = new InjectConstructor(constructor, data.policy, data.value);
        }
        return ret;
    }

    @Override
    public InjectType getInjectType(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Inject.class)) {
            return null;
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            throw new IllegalStateException(String.format("The %s type cannot be abstract", clazz.getName()));
        }
        // Find fields
        List<InjectField> fields = findFields(clazz.getDeclaredFields());
        // Find methods
        List<InjectMethod> methods = findMethods(clazz.getMethods());
        // Find constructor
        InjectConstructor constructor = findConstructor(clazz.getDeclaredConstructors());
        if (fields.isEmpty() && methods.isEmpty() && constructor == null) {
            throw new IllegalStateException("No elements found for injection");
        }
        return new InjectType(clazz, fields, methods, constructor);
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
