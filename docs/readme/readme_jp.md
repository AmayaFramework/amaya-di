<p align="center">
  <img src="../img/logo.png" alt="logo" style="width: 200px; height: auto;">
</p>

# amaya-di – 最新、軽量、高速なDIフレームワーク。

[![amaya-di](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di?strategy=releaseProperty&style=for-the-badge&label=amaya-di&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di/)
[![amaya-di-core](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-core?strategy=releaseProperty&style=for-the-badge&label=amaya-di-core&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-core/)
[![amaya-di-schema](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-schema?strategy=releaseProperty&style=for-the-badge&label=amaya-di-schema&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-schema/)
[![amaya-di-stub](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-stub?strategy=releaseProperty&style=for-the-badge&label=amaya-di-stub&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-stub/)
[![amaya-di-asm](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-asm?strategy=releaseProperty&style=for-the-badge&label=amaya-di-asm&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-asm/)
[![amaya-di-reflect](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-reflect?strategy=releaseProperty&style=for-the-badge&label=amaya-di-reflect&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-reflect/)

- [English](../../README.md)
- [Русский](readme_ru.md)
- [简体中文](readme_zh_cn.md)
- [正體中文](readme_zh_tw.md)
- 日本語 (AI translated, Gemini 2.5 Flash)
- [Deutsch](readme_de.md)
- [Français](readme_fr.md)

Amaya DIは、DIフレームワークのあるべき姿を現代的に捉え、開発者に高性能で柔軟、ミニマリストなIoCコンテナを提供することを目指して作成されました。古臭いXML設定、多数のアノテーション、リフレクションに基づく隠れたマジックは一切ありません。

このフレームワークは、以下の機能を「すぐに使える」状態でサポートしています。

* ジェネリック型との連携 (例: `List<String>` と `List<Integer>` は2つの異なる型として認識されます)
* 無名スコープの作成
* オブジェクトファクトリの自動ランタイム生成:
    * リクエストごとに新しいオブジェクト (transient) とリクエストごとに同じオブジェクト (singleton) の戦略
    * スコープ付き transient および singleton 戦略
* 依存関係グラフの構築と検証:
    * 欠落している型の検索
    * タルジャンの反復アルゴリズムによる循環の検出
* 便利なfluent APIを用いた完全に宣言的なIoCコンテナ構築プロセス
* 既存のライブラリ、フレームワーク、または標準との簡単な統合

生成されたオブジェクトファクトリのパフォーマンスは以下の通りです。

* ASM実装を使用する場合、手動実装と同等です (13.604 ns vs 13.475 ns)。
* リフレクト実装を使用する場合、JNIのオーバーヘッドが含まれますが、それでも十分高速です (58.055 ns vs 13.402 ns)。

詳細については、「[ベンチマーク](#ベンチマーク)」セクションを参照してください。

# はじめに

インストールには以下が必要です。

* JVM 11+
* Maven/Gradle

JVM 11より前のバージョンはサポートされていません。

フレームワークはコンパイル時プラグインを持たず、Java ASTを解析しないため、Java以外の言語も使用可能です。

## インストール

フレームワークを使用するには、2つのモジュールをインストールする必要があります。基本モジュール (`io.github.amayaframework:amaya-di`) と、スタブファクトリの実装 (`:amaya-di-asm` または `:amaya-di-reflect`) です。 選択の詳細については、「[実装の選択](#実装の選択)」セクションを参照してください。

### Gradle

```groovy
dependencies {
    implementation group: 'io.github.amayaframework', name: 'amaya-di', version: '3.0.4'
    // ASM stub implementation
    implementation group: 'io.github.amayaframework', name: 'amaya-di-asm', version: '2.0.2'
    // Or reflect stub implementation
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

## Hello, world\!

コンテナの構築は、ビルダー (`ServiceProviderBuilder` または `ScopedProviderBuilder`) を使用して実行されます。 構築プロセスは3つの段階に分けられます。

* ビルダーオブジェクトの取得と構成
* 必要な型の宣言
* `build()` メソッドの呼び出し

基本的な使用シナリオを見てみましょう。

まず、モジュールを使用する場合、`module-info.java` に依存関係を指定します。

```java
module com.github.romanqed.di.examples {
    requires amayaframework.di; // Main module
    exports com.github.romanqed.di.examples;
}
```

次に、最も簡単なコンテナを構築します。

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

このコードを実行すると、「Hello, world\!」と出力されます。

より複雑なシナリオを考えてみましょう。抽象サービス `IGreeter` があるとします。

```java
public interface IGreeter { 
    String sayHello(String name);
}
```

そして、その2つの実装、1つはグローバルコンテキスト用、もう1つはスコープ付き用です。

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

`module-info.java` にリフレクティブオブジェクトファクトリジェネレーターを追加します。

```java
module com.github.romanqed.di.examples {
    requires amayaframework.di; // Main module
    requires amayaframework.di.reflect; // Use reflective implementation
    exports com.github.romanqed.di.examples;
}
```

コンテナを構築します。

```java
var provider = ProviderBuilders.createScoped(new ReflectStubFactory())
        .addSingleton(IGreeter.class, GlobalGreeter.class)
        .addScoped(String.class)
        .addScopedSingleton(IGreeter.class, ScopedGreeter.class)
        .build();
```

そして、2つのスコープを作成します。

```java
var scope1 = provider.createScoped();
scope1.repository().put("Scope One"); 
var scope2 = provider.createScoped(); 
scope2.repository().put("Scope Two");
```

最後に、結果を出力します。

```java
System.out.println(provider.get(IGreeter.class).sayHello("Roman"));
System.out.println(scope1.get(IGreeter.class).sayHello("Roman")); 
System.out.println(scope2.get(IGreeter.class).sayHello("Roman"));
```

コンソールには次のように表示されます。

```
Hello, Roman!
Hello from scope 'Scope One', Roman!
Hello from scope 'Scope Two', Roman!
```

完全なコードは次のとおりです。

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

実行可能なすべての例は[こちら](../../examples)にあります。

# 基本的な概念

## コア

### ObjectFactoryとTypeProvider

コンテナの基盤となる主要なメカニズムは、`ObjectFactory` と `TypeProvider` インターフェースです。 これらは次のような形式です。

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

`ObjectFactory` は型のインスタンスを作成し、その依存関係を `TypeProvider` が提供します。 この設計は2つの理由で便利です。

1)  オブジェクトファクトリは型プロバイダーに厳密に依存せず、下位のファクトリに渡す前にそれをラップして変更できるため、あらゆるシナリオを実装できます。
2)  作成されたインスタンスではなくファクトリを直接取得することで、余分なルックアップを回避し、他の利点を失うことなくファクトリをキャッシュできます。

一見すると役に立たないように見える `TypeProvider#canProvide` メソッドについては、プロバイダーがオブジェクトファクトリを提供できることを**明確に**確認したい状況で必要です。 なぜなら、コンテナの実装が何らかの目的でファクトリのラッパーを提供する場合、`null` との比較は機能しないからです。 つまり、たとえば `get(Type.class)` は常に `null` を返す `ObjectFactory` の実装を返すことになります。

このような状況を防ぐために、明確に定義された `canProvide` が存在します。 これが `true` を返した場合、`get` は **`null` でない** `ObjectFactory` インスタンスを返します。 そして、それは（コンテナの観点から）ユーザーが最初に提供したファクトリであり、スタブ/一時的なラッパーではないという保証付きです。

`ObjectFactory` の自動生成なしでは、`ComplexHelloWorld` の例は次のようになります (`ManualHelloWorld`)。

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

さらに、`ComplexHelloWorld` にあった `addScoped(String.class)` は、ユーザー提供のファクトリの検証が行われないため、ここでは削除できます。 これらの依存関係（インスタンスおよび `Function0` バージョンを含む）は、有限時間で依存型のセットを決定できないため（つまり、実行せずに）、ルート型と見なされます。

### TypeProviderの拡張: TypeRepository

`TypeRepository` は `TypeProvider` を拡張し、完全なCRUD型ストレージに変換します。 それは次のような形式です。

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

`put(type, factory)` メソッドは、`type->factory` の形式のエントリをリポジトリに格納します。 指定された型に対して既に存在する場合、ファクトリは指定されたインスタンスで上書きされます。

`put(type, provider)`、`put(type, instance)`、`put(instance)` メソッドは、おおよそ（リポジトリの実装に依存しますが）それぞれ `put(type, p -> provider.invoke())`、`put(type, p -> instance)`、`put(instance.getClass(), p -> instance)` に相当します。

`remove(type)` メソッドは、リポジトリから型のエントリを削除し、格納されている `ObjectFactory` または、そのような型がなかった場合は `null` を返します。

`putAll(...)` の両方のバリエーションは、指定されたソースから `type->factory` 形式のエントリをリポジトリにコピーします。

`clear()` メソッドは、リポジトリを完全にクリアし、そのすべての内容を削除します。

`forEach(BiConsumer)` メソッドは、各 `type->factory` エントリに指定された `BiConsumer` インスタンスを適用し、`Iterable<Type>` インターフェースのメソッドは、それが変更可能な `Set<Type>` であるかのように動作します。

`TypeRepository` はあらゆる種類のコンテナを構築する基礎であり、これを通じてコンテナの内容が管理されます。 注目すべき点として、`ObjectFactory#create()` に渡される `TypeProvider` インスタンスが `TypeRepository` のインスタンスであるという**保証はありません**。 一般に、ファクトリ内でリポジトリにアクセスしようとすることは、フレームワークの設計の観点から見て正しくありません。 このような機能は決して実装されません。

### ユニバーサルコンテナ: ServiceProvider

`ServiceProvider` インターフェースは、要求された型の完成された実装を取得するためのインターフェースと、コンテンツを管理するためのインターフェースの両方を提供する抽象DIコンテナを記述します。 これは次のようになります。

```java
public interface ServiceProvider {
    TypeRepository repository();
    
    ServiceProvider createScoped();
    
    <T> T get(Type type);
    
    <T> T get(Class<T> type);
    
    <T> T get(JType<T> type);
}
```

`get(type)` メソッドは、要求された型の `ObjectFactory` を検索し、次の処理を行います。

1)  もし存在すれば、要求された型のインスタンスを作成します。
2)  そうでなければ、`null` を返します。

`repository()` メソッドは、このコンテナで使用される変更可能な `TypeRepository` インスタンスを返します。

`createScoped()` メソッドは、型を検索する際に、メソッドが呼び出された親コンテナにフォールスルーする新しいコンテナを作成します。 つまり、型がこのコンテナで見つからなかった場合、要求は下位のコンテナに送信されます。 スコープは無限に実行でき、リンクされたコンテナチェーンを作成できます。

### その他のコアユーティリティ

上記のインターフェースに加えて、コアには以下が含まれます。

* `repository` フィールドと `createScoped()` を除くすべてのメソッドを含む `ServiceProvider` の抽象実装 (`AbstractServiceProvider`)。
* `Map<Type, ObjectFactory>` に基づく `TypeRepository` の実装 (`HashTypeRepository`)。
* singletonポリシーで使用される `ObjectFactory` のスレッドセーフな遅延ラッパー (`LazyObjectFactory`)。
* あらゆるスコープ付きリポジトリと親リポジトリのペアを接続するユニバーサルなスコープ付き `TypeRepository` 実装 (`ScopedTypeRepository`)。

詳細はJavadocを参照してください。

## スキーマ

### インジェクションスキームの概念

リフレクティブ情報の解析をその取得方法から完全に独立させるために、フレームワークは「インジェクションスキーム」の概念を導入しています。 これは、以下の情報を含む記述子です。

* インジェクションの対象となるオブジェクトまたは「ターゲット」 — クラスメンバーまたはクラス自体。
* インジェクションのターゲットが依存する型のセット。
* 呼び出し可能なターゲット (メソッドまたはコンストラクタ) の型マッピングで、パラメータ番号に対応する型を関連付けます。
* フィールドの単一の型。

クラスのインジェクションスキーマには、コンストラクタスキーマ、メソッドスキーマのセット、およびフィールドスキーマのセットも含まれます。 クラスが依存する型のセットは、すべてのクラスメンバーのスキーマの型のセットの結合を含みます。

各 `...Schema` が実装する基本インターフェースは次のようになります。

```java
public interface Schema<T> {
    T getTarget();
    
    Set<Type> getTypes();
}
```

呼び出し可能なターゲットで使用するために、`ExecutableSchema` がこれを拡張します。

```java
public interface ExecutableSchema<T extends Executable> extends Schema<T> {
    Type[] getMapping();
}
```

全部で4つの実装が存在します。

* `FieldSchema` – フィールドのインジェクションスキーム
* `ConstructorSchema` – コンストラクタのインジェクションスキーム、`ExecutableSchema` インターフェースを実装します。クラスごとに1つのみ許可されます。
* `MethodSchema` – メソッドのインジェクションスキーム、`ExecutableSchema` インターフェースを実装します。
* `ClassSchema` – クラス全体のインジェクションスキーム

### スキーマファクトリ

指定されたクラスのスキーマを生成するために、スキーマファクトリが使用されます。 各スキーマファクトリは `SchemaFactory` インターフェースを実装する必要があります。

```java
public interface SchemaFactory {
    ClassSchema create(Class<?> clazz);
}
```

デフォルトでは、クラスメンバー間でインジェクションターゲットを検索する実装 (`ReflectSchemaFactory`) が提供されます。 次のロジックが適用されます。

1)  クラスのすべてのパブリックコンストラクタ (`Class#getConstructors()`) が検査されます。
    1)  コンストラクタが1つの場合、それが選択されます。
    2)  複数ある場合、マーカーアノテーションでアノテーションされたものが選択されます。
    3)  それ以外の場合、クラスは無効と見なされます。
2)  マーカーアノテーションでアノテーションされたクラスのすべてのパブリック仮想フィールドが検索されます。
3)  マーカーアノテーションでアノテーションされたクラスのすべてのパブリックメソッドが検索されます。
    1)  メソッドが仮想の場合、それが選択されます。
    2)  メソッドが静的で、その最初の引数の型がターゲットクラスと同じかスーパータイプである場合、それが選択されます。
    3)  それ以外の場合、メソッドは無効と見なされます。

