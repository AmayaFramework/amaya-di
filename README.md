<p align="center">
  <img src="docs/img/logo.png" alt="logo" style="width: 200px; height: auto;">
</p>

# amaya-di – a modern, lightweight, and fast DI framework.

| Модуль           | Версия                                                                                                                             |
|:-----------------:|:---------------------------------------------------------------------------------------------------------------------------------:|
| amaya-di          | [![amaya-di](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di?strategy=releaseProperty&style=for-the-badge&label=amaya-di&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di/) |
| amaya-di-core     | [![amaya-di-core](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-core?strategy=releaseProperty&style=for-the-badge&label=amaya-di-core&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-core/) |
| amaya-di-schema   | [![amaya-di-schema](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-schema?strategy=releaseProperty&style=for-the-badge&label=amaya-di-schema&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-schema/) |
| amaya-di-stub     | [![amaya-di-stub](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-stub?strategy=releaseProperty&style=for-the-badge&label=amaya-di-stub&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-stub/) |
| amaya-di-asm      | [![amaya-di-asm](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-asm?strategy=releaseProperty&style=for-the-badge&label=amaya-di-asm&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-asm/) |
| amaya-di-reflect  | [![amaya-di-reflect](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-reflect?strategy=releaseProperty&style=for-the-badge&label=amaya-di-reflect&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-reflect/) |

- English (AI translated, GPT-4o)
- [Русский](docs/readme/readme_ru.md)
- [简体中文](docs/readme/readme_zh_cn.md)
- [正體中文](docs/readme/readme_zh_tw.md)
- [日本語](docs/readme/readme_jp.md)
- [Deutsch](docs/readme/readme_de.md)
- [Français](docs/readme/readme_fr.md)

Amaya DI is a modern view on what a DI framework should be. It is designed to provide developers with a
high-performance, flexible, and minimalist IoC container without outdated XML configurations, excessive annotations, or
hidden reflection-based magic.

The framework supports the following features "out of the box":

* working with generic types (e.g., `List<String>` and `List<Integer>` are recognized as two different types);
* creation of unnamed scopes;
* automatic runtime generation of object factories:
    * new-object-per-request (transient) and same-object-per-request (singleton) strategies;
    * scoped transient and singleton strategies;
* building and validation of the dependency graph:
    * detecting missing types;
    * detecting cycles using Tarjan’s iterative algorithm;
* a fully declarative IoC container build process with a convenient fluent API;
* easy integration with any existing library, framework, or standard.

Performance of generated object factories is:

* using the asm implementation matches manual implementation (13,604 ns vs 13,475 ns);
* using the reflect implementation includes JNI overhead but is still reasonably fast (58,055 ns vs 13,402 ns).

For more details see the [benchmarks](#benchmarks) section.

# Introduction

Requirements:

* JVM 11+
* Maven/Gradle

Any JVM version below 11 is not supported.

Use of languages other than Java is possible since the framework has no compile-time plugins and does not analyze Java
AST.

## Installation

To use the framework, two modules must be installed: the core (`io.github.amayaframework:amaya-di`) and a stub factory
implementation (`:amaya-di-asm` or `:amaya-di-reflect`).  
See [implementation choice](#implementation-choice) for details.

### Gradle

```Groovy
dependencies {
    implementation group: 'io.github.amayaframework', name: 'amaya-di', version: '3.1.0'
    // ASM stub implementation
    implementation group: 'io.github.amayaframework', name: 'amaya-di-asm', version: '2.1.0'
    // Or reflect stub implementation
    implementation group: 'io.github.amayaframework', name: 'amaya-di-reflect', version: '2.1.0'
}
```

### Maven

```
<dependencies>
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-di</artifactId>
        <version>3.1.0</version>
    </dependency>
    <!--ASM stub implementation-->
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-di-asm</artifactId>
        <version>2.1.0</version>
    </dependency>
    <!--Or reflect stub implementation-->
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-di-reflect</artifactId>
        <version>2.1.0</version>
    </dependency>
</dependencies>
```

## Hello, world!

The container is built using a builder (`ServiceProviderBuilder` or `ScopedProviderBuilder`).  
The build process can be split into 3 stages:

* obtain and configure the builder object;
* declare the required types;
* call the build() method.

Let's look at basic usage scenarios.

First, if using modules, declare dependencies in `module-info.java`:

```java
module com.github.romanqed.di.examples {
    requires amayaframework.di; // Core module
    exports com.github.romanqed.di.examples;
}
```

Now let's build a simplest container:

```java
public final class SimpleHelloWorld {

    public static void main(String[] args) {
        var provider = ProviderBuilders.create()
                .addInstance("Hello, world!")
                .build();
        System.out.println(provider.get(String.class));
    }
}
```

Running this will print `Hello, world!`.

Consider a more complex scenario. Suppose we have an abstract service `IGreeter`:

```java
public interface IGreeter {
    String sayHello(String name);
}
```

And two implementations, one for the global context, one for scoped:

```java
public final class GlobalGreeter implements IGreeter {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }
}

public final class ScopedGreeter implements IGreeter {
    private final String scope;

    public ScopedGreeter(String scope) {
        this.scope = scope;
    }

    @Override
    public String sayHello(String name) {
        return "Hello from scope '" + scope + "', " + name + "!";
    }
}
```

Add the reflective factory generator to `module-info.java`:

```java
module com.github.romanqed.di.examples {
    requires amayaframework.di; // Core module
    requires amayaframework.di.reflect; // Using reflect implementation
    exports com.github.romanqed.di.examples;
}
```

Build the container:

```java
var provider = ProviderBuilders.createScoped(new ReflectStubFactory())
        .addSingleton(IGreeter.class, GlobalGreeter.class)
        .addScoped(String.class)
        .addScopedSingleton(IGreeter.class, ScopedGreeter.class)
        .build();
```

Create two scopes:

```java
var scope1 = provider.createScoped();
scope1.repository().put("Scope One");
var scope2 = provider.createScoped();
scope2.repository().put("Scope Two");
```

Finally, print the results:

```java
System.out.println(provider.get(IGreeter.class).sayHello("Roman"));
System.out.println(scope1.get(IGreeter.class).sayHello("Roman"));
System.out.println(scope2.get(IGreeter.class).sayHello("Roman"));
```

The console will output:

```
Hello, Roman!
Hello from scope 'Scope One', Roman!
Hello from scope 'Scope Two', Roman!
```

Full code looks like this:

```java
public final class ComplexHelloWorld {
    public static void main(String[] args) {
        var provider = ProviderBuilders.createScoped(new ReflectStubFactory())
                .addSingleton(IGreeter.class, GlobalGreeter.class)
                .addScoped(String.class)
                .addScopedSingleton(IGreeter.class, ScopedGreeter.class)
                .build();
        var scope1 = provider.createScoped();
        scope1.repository().put("Scope One");
        var scope2 = provider.createScoped();
        scope2.repository().put("Scope Two");
        System.out.println(provider.get(IGreeter.class).sayHello("Roman"));
        System.out.println(scope1.get(IGreeter.class).sayHello("Roman"));
        System.out.println(scope2.get(IGreeter.class).sayHello("Roman"));
    }

    public interface IGreeter {
        String sayHello(String name);
    }

    public static final class GlobalGreeter implements IGreeter {

        @Override
        public String sayHello(String name) {
            return "Hello, " + name + "!";
        }
    }

    public static final class ScopedGreeter implements IGreeter {
        private final String scope;

        public ScopedGreeter(String scope) {
            this.scope = scope;
        }

        @Override
        public String sayHello(String name) {
            return "Hello from scope '" + scope + "', " + name + "!";
        }
    }
}
```

All runnable examples are available [here](examples).

# Basic Concepts

## Core

### ObjectFactory and TypeProvider

The main mechanism underlying the container are the interfaces `ObjectFactory` and `TypeProvider`. Their definitions
are:

```java
@FunctionalInterface
public interface ObjectFactory {
    Object create(TypeProvider provider) throws Throwable;
}
```

```java
@FunctionalInterface
public interface TypeProvider {
    ObjectFactory get(Type type);

    default boolean canProvide(Type type) {
        return get(type) != null;
    }
}
```

`ObjectFactory` creates an instance of a type whose dependencies are supplied by `TypeProvider`. This design is
convenient for two reasons:

1) the object factory has no hard dependency on the type provider and can wrap or modify it before passing it to
   lower-level factories, enabling any scenario;

2) obtaining the factory directly instead of an instance allows avoiding extra lookups and caching the factory without
   losing other advantages.

The seemingly redundant method `TypeProvider#canProvide` is needed when we want to **definitively** ensure the provider
can supply an object factory. Because comparing to `null` does not work in cases where the container implementation
returns wrappers around factories for some reasons. For example, `get(Type.class)` might return an `ObjectFactory`
implementation that always returns `null`.

To prevent such situations, `canProvide` exists as a definitive indicator. If it returns `true`, then `get` will return
a **non-null** `ObjectFactory` instance, guaranteed (from the container's perspective!) to be a user-provided factory,
not a stub or temporary wrapper.

Without automatic generation of `ObjectFactory`, the example `ComplexHelloWorld` looks like this (`ManualHelloWorld`):

```java
public final class ManualHelloWorld {
    public static void main(String[] args) {
        var provider = ProviderBuilders.createScoped()
                .add(IGreeter.class, (ObjectFactory) tp -> new GlobalGreeter())
                .addScoped(IGreeter.class, (ObjectFactory) tp -> {
                    var scope = (String) tp.get(String.class).create(tp);
                    return new ScopedGreeter(scope);
                })
                .build();
        var scope1 = provider.createScoped();
        scope1.repository().put("Scope One");
        var scope2 = provider.createScoped();
        scope2.repository().put("Scope Two");
        System.out.println(provider.get(IGreeter.class).sayHello("Roman"));
        System.out.println(scope1.get(IGreeter.class).sayHello("Roman"));
        System.out.println(scope2.get(IGreeter.class).sayHello("Roman"));
    }
    // IGreeter and its impls
}
```

Note that `addScoped(String.class)`, which was in `ComplexHelloWorld`, can be omitted here because validation of
user-provided factories is not performed. Such dependencies (including instance and `Function0` variants) are considered
root since the set of dependent types cannot be determined in finite time (i.e., without running them).

### Extending TypeProvider: TypeRepository

`TypeRepository` extends `TypeProvider`, turning it into a full CRUD type storage. It looks like:

```java
public interface TypeRepository extends TypeProvider, Iterable<Type> {
    void put(Type type, ObjectFactory factory);

    void put(Type type, Function0<?> provider);

    void put(Type type, Object instance);

    void put(Object instance);

    ObjectFactory remove(Type type);

    void putAll(TypeRepository repository);

    void putAll(Map<Type, ObjectFactory> map);

    void clear();

    void forEach(BiConsumer<Type, ObjectFactory> action);
}
```

The method `put(type, factory)` adds an entry `type->factory`. If one exists for the type, it is overwritten.

Methods `put(type, provider)`, `put(type, instance)`, `put(instance)` are equivalents (depending on repository
implementation) of `put(type, p -> provider.invoke())`, `put(type, p -> instance)` and
`put(instance.getClass(), p -> instance)` respectively.

`remove(type)` removes the entry and returns the stored `ObjectFactory` or `null` if none existed.

Both `putAll(...)` variants copy entries from the given source into the repository.

`clear()` clears the repository completely.

`forEach(BiConsumer)` applies the consumer to each entry, and the `Iterable<Type>` behaves as a mutable `Set<Type>`.

All container variants are built on `TypeRepository`, which manages container contents. Note: **there is no guarantee**
that the `TypeProvider` passed to `ObjectFactory#create()` is an instance of `TypeRepository`. Generally, accessing the
repository inside a factory is considered bad design and will never be implemented.

### Universal Container: ServiceProvider

The `ServiceProvider` interface describes an abstract DI container, providing both access to implementations of
requested types and management of contents:

```java
public interface ServiceProvider {
    TypeRepository repository();

    ServiceProvider createScoped();

    <T> T get(Type type);

    <T> T get(Class<T> type);

    <T> T get(JType<T> type);
}
```

The `get(type)` methods search for an `ObjectFactory` for the requested type and:

1) if found, create an instance;

2) otherwise return `null`.

`repository()` returns a mutable `TypeRepository` instance used by this container.

`createScoped()` creates a new container that uses fallthrough lookup to the parent container if the type is not found
in the current container. Scoping can be nested infinitely.

### Other core utilities

Besides the above interfaces, the core includes:

* an abstract `ServiceProvider` implementation (`AbstractServiceProvider`) containing the `repository` field and all
  methods except `createScoped()`;
* a `TypeRepository` implementation based on `Map<Type, ObjectFactory>` (`HashTypeRepository`);
* a thread-safe lazy wrapper over `ObjectFactory` used for singleton policies (`LazyObjectFactory`);
* a universal scoped `TypeRepository` implementation connecting any pair of scoped and parent repositories (
  `ScopedTypeRepository`).

See javadoc for details.

## Schemas

### Injection schema concept

To fully separate reflective information analysis from how it is obtained, the framework introduces the concept of an
`injection schema`. It is a descriptor containing:

* the target object or injection target – a class member or the class itself;
* a set of types the injection target depends on;
* a mapping of parameter indexes to types for a callable target (method or constructor);
* a single type for a field.

A class injection schema contains a constructor schema, sets of method schemas, and sets of field schemas. The set of
types the class depends on is the union of all member schemas' types.

The base interface implemented by each `...Schema` is:

```java
public interface Schema<T> {
    T getTarget();

    Set<Type> getTypes();
}
```

For callable targets it is extended by `ExecutableSchema`:

```java
public interface ExecutableSchema<T extends Executable> extends Schema<T> {
    Type[] getMapping();
}
```

There are four implementations:

* `FieldSchema` – injection schema for a field;
* `ConstructorSchema` – injection schema for a constructor, implements `ExecutableSchema`; only one allowed per class;
* `MethodSchema` – injection schema for a method, implements `ExecutableSchema`;
* `ClassSchema` – injection schema for a whole class.

### Schema factory

To generate schemas for a given class, schema factories are used. Each factory implements `SchemaFactory`:

```java
public interface SchemaFactory {
    ClassSchema create(Class<?> clazz);
}
```

By default, an implementation that searches for injection targets among class members is provided (
`ReflectSchemaFactory`). Logic:

1) all public constructors (`Class#getConstructors()`) are inspected:
    1) if exactly one constructor, select it;
    2) if multiple, select the one annotated with a marker annotation;
    3) otherwise, class is invalid;
2) all public virtual fields annotated with a marker annotation are selected;
3) all public methods annotated with a marker annotation:
    1) if method is virtual, select it;
    2) if static and first argument type is equal to or supertype of target class, select it;
    3) otherwise invalid.

