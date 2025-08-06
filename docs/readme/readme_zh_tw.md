<p align="center">
  <img src="../img/logo.png" alt="logo" style="width: 200px; height: auto;">
</p>

# amaya-di – 現代、輕量、快速的 DI 框架。

[![amaya-di](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di?strategy=releaseProperty&style=for-the-badge&label=amaya-di&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di/)
[![amaya-di-core](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-core?strategy=releaseProperty&style=for-the-badge&label=amaya-di-core&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-core/)
[![amaya-di-schema](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-schema?strategy=releaseProperty&style=for-the-badge&label=amaya-di-schema&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-schema/)
[![amaya-di-stub](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-stub?strategy=releaseProperty&style=for-the-badge&label=amaya-di-stub&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-stub/)
[![amaya-di-asm](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-asm?strategy=releaseProperty&style=for-the-badge&label=amaya-di-asm&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-asm/)
[![amaya-di-reflect](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-reflect?strategy=releaseProperty&style=for-the-badge&label=amaya-di-reflect&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-reflect/)

- [English](../../README.md)
- [Русский](readme_ru.md)
- [简体中文](readme_zh_cn.md)
- 正體中文 (AI translated, Gemini 2.5 Flash)
- [日本語](readme_jp.md)
- [Deutsch](readme_de.md)
- [Français](readme_fr.md)

Amaya DI 是對 DI 框架應有面貌的現代化詮釋。它旨在為開發者提供一個高性能、靈活且極簡的 IoC 容器，擺脫了過時的 XML 配置、繁多的註解以及基於反射的隱藏魔法。

該框架「開箱即用」支援以下功能：

* 處理泛型型別（例如，`List<String>` 和 `List<Integer>` 將被識別為 2 種不同的型別）；
* 建立未命名作用域；
* 自動執行期物件工廠生成：
    * 每次請求新物件（transient）和每次請求相同物件（singleton）的策略；
    * 作用域內 transient 和 singleton 策略；
* 依賴圖的建構和驗證：
    * 尋找缺失的型別；
    * 使用 Tarjan 迭代演算法檢測循環；
* 透過便捷的 fluent API 實現完全宣告式的 IoC 容器組建過程；
* 與任何現有程式庫、框架或標準的簡易整合實現。

生成的物件工廠的性能為：

* 使用 ASM 實作時，與手動實作相符（13,604 ns vs 13,475 ns）；
* 使用 Reflect 實作時，包含 JNI 的開銷，但仍足夠快（58,055 ns vs 13,402 ns）。

更多詳細資訊，請參閱[基準測試](#基準測試)部分。

# 簡介

安裝所需：

* JVM 11+
* Maven/Gradle

不支援任何低於 JVM 11 的版本。

由於框架沒有編譯期外掛且不分析 Java AST，因此可以使用除 Java 之外的其他語言。

## 安裝

要使用該框架，需要安裝兩個模組：基礎模組（`io.github.amayaframework:amaya-di`）和 Stub 工廠的實作模組（`:amaya-di-asm` 或 `:amaya-di-reflect`）。有關選擇的更多資訊，請參閱[實作選擇](#實作選擇)部分。

### Gradle

```groovy
dependencies {
    implementation group: 'io.github.amayaframework', name: 'amaya-di', version: '3.0.4' // 核心模組
    // ASM stub 實作
    implementation group: 'io.github.amayaframework', name: 'amaya-di-asm', version: '2.0.2' // ASM Stub 實作
    // 或 reflect stub 實作
    implementation group: 'io.github.amayaframework', name: 'amaya-di-reflect', version: '2.0.0' // Reflect Stub 實作
}
```

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-di</artifactId>
        <version>3.0.4</version>
    </dependency>
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-di-asm</artifactId>
        <version>2.0.2</version>
    </dependency>
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-di-reflect</artifactId>
        <version>2.0.0</version>
    </dependency>
</dependencies>
```

## Hello, world!

容器的建構是透過建構器（`ServiceProviderBuilder` 或 `ScopedProviderBuilder`）來完成的。建構過程可分為 3 個階段：

* 獲取並配置建構器物件；
* 宣告所需的型別；
* 呼叫 `build()` 方法。

讓我們看看基本的使用場景。

首先，如果使用模組，我們將在 `module-info.java` 中指定依賴項：

```java
module com.github.romanqed.di.examples {
    requires amayaframework.di; // 主模組
    exports com.github.romanqed.di.examples;
}
```

現在，我們來組建最簡單的容器：

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

執行此程式碼後，將輸出 `Hello, world!`。

考慮一個更複雜的場景。假設我們有一個抽象服務 `IGreeter`：

```java
public interface IGreeter { 
    String sayHello(String name);
}
```

以及它的兩個實作，一個用於全域上下文，另一個用於作用域上下文：

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

在 `module-info.java` 中添加反射物件工廠生成器：

```java
module com.github.romanqed.di.examples {
    requires amayaframework.di; // 主模組
    requires amayaframework.di.reflect; // 使用反射實作
    exports com.github.romanqed.di.examples;
}
```

組建容器：

```java
var provider = ProviderBuilders.createScoped(new ReflectStubFactory())
        .addSingleton(IGreeter.class, GlobalGreeter.class)
        .addScoped(String.class)
        .addScopedSingleton(IGreeter.class, ScopedGreeter.class)
        .build();
```

並建立兩個作用域：

```java
var scope1 = provider.createScoped();
scope1.repository().put("Scope One"); 
var scope2 = provider.createScoped(); 
scope2.repository().put("Scope Two");
```

最後，輸出結果：

```java
System.out.println(provider.get(IGreeter.class).sayHello("Roman"));
System.out.println(scope1.get(IGreeter.class).sayHello("Roman")); 
System.out.println(scope2.get(IGreeter.class).sayHello("Roman"));
```

控制台將顯示：

```
Hello, Roman!
Hello from scope 'Scope One', Roman!
Hello from scope 'Scope Two', Roman!
```

完整的程式碼如下所示：

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

所有可執行的範例都可以在[這裡](../../examples)找到。

# 基本概念

## 核心

### ObjectFactory 和 TypeProvider

容器的核心機制是 `ObjectFactory` 和 `TypeProvider` 介面。它們具有以下形式：

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

`ObjectFactory` 執行型別實例的建立，其依賴項由 `TypeProvider` 提供。這種設計方便的原因有二：

1)  物件工廠與型別供應商沒有硬性依賴，可以在傳遞給下游工廠之前包裝和修改它，從而實現任何場景；
2)  直接獲取工廠而不是已建立的實例可以避免不必要的查找並快取工廠，同時不損失其他優勢。

至於乍一看似乎無用的 `TypeProvider#canProvide` 方法，它在我們希望**明確**確認供應商提供物件工廠的能力時需要。因為在容器實作由於某些目的提供工廠的包裝器時，與 `null` 的比較將不起作用。也就是說，假設 `get(Type.class)` 將返回一個 `ObjectFactory` 實作，它將始終返回 `null`。

正是為了防止這種情況，存在一個明確可確定的 `canProvide`。如果它返回 `true`，則意味著 `get` 將返回**非 `null`** 的 `ObjectFactory` 實例。而且有保證（從容器的角度來看！），它是使用者最初提供的工廠，而不是 Stub/臨時包裝器。

如果沒有自動生成 `ObjectFactory`，`ComplexHelloWorld` 的範例將如下所示 (`ManualHelloWorld`)：

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
    // IGreeter 及其實作
}
```

此外，`ComplexHelloWorld` 中的 `addScoped(String.class)` 在這裡可以移除，因為不執行使用者提供的工廠的驗證。這些依賴項（包括實例和 `Function0` 版本）被視為根類型，因為無法在有限時間內（即不執行它們）確定其依賴型別集合。

### 擴展 TypeProvider：TypeRepository

`TypeRepository` 擴展了 `TypeProvider`，將其轉換為一個完整的 CRUD 型別儲存庫。它看起來如下：

```java
public interface TypeRepository extends TypeProvider, Iterable<Type> {
    void put(Type type, ObjectFactory factory); // 將型別和工廠放入儲存庫
    
    void put(Type type, Function0<?> provider); // 將型別和提供者放入儲存庫
    
    void put(Type type, Object instance); // 將型別和實例放入儲存庫
    
    void put(Object instance); // 將實例放入儲存庫
    
    ObjectFactory remove(Type type); // 從儲存庫中移除型別
    
    void putAll(TypeRepository repository); // 從另一個儲存庫複製所有項目
    
    void putAll(Map<Type, ObjectFactory> map); // 從 Map 複製所有項目
    
    void clear(); // 清空儲存庫
    
    void forEach(BiConsumer<Type, ObjectFactory> action); // 對每個項目執行操作
}
```

`put(type, factory)` 方法將 `型別->工廠` 形式的條目放入儲存庫。如果傳入的型別已存在，則工廠會被指定實例覆蓋。

`put(type, provider)`、`put(type, instance)`、`put(instance)` 方法（取決於儲存庫實作）分別是 `put(type, p -> provider.invoke())`、`put(type, p -> instance)` 和 `put(instance.getClass(), p -> instance)` 的類似物。

`remove(type)` 方法從儲存庫中刪除型別的條目並返回儲存的 `ObjectFactory` 或 `null`（如果該型別不存在）。

兩種 `putAll(...)` 變體都將 `型別->工廠` 形式的條目從傳入的來源複製到儲存庫。

`clear()` 方法完全清空儲存庫，刪除其所有內容。

`forEach(BiConsumer)` 方法將傳入的 `BiConsumer` 實例應用於每個 `型別->工廠` 條目，而 `Iterable<Type>` 介面的方法行為與其是可變的 `Set<Type>` 相同。

所有類型的容器都建立在 `TypeRepository` 的基礎上，並透過它管理容器的內容。請注意 – **不保證**傳遞給 `ObjectFactory#create()` 的 `TypeProvider` 實例是 `TypeRepository` 的實例。一般來說，從框架設計的角度來看，在工廠內部嘗試訪問儲存庫是不正確的。此類功能將永遠不會實現。

### 通用容器：ServiceProvider

`ServiceProvider` 介面描述了一個抽象的 DI 容器，它提供用於獲取所請求型別的現成實作以及管理內容的介面。它看起來是這樣的：

```java
public interface ServiceProvider {
    TypeRepository repository(); // 返回型別儲存庫
    
    ServiceProvider createScoped(); // 建立新的作用域容器
    
    <T> T get(Type type); // 獲取指定型別的實例
    
    <T> T get(Class<T> type); // 獲取指定類別的實例
    
    <T> T get(JType<T> type); // 獲取指定 JType 的實例
}
```

`get(type)` 方法執行所請求型別的 `ObjectFactory` 搜尋，然後：

1)  如果存在，則為所請求型別建立實例；
2)  否則返回 `null`。

`repository()` 方法返回此容器使用的可變 `TypeRepository` 實例。

`createScoped()` 方法建立一個新容器，在搜尋型別時會回溯到呼叫該方法的父容器。也就是說，如果在此容器中找不到所請求的型別，則請求會轉發到下級容器。作用域可以無限執行，建立一個連結的容器鏈。

### 核心的其他工具

除了上述介面，核心還包括：

* `ServiceProvider` 的抽象實作（`AbstractServiceProvider`），包含 `repository` 欄位和除 `createScoped()` 之外的所有方法；
* 基於 `Map<Type, ObjectFactory>` 的 `TypeRepository` 實作（`HashTypeRepository`）；
* `ObjectFactory` 的執行緒安全懶惰包裝器，用於單例策略（`LazyObjectFactory`）；
* 通用的作用域 `TypeRepository` 實作，連接任意一對作用域和父儲存庫（`ScopedTypeRepository`）。

詳情請參閱 javadoc。

## 模式

### 注入模式的概念

為確保反射資訊分析與其獲取方法完全獨立，框架引入了「注入模式」的概念。它表示一個描述符，包含以下資訊：

* 注入物件或「目標」——類別成員或類別本身；
* 注入目標所依賴的型別集合；
* 用於呼叫目標（方法或建構子）的型別映射，將參數編號與相應型別關聯起來；
* 欄位的單一型別。

類別注入模式還包含一個建構子模式、多個方法模式和多個欄位模式。此時，類別所依賴的型別集合包含所有類別成員模式的型別集合的聯集。

每個 `...Schema` 實作的基本介面如下所示：

```java
public interface Schema<T> {
    T getTarget(); // 獲取目標
    
    Set<Type> getTypes(); // 獲取依賴型別
}
```

為了與可呼叫目標一起使用，它擴展了 `ExecutableSchema`：

```java
public interface ExecutableSchema<T extends Executable> extends Schema<T> {
    Type[] getMapping(); // 獲取參數映射
}
```

共有 4 種實作：

* `FieldSchema` – 欄位的注入模式；
* `ConstructorSchema` – 建構子的注入模式，實作 `ExecutableSchema` 介面；每個類別只允許一個；
* `MethodSchema` – 方法的注入模式，實作 `ExecutableSchema` 介面；
* `ClassSchema` – 整個類別的注入模式。

### 模式工廠

為了為指定類別生成模式，使用模式工廠。每個這樣的工廠都必須實作 `SchemaFactory` 介面：

```java
public interface SchemaFactory {
    ClassSchema create(Class<?> clazz); // 建立指定類別的模式
}
```

預設實作會在類別成員中尋找注入目標（`ReflectSchemaFactory`）。採用以下邏輯：

1)  檢查類別的所有公共建構子（`Class#getConstructors()`）：
    1)  如果只有 1 個這樣的建構子，則選擇它；
    2)  如果有多個，則選擇帶有標記註解的建構子；
    3)  否則，該類別被視為無效；
2)  尋找類別中所有帶有標記註解的公共虛擬欄位；
3)  尋找類別中所有帶有標記註解的公共方法：
    1)  如果方法是虛擬的，則選擇它；
    2)  如果方法是靜態的，並且其第一個參數的型別等於或超出了目標類別的型別，則選擇它；
    3)  否則，該方法被視為無效。

