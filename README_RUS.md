# amaya-di [![maven-central](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di?color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di/)

Фреймворк, отвечающий за контроль и автоматизацию процесса внедрения зависимостей.

[English version](README.md)

## Философия

Учитывая особенности как существующих реализаций, так и JVM и языка Java в целом, фреймворк создавался в строгом
соответствии со следующими принципами:

* Поддержка только новых версий java (11+)
* Полный отказ от рефлективных вызовов в процессе инстанцирования объектов
* Минимально возможный размер фреймворка
* Минимально возможный набор зависимостей
* Отсутствие транзитивных зависимостей (т.е. получая фреймворк, вы получаете исключительно его и
  несколько служебных библиотек, необходимых для его функционирования)
* Отсутствие зависимостей за пределами jdk (никаких плагинов, утилит и скриптов)
* Отсутствие встроенных интеграций
* Максимально возможная гибкость, позволяющая адаптировать фреймворк для поддержки спецификаций любого формата
* Избегание принятия сложных решений (если что-то нельзя однозначно определить за конечное время,
  это не будет определено)

## Начало работы

Чтобы установить фреймворк, понадобятся:

* java 11+
* Maven/Gradle

## Установка

### Gradle

```Groovy
dependencies {
    implementation group: 'io.github.amayaframework', name: 'amaya-di', version: 'LATEST'
}
```

### Maven

```
<dependency>
    <groupId>io.github.amayaframework</groupId>
    <artifactId>amaya-di</artifactId>
    <version>LATEST</version>
</dependency>
```

## Примеры использования

Важно: порядок передачи сервисов в сборщик значения НЕ имеет, никакие изменения и никакие исключения не возникнут,
пока не вызван метод ServiceProviderBuilder#build.

### Hello, world!

```Java
import io.github.amayaframework.di.Builders;

public class Main {
    public static void main(String[] args) {
        var provider = Builders
                .createChecked()
                .addService(String.class, () -> "Hello, world!")
                .build();
        System.out.println(provider.instantiate(String.class));
    }
}
```

### Два сервиса и зависимый класс

```Java
import io.github.amayaframework.di.Builders;

public class Main {
    public static void main(String[] args) {
        var provider = Builders
                .createChecked()
                .addTransient(Service1.class)
                .addSingleton(Service2.class)
                .addTransient(App.class)
                .build();
        System.out.println(provider.instantiate(App.class));
        System.out.println(provider.instantiate(App.class));
    }

    public static final class Service1 {
        @Override
        public String toString() {
            return "Service1, " + hashCode();
        }
    }

    public static final class Service2 {
        @Override
        public String toString() {
            return "Service2, " + hashCode();
        }
    }

    public static final class App {
        final Service1 s1;
        final Service2 s2;

        public App(Service1 s1, Service2 s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        @Override
        public String toString() {
            return "hash=" + hashCode() + "\ns1=" + s1 + "\ns2=" + s2;
        }
    }
}
```

Этот код выведет:

```
hash=1852584274
s1=Service1, 280744458
s2=Service2, 377478451
hash=394714818
s1=Service1, 1952779858
s2=Service2, 377478451
```

### Зависимости с дженериками

```Java
import io.github.amayaframework.di.Builders;
import com.github.romanqed.jtype.JType;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var provider = Builders
                .createChecked()
                .addService(new JType<List<String>>(){}, () -> List.of("Hi", "World"))
                .addService(new JType<List<Integer>>(){}, () -> List.of(1, 2, 3))
                .addTransient(App.class)
                .build();
        System.out.println(provider.instantiate(App.class));
    }
  
    public static final class App {
        final List<String> s1;
        final List<Integer> s2;
    
        public App(List<String> s1, List<Integer> s2) {
            this.s1 = s1;
            this.s2 = s2;
        }
        
        @Override
        public String toString() {
            return "hash=" + hashCode() + "\ns1=" + s1 + "\ns2=" + s2;
        }
    }
}
```

Вывод:

```
hash=1354011814
s1=[Hi, World]
s2=[1, 2, 3]
```

### Поля, методы, несколько конструкторов

