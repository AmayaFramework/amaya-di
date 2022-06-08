package io.github.amayaframework.di.containers;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class ProviderType {
    private final Class<?> type;
    private final Method method;

    private ProviderType(Class<?> type, Method method) {
        this.type = type;
        this.method = method;
    }

    public static ProviderType fromClass(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        List<Method> methods = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Provider.class)
                        && Modifier.isStatic(method.getModifiers())
                        && Modifier.isPublic(method.getModifiers()))
                .collect(Collectors.toList());
        if (methods.isEmpty()) {
            throw new IllegalStateException("No one annotated static method found");
        }
        if (methods.size() > 1) {
            throw new IllegalStateException("More than one annotated static method found");
        }
        Method method = methods.get(0);
        Class<?> container = method.getReturnType();
        if (!Container.class.isAssignableFrom(container)) {
            throw new IllegalStateException("The returned type doesn't implement the container interface");
        }
        if (method.getParameterCount() != 0) {
            throw new IllegalStateException("The found method contains parameters");
        }
        return new ProviderType(clazz, method);
    }

    public Class<?> getType() {
        return type;
    }

    public Method getMethod() {
        return method;
    }
}