同時，標記註解可以是任何註解（框架預設使用 `@Inject`，它與 `amaya-di` 模組一起提供），並作為建構子參數指定。

`ReflectSchemaFactory` 還允許指定自訂的「型別處理器」，它必須實作以下介面：

```java
public interface TypeProcessor {
    Type process(Type type, AnnotatedElement element); // 處理型別和註解元素
}
```

預設使用的型別處理器 (`ReflectTypeProcessor`) 執行「簡單」型別（`Class` 實例）和泛型型別的解析。此外，在處理過程中，所有萬用字元型別都會轉換為上限：

* `List<? extends String>` =\> `List<String>`；
* `List<? super String>` =\> `List<Object>`；
* `List<?>` =\> `List<Object>`。

型別變數是完全禁止的，因為無法明確執行它們的轉換。例如，如果將它們轉換為上限中的第一個型別（`<T>` =\> `Object`，`<T extends Number>` =\> `Number`），則在循環 `class A<T extends A<T>>` 的情況下，無法明確確定如何打破它。

## ObjectFactory 的生成

### Stub 工廠

在框架的上下文中，Stub 是指根據類別的注入模式自動生成的 `ObjectFactory` 實作。Stub 是使用 `StubFactory` 建立的，其基本介面如下所示：

```java
@FunctionalInterface
public interface StubFactory {
    ObjectFactory create(ClassSchema schema, CacheMode mode); // 根據模式和快取模式建立工廠
    
    default ObjectFactory create(ClassSchema schema) {
        return create(schema, CacheMode.NONE);
    }
}
```