マーカーアノテーションは任意のものを指定でき（フレームワークではデフォルトで `amaya-di` モジュールと共に提供される `@Inject` が使用されます）、コンストラクタパラメータとして指定されます。

また、`ReflectSchemaFactory` では、次のインターフェースを実装する必要があるカスタムの「型ハンドラ」を指定できます。

```java
public interface TypeProcessor {
    Type process(Type type, AnnotatedElement element);
}
```

デフォルトで使用される型ハンドラ (`ReflectTypeProcessor`) は、"単純な"型 (`Class` のインスタンス) とジェネリック型の両方を解析します。 処理中に、すべてのワイルドカード型は上限に変換されます。

* `List<? extends String>` =\> `List<String>`
* `List<? super String>` =\> `List<Object>`
* `List<?>` =\> `List<Object>`

型変数は、それらの変換を明確に実行できないため、完全に禁止されています。 たとえば、上限の最初の型に変換する場合 (`<T>` =\> `Object`、`<T extends Number>` =\> `Number`)、`class A<T extends A<T>>` のような循環の場合、それをどのように解消するかを明確に決定することは不可能です。

## ObjectFactoryの生成

### スタブファクトリ

フレームワークのコンテキストにおけるスタブとは、クラスのインジェクションスキーマに従って自動的に生成される `ObjectFactory` の実装を指します。 スタブは `StubFactory` を使用して作成され、その基本インターフェースは次のとおりです。

