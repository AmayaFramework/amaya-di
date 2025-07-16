# amaya-di – современный, легковесный и быстрый DI фреймворк.

[![amaya-di](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di?strategy=releaseProperty&style=for-the-badge&label=amaya-di&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di/)
[![amaya-di-core](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-core?strategy=releaseProperty&style=for-the-badge&label=amaya-di-core&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-core/)
[![amaya-di-schema](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-schema?strategy=releaseProperty&style=for-the-badge&label=amaya-di-schema&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-schema/)
[![amaya-di-stub](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-stub?strategy=releaseProperty&style=for-the-badge&label=amaya-di-stub&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-stub/)
[![amaya-di-asm](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-asm?strategy=releaseProperty&style=for-the-badge&label=amaya-di-asm&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-asm/)
[![amaya-di-reflect](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-reflect?strategy=releaseProperty&style=for-the-badge&label=amaya-di-reflect&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-reflect/)

Amaya DI — современный взгляд на то, каким должен быть DI фреймворк. Он создан с целью 
предоставить разработчикам высокопроизводительный, гибкий и минималистичный IoC-контейнер без
устаревших XML-конфигураций, множества аннотаций и скрытой магии, основанной на рефлексии.

Фреймворк поддерживает "из коробки" следующие фичи:

* работу с generic-типами (например, `List<String>` и `List<Integer>` будут распознаны как 2 разных типа);
* создание неименованных scope;
* автоматическую runtime-генерацию фабрик объектов:
  * стратегии new-object-per-request (transient) и same-object-per-request (singleton);
  * scoped-стратегии transient и singleton;
* построение и валидация графа зависимостей:
  * поиск отсутствующих типов;
  * обнаружение циклов итеративным алгоритмом Тарьяна;
* полностью декларативный процесс сборки IoC-контейнера с помощью удобного fluent api;
* простая реализация интеграции с любой существующей библиотекой, фреймворком или стандартом.

Быстродействие сгенерированных фабрик объектов составляет:
* при использовании asm-реализации соответствует ручной реализации (13,604 ns vs 13,475 ns);
* при использовании reflect-реализации включает накладные расходы на JNI, но всё ещё достаточно быстро (58,055 ns vs 13,402 ns).

Для более подробной информации см. раздел [бенчмарки](#бенчмарки).

# Введение

Для установки вам потребуется:

* JVM 11+
* Maven/Gradle

Любая версия JVM ниже 11 не поддерживается. 

Использование других языков, кроме Java, возможно, поскольку фреймворк не имеет compile-time плагинов и 
не анализирует Java AST.

## Установка

Для использования фреймворка необходимо установить два модуля: базовый (`io.github.amayaframework:amaya-di`) и 
реализацию stub-фабрики (`:amaya-di-asm` или `:amaya-di-reflect`). 
Подробнее о выборе см. раздел [выбор реализации](#выбор-реализации).

### Gradle

```Groovy
dependencies {
    implementation group: 'io.github.amayaframework', name: 'amaya-di', version: '3.0.4'
    // ASM stub implementation
    implementation group: 'io.github.amayaframework', name: 'amaya-di-asm', version: '2.0.2'
    // Или reflect stub implementation
    implementation group: 'io.github.amayaframework', name: 'amaya-di-reflect', version: '2.0.0'
}
```

### Maven

```
<dependency>
    <groupId>io.github.amayaframework</groupId>
    <artifactId>amaya-di</artifactId>
    <version>3.0.4</version>
</dependency>
<!--ASM stub implementation-->
<dependency>
    <groupId>io.github.amayaframework</groupId>
    <artifactId>amaya-di-asm</artifactId>
    <version>2.0.2</version>
</dependency>
<!--Или reflect stub implementation-->
<dependency>
    <groupId>io.github.amayaframework</groupId>
    <artifactId>amaya-di-reflect</artifactId>
    <version>2.0.0</version>
</dependency>
```

## Hello, world!

Сборка контейнера выполняется с помощью билдера (`ServiceProviderBuilder` или `ScopedProviderBuilder`). 
Процесс сборки можно разделить на 3 этапа:

* получение и конфигурация объекта билдера;
* объявление требуемых типов;
* вызов метода build().

Рассмотрим базовые сценарии использования.

Сначала, в случае использования модулей укажем зависимости для `module-info.java`:

```Java
module com.github.romanqed.di.examples {
    requires io.github.amayaframework.di; // Основной модуль
    exports com.github.romanqed.di.examples;
}
```

Теперь соберем простейший контейнер:

```Java
package com.github.romanqed.di.examples;

import io.github.amayaframework.di.ProviderBuilders;

public final class SimpleHelloWorld {

  public static void main(String[] args) {
    var provider = ProviderBuilders.create()
            .addInstance("Hello, world!")
            .build();
    System.out.println(provider.get(String.class));
  }
}
```

После запуска этот код выведет `Hello, world!`.

Рассмотрим более сложный сценарий. Допустим, у нас есть некоторый абстрактный сервис `IGreeter`:

```Java
public interface IGreeter { 
    String sayHello(String name);
}
```

И две его реализации, одна для глобального контекста, вторая для scoped:

```Java
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

Добавим в `module-info.java` рефлективный генератор фабрик объектов:

```java
module com.github.romanqed.di.examples {
    requires io.github.amayaframework.di; // Основной модуль
    requires io.github.amayaframework.di.reflect; // Используем рефлективную реализацию
    exports com.github.romanqed.di.examples;
}
```

Соберём контейнер:

```Java
var provider = ProviderBuilders.createScoped(new ReflectStubFactory())
        .addSingleton(IGreeter.class, GlobalGreeter.class)
        .addScoped(String.class)
        .addScopedSingleton(IGreeter.class, ScopedGreeter.class)
        .build();
```

И создадим два scope'а:
```Java
var scope1 = provider.createScoped();
scope1.repository().put("Scope One"); 
var scope2 = provider.createScoped(); 
scope2.repository().put("Scope Two");
```

И, наконец, выведем результаты:

```Java
System.out.println(provider.get(IGreeter.class).sayHello("Roman"));
System.out.println(scope1.get(IGreeter.class).sayHello("Roman")); 
System.out.println(scope2.get(IGreeter.class).sayHello("Roman"));
```

В консоли окажется:
```
Hello, Roman!
Hello from scope 'Scope One', Roman!
Hello from scope 'Scope Two', Roman!
```

Полный код выглядит следующим образом:

```Java
package com.github.romanqed.di.examples;

import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.reflect.ReflectStubFactory;

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

Все запускаемые примеры можно найти [здесь](../examples).

# Базовые концепции

## Ядро

### ObjectFactory и TypeProvider

Основным механизмом, лежащим в основе контейнера, являются интерфейсы `ObjectFactory` и `TypeProvider`. Они имеют
следующий вид:

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

`ObjectFactory` выполняет создание инстанса типа, зависимости которого поставляет `TypeProvider`. Данный дизайн удобен
по двум причинам:

1) фабрика объектов не имеет жесткой зависимости от поставщика типов и может оборачивать и модифицировать его перед
передачей нижележащим фабрикам, что позволяет реализовать любые сценарии;
2) получение фабрики напрямую вместо созданного инстанса позволяет избежать лишних лукапов и кэшировать фабрику,
не теряя остальных преимуществ.

Что касается на первый взгляд бесполезного метода `TypeProvider#canProvide`, он нужен в ситуациях, когда мы хотим
**однозначно** убедится в способности провайдера предоставить нам фабрику объектов. Потому что сравнение с `null` 
не сработает в тех случаях, когда реализация контейнера для каких-либо целей предоставляет обёртки над фабриками. То
есть, допустим, `get(Type.class)` будет возвращать реализацию `ObjectFactory`, которая будет всегда возвращать `null`.

Именно для предотвращения таких ситуаций существует однозначно определимый `canProvide`. Если он вернул `true` - значит
`get` вернёт **не `null`** инстанс `ObjectFactory`. Причём с гарантией (с точки зрения контейнера!), что это 
изначально предоставленная пользователем фабрика, а не заглушка/временная обёртка.

Без автоматической генерации `ObjectFactory` пример `ComplexHelloWorld` будет 
выглядеть следующим образом (`ManualHelloWorld`):

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

Причём `addScoped(String.class)`, бывший в `ComplexHelloWorld`, здесь можно убрать, поскольку валидация предоставленных
пользователем фабрик не выполняется. Такие зависимости (в том числе instance и `Function0` версии) считаются корневыми,
поскольку для них невозможно определить множество зависимых типов за конечное время (т.е. без их запуска).

### Расширение TypeProvider: TypeRepository

`TypeRepository` расширяет `TypeProvider`, превращая его в полноценное CRUD-хранилище типов. Он выглядит следующим
образом:

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

Метод `put(type, factory)` заносит в репозиторий запись вида `тип->фабрика`. Если такая для переданного типа уже
существует, фабрика перезаписывается указанным инстансом.

Методы `put(type, provider)`, `put(type, instance)`, `put(instance)` условно (зависит от реализации репозитория) 
являются аналогами `put(type, p -> provider.invoke())`, `put(type, p -> instance)` 
и `put(instance.getClass(), p -> instance)` соответственно.

Метод `remove(type)` удаляет из репозитория запись о типе и возвращает хранимую для него `ObjectFactory` или `null`,
если такого типа не было.

Обе вариации `putAll(...)` выполняют копирование записей вида `тип->фабрика` из переданного источника в репозиторий.

Метод `clear()` полностью очищает репозиторий, удаляя все его содержимое.

Метод `forEach(BiConsumer)` применяет переданный инстанс `BiConsumer` для каждой записи `тип->фабрика`, а методы 
интерфейса `Iterable<Type>` ведут себя так же, как если бы это был изменяемый `Set<Type>`.

На основе `TypeRepository` строятся все разновидности контейнеров, и через него осуществляется управление 
содержимым контейнера. Обратите внимание – **нет никакой гарантии**, что переданный в `ObjectFactory#create()` инстанс
`TypeProvider` является инстансом `TypeRepository`. В общем случае попытка получить доступ к репозиторию внутри фабрики
некорректна с точки зрения дизайна фреймворка. Подобный функционал никогда не будет реализован.

### Универсальный контейнер: ServiceProvider

Интерфейс `ServiceProvider` описывает абстрактный di-контейнер, предоставляющий интерфейс как для получения готовых
реализаций запрошенных типов, так и для управления содержимым. Он выглядит так:
```java
public interface ServiceProvider {
    TypeRepository repository();
    
    ServiceProvider createScoped();
    
    <T> T get(Type type);
    
    <T> T get(Class<T> type);
    
    <T> T get(JType<T> type);
}
```

Методы `get(type)` выполняют поиск `ObjectFactory` для запрошенного типа и:

1) если таковая существует, создают инстанс для запрошенного типа;
2) иначе возвращают `null`.

Метод `repository()` возвращает изменяемый инстанс `TypeRepository`, используемый данным контейнером. 

Метод `createScoped()` создаёт новый контейнер, использующий при поиске типов fallthrough в родительский контейнер,
из которого был вызван метод. То есть, если при запросе типа он не был найден в этом контейнере, запрос направляется в 
нижележащий. Скопинг можно выполнять бесконечно, создавая связанную цепочку контейнеров.

### Остальные утилиты ядра

Кроме вышеописанных интерфейсов ядро включает:

* абстрактную реализацию `ServiceProvider` (`AbstractServiceProvider`), содержащую поле `repository` и все методы,
кроме `createScoped()`;
* реализацию `TypeRepository` на основе `Map<Type, ObjectFactory>` (`HashTypeRepository`);
* потокобезопасную ленивую обёртку над `ObjectFactory`, что используется для singleton-политик (`LazyObjectFactory`);
* универсальную scoped-реализацию `TypeRepository`, соединяющую между собой любую пару 
scoped- и parent- репозиториев (`ScopedTypeRepository`).

Для подробностей см. javadoc.

## Схемы

### Понятие схемы внедрения

Для обеспечения полной независимости анализа рефлективной информации от способа её получения фреймворк вводит понятие 
`схемы внедрения`. Оно представляет собой дескриптор, содержащий следующую информацию:

* объект или "цель" внедрения – член класса или сам класс;
* множество типов, от которых зависит цель внедрения;
* маппинг типов для вызываемой цели (метода или конструктора), сопоставляющий номеру параметра соответствующий тип;
* одиночный тип для поля.

Схема внедрения класса также содержит схему конструктора, множество схем методов и множество схем полей. Множество
типов, от которых зависит класс, при этом содержит объединение множеств типов всех схем членов класса.

Базовый интерфейс, который реализует каждая `...Schema`, выглядит следующим образом:

```java
public interface Schema<T> {
    T getTarget();
    
    Set<Type> getTypes();
}
```

Для использования с вызываемыми целями его расширяет `ExecutableSchema`:

```java
public interface ExecutableSchema<T extends Executable> extends Schema<T> {
    Type[] getMapping();
}
```

Всего существует 4 реализации:

* `FieldSchema` – схема внедрения для поля;
* `ConstructorSchema` – схема внедрения для конструктора, реализует интерфейс `ExecutableSchema`; допускается только
одна на класс;
* `MethodSchema` – схема внедрения для метода, реализует интерфейс `ExecutableSchema`;
* `ClassSchema` – схема внедрения для всего класса.

### Фабрика схем

Для генерации схем для указанного класса используются фабрики схем. Каждая такая фабрика должна реализовывать интерфейс
`SchemaFactory`:

```java
public interface SchemaFactory {
    ClassSchema create(Class<?> clazz);
}
```

По умолчанию поставляется реализация, выполняющая поиск целей для внедрения среди членов класса (`ReflectSchemaFactory`). 
Применяется следующая логика:

1) просматриваются все публичные конструкторы класса (`Class#getConstructors()`):
   1) если такой конструктор 1, выбирается он;
   2) если их несколько, выбирается аннотированной маркерной аннотацией;
   3) иначе класс считается недопустимым;
