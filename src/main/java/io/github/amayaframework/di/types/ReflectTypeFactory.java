package io.github.amayaframework.di.types;

import io.github.amayaframework.di.DirectInject;
import io.github.amayaframework.di.Value;
import org.atteo.classindex.ClassIndex;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public final class ReflectTypeFactory implements InjectTypeFactory {
    private Data extractData(AnnotatedElement element, String name) {
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
        if (policy == InjectPolicy.VALUE) {
            String value = ((Value) annotation).value();
            name = !value.isEmpty() ? value : name;
            Objects.requireNonNull(name);
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
            Data data = extractData(field, field.getName());
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
            Data data = extractData(method, null);
            if (data == null) {
                continue;
            }
            checkParameters(method);
            if (Modifier.isStatic(method.getModifiers())) {
                throw new IllegalStateException("Can't inject values into static method");
            }
            ret.add(new InjectMethod(method, data.policy, data.value));
        }
        return ret;
    }

    private List<InjectConstructor> findConstructors(Constructor<?>[] constructors) {
        List<InjectConstructor> ret = new LinkedList<>();
        for (Constructor<?> constructor : constructors) {
            Data data = extractData(constructor, null);
            if (data == null) {
                continue;
            }
            checkParameters(constructor);
            ret.add(new InjectConstructor(constructor, data.policy, data.value));
        }
        return ret;
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
        List<InjectField> fields = findFields(clazz.getDeclaredFields());
        // Find methods
        List<InjectMethod> methods = findMethods(clazz.getMethods());
        // Find constructors
        List<InjectConstructor> constructors = findConstructors(clazz.getDeclaredConstructors());
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
