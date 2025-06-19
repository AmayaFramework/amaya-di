# amaya-di [![maven-central](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di?color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di)

A framework responsible for monitoring and automating the dependency injection process.
<br>
It is only **1.27 times** (15.829 vs 12.427) slower than manual injection! (see benchmark section).

[Russian version](README_RUS.md)

## Philosophy

Taking into account the features of both existing implementations, and the JVM and the Java language in general,
the framework was created in strict accordance with the following principles:

* Support only for new versions of java (11+)
* The minimum possible size of the framework
* The minimum possible set of dependencies
* No transitive dependencies (i.e., when you get a framework, you get only it and
  several service libraries necessary for its operation)
* No dependencies outside the jdk (no plugins, utilities, or scripts)
* No built-in integrations
* Maximum possible flexibility to adapt the framework to support specifications of any format
* Avoiding making difficult decisions (if something cannot be unambiguously determined in a finite time,
  it will not be determined)

## Getting Started

To install it, you will need:

* java 11+
* Maven/Gradle

## Installing

### Gradle dependency

```Groovy
dependencies {
    implementation group: 'io.github.amayaframework', name: 'amaya-di', version: '2.3.2'
    // ASM stub implementation
    implementation group: 'io.github.amayaframework', name: 'amaya-di-asm', version: '1.0.1'
    // Or reflect stub implementation
    implementation group: 'io.github.amayaframework', name: 'amaya-di-reflect', version: '1.0.2'
}
```

### Maven dependency

```
<dependency>
    <groupId>io.github.amayaframework</groupId>
    <artifactId>amaya-di</artifactId>
    <version>2.3.2</version>
</dependency>
<!--ASM stub implementation-->
<dependency>
    <groupId>io.github.amayaframework</groupId>
    <artifactId>amaya-di-asm</artifactId>
    <version>1.0.1</version>
</dependency>
<!--Reflect stub implementation-->
<dependency>
    <groupId>io.github.amayaframework</groupId>
    <artifactId>amaya-di-reflect</artifactId>
    <version>1.0.2</version>
</dependency>
```

## ASM or reflect stub factory

The choice between asm and reflect implementations depends only on how DI is used.
1) If you create a container once (or many times) and request service implementations ONCE, 
then you should use amaya-di-reflect.
2) If you request service implementations MANY TIMES (that is, constantly throughout the lifetime of the application), 
then you should use amaya-di-asm.

Or you can always create your own implementation :)

## Benchmark

The time spent requesting the implementation of a service 
with multiple dependencies from already built container is measured.

See [asm benchmark](amaya-di-asm/src/jmh/java/io/github/amayaframework/di/asm/ServiceProviderBenchmark.java) and
[reflect benchmark](amaya-di-reflect/src/jmh/java/io/github/amayaframework/di/asm/ServiceProviderBenchmark.java) and
Running on your machine:

```
gradle amaya-di-asm:jmh
gradle amaya-di-reflect:jmh
```

Results:
<br>

ASM
```
# JMH version: 1.36
# VM version: JDK 11.0.22, OpenJDK 64-Bit Server VM, 11.0.22+7-LTS
# VM invoker: ~/.jdks/corretto-11.0.22/bin/java.exe

Benchmark                                      Mode  Cnt   Score   Error  Units
ServiceProviderBenchmark.benchAmayaInjection   avgt   25  15,829 ± 1,685  ns/op
ServiceProviderBenchmark.benchManualInjection  avgt   25  12,427 ± 0,930  ns/op
```

Reflect
```
# JMH version: 1.36
# VM version: JDK 11.0.22, OpenJDK 64-Bit Server VM, 11.0.22+7-LTS
# VM invoker: ~/.jdks/corretto-11.0.22/bin/java.exe

Benchmark                                      Mode  Cnt   Score   Error  Units
ServiceProviderBenchmark.benchAmayaInjection   avgt   25  59,779 ± 1,011  ns/op
ServiceProviderBenchmark.benchManualInjection  avgt   25  11,582 ± 0,122  ns/op
```

## Usage example

Important: the order of transferring services to the builder does NOT matter, no changes and no exceptions will occur,
the ServiceProviderBuilder#build() method has not been called yet. 

