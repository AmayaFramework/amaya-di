package io.github.amayaframework.di.scheme;

import com.github.romanqed.jfunc.Exceptions;
import io.github.amayaframework.di.Artifact;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A factory that creates a class schema based on information obtained through a java reflection api.
 * The scheme is based on the following rules:
 * <br>
 * 1. The constructor is selected from the public ones,
 * and this choice is made strictly unambiguously, i.e. the following cases will be incorrect:
 * <pre>
 *
 *     class Service1 {
 *         private Service1() {}
 *     }
 *     ...
 *     class Service2 {
 *         public Service2(int i) {}
 *         public Service2() {}
 *     }
 * </pre>
 * However, if the class contains several public constructors,
 * you can specify the necessary one using a marker annotation:
 * <pre>
 *     class Service {
 *         public Service(int i) {}
 *
 *        {@literal @}Inject
 *         public Service() {}
 *     }
 * </pre>
 * 2. The fields are selected exclusively from the virtual and public ones marked with a marker annotation.
 * 3. Methods are selected from public ones marked with a marker annotation and containing at least 1 parameter,
 * otherwise, even if there is an annotation, the method will be discarded.
 * <br>
 * If the method is static, it must match the following pattern:
 * <pre>
 *     class Service {
 *         public static void setter(? super Service, Dependency d, ...) {}
 *     }
 * </pre>
 * Parameterized classes and methods are not supported,
 * and super-wildcards are not supported for statically defined generics.
 * In other cases, type inference for wildcards will work as follows:
 * <br>
 * {@code ? => Object}
 * <br>
 * {@code ? extends Object => Object}
 * <br>
 * {@code ? extends Type => Type}
 */
public final class ReflectionSchemeFactory implements SchemeFactory {
    private static final String ARRAY = "[";
    private static final String REFERENCE = "L";

    private final Class<? extends Annotation> annotation;

    /**
     * Constructs a factory that will use the specified annotation as a marker
     * to identify the dependent members of the class.
     *
     * @param annotation the specified annotation type
     */
    public ReflectionSchemeFactory(Class<? extends Annotation> annotation) {
        this.annotation = Objects.requireNonNull(annotation);
    }

    private static Class<?> of(Type type, int array) {
        if (!(type instanceof Class)) {
            throw new IllegalTypeException(type);
        }
        var clazz = (Class<?>) type;
        if (array == 0) {
            return clazz;
        }
        return Exceptions.suppress(() -> Class.forName(
                ARRAY.repeat(array) + REFERENCE + clazz.getTypeName() + ";",
                false,
                clazz.getClassLoader()
        ));
    }

    private static Type unpackWildcard(Type type) {
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

    private static Object process(Type type) {
        type = unpackWildcard(type);
        var array = 0;
        while (type instanceof GenericArrayType) {
            type = ((GenericArrayType) type).getGenericComponentType();
            ++array;
        }
        if (!(type instanceof ParameterizedType)) {
            return of(type, array);
        }
        var parameterized = (ParameterizedType) type;
        var clazz = of(parameterized.getRawType(), array);
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

    private static Artifact makeArtifact(Type type, Class<?> clazz) {
        if (clazz.isPrimitive()) {
            throw new IllegalTypeException("Primitive types are not supported", type);
        }
        var ret = process(type);
        if (ret instanceof Artifact) {
            return (Artifact) ret;
        }
        return new Artifact((Class<?>) ret);
    }

    private static void process(Parameter[] parameters, int start, Set<Artifact> artifacts, Artifact[] mapping) {
        for (var i = start; i < parameters.length; ++i) {
            var parameter = parameters[i];
            var artifact = makeArtifact(parameter.getParameterizedType(), parameter.getType());
            artifacts.add(artifact);
            mapping[i - start] = artifact;
        }
    }

    private static void process(Parameter[] parameters, Set<Artifact> artifacts, Artifact[] mapping) {
        process(parameters, 0, artifacts, mapping);
    }

    private static ConstructorScheme create(Constructor<?> constructor) {
        if (constructor.getTypeParameters().length != 0) {
            throw new IllegalMemberException("Cannot use parameterized constructor", constructor);
        }
        var artifacts = new HashSet<Artifact>();
        var mapping = new Artifact[constructor.getParameterCount()];
        process(constructor.getParameters(), artifacts, mapping);
        return new ConstructorScheme(constructor, artifacts, mapping);
    }

    private static MethodScheme create(Method method) {
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
            throw new IllegalClassException(
                    "The first parameter of the static method must be the superclass of the current class",
                    owner
            );
        }
        var mapping = new Artifact[method.getParameterCount() - 1];
        process(method.getParameters(), 1, artifacts, mapping);
        return new MethodScheme(method, artifacts, mapping);
    }

    private static FieldScheme create(Field field) {
        var artifact = makeArtifact(field.getGenericType(), field.getType());
        return new FieldScheme(field, artifact);
    }

    private ConstructorScheme findConstructor(Class<?> clazz) {
        // Get all public constructors
        var constructors = clazz.getConstructors();
        // If there is no public constructors, then we cannot build class scheme
        if (constructors.length == 0) {
            throw new IllegalClassException("No public constructor was found", clazz);
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
            throw new IllegalClassException("There are no annotated constructors", clazz);
        }
        if (found.size() != 1) {
            throw new IllegalClassException("It is impossible to select a constructor", clazz);
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
        Objects.requireNonNull(clazz);
        // Check class
        if (clazz.getTypeParameters().length != 0) {
            throw new IllegalClassException("Cannot create scheme of parameterized class", clazz);
        }
        var modifiers = clazz.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalClassException("Cannot create scheme of non-public class", clazz);
        }
        if (Modifier.isAbstract(modifiers)) {
            throw new IllegalClassException("Cannot create scheme of abstract class", clazz);
        }
        if (clazz.isEnum()) {
            throw new IllegalClassException("Cannot create scheme of enum class", clazz);
        }
        if (clazz.isPrimitive()) {
            throw new IllegalClassException("Cannot create scheme of primitive class", clazz);
        }
        if (clazz.isArray()) {
            throw new IllegalClassException("Cannot create scheme of array class", clazz);
        }
        if (clazz.isAnnotation()) {
            throw new IllegalClassException("Cannot create scheme of annotation class", clazz);
        }
        if (clazz.isAnonymousClass()) {
            throw new IllegalClassException("Cannot create scheme of anonymous class", clazz);
        }
        if (clazz.getDeclaringClass() != null && !Modifier.isStatic(modifiers)) {
            throw new IllegalClassException("Cannot create scheme of non-static member class", clazz);
        }
        var constructor = findConstructor(clazz);
        var fields = findFields(clazz);
        var methods = findMethods(clazz);
        return new ClassScheme(clazz, constructor, fields, methods);
    }
}
