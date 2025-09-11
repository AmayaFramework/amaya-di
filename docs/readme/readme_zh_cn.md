<p align="center">
  <img src="../img/logo.png" alt="logo" style="width: 200px; height: auto;">
</p>

# amaya-di – 现代、轻量且高性能的依赖注入框架（DI）

|      Module      |                                                                                                                              Version                                                                                                                              |
|:----------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|     amaya-di     |                 [![amaya-di](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di?strategy=releaseProperty&style=for-the-badge&label=amaya-di&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di/)                 |
|  amaya-di-core   |       [![amaya-di-core](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-core?strategy=releaseProperty&style=for-the-badge&label=amaya-di-core&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-core/)       |
| amaya-di-schema  |   [![amaya-di-schema](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-schema?strategy=releaseProperty&style=for-the-badge&label=amaya-di-schema&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-schema/)   |
|  amaya-di-stub   |       [![amaya-di-stub](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-stub?strategy=releaseProperty&style=for-the-badge&label=amaya-di-stub&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-stub/)       |
|   amaya-di-asm   |         [![amaya-di-asm](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-asm?strategy=releaseProperty&style=for-the-badge&label=amaya-di-asm&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-asm/)         |
| amaya-di-reflect | [![amaya-di-reflect](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-reflect?strategy=releaseProperty&style=for-the-badge&label=amaya-di-reflect&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-reflect/) |

- [English](../../README.md)
- [Русский](readme_ru.md)
- 简体中文 (AI translated, GPT-4o)
- [正體中文](readme_zh_tw.md)
- [日本語](readme_jp.md)
- [Deutsch](readme_de.md)
- [Français](readme_fr.md)

Amaya DI 是对依赖注入框架应有形态的现代诠释。它旨在为开发者提供一个高性能、灵活且极简的 IoC 容器，
摒弃过时的 XML 配置、繁多的注解以及基于反射的隐式魔法。

该框架开箱即用支持以下特性：

* 支持泛型类型（例如，`List<String>` 和 `List<Integer>` 会被识别为两种不同类型）；
* 创建无命名的作用域（scope）；
* 自动运行时生成对象工厂：
    * 支持每次请求创建新对象（transient）和同一请求共享对象（singleton）策略；
    * 支持作用域内的 transient 和 singleton 策略；
* 构建和验证依赖关系图：
    * 查找缺失的类型；
    * 使用 Tarjan 的迭代算法检测循环依赖；
* 通过便捷的 fluent API 完全声明式构建 IoC 容器；
* 简单集成现有的任意库、框架或标准。

生成工厂的性能表现为：

* 使用 asm 实现时，性能接近手写实现（13,604 ns 对比 13,475 ns）；
* 使用反射实现时，包含 JNI 开销但依然较快（58,055 ns 对比 13,402 ns）。

更多详细信息请参见 [基准测试](#基准测试) 部分。

# 介绍

安装要求：

* JVM 11 及以上版本
* Maven 或 Gradle

低于 JVM 11 的版本不受支持。

支持除 Java 以外的语言，因为框架没有编译时插件且不分析 Java AST。

## 安装

使用时需安装两个模块：基础模块 (`io.github.amayaframework:amaya-di`) 和 stub 工厂实现模块（`:amaya-di-asm` 或
`:amaya-di-reflect`）。  
关于如何选择实现，请参阅 [选择实现](#选择实现)。

### Gradle

```Groovy
dependencies {
    implementation group: 'io.github.amayaframework', name: 'amaya-di', version: '3.1.0'
    // ASM stub 实现
    implementation group: 'io.github.amayaframework', name: 'amaya-di-asm', version: '2.1.0'
    // 或者反射 stub 实现
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
    <!-- ASM stub 实现 -->
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-di-asm</artifactId>
        <version>2.1.0</version>
    </dependency>
    <!-- 或者反射 stub 实现 -->
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-di-reflect</artifactId>
        <version>2.1.0</version>
    </dependency>
</dependencies>
```

## Hello, world!

容器构建通过构建器完成（`ServiceProviderBuilder` 或 `ScopedProviderBuilder`）。  
构建过程可分为三个阶段：

* 获取并配置构建器对象；
* 声明所需类型；
* 调用 `build()` 方法。

先看基础用例。

使用模块时，先声明 `module-info.java` 中的依赖：

```java
module com.github.romanqed.di.examples {
    requires amayaframework.di; // 主模块
    exports com.github.romanqed.di.examples;
}
```

构建最简单的容器：

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

运行后控制台输出 `Hello, world!`。

更复杂的例子：假设有一个抽象服务接口 `IGreeter`：

```java
public interface IGreeter {
    String sayHello(String name);
}
```

有两个实现，一个用于全局上下文，一个用于作用域内：

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

在 `module-info.java` 中添加反射工厂实现依赖：

```java
module com.github.romanqed.di.examples {
    requires amayaframework.di; // 主模块
    requires amayaframework.di.reflect; // 使用反射实现
    exports com.github.romanqed.di.examples;
}
```

构建容器：

```java
var provider = ProviderBuilders.createScoped(new ReflectStubFactory())
        .addSingleton(IGreeter.class, GlobalGreeter.class)
        .addScoped(String.class)
        .addScopedSingleton(IGreeter.class, ScopedGreeter.class)
        .build();
```

创建两个作用域：

```java
var scope1 = provider.createScoped();
scope1.repository().put("Scope One");
var scope2 = provider.createScoped();
scope2.repository().put("Scope Two");
```

打印输出：

```java
System.out.println(provider.get(IGreeter.class).sayHello("Roman"));
System.out.println(scope1.get(IGreeter.class).sayHello("Roman"));
System.out.println(scope2.get(IGreeter.class).sayHello("Roman"));
```

控制台结果：

```
Hello, Roman!
Hello from scope 'Scope One', Roman!
Hello from scope 'Scope Two', Roman!
```

完整代码：

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

所有示例代码均可在[此处](../../examples)找到。

# 基础概念

## 核心

### ObjectFactory 和 TypeProvider

容器底层机制是接口 `ObjectFactory` 和 `TypeProvider`，定义如下：

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

`ObjectFactory` 用于创建实例，所依赖的类型由 `TypeProvider` 提供。此设计有两个优点：

1) 对类型提供者的依赖不硬绑定，允许在传递给下游工厂前进行包装和修改，实现灵活的场景；
2) 直接获取工厂而非实例，可避免重复查找和缓存工厂，提升性能且不损失其他优势。

方法 `TypeProvider#canProvide` 用于确定提供者是否能明确提供某类型的工厂。  
简单的 null 比较不可靠，因为某些实现可能会返回始终返回 null 的工厂包装。  
`canProvide` 返回 true 时保证 `get` 返回非 null 的用户工厂实例，而非占位或临时包装。

没有自动生成 `ObjectFactory` 时，`ComplexHelloWorld` 示例对应的手动实现如下 (`ManualHelloWorld`)：

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
    // IGreeter 及其实现
}
```

注意，这里可以去掉 `addScoped(String.class)`，因为不会对用户提供的工厂做校验。  
此类依赖（包括 instance 和 `Function0` 版本）视为根节点，因为它们的依赖无法在有限时间内确定（即必须执行）。

### 扩展 TypeProvider：TypeRepository

`TypeRepository` 继承自 `TypeProvider`，是一个支持 CRUD 的完整类型仓库：

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

`put(type, factory)` 将 `type->factory` 记录写入仓库，已有时覆盖。  
`put(type, provider)`、`put(type, instance)` 和 `put(instance)` 等同于将 `provider.invoke()`、`instance`
等封装为工厂再放入。  
`remove(type)` 删除类型记录，返回对应工厂或 null。  
`putAll(...)` 将其它仓库或映射中的记录复制进来。  
`clear()` 清空仓库。  
`forEach(BiConsumer)` 对所有记录执行动作。  
`Iterable<Type>` 行为等同于可变的 `Set<Type>`。

各种容器均基于 `TypeRepository` 构建，通过它管理内容。  
注意，传入 `ObjectFactory#create()` 的 `TypeProvider` 不一定是 `TypeRepository`。在工厂内部访问仓库视为设计不当，框架不支持此行为。

### 通用容器：ServiceProvider

`ServiceProvider` 表示抽象的 DI 容器，既能获取请求类型的实例，也能管理内容：

```java
public interface ServiceProvider {
    TypeRepository repository();

    ServiceProvider createScoped();

    <T> T get(Type type);

    <T> T get(Class<T> type);

    <T> T get(JType<T> type);
}
```

`get(type)` 查找类型对应的 `ObjectFactory`：

1) 存在时，创建实例；
2) 不存在时，返回 null。

`repository()` 返回容器的 `TypeRepository` 实例。  
`createScoped()` 创建新容器，查找时若未找到类型会向上级容器继续查找，支持无限级作用域链。

### 其他核心工具

除了上述接口，核心还包含：

* `ServiceProvider` 的抽象实现 `AbstractServiceProvider`，含 `repository` 字段和除 `createScoped()` 外所有方法；
* 基于 `Map<Type, ObjectFactory>` 的 `TypeRepository` 实现 `HashTypeRepository`；
* 线程安全的惰性加载包装 `ObjectFactory` 用于 singleton 策略 (`LazyObjectFactory`)；
* 通用作用域 `TypeRepository` 实现，连接 scoped 和 parent 仓库 (`ScopedTypeRepository`)。

详见 javadoc。

## 注入方案（Schemas）

### 注入方案概念

为实现注入信息分析与获取方式的彻底解耦，框架引入“注入方案”（Injection Schema）概念。  
注入方案是描述符，包含以下信息：

* 注入目标：类成员或类本身；
* 目标依赖的类型集合；
* 调用目标（构造器或方法）参数类型映射（参数索引到类型）；
* 字段的单一类型。

类的注入方案还包含构造器方案、方法方案集合和字段方案集合，依赖类型为所有成员方案类型集合的并集。

基本接口：

```java
public interface Schema<T> {
    T getTarget();

    Set<Type> getTypes();
}
```

用于调用目标时扩展为：

```java
public interface ExecutableSchema<T extends Executable> extends Schema<T> {
    Type[] getMapping();
}
```

共有四个实现：

* `FieldSchema` – 字段注入方案；
* `ConstructorSchema` – 构造器注入方案，继承 `ExecutableSchema`，每类仅允许一个；
* `MethodSchema` – 方法注入方案，继承 `ExecutableSchema`；
* `ClassSchema` – 类注入方案。

### 方案工厂

方案由方案工厂生成，接口为：

```java
public interface SchemaFactory {
    ClassSchema create(Class<?> clazz);
}
```

默认实现在类成员中查找注入目标（`ReflectSchemaFactory`），逻辑为：

1) 查找所有公共构造器 (`Class#getConstructors()`)：
    1) 仅一个时选它；
    2) 多个时选被标记注解的；
    3) 否则类无效；