The benchAmayaInjection() method measures the time of the request from the container, the benchManualInjection() 
method measures the time of the "build" of an instance of the same service completely manually.

### Hello, world!

```Java
import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.asm.BytecodeStubFactory;

public class Main {
    public static void main(String[] args) {
        var provider = ProviderBuilders
                .createChecked(new BytecodeStubFactory())
                .addInstance("Hello, world!")
                .build();
        System.out.println(provider.get(String.class));
    }
}
```

### Two services and a dependent class

```Java
import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.asm.BytecodeStubFactory;

public class Main {
    public static void main(String[] args) {
        var provider = ProviderBuilders
                .createChecked(new BytecodeStubFactory())
                .addTransient(Service1.class)
                .addSingleton(Service2.class)
                .addTransient(App.class)
                .build();
        System.out.println(provider.get(App.class));
        System.out.println(provider.get(App.class));
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

This code will output:

```
hash=1852584274
s1=Service1, 280744458
s2=Service2, 377478451
hash=394714818
s1=Service1, 1952779858
s2=Service2, 377478451
```

### Generics

```Java
import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.asm.BytecodeStubFactory;
import com.github.romanqed.jtype.JType;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var provider = ProviderBuilders
                .createChecked(new BytecodeStubFactory())
                .addInstance(new JType<>(){}, List.of("Hi", "World"))
                .addInstance(new JType<>(){}, List.of(1, 2, 3))
                .addTransient(App.class)
                .build();
        System.out.println(provider.get(App.class));
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

Output:

```
hash=1354011814
s1=[Hi, World]
s2=[1, 2, 3]
```

### Fields, methods, multiple constructors

```Java
import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.asm.BytecodeStubFactory;

public class Main {
    public static void main(String[] args) {
        var provider = ProviderBuilders
                .createChecked(new BytecodeStubFactory())
                .addTransient(Service1.class)
                .addTransient(Service2.class)
                .addTransient(Service3.class)
                .addTransient(Service4.class)
                .addTransient(App.class)
                .build();
        System.out.println(provider.get(App.class).s1);
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

Output:

```
Service2=io.github.amayaframework.di.Main$Service2@9660f4e
App=io.github.amayaframework.di.Main$App@396f6598, Service4=io.github.amayaframework.di.Main$Service4@394e1a0f
Service3=io.github.amayaframework.di.Main$Service3@27a5f880
io.github.amayaframework.di.Main$Service1@1d29cf23
```

### Missing dependency

```Java
import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.asm.BytecodeStubFactory;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            var provider = ProviderBuilders
                    .createChecked(new BytecodeStubFactory())
                    .addTransient(App.class)
                    .build();
            System.out.println(provider.get(App.class));
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

Output:

```
java.util.List<java.lang.String> not found
```

### Cyclical dependency

```Java
import io.github.amayaframework.di.ProviderBuilders;
import io.github.amayaframework.di.asm.BytecodeStubFactory;

public class Main {
    public static void main(String[] args) {
        try {
            var provider = ProviderBuilders
                    .createChecked(new BytecodeStubFactory())
                    .addTransient(Service.class)
                    .addTransient(App.class)
                    .build();
            System.out.println(provider.get(App.class));
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

Output:

```
Found cycle: [class io.github.amayaframework.di.Main$App, class io.github.amayaframework.di.Main$Service]
```

## Built With

* [Gradle](https://gradle.org) - Dependency management
* [ASM](http://asm.ow2.io) - Generation of proxy classes
* [jeflect](https://github.com/RomanQed/jeflect) - Defining classes from bytecode, utilities for ASM
* [jfunc](https://github.com/RomanQed/jfunc) - "Lazy" containers, functional interfaces, utilities
* [jtype](https://github.com/RomanQed/jtype) - Utilities for interaction with generic types
* [jgraph](graph) - Structure and Tarjan's algorithm for oriented graph

## Authors

* [RomanQed](https://github.com/RomanQed) - *Main work*

See also the list of [contributors](https://github.com/AmayaFramework/amaya-di/contributors) who participated
in this project.

## License

This project is licensed under the Apache License Version 2.0 - see the [LICENSE](LICENSE) file for details