```java
@FunctionalInterface
public interface StubFactory {
    ObjectFactory create(ClassSchema schema, CacheMode mode);
    
    default ObjectFactory create(ClassSchema schema) {
        return create(schema, CacheMode.NONE);
    }
}
```

すべての実装は、指定されたインジェクションスキーマとキャッシュモードに厳密に従う**必要があります**。 何らかの理由で**すぐに使用できる** `ObjectFactory` インスタンスを作成できない場合、または一部の依存関係をインジェクションできない場合（たとえば、ターゲットフィールドが何らかの理由でプライベートであった場合）、ファクトリは例外をスローする必要があります。 `null` の返却や `provider -> null` のような原始的な実装の返却は許可されません。

### ObjectFactoryのキャッシュ

ほとんどのシナリオでは、構築されたコンテナは読み取り専用モードでのみ使用されます。 したがって、生成された `ObjectFactory` の場合、それらが依存する型のファクトリ実装を直接保存することは理にかなっています。 これにより、`TypeProvider#get(type)` の不要な呼び出しを最小限に抑える（または完全に回避する）ことができ、オーバーヘッドが大幅に削減されます。

キャッシュを明示的に制御するために、次のモードが導入されています。

* `CacheMode.FULL` – `TypeProvider` へのリクエストはまったく行われず、必要なファクトリは内部キャッシュから直接取得されます。
* `CacheMode.PARTIAL` – 内部キャッシュに必要なファクトリがない場合に `TypeProvider` へのリクエストが行われます。リクエストの結果は**保存されません**（コンテナへのスレッドセーフなアクセスのため）。
* `CacheMode.NONE` – `TypeProvider` へのリクエストは常に行われ、内部キャッシュは存在しません。