所有實作都**必須**嚴格遵守指定的注入模式和快取模式。如果由於某種原因無法建立**可用**的 `ObjectFactory` 實例，或者無法注入部分依賴項（例如，目標欄位以某種方式變成私有），則工廠必須拋出異常。不允許返回 `null` 和/或返回像 `provider -> null` 這樣的原始實作。

### ObjectFactory 快取

在大多數情況下，組建好的容器僅用於唯讀模式。因此，在這種情況下，對於生成的 `ObjectFactory`，直接儲存其所依賴型別的工廠實作是有意義的。這將最大限度地減少（甚至完全避免）不必要的 `TypeProvider#get(type)` 呼叫，從而顯著降低開銷。

為了明確控制快取，引入了以下模式：

* `CacheMode.FULL` – 不執行 `TypeProvider` 請求，所需的工廠直接從內部快取中獲取；
* `CacheMode.PARTIAL` – 如果內部快取中沒有所需的工廠，則執行 `TypeProvider` 請求；請求結果**不儲存**（用於執行緒安全的容器存取）；
* `CacheMode.NONE` – 始終執行 `TypeProvider` 請求，不存在內部快取。

此外，所有具有內部快取的 `ObjectFactory` 都必須實作 `CachedObjectFactory` 介面，該介面允許初始化或更新內容：

