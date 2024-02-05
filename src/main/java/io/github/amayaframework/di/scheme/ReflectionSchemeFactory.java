package io.github.amayaframework.di.scheme;

import com.github.romanqed.jfunc.Exceptions;
import io.github.amayaframework.di.Artifact;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public final class ReflectionSchemeFactory implements SchemeFactory {
    private static final String ARRAY = "[";
    private static final String REFERENCE = "L";

    private final Class<? extends Annotation> annotation;
    private final ClassLoader loader;

    public ReflectionSchemeFactory(Class<? extends Annotation> annotation, ClassLoader loader) {
        this.annotation = Objects.requireNonNull(annotation);
        this.loader = loader;
    }

    public ReflectionSchemeFactory(Class<? extends Annotation> annotation) {
        this(annotation, ReflectionSchemeFactory.class.getClassLoader());
    }

    private Class<?> of(String name, int array) {
        if (array == 0) {
            return Exceptions.suppress(() -> Class.forName(name, false, loader));
        }
        return Exceptions.suppress(() -> Class.forName(ARRAY.repeat(array) + REFERENCE + name + ";", false, loader));
    }

    private Type unpackWildcard(Type type) {
        if (!(type instanceof WildcardType)) {
            return type;
        }
        var wildcard = (WildcardType) type;
        if (wildcard.getLowerBounds().length != 0) {
            throw new IllegalTypeException("Super wildcards are not supported", type);
        }
        var bounds = wildcard.getUpperBounds();
        if (bounds.length != 1) {
            throw new IllegalTypeException("Multiple wildcards are not supported", type);
        }
        return bounds[0];
    }

    private Object process(Type type) {
        type = unpackWildcard(type);
        var array = 0;
        while (type instanceof GenericArrayType) {
            type = ((GenericArrayType) type).getGenericComponentType();
            ++array;
        }
        if (!(type instanceof ParameterizedType)) {
            return of(type.getTypeName(), array);
        }
        var parameterized = (ParameterizedType) type;
        var clazz = of(parameterized.getRawType().getTypeName(), array);
        var arguments = parameterized.getActualTypeArguments();
        var metadata = new Object[arguments.length];
        var wildcards = 0;
        for (var i = 0; i < arguments.length; ++i) {
            var object = process(arguments[i]);
            if (object == Object.class) {
                ++wildcards;
            }
            metadata[i] = object;
        }
        if (metadata.length == wildcards) {
            return clazz;
        }
        return new Artifact(clazz, metadata);
    }

    private Artifact makeArtifact(Type type) {
        var ret = process(type);
        if (ret instanceof Artifact) {
            return (Artifact) ret;
        }
        return new Artifact((Class<?>) ret);
    }

    private void process(Parameter[] parameters, int start, Set<Artifact> artifacts, Artifact[] mapping) {
        for (var i = start; i < parameters.length; ++i) {
            var parameter = parameters[i];
            var artifact = makeArtifact(parameter.getParameterizedType());
            artifacts.add(artifact);
            mapping[i - start] = artifact;
        }
    }

    private void process(Parameter[] parameters, Set<Artifact> artifacts, Artifact[] mapping) {
        process(parameters, 0, artifacts, mapping);
    }

    private ConstructorScheme create(Constructor<?> constructor) {
        if (constructor.getTypeParameters().length != 0) {
            throw new IllegalMemberException("Cannot use parameterized constructor", constructor);
        }
        var artifacts = new HashSet<Artifact>();
        var mapping = new Artifact[constructor.getParameterCount()];
        process(constructor.getParameters(), artifacts, mapping);
        return new ConstructorScheme(constructor, artifacts, mapping);
    }

    private MethodScheme create(Method method) {
        if (method.getTypeParameters().length != 0) {
            throw new IllegalMemberException("Cannot use parameterized method", method);
        }
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

    private FieldScheme create(Field field) {
        var artifact = makeArtifact(field.getGenericType());
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
        if (clazz.getTypeParameters().length != 0) {
            throw new IllegalSchemeException(clazz, "Cannot create scheme of parameterized class");
        }
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