また、内部キャッシュを持つすべての `ObjectFactory` は、コンテンツを初期化または更新できる `CachedObjectFactory` インターフェースを実装する必要があります。

```java
public interface CachedObjectFactory extends ObjectFactory {
    void set(Type type, ObjectFactory factory);
}
```

# コンテナの構築

コンテナの作成と充填は、直接（必要なすべてのエンティティを実装およびインスタンス化することによって）行うことも、カスタムユーティリティを使用して行うこともできます。

デフォルトでは、フレームワークは「ビルダー」パターンに基づいた構築メカニズムを提供します。 その機能には以下が含まれます。

* [jtype](https://github.com/RomanQed/jtype) を使用したジェネリック型との連携
* カスタム `ObjectFactory` 実装の追加
* クラスによるtransientおよびsingleton型の追加
* スコープ付き型の追加
* 循環および欠落した型に関する依存関係グラフの検証

## 基本的な使用シナリオ

ビルダーのバリエーションは、`ServiceProviderBuilder` インターフェースとその拡張である `ScopedProviderBuilder` によって表現され、スコープ付き機能が追加されます。 それらのインスタンスの取得は、ユーティリティクラス `ProviderBuilders` を使用して行われます。 このクラスは、フレームワークに含まれるすべての実装を作成するための静的メソッドを提供し、本質的にAPIへのエントリポイントとなります。

以下の主要なメソッドが存在します（オーバーロードはJavadocを参照）。

```java
// 指定されたファクトリとチェックでビルダーを作成します
// checks == BuilderChecks.NO_CHECKS の場合、検証メカニズムを持たない実装が使用されます
public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {...}

// スキーマファクトリとデフォルトのキャッシュモード (ProviderBuilders#SCHEMA_FACTORY および #CACHE_MODE) でビルダーを作成します
// checks == BuilderChecks.NO_CHECKS の場合、検証メカニズムを持たない実装が使用されます
public static ServiceProviderBuilder create(int checks) {...}

// 指定されたファクトリ、ProviderBuilders#CACHE_MODE、およびチェックなしでビルダーを作成します
public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory) {}

// ProviderBuilders#SCHEMA_FACTORY、#CACHE_MODE、およびチェックなしでビルダーを作成します。
public static ServiceProviderBuilder create() {...}

// ProviderBuilders#SCHEMA_FACTORY、#CACHE_MODE、および BuilderChecks.VALIDATE_ALL でビルダーを作成します。
public static ServiceProviderBuilder createChecked() {...}

// 指定されたファクトリとチェックでスコープ付きビルダーを作成します
// checks == BuilderChecks.NO_CHECKS の場合、検証メカニズムを持たない実装が使用されます
public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {...}

// スキーマファクトリとデフォルトのキャッシュモード (ProviderBuilders#SCHEMA_FACTORY および #CACHE_MODE) でスコープ付きビルダーを作成します
// checks == BuilderChecks.NO_CHECKS の場合、検証メカニズムを持たない実装が使用されます
public static ScopedProviderBuilder createScoped(int checks) {...}

// 指定されたファクトリ、ProviderBuilders#CACHE_MODE、およびチェックなしでスコープ付きビルダーを作成します
public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory) {...}

// ProviderBuilders#SCHEMA_FACTORY、#CACHE_MODE、およびチェックなしでスコープ付きビルダーを作成します。
public static ScopedProviderBuilder createScoped() {...}

// ProviderBuilders#SCHEMA_FACTORY、#CACHE_MODE、および BuilderChecks.VALIDATE_ALL でスコープ付きビルダーを作成します。
public static ScopedProviderBuilder createCheckedScoped() {...}
```

ビルダーのすべての機能を示す例は[こちら](../../examples/src/main/java/com/github/romanqed/di/examples/AllMethods.java)を参照してください。

## ジェネリック型

コンテナにジェネリック型を登録するには、jtype と、パラメータ化された型の `java.lang.reflect.Type` インターフェースの実装を提供できるサードパーティライブラリの両方を使用できます。 以下に、型の自動キャッチと手動作成を示す例を示します。

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

結果として、以下が出力されます。

```
[1, 2, 3]
[str1, str2, str3]
{1=1, 2=2}
{s2=k2, s1=k1}
```

## 依存関係グラフの検証

検証メカニズムを実装するために、次の規則が使用されます。

* ユーザーが提供するすべての `ObjectFactory` 実装は、無条件に正しいと見なされ、それ自体の依存関係を持たない「ルート」タイプとして扱われます。
* すべてのユーザー定義 `ServiceWrapper` は正しい `ObjectFactory` を返し、エラーを生成しません。
* インジェクションスキームを構築できるすべてのタイプは「弱い」と見なされ、以下がチェックされます。
    * 不足している依存関係の要求
    * 依存関係グラフにおける循環の作成

スコープ付きコンテナの場合、さらに以下が導入されます。

* プロミス型を登録すると、スコープ付きコンテナの作成時に、作業を開始する前に、その型がリポジトリに提供されることが**保証されます**。
* スコープ型は、他のスコープ型および基本型を参照できます。
* 基本型は、他の基本型のみを参照できます。

これに基づいてコンテナを依存関係グラフとして考えると、5つの種類の潜在的なエラーが区別できます。

1.  基本コンテナに、他の基本型が依存する型が欠落している。

例:

```
class A -> class String
class String -> missing
```

2.  スコープ付きコンテナに、他のスコープ付き型が依存する型が欠落している。

例:

```
class ScopedA -> class String
class String -> missing in scoped and base container
```

3)  基本コンテナで、基本型間に循環依存が発生した。

