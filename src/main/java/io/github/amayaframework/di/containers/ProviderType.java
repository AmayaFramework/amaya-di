package io.github.amayaframework.di.containers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A class that stores data about a singleton class that stores a {@link Container}.
 */
public class ProviderType {
    private static final Class<? extends Annotation> PROVIDER = ContainerProvider.class;
    private static final Class<? extends Annotation> LOCK = LockProvider.class;
    private final Class<?> type;
    private final Method containerMethod;
    private final Method lockMethod;

    private ProviderType(Class<?> type, Method containerMethod, Method lockMethod) {
        this.type = type;
        this.containerMethod = containerMethod;
        this.lockMethod = lockMethod;
    }

    private static Method findMethodAnnotatedWith(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Method> methods = Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotation)
                        && Modifier.isStatic(method.getModifiers())
                        && Modifier.isPublic(method.getModifiers()))
                .collect(Collectors.toList());
        if (methods.isEmpty()) {
            throw new IllegalStateException("No one annotated static method found");
        }
        if (methods.size() > 1) {
            throw new IllegalStateException("More than one annotated static method found");
        }
        return methods.get(0);
    }

    private static void checkMethod(Method method) {
        if (method.getParameterCount() != 0) {
            throw new IllegalStateException("The found method contains parameters");
        }
    }

    public static ProviderType fromClass(Class<?> clazz) {
        Objects.requireNonNull(clazz);
        Method containerMethod = findMethodAnnotatedWith(clazz, PROVIDER);
        checkMethod(containerMethod);
        Class<?> container = containerMethod.getReturnType();
        if (!Container.class.isAssignableFrom(container)) {
            throw new IllegalStateException("The returned type doesn't implement the container interface");
        }
        Method lockMethod = findMethodAnnotatedWith(clazz, LOCK);
        checkMethod(lockMethod);
        return new ProviderType(clazz, containerMethod, lockMethod);
    }

    public Class<?> getType() {
        return type;
    }

    public Method getContainerMethod() {
        return containerMethod;
    }

    public Method getLockMethod() {
        return lockMethod;
    }
}