```java
public interface CachedObjectFactory extends ObjectFactory {
    void set(Type type, ObjectFactory factory); // 設定指定型別的工廠
}
```

# 容器組建

容器的建立和填充可以直接進行（透過實作和實例化所有必要的實體），也可以透過使用者工具進行。

預設情況下，框架提供基於「建造者」模式的組建機制。其功能包括：

* 使用 [jtype](https://github.com/RomanQed/jtype) 處理泛型型別；
* 添加自訂的 `ObjectFactory` 實作；
* 根據類別添加 transient 和 singleton 型別；
* 添加作用域型別；
* 驗證依賴圖是否存在循環和缺失型別。

## 基本使用場景

建構器變體由 `ServiceProviderBuilder` 介面及其擴展 `ScopedProviderBuilder` 表示，後者添加了作用域功能。透過工具類別 `ProviderBuilders` 獲取其實例。它提供了靜態方法來建立框架中包含的所有實作，並且本質上是 API 的入口點。

存在以下主要方法（重載請參閱 javadoc）：

```java
// 建立具有指定工廠和檢查的建構器
// 如果 checks == BuilderChecks.NO_CHECKS，則使用沒有驗證機制的實作
public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {...}

// 建立具有預設模式工廠和快取模式（ProviderBuilders#SCHEMA_FACTORY 和 #CACHE_MODE）的建構器
// 如果 checks == BuilderChecks.NO_CHECKS，則使用沒有驗證機制的實作 
public static ServiceProviderBuilder create(int checks) {...}

// 建立具有指定工廠、ProviderBuilders#CACHE_MODE 和無檢查的建構器
public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory) {}

// 建立具有 ProviderBuilders#SCHEMA_FACTORY、#CACHE_MODE 和無檢查的建構器
public static ServiceProviderBuilder create() {...}

// 建立具有 ProviderBuilders#SCHEMA_FACTORY、#CACHE_MODE 和 BuilderChecks.VALIDATE_ALL 的建構器
public static ServiceProviderBuilder createChecked() {...}

// 建立具有指定工廠和檢查的作用域建構器
// 如果 checks == BuilderChecks.NO_CHECKS，則使用沒有驗證機制的實作
public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {...}

// 建立具有預設模式工廠和快取模式（ProviderBuilders#SCHEMA_FACTORY 和 #CACHE_MODE）的作用域建構器
// 如果 checks == BuilderChecks.NO_CHECKS，則使用沒有驗證機制的實作 
public static ScopedProviderBuilder createScoped(int checks) {...}

// 建立具有指定工廠、ProviderBuilders#CACHE_MODE 和無檢查的作用域建構器
public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory) {...}

// 建立具有 ProviderBuilders#SCHEMA_FACTORY、#CACHE_MODE 和無檢查的作用域建構器
public static ScopedProviderBuilder createScoped() {...}

// 建立具有 ProviderBuilders#SCHEMA_FACTORY、#CACHE_MODE 和 BuilderChecks.VALIDATE_ALL 的作用域建構器
public static ScopedProviderBuilder createCheckedScoped() {...}
```

顯示建構器所有功能的範例，請參閱[此處](../../examples/src/main/java/com/github/romanqed/di/examples/AllMethods.java)。

## 泛型型別

要將泛型型別註冊到容器中，可以使用 jtype 或能夠為參數化型別提供 `java.lang.reflect.Type` 介面實作的第三方程式庫。以下範例顯示了型別的自動捕獲和手動建立：

```java
public final class GenericTypes {
    public static void main(String[] args) {
        var provider = ProviderBuilders.create(new ReflectStubFactory(), BuilderChecks.VALIDATE_MISSING_TYPES)
                .addInstance(new JType<>(){}, List.of("str1", "str2", "str3")) // 自動捕獲
                .addInstance(Types.of(List.class, Integer.class), List.of(1, 2, 3)) // 手動
                // 對於 "new JType<>(){}"，型別將是 Map<String, String>
                .addInstance(new JType<Map<String, Object>>(){}, Map.of("s1", "k1", "s2", "k2")) // 以及對於 Map
                .addInstance(Types.of(Map.class, Integer.class, Object.class), Map.of(1, 1, 2, 2))
                .addTransient(A1.class)
                .addTransient(A2.class)
                // B<A1> 和 B<A2>
                .add(Types.of(B.class, A1.class), tp -> new B<>((A1) tp.get(A1.class).create(tp)))
                .add(new JType<B<A2>>(){}, tp -> new B<>((A2) tp.get(A2.class).create(tp)))
                // 最後是 C
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

結果將輸出：

```
[1, 2, 3]
[str1, str2, str3]
{1=1, 2=2}
{s2=k2, s1=k1}
```

## 依賴圖驗證

為實現驗證機制，採用以下約定：

* 使用者提供的所有 `ObjectFactory` 實作都無條件地被視為正確，並被視為「根」型別，沒有自己的依賴項；
* 所有使用者 `ServiceWrapper` 都返回正確的 `ObjectFactory` 並且不產生錯誤；
* 所有可以建立注入模式的型別都被視為「弱」型別，並將檢查：
    * 缺少依賴項的請求；
    * 依賴圖中的循環建立。

對於作用域容器，額外引入：

* 承諾型別的註冊**保證**在建立作用域容器時，在開始使用它之前，該型別將被提供給儲存庫；
* 作用域型別可以參考其他作用域型別和基本型別；
* 基本型別只能參考其他基本型別。

基於此，並將容器視為依賴圖，可以區分五類可能的錯誤。

1.  基本容器中缺少另一個基本型別所依賴的型別。

    範例：

    ```
    class A -> class String
    class String -> 缺失
    ```

2.  作用域容器中缺少另一個作用域型別所依賴的型別。

    範例：

    ```
    class ScopedA -> class String
    class String -> 在作用域和基本容器中都缺失
    ```

<!-- end list -->

3)  基本容器中基本型別之間出現循環依賴。

    範例：

    ```
    class A -> class B
    class B -> class C
    class C -> class A
    ```

4)  作用域容器中作用域型別之間出現循環依賴。

5)  由於作用域容器重寫型別，容器之間出現循環依賴。

    範例：

    ```
    class A -> class B
    class B -> inteface IC
    interface IC: C
    interface IC: ScopedC -> A
    ```

內建驗證機制支援處理所有這 5 種類型的錯誤。範例請參見[此處](../../examples/src/main/java/com/github/romanqed/di/examples/AllChecks.java)。

# ObjectFactory 生成器選項

框架提供了兩種 `StubFactory` 實作，分別在 `amaya-di-asm` 和 `amaya-di-reflect` 模組中提供。

## ASM

ASM 實作使用即時位元碼生成來建立 `ObjectFactory`。生成的類別名稱由注入目標類別的名稱和所用快取模式的別名組成。因此，在重複生成請求時，不會再次生成，而是使用已載入的、按名稱獲取的類別。

ASM 實作的特點是準備速度相對較慢，但執行速度盡可能快，實際上與普通程式碼的速度相同。

還實現了位元碼快取，允許重複使用一次生成的類別。這在以下情況下非常有用：最終（重要的是，未來不變）的 JAR 文件已組建並將在生產環境中發布：

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

這段程式碼在首次執行後將建立一個名為 `cache` 的目錄，用於儲存生成的位元碼。在後續執行中，它將直接載入位元碼，而不是再次生成。如果快取被刪除，則會再次生成。

## Reflect

Reflect 實作使用反射方法呼叫和欄位存取來建立 `ObjectFactory`。請求的類別成員被標記為可存取，無需檢查修飾符（`setAccessible(true)`），並包裝在準備好的工廠實作中。

Reflect 實作的特點是準備速度相對較快，但執行速度較慢，包括反射存取機制的大量開銷。

## 實作選擇

ASM 和 Reflect 實作之間的選擇應基於容器使用場景的特性來進行。如果您的應用程式中的容器：

* 頻繁重新組建；
* 在啟動期間僅使用一次；
* 不在「熱門」程式碼中使用（或高開銷是可接受的）；

那麼應該使用 Reflect 實作。

如果容器：

* 組建一次；
* 在熱門程式碼中持續使用；
* 必須在最短時間內提供依賴項；

那麼應該使用 ASM 實作。

## 基準測試

基準測試在 Amazon Corretto 11 (`openjdk version "11.0.27" 2025-04-15 LTS`)，配備 Intel i5-9400F、40 GB RAM 和 Windows 10 22H2 的 PC (非伺服器) 上進行。使用了 JMH 1.37 版框架。

依賴項集包括：

* Service1；
* Service2；
* Service3，依賴於 Service1 (建構子)；
* App，依賴於所有三個 (Service1 – 建構子，Service2 – 欄位，Service3 – 方法)。

容器在基準測試前已完全準備好，沒有與其進行任何互動。

為了獲得可用於比較其他結果的基準時間，編寫了一個實現「手動」建立 App 和注入依賴項的簡單程式碼：

```java
var app = new App(new Service1());
app.s2 = new Service2();
app.setS3(new Service3(new Service1()));
```

同時，關於儲存庫自身開銷（用於型別查找）的基準測試在兩個版本中進行：

* 測量手動建立 App 的「純」執行時間；
* 將 App 的建立包裝在 `ObjectFactory` 中並放入容器，測量容器中查找呼叫和 `create()` 呼叫的時間。

還分別測量了每個 CacheMode 的時間，並再次測量了 CacheMode.FULL（預設模式）。

獲得以下結果：

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

基準測試的執行方式如下：

```
>./gradlew asm:jmh
>./gradlew reflect:jmh
>./gradlew :jmh
```

# 與 2.x 的差異

從 2.x 遷移到 3.x 時，框架的基本 API 完全失去了相容性。除了新方法之外，建構器的 API 保持不變。對於之前使用 amaya-di 2.x 並希望遷移到 3.x 的使用者，應注意以下關鍵變更：

* 現在使用 `ObjectFactory` 作為物件工廠，而不是 `Function0`；
* `ServiceRepository` 介面已重新命名為 `TypeRepository`；
* `TypeProvider` 介面（曾是 Stub 模組所需的本地機制）已完全刪除；
* `ManualProviderBuilder` 介面已完全刪除；
* `StubFactory` 的 API 已更改：`create(schema, typeProvider)` =\> `create(schema, cacheMode)`；
* 現在 `StubFactory` 在建立時不會填充工廠快取，需要自行完成；
* `jgraph` 模組不再使用並已從依賴項中排除；
* 抽象類別 `AbstractProviderBuilder` 已被 `AbstractServiceProviderBuilder` 取代，保護 API 已完全更改；
* `ServiceWrapper` 介面不再繼承 `Function1` 介面，其功能方法的簽名現在是 `ObjectFactory wrap(ObjectFactory factory)`；
* `LazyProvider` 類別已完全刪除；
* 出現了 `CyclesFoundException` 異常，如果檢測到多個循環，可能會拋出此異常；
* 模組劃分已完全更改：
    * `ServiceProvider` 和相關型別已移至單獨的 `amaya-di-core` 模組；
    * `ClassSchema` 和相關型別已移至單獨的 `amaya-di-schema` 模組；
    * `StubFactory` 和相關型別已移至單獨的 `amaya-di-stub` 模組；
    * `amaya-di` 模組現在包含這三個模組，`amaya-di-core` 模組被指定為遞歸依賴項；
* 出現了新的作用域機制。

# 貢獻

如果您願意為 amaya-di 提供更正、改進和功能擴展，我將不勝感激。詳細的貢獻指南位於[此處](../contributing/contributing_zh_tw.md)。

# 建立者

* [Gradle](https://gradle.org) - 依賴管理
* [ASM](https://asm.ow2.io) - `ObjectFactory` 生成
* [jeflect-loader](https://github.com/RomanQed/jeflect) - 執行期位元碼載入
* [jfunc](https://github.com/RomanQed/jfunc) - 功能介面，工具
* [jtype](https://github.com/RomanQed/jtype) - 泛型工具

# 作者

* [RomanQed](https://github.com/RomanQed) - *主要工作*

另請參閱對此專案做出貢獻的[貢獻者](https://github.com/AmayaFramework/amaya-di/contributors)列表。

# 授權

此專案根據 Apache License Version 2.0 授權 - 有關詳細資訊，請參閱 [LICENSE](../../LICENSE) 文件。