例:

```
class A -> class B
class B -> class C
class C -> class A
```

4)  スコープ付きコンテナで、スコープ付き型間に循環依存が発生した。

5)  スコープ付きコンテナによる型の書き換えにより、コンテナ間に循環依存が発生した。

例:

```
class A -> class B
class B -> inteface IC
interface IC: C
interface IC: ScopedC -> A
```

内蔵の検証メカニズムは、これら5種類すべてのエラーの処理をサポートしています。 例は[こちら](../../examples/src/main/java/com/github/romanqed/di/examples/AllChecks.java)を参照してください。

# ObjectFactoryジェネレータのバリエーション

フレームワークは、`amaya-di-asm` と `amaya-di-reflect` モジュールで提供される `StubFactory` の2つの実装を提供します。

## ASM

ASM実装は、`ObjectFactory` の作成にバイトコードのオンザフライ生成を使用します。 生成されたクラスは、インジェクションターゲットとなるクラス名と使用されるキャッシュモードのエイリアスを組み合わせた名前を受け取ります。 したがって、生成が再度要求された場合、生成は行われず、すでにロードされているクラスが名前で取得されて使用されます。

ASM実装の特長は、準備速度が比較的長く、実行速度が可能な限り高速で、実質的に通常のコードと同じ速度であることです。