2) 查找所有带标记注解的公共非静态字段；
3) 查找所有带标记注解的公共方法：
    1) 虚方法选中；
    2) 静态方法且首参数类型为或超类为目标类也选中；
    3) 否则无效。

标记注解可自定义，默认为随模块 `amaya-di` 提供的 `@Inject`，可通过构造参数指定。

此外，`ReflectSchemaFactory` 支持自定义类型处理器，接口为：

```java
public interface TypeProcessor {
    Type process(Type type, AnnotatedElement element);
}
```

默认处理器 `ReflectTypeProcessor` 支持普通类型（`Class` 实例）和泛型。  
处理时将所有通配符类型统一为上界：

* `List<? extends String>` => `List<String>`；
* `List<? super String>` => `List<Object>`；
* `List<?>` => `List<Object>`。

完全禁止类型变量（Type variable），因无法唯一确定其转换方式。  
例如循环定义 `class A<T extends A<T>>` 无法断开。

## ObjectFactory 生成

### Stub 工厂

Stub 是框架中根据类注入方案自动生成的 `ObjectFactory` 实现。  
由接口 `StubFactory` 定义：

```java
@FunctionalInterface
public interface StubFactory {
    ObjectFactory create(ClassSchema schema, CacheMode mode);

    default ObjectFactory create(ClassSchema schema) {
        return create(schema, CacheMode.NONE);
    }
}
```