2) ищутся все публичные виртуальные поля класса, аннотированные маркерной аннотацией;
3) ищутся все публичные методы класса, аннотированные маркерной аннотацией:
   1) если метод виртуальный, он выбирается;
   2) если метод статичный, а тип его первого аргумента равен или является супер-типом целевого класса, он выбирается;
   3) иначе метод считается недопустимым.

При этом маркерная аннотация может быть любой (в фреймворке по умолчанию используется `@Inject`, которая поставляется
вместе с модулем `amaya-di`) и указывается как параметр конструктора.

Также `ReflectSchemaFactory` позволяет указать пользовательский "обработчик типов", который должен реализовывать 
следующий интерфейс:

```java
public interface TypeProcessor {
    Type process(Type type, AnnotatedElement element);
}
```

Обработчик типов, используемый по умолчанию (`ReflectTypeProcessor`), выполняет разбор как "простых" типов 
(инстансы `Class`), так и generic. Причём при обработке все wildcard-типы приводятся к upper bounds:

* `List<? extends String>` => `List<String>`; 
* `List<? super String>` => `List<Object>`;
* `List<?>` => `List<Object>`.

Type variable полностью запрещены, поскольку невозможно однозначно выполнить их приведение. Например, если приводить их
к первому типу в upper bounds (`<T>` => `Object`, `<T extends Number>` => `Number`), то в случае цикла 
`class A<T extends A<T>>` невозможно однозначно определить, как его разорвать.