バイトコードのキャッシュも実装されており、一度生成されたクラスを再利用できます。 これは、最終的な（そして将来変更されないことが重要な）jarファイルが構築され、本番環境にデプロイされる場合に非常に役立ちます。

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

このコードは、最初の実行後に「cache」ディレクトリを作成し、生成されたバイトコードをそこに保存します。 後続の実行では、生成ではなく直接バイトコードをロードします。 キャッシュが削除された場合、生成が再度行われます。

## Reflect

Reflect実装は、`ObjectFactory` の作成にリフレクティブなメソッド呼び出しとフィールドアクセスを使用します。 要求されたクラスメンバーは `setAccessible(true)` 修飾子のチェックなしでアクセス可能としてマークされ、準備されたファクトリ実装でラップされます。

Reflect実装の特長は、準備速度が比較的速く、実行速度が遅いことで、リフレクティブアクセス機構にかなりのオーバーヘッドを伴います。

## 実装の選択

asmとreflectの実装の選択は、コンテナの使用シナリオの特性に基づいて行うべきです。 アプリケーションでコンテナが次の場合:

* 頻繁に再構築される
* 起動時に一度だけ使用される
* 「ホット」なコードで使用されない（または高いオーバーヘッドが許容される）

その場合、reflect実装を使用すべきです。

一方、コンテナが次の場合:

* 一度だけ構築される
* ホットなコードで常に使用される
* 最小時間で依存関係を提供する必要がある

その場合、asm実装を使用すべきです。

## ベンチマーク

ベンチマークは、Amazon Corretto 11 (`openjdk version "11.0.27" 2025-04-15 LTS`)、Intel i5-9400F、40 GB RAM、Windows 10 22H2を搭載したPC（サーバーではない）で実施されました。 JMHフレームワークのバージョン1.37が使用されました。

