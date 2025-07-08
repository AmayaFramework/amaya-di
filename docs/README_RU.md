# amaya-di – современный, легковесный и быстрый DI фреймворк.

[![amaya-di](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di?strategy=releaseProperty&style=for-the-badge&label=amaya-di&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di/)
[![amaya-di-core](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-core?strategy=releaseProperty&style=for-the-badge&label=amaya-di-core&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-core/)
[![amaya-di-schema](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-schema?strategy=releaseProperty&style=for-the-badge&label=amaya-di-schema&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-schema/)
[![amaya-di-stub](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-stub?strategy=releaseProperty&style=for-the-badge&label=amaya-di-stub&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-stub/)
[![amaya-di-asm](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-asm?strategy=releaseProperty&style=for-the-badge&label=amaya-di-asm&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-asm/)
[![amaya-di-reflect](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-reflect?strategy=releaseProperty&style=for-the-badge&label=amaya-di-reflect&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-reflect/)

Amaya DI — современный взгляд на то, каким должен быть DI фреймворк. Он создан с целью 
предоставить разработчикам высокопроизводительный, гибкий и минималистичный IoC-контейнер без
морально устаревших XML-биндингов, множества аннотаций и неявной рефлективной магии.

Фреймворк поддерживает "из коробки" следующие фичи:

* работу с generic-типами (например, `List<String>` и `List<Integer>` будут распознаны как 2 разных типа);
* автоматическую runtime-генерацию фабрик объектов:
  * стратегии new-object-per-request (transient) и same-object-per-request (singleton);
  * создание неименованных scope (а также scoped-transient и scoped-singleton);
* построение и валидация графа зависимостей:
  * поиск отсутствующих типов;
  * обнаружение циклов итеративным алгоритмом Тарьяна;
* полностью декларативный процесс сборки IoC-контейнера с помощью удобного fluent api;
* простая реализация интеграции с любой существующей библиотекой, фреймворком или стандартом.

Быстродействие сгенерированных фабрик объектов составляет:
* при использовании asm-реализации соответствует ручной реализации (13,604 ns vs 13,475 ns);
* при использовании reflect-реализации включает накладные расходы на JNI, но всё ещё достаточно быстро (58,055 ns vs 13,402 ns).

Для более подробной информации см. раздел [Бенчмарки](#бенчмарки).

# Введение

Для установки вам потребуется:

* JVM 11+
* Maven/Gradle

Любая версия JVM ниже 11 не поддерживается. 

Использование других языков, кроме Java, возможно, поскольку фреймворк не реализует compile-only функций, 
опирающихся на Java AST.

## Установка

Для использования фреймворка необходимо установить два модуля: базовый (`io.github.amayaframework:amaya-di`) и 
реализацию stub-фабрики (`:amaya-di-asm` или `:amaya-di-reflect`). 
Подробнее о выборе см. раздел [Выбор реализации](#выбор-реализации).

### Gradle

```Groovy
dependencies {
    implementation group: 'io.github.amayaframework', name: 'amaya-di', version: '3.0.1'
    // ASM stub implementation
    implementation group: 'io.github.amayaframework', name: 'amaya-di-asm', version: '2.0.1'
    // Или reflect stub implementation
    implementation group: 'io.github.amayaframework', name: 'amaya-di-reflect', version: '2.0.0'
}
```

### Maven

```
<dependency>
    <groupId>io.github.amayaframework</groupId>
    <artifactId>amaya-di</artifactId>
    <version>3.0.1</version>
</dependency>
<!--ASM stub implementation-->
<dependency>
    <groupId>io.github.amayaframework</groupId>
    <artifactId>amaya-di-asm</artifactId>
    <version>2.0.1</version>
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
    requires io.github.amayaframework.di.reflect; // Используем рефлективную реализацию
    exports com.github.romanqed.di.examples;
}
```

Теперь соберем простейший контейнер:

```Java
package com.github.romanqed.di.examples;

import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.reflect.ReflectStubFactory;

public final class SimpleHelloWorld {

  public static void main(String[] args) {
    var provider = ProviderBuilders.create(new ReflectStubFactory())
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

Все запускаемые примеры можно найти [здесь](examples).

# Базовые концепции

## Ядро

- ObjectFactory
- TypeProvider
- TypeRepository
- ServiceProvider

## Схемы

- Schema (+ все вариации)
- SchemaFactory 
- ReflectSchemaFactory
- TypeProcessor

## Генерация ObjectFactory

- StubFactory
- CachedObjectFactory + CacheMode

# Сборка контейнера

## Generic-типы

## Валидация графа зависимостей

## ServiceProviderBuilder

## ScopedProviderBuilder

# Варианты генераторов ObjectFactory

## ASM

\+ кэширование

## Reflect

## Выбор реализации

## Бенчмарки

# Отличия от предыдущего релиза

# Внести вклад

# Создано с помощью

# Авторы

# Лицензия