## Генерация ObjectFactory

### Фабрика stub'ов

Stub'ами в контексте фреймворка называются реализации `ObjectFactory`, автоматически генерируемые в соответствии со
схемой внедрения класса. Stub'ы создаются с помощью `StubFactory`, чей базовый интерфейс выглядит следующим образом:

```java
@FunctionalInterface
public interface StubFactory {
    ObjectFactory create(ClassSchema schema, CacheMode mode);
    
    default ObjectFactory create(ClassSchema schema) {
        return create(schema, CacheMode.NONE);
    }
}
```

Все реализации **обязаны** строго следовать указанной схеме внедрения и режиму кэширования. Если по каким-либо причинам
создать **готовый к использованию** инстанс `ObjectFactory` невозможно, или же невозможно внедрение части зависимостей (
например, целевое поле каким-то образом оказалось приватным), фабрика должна выбросить исключение. Возврат `null` и/или
возврат примитивной реализации вроде `provider -> null` не допускаются.

### Кэширование ObjectFactory

В большинстве сценариев собранный контейнер используется только в режиме read-only. Следовательно, для сгенерированных
`ObjectFactory` в таких случаях имеет смысл напрямую сохранить реализации фабрик для тех типов, от которых они зависят.
Это позволит минимизировать (или вовсе избежать) лишние вызовы `TypeProvider#get(type)`, что значительно сокращает
накладные расходы.