依存関係のセットは以下で構成されます。

* Service1
* Service2
* Service3、Service1に依存 (コンストラクタ)
* App、すべての3つに依存 (Service1 – コンストラクタ、Service2 – フィールド、Service3 – メソッド)

コンテナはベンチマークの前に完全に準備され、いかなる対話も行われません。

他の結果と比較するための参照時間を得るために、「手動」でAppを作成し依存関係を注入する簡単なコードが書かれました。

```java
var app = new App(new Service1());
app.s2 = new Service2();
app.setS3(new Service3(new Service1()));
```

また、リポジトリ自体のオーバーヘッド（型検索）に関するデータを取得するためのベンチマークは、2つのバージョンで実施されました。

* Appを手動で作成する「純粋な」実行時間が測定されました。
* Appの作成は `ObjectFactory` にラップされ、コンテナに配置され、コンテナ内の検索呼び出しと `create()` 呼び出しの時間が測定されました。

各CacheModeについても個別に時間が測定され、CacheMode.FULL（デフォルトモード）についても再度測定されました。

以下の結果が得られました。

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

ベンチマークの実行方法は以下の通りです。

```
>./gradlew asm:jmh
>./gradlew reflect:jmh
>./gradlew :jmh
```

# 2.xとの違い

2.xから3.xへの移行で、フレームワークの基本APIは完全に互換性を失いました。 ビルダーAPIは、新しいメソッドを除けば以前と同じです。 これまでamaya-di 2.xを使用しており、3.xに移行したいと考えている方は、以下の主要な変更点に留意してください。

* オブジェクトファクトリとして、`Function0` ではなく `ObjectFactory` が使用されるようになりました。
* `ServiceRepository` インターフェースは `TypeRepository` に名前が変更されました。
* スタブモジュールのニーズのためのローカルメカニズムであった `TypeProvider` インターフェースは完全に削除されました。
* `ManualProviderBuilder` インターフェースは完全に削除されました。
* `StubFactory` のAPIが変更されました: `create(schema, typeProvider)` =\> `create(schema, cacheMode)`
* `StubFactory` は作成時にファクトリのキャッシュを埋めなくなりました。これは自分でやる必要があります。
* `jgraph` モジュールは使用されなくなり、依存関係から除外されました。
* 抽象クラス `AbstractProviderBuilder` は `AbstractServiceProviderBuilder` に置き換えられ、protected APIは完全に変更されました。
* `ServiceWrapper` インターフェースは `Function1` インターフェースを継承しなくなりました。その機能メソッドのシグネチャは `ObjectFactory wrap(ObjectFactory factory)` となりました。
* `LazyProvider` クラスは完全に削除されました。
* 複数の循環が検出された場合にスローされる可能性がある `CyclesFoundException` 例外が導入されました。
* モジュールによる分割が完全に変更されました。
    * `ServiceProvider` および関連する型は、別のモジュール `amaya-di-core` に移動されました。
    * `ClassSchema` および関連する型は、別のモジュール `amaya-di-schema` に移動されました。
    * `StubFactory` および関連する型は、別のモジュール `amaya-di-stub` に移動されました。
    * `amaya-di` モジュールには、これら3つのモジュールが含まれ、`amaya-di-core` モジュールは推移的依存関係として記述されています。
* 新しいスコープメカニズムが導入されました。

# 貢献する

amaya-di の修正、改善、機能拡張を提案していただければ大変嬉しく思います。 詳細な貢献ガイドは[こちら](../contributing/contributing_jp.md)にあります。

# 作成に使用された技術

* [Gradle](https://gradle.org) - 依存関係管理
* [ASM](https://asm.ow2.io) - `ObjectFactory` の生成
* [jeflect-loader](https://github.com/RomanQed/jeflect) - ランタイムでのバイトコードのロード
* [jfunc](https://github.com/RomanQed/jfunc) - 関数型インターフェース、ユーティリティ
* [jtype](https://github.com/RomanQed/jtype) - ジェネリック型操作ユーティリティ

# 作者

* [RomanQed](https://github.com/RomanQed) - *主な作業*

このプロジェクトに貢献してくれた[貢献者リスト](https://github.com/AmayaFramework/amaya-di/contributors)もご覧ください。

# ライセンス

このプロジェクトは Apache License Version 2.0 の下でライセンスされています。詳細は [LICENSE](../../LICENSE) ファイルを参照してください。