package io.github.amayaframework.di.scheme;

import io.github.amayaframework.di.Artifact;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public final class ReflectionSchemeFactory implements SchemeFactory {
    private final Class<? extends Annotation> annotation;

    public ReflectionSchemeFactory(Class<? extends Annotation> annotation) {
        this.annotation = Objects.requireNonNull(annotation);
    }

    private static Artifact of(Class<?> clazz, Type type) {
        if (!(type instanceof ParameterizedType)) {
            return new Artifact(clazz);
        }
        return new Artifact(clazz, ((ParameterizedType) type).getActualTypeArguments());
    }

    private static void process(Parameter[] parameters, int start, Set<Artifact> artifacts, Artifact[] mapping) {
        for (var i = start; i < parameters.length; ++i) {
            var parameter = parameters[i];
            var artifact = of(parameter.getType(), parameter.getParameterizedType());
            artifacts.add(artifact);
            mapping[i - start] = artifact;
        }
    }

    private static void process(Parameter[] parameters, Set<Artifact> artifacts, Artifact[] mapping) {
        process(parameters, 0, artifacts, mapping);
    }

    private static ConstructorScheme create(Constructor<?> constructor) {
        var artifacts = new HashSet<Artifact>();
        var mapping = new Artifact[constructor.getParameterCount()];
        process(constructor.getParameters(), artifacts, mapping);
        return new ConstructorScheme(constructor, artifacts, mapping);
    }

    private static MethodScheme create(Method method) {
        var artifacts = new HashSet<Artifact>();
        if (!Modifier.isStatic(method.getModifiers())) {
            var mapping = new Artifact[method.getParameterCount()];
            process(method.getParameters(), artifacts, mapping);
            return new MethodScheme(method, artifacts, mapping);
        }
        var first = method.getParameterTypes()[0];
        var owner = method.getDeclaringClass();
        if (!first.isAssignableFrom(owner)) {
            throw new IllegalSchemeException(
                    owner,
                    "The first parameter of the static method must be the superclass of the current class"
            );
        }
        var mapping = new Artifact[method.getParameterCount() - 1];
        process(method.getParameters(), 1, artifacts, mapping);
        return new MethodScheme(method, artifacts, mapping);
    }

    private static FieldScheme create(Field field) {
        var artifact = of(field.getType(), field.getGenericType());
        return new FieldScheme(field, artifact);
    }

    private ConstructorScheme findConstructor(Class<?> clazz) {
        // Get all public constructors
        var constructors = clazz.getConstructors();
        // If there is no public constructors, then we cannot build class scheme
        if (constructors.length == 0) {
            throw new IllegalSchemeException(clazz, "No public constructor was found");
        }
        // If there is 1 public constructor, just use it
        if (constructors.length == 1) {
            return create(constructors[0]);
        }
        // Else try to find annotated constructor
        var found = Arrays
                .stream(constructors)
                .filter(e -> e.isAnnotationPresent(annotation))
                .collect(Collectors.toList());
        if (found.isEmpty()) {
            throw new IllegalSchemeException(clazz, "There are no annotated constructors");
        }
        if (found.size() != 1) {
            throw new IllegalSchemeException(clazz, "It is impossible to select a constructor");
        }
        return create(found.get(0));
    }

    private Set<FieldScheme> findFields(Class<?> clazz) {
        // Collect all public virtual non-final fields, annotated with specified annotation
        var fields = Arrays
                .stream(clazz.getFields())
                .filter(field -> {
                    var modifiers = field.getModifiers();
                    return !Modifier.isStatic(modifiers)
                            && !Modifier.isFinal(modifiers)
                            && field.isAnnotationPresent(annotation);
                })
                .collect(Collectors.toList());
        if (fields.isEmpty()) {
            return Collections.emptySet();
        }
        var ret = new HashSet<FieldScheme>();
        fields.forEach(field -> ret.add(create(field)));
        return ret;
    }

    private boolean checkMethod(Method method) {
        if (!method.isAnnotationPresent(annotation)) {
            return false;
        }
        var parameters = method.getParameterCount();
        if (Modifier.isStatic(method.getModifiers())) {
            return parameters > 1;
        }
        return parameters > 0;
    }

    private Set<MethodScheme> findMethods(Class<?> clazz) {
        // Collect all public methods, annotated with specified annotation
        // (static or virtual - it does not matter)
        var methods = Arrays
                .stream(clazz.getMethods())
                .filter(this::checkMethod)
                .collect(Collectors.toList());
        if (methods.isEmpty()) {
            return Collections.emptySet();
        }
        var ret = new HashSet<MethodScheme>();
        methods.forEach(method -> ret.add(create(method)));
        return ret;
    }

    @Override
    public ClassScheme create(Class<?> clazz) {
        // Check class
        var modifiers = clazz.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalSchemeException(clazz, "Cannot create scheme of non-public class");
        }
        if (Modifier.isAbstract(modifiers)) {
            throw new IllegalSchemeException(clazz, "Cannot create scheme of abstract class");
        }
        if (clazz.isEnum()) {
            throw new IllegalSchemeException(clazz, "Cannot create scheme of enum class");
        }
        if (clazz.isPrimitive()) {
            throw new IllegalSchemeException(clazz, "Cannot create scheme of primitive class");
        }
        if (clazz.isArray()) {
            throw new IllegalSchemeException(clazz, "Cannot create scheme of array class");
        }
        if (clazz.isAnnotation()) {
            throw new IllegalSchemeException(clazz, "Cannot create scheme of annotation class");
        }
        if (clazz.isAnonymousClass()) {
            throw new IllegalSchemeException(clazz, "Cannot create scheme of anonymous class");
        }
        if (clazz.getDeclaringClass() != null && !Modifier.isStatic(modifiers)) {
            throw new IllegalSchemeException(clazz, "Cannot create scheme of non-static member class");
        }
        var constructor = findConstructor(clazz);
        var fields = findFields(clazz);
        var methods = findMethods(clazz);
        return new ClassScheme(clazz, constructor, fields, methods);
    }
}