Для явного управления кэшированием вводятся следующие режимы:

* `CacheMode.FULL` – запрос в `TypeProvider` не осуществляется вообще, необходимые фабрики берутся напрямую из
внутренного кэша;
* `CacheMode.PARTIAL` – запрос в `TypeProvider` осуществляется в том случае, если во внутреннем кэше нет нужной фабрики;
результат запроса **не сохраняется** (для потокобезопасного доступа к контейнеру);
* `CacheMode.NONE` – запрос в `TypeProvider` осуществляется всегда, внутреннего кэша не существует.

Также все `ObjectFactory` с внутренним кэшем должны реализовывать интерфейс `CachedObjectFactory`, позволяющий 
инициализировать или обновить содержимое:

```java
public interface CachedObjectFactory extends ObjectFactory {
    void set(Type type, ObjectFactory factory);
}
```

# Сборка контейнера

Создание и заполнение контейнера может производиться как напрямую (путём реализации и инстанциации всех необходимых 
сущностей), так и с помощью пользовательских утилит. 

По умолчанию фреймворк предоставляет механизм сборки, основанный на паттерне "builder". Его функционал включает:

* работу с generic-типами с помощью [jtype](https://github.com/RomanQed/jtype);
* добавление пользовательских реализаций `ObjectFactory`;
* добавление transient и singleton типов по их классу;
* добавление scoped типов;
* валидацию графа зависимостей на предмет циклов и отсутствующих типов.

## Базовые сценарии использования

Варианты билдеров представлены интерфейсами `ServiceProviderBuilder` и его расширением `ScopedProviderBuilder`, которое
добавляет scoped-фичи. Получение их инстансов предусмотрено с помощью утилитного класса `ProviderBuilders`. Он
предоставляет статические методы для создания всех реализаций, включенных в состав фреймворка, и, по сути, является 
точкой входа в API.

Существуют следующие основные методы (перегрузки см. в javadoc):

```java
// Создаёт билдер с указанными фабриками и проверками
// Если checks == BuilderChecks.NO_CHECKS, используется реализация, не имеющая механизмов валидации
public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {...}

// Создаёт билдер с фабрикой схем внедрения и cache mode по умолчанию (ProviderBuilders#SCHEMA_FACTORY и #CACHE_MODE)
// Если checks == BuilderChecks.NO_CHECKS, используется реализация, не имеющая механизмов валидации 
public static ServiceProviderBuilder create(int checks) {...}

// Создаёт билдер с указанными фабриками, ProviderBuilders#CACHE_MODE и без проверок
public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory) {}

// Создаёт билдер с ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE и без проверок.
public static ServiceProviderBuilder create() {...}

// Создаёт билдер с ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE и BuilderChecks.VALIDATE_ALL.
public static ServiceProviderBuilder createChecked() {...}

// Создаёт scoped-билдер с указанными фабриками и проверками
// Если checks == BuilderChecks.NO_CHECKS, используется реализация, не имеющая механизмов валидации
public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {...}

// Создаёт scoped-билдер с фабрикой схем внедрения и cache mode по умолчанию (ProviderBuilders#SCHEMA_FACTORY и #CACHE_MODE)
// Если checks == BuilderChecks.NO_CHECKS, используется реализация, не имеющая механизмов валидации 
public static ScopedProviderBuilder createScoped(int checks) {...}

// Создаёт scoped-билдер с указанными фабриками, ProviderBuilders#CACHE_MODE и без проверок
public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory) {...}

// Создаёт scoped-билдер с ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE и без проверок.
public static ScopedProviderBuilder createScoped() {...}

// Создаёт scoped-билдер с ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE и BuilderChecks.VALIDATE_ALL.
public static ScopedProviderBuilder createCheckedScoped() {...}
```

Пример, показывающий все возможности билдеров, см. [тут](../examples/src/main/java/com/github/romanqed/di/examples/AllMethods.java).

## Generic-типы

Для регистрации в контейнере generic-типа можно использовать как jtype, так и сторонние библиотеки, способные
предоставлять реализации интерфейса `java.lang.reflect.Type` для параметризованных типов. Ниже показан пример, в 
котором показаны автозахват типа и его ручное создание:

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

В результате будет выведено:

```
[1, 2, 3]
[str1, str2, str3]
{1=1, 2=2}
{s2=k2, s1=k1}
```

## Валидация графа зависимостей



# Варианты генераторов ObjectFactory

## ASM

\+ кэширование

## Reflect

## Выбор реализации

## Бенчмарки

# Отличия от предыдущего релиза

# Внести вклад

# Создано с помощью

* [Gradle](https://gradle.org) - Управление зависимостями
* [ASM](https://asm.ow2.io) - Генерация `ObjectFactory`
* [jeflect-loader](https://github.com/RomanQed/jeflect) - Загрузка байт-кода в рантайме
* [jfunc](https://github.com/RomanQed/jfunc) - Функциональные интерфейсы, утилиты
* [jtype](https://github.com/RomanQed/jtype) - Утилиты для работы с дженериками

# Авторы

* [RomanQed](https://github.com/RomanQed) - *Основная работа*

Загляните также в список [участников](https://github.com/AmayaFramework/amaya-di/contributors), которые внесли вклад
в этот проект.

# Лицензия

Этот проект лицензирован под Apache License Version 2.0 - см. [LICENSE](../LICENSE) файл для подробностей.