The marker annotation can be any (by default `@Inject` provided with `amaya-di`) and is passed as a constructor
parameter.

`ReflectSchemaFactory` also allows specifying a custom "type processor" implementing:

```java
public interface TypeProcessor {
    Type process(Type type, AnnotatedElement element);
}
```

The default processor (`ReflectTypeProcessor`) parses both "simple" types (`Class` instances) and generics. Wildcards
are normalized to upper bounds:

* `List<? extends String>` => `List<String>`;
* `List<? super String>` => `List<Object>`;
* `List<?>` => `List<Object>`.

Type variables are disallowed because their unambiguous normalization is impossible. For example, if mapped to the first
upper bound type (`<T>` => `Object`, `<T extends Number>` => `Number`), cyclic bounds like `class A<T extends A<T>>`
cannot be resolved.

## ObjectFactory generation

### Stub factory

In this framework, stubs are `ObjectFactory` implementations automatically generated according to the class injection
schema. Stubs are created by `StubFactory`, whose interface is:

```java
@FunctionalInterface
public interface StubFactory {
    ObjectFactory create(ClassSchema schema, CacheMode mode);

    default ObjectFactory create(ClassSchema schema) {
        return create(schema, CacheMode.NONE);
    }
}
```

All implementations **must** strictly follow the given injection schema and cache mode. If for any reason a *
*ready-to-use** `ObjectFactory` instance cannot be created, or part of dependencies cannot be injected (e.g., a target
field is private), the factory must throw an exception. Returning `null` or a dummy implementation like
`provider -> null` is forbidden.

