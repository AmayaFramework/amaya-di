# amaya-di – 现代、轻量且高性能的依赖注入框架（DI）

[![amaya-di](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di?strategy=releaseProperty&style=for-the-badge&label=amaya-di&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di/)
[![amaya-di-core](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-core?strategy=releaseProperty&style=for-the-badge&label=amaya-di-core&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-core/)
[![amaya-di-schema](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-schema?strategy=releaseProperty&style=for-the-badge&label=amaya-di-schema&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-schema/)
[![amaya-di-stub](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-stub?strategy=releaseProperty&style=for-the-badge&label=amaya-di-stub&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-stub/)
[![amaya-di-asm](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-asm?strategy=releaseProperty&style=for-the-badge&label=amaya-di-asm&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-asm/)
[![amaya-di-reflect](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-reflect?strategy=releaseProperty&style=for-the-badge&label=amaya-di-reflect&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-reflect/)

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

更多详细信息请参见 [基准测试](#бенчмарки) 部分。

# 介绍

安装要求：

* JVM 11 及以上版本
* Maven 或 Gradle

低于 JVM 11 的版本不受支持。

支持除 Java 以外的语言，因为框架没有编译时插件且不分析 Java AST。

## 安装

使用时需安装两个模块：基础模块 (`io.github.amayaframework:amaya-di`) 和 stub 工厂实现模块（`:amaya-di-asm` 或 `:amaya-di-reflect`）。  
关于如何选择实现，请参阅 [选择实现](#выбор-реализации)。

### Gradle

```Groovy
dependencies {
implementation group: 'io.github.amayaframework', name: 'amaya-di', version: '3.0.4'
// ASM stub 实现
implementation group: 'io.github.amayaframework', name: 'amaya-di-asm', version: '2.0.2'
// 或者反射 stub 实现
implementation group: 'io.github.amayaframework', name: 'amaya-di-reflect', version: '2.0.0'
}
```

### Maven

```
<dependencies>
<dependency>
<groupId>io.github.amayaframework</groupId>
<artifactId>amaya-di</artifactId>
<version>3.0.4</version>
</dependency>
<!-- ASM stub 实现 -->
<dependency>
<groupId>io.github.amayaframework</groupId>
<artifactId>amaya-di-asm</artifactId>
<version>2.0.2</version>
</dependency>
<!-- 或者反射 stub 实现 -->
<dependency>
<groupId>io.github.amayaframework</groupId>
<artifactId>amaya-di-reflect</artifactId>
<version>2.0.0</version>
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
requires io.github.amayaframework.di; // 主模块
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
requires io.github.amayaframework.di; // 主模块
requires io.github.amayaframework.di.reflect; // 使用反射实现
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
`put(type, provider)`、`put(type, instance)` 和 `put(instance)` 等同于将 `provider.invoke()`、`instance` 等封装为工厂再放入。  
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