所有实现**必须**严格遵守方案和缓存模式。  
若无法创建可用的 `ObjectFactory` 实例，或无法注入部分依赖（如目标字段是私有），应抛出异常。  
禁止返回 `null` 或简单实现如 `provider -> null`。

### ObjectFactory 缓存

大多数场景下容器为只读模式，故对生成的 `ObjectFactory` 缓存其依赖的工厂是合理的，可减少调用 `TypeProvider#get(type)`，降低开销。

缓存模式：

* `CacheMode.FULL` – 不调用 `TypeProvider`，全部工厂从内部缓存取；
* `CacheMode.PARTIAL` – 若缓存缺失则调用 `TypeProvider`，结果不保存（线程安全访问）；
* `CacheMode.NONE` – 总调用 `TypeProvider`，无缓存。

带缓存的 `ObjectFactory` 实现需实现接口 `CachedObjectFactory`，支持初始化和更新缓存内容：

```java
public interface CachedObjectFactory extends ObjectFactory {
    void set(Type type, ObjectFactory factory);
}
```

# 容器构建

容器的创建和填充可以通过直接实现和实例化所有必需的实体完成，也可以通过用户自定义的工具来完成。

默认情况下，框架提供了基于“builder”模式的构建机制。其功能包括：

* 使用 [jtype](https://github.com/RomanQed/jtype) 处理泛型类型；
* 添加用户自定义的 `ObjectFactory` 实现；
* 根据类添加 transient 和 singleton 类型；
* 添加 scoped 类型；
* 验证依赖图，检查循环依赖和缺失类型。

## 基本使用场景

Builder 的变体由接口 `ServiceProviderBuilder` 及其扩展接口 `ScopedProviderBuilder` 表示，后者增加了 scoped 功能。通过工具类 `ProviderBuilders` 可以获取它们的实例。该类提供静态方法来创建框架中包含的所有实现，本质上是 API 的入口点。

主要方法如下（具体重载请参考 javadoc）：

```java
// 创建带指定工厂和校验的 builder
// 当 checks == BuilderChecks.NO_CHECKS 时，使用无验证机制的实现
public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {...}

// 创建带注入方案工厂和默认缓存模式的 builder（ProviderBuilders#SCHEMA_FACTORY 和 #CACHE_MODE）
// 当 checks == BuilderChecks.NO_CHECKS 时，使用无验证机制的实现
public static ServiceProviderBuilder create(int checks) {...}

// 创建带指定工厂、ProviderBuilders#CACHE_MODE 且无校验的 builder
public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory) {}

// 创建带 ProviderBuilders#SCHEMA_FACTORY、#CACHE_MODE 且无校验的 builder
public static ServiceProviderBuilder create() {...}

// 创建带 ProviderBuilders#SCHEMA_FACTORY、#CACHE_MODE 及 BuilderChecks.VALIDATE_ALL 的 builder
public static ServiceProviderBuilder createChecked() {...}

// 创建带指定工厂和校验的 scoped builder
// 当 checks == BuilderChecks.NO_CHECKS 时，使用无验证机制的实现
public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {...}

// 创建带注入方案工厂和默认缓存模式的 scoped builder（ProviderBuilders#SCHEMA_FACTORY 和 #CACHE_MODE）
// 当 checks == BuilderChecks.NO_CHECKS 时，使用无验证机制的实现
public static ScopedProviderBuilder createScoped(int checks) {...}

// 创建带指定工厂、ProviderBuilders#CACHE_MODE 且无校验的 scoped builder
public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory) {...}

// 创建带 ProviderBuilders#SCHEMA_FACTORY、#CACHE_MODE 且无校验的 scoped builder
public static ScopedProviderBuilder createScoped() {...}

// 创建带 ProviderBuilders#SCHEMA_FACTORY、#CACHE_MODE 及 BuilderChecks.VALIDATE_ALL 的 scoped builder
public static ScopedProviderBuilder createCheckedScoped() {...}
```

示例（展示所有 builder 功能）见 [这里](../../examples/src/main/java/com/github/romanqed/di/examples/AllMethods.java)。

## 泛型类型

注册泛型类型到容器时，可以使用 jtype 或支持提供 `java.lang.reflect.Type` 接口实现的第三方库。以下示例展示了类型的自动捕获和手动创建：

```java
public final class GenericTypes {
    public static void main(String[] args) {
        var provider = ProviderBuilders.create(new ReflectStubFactory(), BuilderChecks.VALIDATE_MISSING_TYPES)
                .addInstance(new JType<>(){}, List.of("str1", "str2", "str3")) // 自动捕获
                .addInstance(Types.of(List.class, Integer.class), List.of(1, 2, 3)) // 手动指定
                // 对于 "new JType<>(){}" 类型是 Map<String, String>
                .addInstance(new JType<Map<String, Object>>(){}, Map.of("s1", "k1", "s2", "k2")) // and for map
                .addInstance(Types.of(Map.class, Integer.class, Object.class), Map.of(1, 1, 2, 2))
                .addTransient(A1.class)
                .addTransient(A2.class)
                // B<A1> 和 B<A2>
                .add(Types.of(B.class, A1.class), tp -> new B<>((A1) tp.get(A1.class).create(tp)))
                .add(new JType<B<A2>>(){}, tp -> new B<>((A2) tp.get(A2.class).create(tp)))
                // 最后是 C
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

输出结果为：

```
[1, 2, 3]
[str1, str2, str3]
{1=1, 2=2}
{s2=k2, s1=k1}
```

## 依赖图验证

实现验证机制遵循以下约定：

* 所有用户提供的 `ObjectFactory` 实现被无条件视为正确，视作无依赖的“根”类型；
* 所有用户自定义的 `ServiceWrapper` 返回正确的 `ObjectFactory`，且不产生错误；
* 所有能够构建注入方案的类型被视为“弱”类型，将进行以下检查：
    * 检查缺失依赖；
    * 检查依赖图中的循环。

对于 scoped 容器，额外规则包括：

* 注册的 promised 类型**保证**在 scoped 容器创建且使用前，类型已被提供到仓库；
* scope 类型可以引用其他 scope 类型和基础类型；
* 基础类型只能引用其他基础类型。

基于此，依赖容器作为依赖图可区分五类可能错误：

1. 基础容器中缺少基础类型依赖。

示例：
```
class A -> class String
class String -> 缺失
```

2. scoped 容器中缺少 scoped 类型依赖。

示例：
```
class ScopedA -> class String
class String -> scoped 和基础容器中均缺失
```

3. 基础容器内基础类型循环依赖。

示例：
```
class A -> class B
class B -> class C
class C -> class A
```

4. scoped 容器内 scoped 类型循环依赖。

5. scoped 容器覆盖类型导致容器间循环依赖。

示例：
```
class A -> class B
class B -> interface IC
interface IC: C
interface IC: ScopedC -> A
```

内置验证机制支持处理以上五类错误。示例见 [这里](../../examples/src/main/java/com/github/romanqed/di/examples/AllChecks.java)。

# ObjectFactory 生成器选项

框架提供两个 `StubFactory` 实现，分别包含于 `amaya-di-asm` 和 `amaya-di-reflect` 模块。

## ASM

ASM 实现通过即时字节码生成来创建 `ObjectFactory`。生成的类名由目标类名与缓存模式别名组成。重复请求时不再生成，而是复用已加载类。

ASM 实现的特点是准备时间较长，但运行速度极快，接近普通代码的速度。

此外支持字节码缓存，可复用生成的类。此功能对已打包成最终且未来不变的 jar 文件发布到生产环境非常有用：

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
            try (var reader = Files.newInputStream(path)){
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

该代码首次运行时会创建 cache 目录，用于存放生成的字节码。后续运行会直接加载缓存。删除缓存后重新生成。

## Reflect

Reflect 实现通过反射调用方法和访问字段来创建 `ObjectFactory`。被请求的成员通过 `setAccessible(true)` 标记为可访问，并封装成工厂实现。

Reflect 实现准备速度较快，但运行时较慢，反射访问带来较大开销。

## 选择实现

根据容器使用场景决定使用 asm 还是 reflect 实现：

如果应用场景是：

* 容器频繁重建；
* 容器只在启动时使用一次；
* 不在“热点”代码路径，允许较高开销；

建议使用 reflect 实现。

如果容器是：

* 只构建一次；
* 持续在热点代码中使用；
* 需要尽可能快地提供依赖；

建议使用 asm 实现。

## 基准测试

基准测试在 Amazon Corretto 11（`openjdk version "11.0.27" 2025-04-15 LTS`）环境下进行，测试设备为 Intel i5-9400F、40GB RAM、Windows 10 22H2。采用 jmh 1.37 框架。

测试依赖项包括：

* Service1；
* Service2；
* Service3（依赖 Service1 构造器）；
* App（依赖所有三个：Service1 构造器，Service2 字段，Service3 方法注入）。

容器完全准备好后开始测试，无交互。

作为基准参考，写了手动创建 App 并注入依赖的代码：

```java
var app = new App(new Service1());
app.s2 = new Service2();
app.setS3(new Service3(new Service1()));
```

另外，测试了两种版本仓库开销：

* 手动创建 App 的纯执行时间；
* 将 App 封装进 `ObjectFactory`，测量容器内查找及调用 `create()` 的时间。

分别对不同 CacheMode 测试，也针对默认 CacheMode.FULL 重复测试。

结果如下：

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

基准测试通过以下命令执行：

```
>./gradlew asm:jmh
>./gradlew reflect:jmh
>./gradlew :jmh
```

# 与 2.x 版本的差异

从 2.x 升级到 3.x 后，框架的基础 API 完全不兼容。Builder API 除新增方法外基本保持不变。对于从 amaya-di 2.x 迁移到 3.x 的用户，请注意以下关键变更：

* 对象工厂由 `Function0` 改为 `ObjectFactory`；
* 接口 `ServiceRepository` 更名为 `TypeRepository`；
* 作为 stub 模块本地机制的接口 `TypeProvider` 被完全移除；
* 接口 `ManualProviderBuilder` 被完全移除；
* `StubFactory` 的 API 由 `create(schema, typeProvider)` 改为 `create(schema, cacheMode)`；
* 现在 `StubFactory` 不再在创建时填充工厂缓存，需要用户手动完成；
* 移除了 `jgraph` 模块依赖；
* 抽象类 `AbstractProviderBuilder` 替换为 `AbstractServiceProviderBuilder`，protected API 完全变更；
* 接口 `ServiceWrapper` 不再继承 `Function1`，其函数签名变为 `ObjectFactory wrap(ObjectFactory factory)`；
* `LazyProvider` 类被完全删除；
* 新增异常 `CyclesFoundException`，当检测到多个循环依赖时会抛出；
* 模块划分发生大变化：
    * `ServiceProvider` 及相关类型移至独立模块 `amaya-di-core`；
    * `ClassSchema` 及相关类型移至独立模块 `amaya-di-schema`；
    * `StubFactory` 及相关类型移至独立模块 `amaya-di-stub`；
    * `amaya-di` 模块现在包含上述三个模块，`amaya-di-core` 被设为传递依赖；
* 引入了新的 scope 机制。

# 贡献指南

欢迎提出修正、改进和功能扩展。详细的贡献指南请见 [这里](../contributing/contributing_zh_cn.md)。

# 使用技术

* [Gradle](https://gradle.org) - 依赖管理
* [ASM](https://asm.ow2.io) - 生成 `ObjectFactory`
* [jeflect-loader](https://github.com/RomanQed/jeflect) - 运行时加载字节码
* [jfunc](https://github.com/RomanQed/jfunc) - 函数接口及工具
* [jtype](https://github.com/RomanQed/jtype) - 泛型处理工具

# 作者

* [RomanQed](https://github.com/RomanQed) - *主要贡献者*

也可参考项目[贡献者列表](https://github.com/AmayaFramework/amaya-di/contributors)。

# 许可协议

本项目基于 Apache License Version 2.0 许可，详见 [LICENSE](../../LICENSE) 文件。