### ObjectFactory caching

In most scenarios, the built container is used in read-only mode. Therefore, for generated `ObjectFactory`s it makes
sense to cache the factories of dependent types directly. This minimizes (or avoids) extra calls to
`TypeProvider#get(type)`, greatly reducing overhead.

Caching modes:

* `CacheMode.FULL` – no calls to `TypeProvider`, factories taken directly from internal cache;
* `CacheMode.PARTIAL` – calls to `TypeProvider` only if the needed factory is missing in the internal cache; results *
  *are not cached** (for thread-safe container access);
* `CacheMode.NONE` – always calls `TypeProvider`; no internal cache.

All internally cached `ObjectFactory`s must implement `CachedObjectFactory` interface, which allows initializing or
updating cache content:

```java
public interface CachedObjectFactory extends ObjectFactory {
    void set(Type type, ObjectFactory factory);
}
```

# Container Building

Creating and populating the container can be done either directly (by implementing and instantiating all necessary
entities) or using custom utilities.

By default, the framework provides a build mechanism based on the "builder" pattern. Its functionality includes:

* working with generic types using [jtype](https://github.com/RomanQed/jtype);
* adding custom implementations of `ObjectFactory`;
* adding transient and singleton types by their class;
* adding scoped types;
* validating the dependency graph for cycles and missing types.

## Basic Usage Scenarios

Builder variants are represented by the interfaces `ServiceProviderBuilder` and its extension `ScopedProviderBuilder`,
which
adds scoped features. Instances of these are provided via the utility class `ProviderBuilders`. It offers static methods
to create all implementations included in the framework and effectively serves as the API entry point.

The main methods are as follows (overloads see javadoc):

```java
// Creates a builder with the specified factories and checks
// If checks == BuilderChecks.NO_CHECKS, an implementation without validation mechanisms is used
public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {...}

// Creates a builder with the injection schema factory and default cache mode (ProviderBuilders#SCHEMA_FACTORY and #CACHE_MODE)
// If checks == BuilderChecks.NO_CHECKS, an implementation without validation mechanisms is used
public static ServiceProviderBuilder create(int checks) {...}

// Creates a builder with specified factories, ProviderBuilders#CACHE_MODE, and no checks
public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory) {...}

// Creates a builder with ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE, and no checks.
public static ServiceProviderBuilder create() {...}

// Creates a builder with ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE, and BuilderChecks.VALIDATE_ALL.
public static ServiceProviderBuilder createChecked() {...}

// Creates a scoped builder with specified factories and checks
// If checks == BuilderChecks.NO_CHECKS, an implementation without validation mechanisms is used
public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {...}

// Creates a scoped builder with the injection schema factory and default cache mode (ProviderBuilders#SCHEMA_FACTORY and #CACHE_MODE)
// If checks == BuilderChecks.NO_CHECKS, an implementation without validation mechanisms is used
public static ScopedProviderBuilder createScoped(int checks) {...}

// Creates a scoped builder with specified factories, ProviderBuilders#CACHE_MODE, and no checks
public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory) {...}

// Creates a scoped builder with ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE, and no checks.
public static ScopedProviderBuilder createScoped() {...}

// Creates a scoped builder with ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE, and BuilderChecks.VALIDATE_ALL.
public static ScopedProviderBuilder createCheckedScoped() {...}
```

An example demonstrating all builder capabilities is
available [here](examples/src/main/java/com/github/romanqed/di/examples/AllMethods.java).

## Generic Types

To register a generic type in the container, you can use either jtype or third-party libraries capable
of providing implementations of the `java.lang.reflect.Type` interface for parameterized types. Below is an example
showing both automatic capture of the type and manual creation:

```java
public final class GenericTypes {
    public static void main(String[] args) {
        var provider = ProviderBuilders.create(new ReflectStubFactory(), BuilderChecks.VALIDATE_MISSING_TYPES)
                .addInstance(new JType<>(){}, List.of("str1", "str2", "str3")) // auto-catch
                .addInstance(Types.of(List.class, Integer.class), List.of(1, 2, 3)) // manual
                // for "new JType<>(){}" type will be Map<String, String>
                .addInstance(new JType<Map<String, Object>>(){}, Map.of("s1", "k1", "s2", "k2")) // and for map
                .addInstance(Types.of(Map.class, Integer.class, Object.class), Map.of(1, 1, 2, 2))
                .addTransient(A1.class)
                .addTransient(A2.class)
                // B<A1> and B<A2>
                .add(Types.of(B.class, A1.class), tp -> new B<>((A1) tp.get(A1.class).create(tp)))
                .add(new JType<B<A2>>(){}, tp -> new B<>((A2) tp.get(A2.class).create(tp)))
                // And, finally, C
                .addTransient(C.class)
                .build();
        var c = provider.get(C.class);
        System.out.println(c.b1.value.ints);
        System.out.println(c.b1.value.strings);
        System.out.println(c.b2.value.intMap);
        System.out.println(c.b2.value.strMap);
    }

    public static final class A1 {
        @Inject
        public List<String> strings;
        @Inject
        public List<Integer> ints;
    }

    public static final class A2 {
        @Inject
        public Map<String, Object> strMap;
        @Inject
        public Map<Integer, Object> intMap;
    }

    public static final class B<T> {
        T value;

        B(T value) {
            this.value = value;
        }
    }

    public static final class C {
        B<A1> b1;
        B<A2> b2;

        public C(B<A1> b1, B<A2> b2) {
            this.b1 = b1;
            this.b2 = b2;
        }
    }
}
```

The output will be:

```
[1, 2, 3]
[str1, str2, str3]
{1=1, 2=2}
{s2=k2, s1=k1}
```

## Dependency Graph Validation

The validation mechanism is implemented using the following conventions:

* all user-provided implementations of `ObjectFactory` are unconditionally considered valid and treated
  as "root" types with no dependencies;
* all user `ServiceWrapper`s return valid `ObjectFactory` instances and do not cause errors;
* all types for which an injection schema can be constructed are considered "weak" and will be validated for:
    * requests for missing dependencies;
    * creation of cycles in the dependency graph.

For scoped containers, additionally:

* registering a promised type **guarantees** that when creating a scoped container, BEFORE starting work with it, the
  type will be provided in the repository;
* scope types can refer to other scope types and to base types;
* base types can ONLY refer to other base types.

Based on this and viewing the container as a dependency graph, five classes of possible errors can be distinguished.

1. A type is missing in the base container, on which another base type depends.

Example:

```
class A -> class String
class String -> missing
```

2. A type is missing in the scoped container, on which another scoped type depends.

Example:

```
class ScopedA -> class String
class String -> missing in both scoped and base containers
```

3. A cyclic dependency occurs between base types in the base container.

Example:

```
class A -> class B
class B -> class C
class C -> class A
```

4. A cyclic dependency occurs between scoped types in the scoped container.

5. A cyclic dependency occurs between containers due to overriding a type by the scoped container.

Example:

```
class A -> class B
class B -> interface IC
interface IC: C
interface IC: ScopedC -> A
```

The built-in validation mechanism supports handling all these five types of errors.
See an example [here](examples/src/main/java/com/github/romanqed/di/examples/AllChecks.java).

# Variants of ObjectFactory Generators

The framework provides two implementations of `StubFactory`, supplied in the modules `amaya-di-asm` and
`amaya-di-reflect`.

## ASM

The ASM implementation creates `ObjectFactory` instances by generating bytecode on the fly. The generated class
is named by combining the target injection class name and an alias of the caching mode used. Accordingly,
no regeneration occurs on repeated requests; the already loaded class is used by name.

The ASM implementation is characterized by relatively long preparation time and maximal possible execution speed,
practically equal to normal code speed.

It also implements bytecode caching, allowing reuse of once-generated classes. This is extremely useful
when assembling a final (and importantly, unchanging) jar file that will be deployed to production:

```java
public final class CachedAsmHelloWorld {
    public static void main(String[] args) {
        var baseLoader = new DefineClassLoader();
        var cachedLoader = new CachedClassLoader(baseLoader, new LocalClassCache());
        var stubFactory = new AsmStubFactory(cachedLoader);
        var provider = ProviderBuilders.createScoped(stubFactory)
                .addSingleton(IGreeter.class, GlobalGreeter.class)
                .addScoped(String.class)
                .addScopedSingleton(IGreeter.class, ScopedGreeter.class)
                .build();
        var scope1 = provider.createScoped();
        scope1.repository().put("Scope One");
        var scope2 = provider.createScoped();
        scope2.repository().put("Scope Two");
        System.out.println(provider.get(IGreeter.class).sayHello("Roman"));
        System.out.println(scope1.get(IGreeter.class).sayHello("Roman"));
        System.out.println(scope2.get(IGreeter.class).sayHello("Roman"));
    }

    public static final class LocalClassCache implements ClassCache {
        private static final Path CACHE_ROOT = Path.of("cache").toAbsolutePath();

        static {
            if (!Files.isDirectory(CACHE_ROOT)) {
                Exceptions.suppress(() -> Files.createDirectories(CACHE_ROOT));
            }
        }

        @Override
        public byte[] get(String name) {
            var path = CACHE_ROOT.resolve(name + ".class");
            if (!Files.isRegularFile(path)) {
                return null;
            }
            try (var reader = Files.newInputStream(path)) {
                return reader.readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void set(String name, byte[] buffer) {
            var path = CACHE_ROOT.resolve(name + ".class");
            try (var writer = Files.newOutputStream(
                    path,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING
            )) {
                writer.write(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public interface IGreeter {
        String sayHello(String name);
    }

    public static final class GlobalGreeter implements IGreeter {

        @Override
        public String sayHello(String name) {
            return "Hello, " + name + "!";
        }
    }

    public static final class ScopedGreeter implements IGreeter {
        private final String scope;

        public ScopedGreeter(String scope) {
            this.scope = scope;
        }

        @Override
        public String sayHello(String name) {
            return "Hello from scope '" + scope + "', " + name + "!";
        }
    }
}
```

This code will create a `cache` directory on the first run, where the generated bytecode is stored.
On subsequent runs, it will load the bytecode instead of generating it again.
If the cache is deleted, generation will occur anew.

## Reflect

The Reflect implementation creates `ObjectFactory` instances using reflective method calls and field access.
Requested class members are marked as accessible without modifier checks (`setAccessible(true)`)
and wrapped in a prepared factory implementation.

The Reflect implementation is characterized by relatively fast preparation time and slow runtime speed,
including significant overhead from reflection mechanisms.

## Implementation Choice

The choice between asm and reflect implementations should be made based on the container usage scenario. If your
application container:

* is rebuilt frequently;
* is used once at startup;
* is not used in "hot" code (or high overhead is acceptable);

then the reflect implementation should be used.

If the container:

* is built once;
* is used continuously in hot code;
* must provide dependencies with minimal latency;

then the asm implementation is preferred.

## Benchmarks

Benchmarks were performed on Amazon Corretto 11 (`openjdk version "11.0.27" 2025-04-15 LTS`), on a PC (not a server)
with Intel i5-9400F, 40 GB RAM, and Windows 10 22H2. The jmh framework version 1.37 was used.

The dependency set includes:

* Service1;
* Service2;
* Service3, dependent on Service1 (constructor);
* App, dependent on all three (Service1 — constructor, Service2 — field, Service3 — method).

The container is fully prepared before benchmarking, with no interactions during measurement.

To get a baseline time to compare results, simple code was written to "manually" create App and inject dependencies:

```java
var app = new App(new Service1());
app.s2 = new Service2();
app.setS3(new Service3(new Service1()));
```

Benchmarks to measure repository overhead (type lookup) were conducted in two versions:

* measuring "pure" manual App creation time;
* wrapping App creation in an `ObjectFactory` inside the container, measuring lookup and `create()` call time.

Times were also measured separately for each CacheMode, and again for CacheMode.FULL (default mode).

The results are:

```
Benchmark                                    Mode  Cnt   Score   Error  Units
AsmStubBenchmark.benchFullCacheInjection     avgt   25  13,604 ± 0,020  ns/op
AsmStubBenchmark.benchManualInjection        avgt   25  13,475 ± 0,119  ns/op
AsmStubBenchmark.benchNoCacheInjection       avgt   25  23,962 ± 0,309  ns/op
AsmStubBenchmark.benchPartialCacheInjection  avgt   25  16,424 ± 0,036  ns/op
```

```
Benchmark                                        Mode  Cnt   Score   Error  Units
ReflectStubBenchmark.benchFullCacheInjection     avgt   25  58,055 ± 0,513  ns/op
ReflectStubBenchmark.benchManualInjection        avgt   25  13,402 ± 0,096  ns/op
ReflectStubBenchmark.benchNoCacheInjection       avgt   25  81,497 ± 1,746  ns/op
ReflectStubBenchmark.benchPartialCacheInjection  avgt   25  66,429 ± 1,045  ns/op
```

```
Benchmark                                                   Mode  Cnt    Score   Error  Units
AsmStubBenchmark.benchFullCacheInjection                    avgt   25   15,597 ± 0,768  ns/op
AsmStubBenchmark.benchManualInjection                       avgt   25   11,265 ± 0,098  ns/op
AsmStubBenchmark.benchNoCacheInjection                      avgt   25   26,064 ± 0,760  ns/op
AsmStubBenchmark.benchPartialCacheInjection                 avgt   25   15,225 ± 0,416  ns/op
AsmStubBenchmark.benchScopeCreation                         avgt   25   39,367 ± 0,853  ns/op
AsmStubBenchmark.benchScopeCreationAndInjection             avgt   25   59,320 ± 0,859  ns/op
AsmStubBenchmark.benchWrappedScopeCreation                  avgt   25   39,993 ± 0,963  ns/op
AsmStubBenchmark.benchWrappedScopeCreationAndInjection      avgt   25   75,249 ± 1,509  ns/op
ReflectStubBenchmark.benchFullCacheInjection                avgt   25   61,459 ± 0,963  ns/op
ReflectStubBenchmark.benchManualInjection                   avgt   25   11,561 ± 0,461  ns/op
ReflectStubBenchmark.benchNoCacheInjection                  avgt   25   83,394 ± 1,433  ns/op
ReflectStubBenchmark.benchPartialCacheInjection             avgt   25   61,693 ± 0,204  ns/op
ReflectStubBenchmark.benchScopeCreation                     avgt   25   41,413 ± 0,844  ns/op
ReflectStubBenchmark.benchScopeCreationAndInjection         avgt   25  129,332 ± 1,293  ns/op
ReflectStubBenchmark.benchWrappedScopeCreation              avgt   25   40,684 ± 0,741  ns/op
ReflectStubBenchmark.benchWrappedScopeCreationAndInjection  avgt   25  129,076 ± 1,930  ns/op
```

Benchmarks can be run as follows:

```
>./gradlew asm:jmh
>./gradlew reflect:jmh
>./gradlew :jmh
```

# Differences from 2.x

When migrating from 2.x to 3.x, the framework’s core API lost full backward compatibility. Builder APIs remain mostly
the same except for new methods. Users migrating from amaya-di 2.x to 3.x should note these key changes:

* `ObjectFactory` is now used as the object factory instead of `Function0`;
* the `ServiceRepository` interface was renamed to `TypeRepository`;
* the `TypeProvider` interface, previously a local mechanism for the stub module, was completely removed;
* the `ManualProviderBuilder` interface was completely removed;
* `StubFactory` API changed from `create(schema, typeProvider)` to `create(schema, cacheMode)`;
* `StubFactory` no longer fills the factory cache on creation — this must be done manually;
* the `jgraph` module is no longer used and removed from dependencies;
* abstract class `AbstractProviderBuilder` replaced by `AbstractServiceProviderBuilder`, with completely changed
  protected API;
* `ServiceWrapper` no longer extends `Function1`; its functional method signature is now
  `ObjectFactory wrap(ObjectFactory factory)`;
* `LazyProvider` class was completely removed;
* a new exception `CyclesFoundException` was introduced, thrown when multiple cycles are detected;
* module separation changed completely:
    * `ServiceProvider` and related types moved to separate module `amaya-di-core`;
    * `ClassSchema` and related types moved to `amaya-di-schema`;
    * `StubFactory` and related types moved to `amaya-di-stub`;
    * `amaya-di` now includes these three modules, with `amaya-di-core` as a transitive dependency;
* a new scope mechanism was introduced.

# Contributing

I will be happy if you want to propose your fixes, improvements, and feature extensions for amaya-di. A detailed
contributing guide is available [here](CONTRIBUTING.md).

# Built With

* [Gradle](https://gradle.org) - Dependency management
* [ASM](https://asm.ow2.io) - `ObjectFactory` generation
* [jeflect-loader](https://github.com/RomanQed/jeflect) - Runtime bytecode loading
* [jfunc](https://github.com/RomanQed/jfunc) - Functional interfaces, utilities
* [jtype](https://github.com/RomanQed/jtype) - Utilities for working with generics

# Authors

* [RomanQed](https://github.com/RomanQed) - *Main work*

Also check the list of [contributors](https://github.com/AmayaFramework/amaya-di/contributors) who contributed to this
project.

# License

This project is licensed under the Apache License Version 2.0 — see the [LICENSE](LICENSE) file for details.