```Java
import io.github.amayaframework.di.Builders;

public class Main {
    public static void main(String[] args) {
        var provider = Builders
                .createChecked()
                .addTransient(Service1.class)
                .addTransient(Service2.class)
                .addTransient(Service3.class)
                .addTransient(Service4.class)
                .addTransient(App.class)
                .build();
        System.out.println(provider.instantiate(App.class).s1);
    }

    public static final class Service1 {
    }

    public static final class Service2 {
    }

    public static final class Service3 {
    }

    public static final class Service4 {
    }

    public static final class App {
        @Inject
        public Service1 s1;

        public App() {
            System.out.println("Empty ctor");
        }

        @Inject
        public App(Service2 s2) {
            System.out.println("Service2=" + s2);
        }

        @Inject
        public void setService3(Service3 s3) {
            System.out.println("Service3=" + s3);
        }

        @Inject
        public static void setService4(App app, Service4 s4) {
            System.out.println("App=" + app + ", Service4=" + s4);
        }
    }
}

```

Вывод:

```
Service2=io.github.amayaframework.di.Main$Service2@9660f4e
App=io.github.amayaframework.di.Main$App@396f6598, Service4=io.github.amayaframework.di.Main$Service4@394e1a0f
Service3=io.github.amayaframework.di.Main$Service3@27a5f880
io.github.amayaframework.di.Main$Service1@1d29cf23
```

### Потерянная зависимость

```Java
import io.github.amayaframework.di.Builders;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            var provider = Builders
                    .createChecked()
                    .addTransient(App.class)
                    .build();
            System.out.println(provider.instantiate(App.class));
        } catch (TypeNotFoundException e) {
            System.out.println(e.getType() + " not found");
        }
    }

    public static final class App {
        final List<String> s1;
        final List<Integer> s2;

        public App(List<String> s1, List<Integer> s2) {
            this.s1 = s1;
            this.s2 = s2;
        }

        @Override
        public String toString() {
            return "hash=" + hashCode() + "\ns1=" + s1 + "\ns2=" + s2;
        }
    }
}
```

Вывод:

```
java.util.List<java.lang.String> not found
```

### Циклическая зависимость

```Java
import io.github.amayaframework.di.Builders;

public class Main {
    public static void main(String[] args) {
        try {
            var provider = Builders
                    .createChecked()
                    .addTransient(Service.class)
                    .addTransient(App.class)
                    .build();
            System.out.println(provider.instantiate(App.class));
        } catch (CycleFoundException e) {
            System.out.println("Found cycle: " + e.getCycle());
        }
    }

    public static final class Service {
        public Service(App app) {
        }
    }

    public static final class App {
        public App(Service s) {
        }
    }
}
```

Вывод:

```
Found cycle: [class io.github.amayaframework.di.Main$App, class io.github.amayaframework.di.Main$Service]
```

## Бенчмарк

См. [jmh бенчмарк](src/jmh/java/io/github/amayaframework/di/ServiceProviderBenchmark.java).
Запуск на вашей машине:

```
gradle jmh
```

Результаты:

```
# JMH version: 1.36
# VM version: JDK 11.0.22, OpenJDK 64-Bit Server VM, 11.0.22+7-LTS
# VM invoker: ~/.jdks/corretto-11.0.22/bin/java.exe

Benchmark                                      Mode  Cnt   Score   Error  Units
ServiceProviderBenchmark.benchAmayaInjection   avgt   25  17,586 ± 0,240  ns/op
ServiceProviderBenchmark.benchManualInjection  avgt   25  11,586 ± 0,085  ns/op
```

## Используемые зависимости

* [Gradle](https://gradle.org) - Управление зависимостями
* [ASM](https://asm.ow2.io) - Генерация прокси-классов
* [jeflect](https://github.com/RomanQed/jeflect) - Загрузка классов из байт-кода, утилиты для ASM
* [jfunc](https://github.com/RomanQed/jfunc) - "Ленивые" контейнеры, функциональные интерфейсы, утилиты
* [jtype](https://github.com/RomanQed/jtype) - Утилиты для работы с дженериками

## Авторы

* [RomanQed](https://github.com/RomanQed) - *Основная работа*

Загляните также в список [участников](https://github.com/AmayaFramework/amaya-di/contributors), которые внесли вклад
в этот проект.

## Лицензия

Этот проект лицензирован под Apache License Version 2.0 - см. [LICENSE](LICENSE) файл для подробностей
