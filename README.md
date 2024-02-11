# amaya-di [![maven-central](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di?color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di/)

A framework responsible for monitoring and automating the dependency injection process.

[Russian version](README_RUS.md)

## Philosophy

Taking into account the features of both existing implementations, and the JVM and the Java language in general,
the framework was created in strict accordance with the following principles:

* Support only for new versions of java (11+)
* Complete rejection of reflective calls in the process of instantiating objects
* The minimum possible size of the framework
* The minimum possible set of dependencies
* Absence of transitive dependencies (i.e., when you get a framework, you get only it and
  several service libraries necessary for its operation)
* No dependencies outside the jdk (no plugins, utilities, or scripts)
* Lack of built-in integrations
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
    implementation group: 'io.github.amayaframework', name: 'amaya-di', version: 'LATEST'
}
```

### Maven dependency

```
<dependency>
    <groupId>io.github.amayaframework</groupId>
    <artifactId>amaya-di</artifactId>
    <version>LATEST</version>
</dependency>
```

## Usage example

Important: the order of transferring services to the collector does NOT matter, no changes and no exceptions will occur,
the ServiceProviderBuilder#build method has not been called yet.

### Hello, world!

```Java
import io.github.amayaframework.di.CheckedProviderBuilder;

public class Main {
    public static void main(String[] args) {
        var provider = CheckedProviderBuilder
                .create()
                .addService(String.class, () -> "Hello, world!")
                .build();
        System.out.println(provider.instantiate(String.class));
    }
}
```

### Two services and a dependent class

```Java
import io.github.amayaframework.di.CheckedProviderBuilder;

public class Main {
    public static void main(String[] args) {
        var provider = CheckedProviderBuilder
                .create()
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
import io.github.amayaframework.di.CheckedProviderBuilder;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var provider = CheckedProviderBuilder
                .create()
                .addService(Artifact.of(List.class, String.class), () -> List.of("Hi", "World"))
                .addService(Artifact.of(List.class, Integer.class), () -> List.of(1, 2, 3))
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

Output:

```
hash=1354011814
s1=[Hi, World]
s2=[1, 2, 3]
```

### Fields, methods, multiple constructors

```Java
import io.github.amayaframework.di.CheckedProviderBuilder;

public class Main {
    public static void main(String[] args) {
        var provider = CheckedProviderBuilder
                .create()
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

Output:

```
Service2=io.github.amayaframework.di.Main$Service2@9660f4e
App=io.github.amayaframework.di.Main$App@396f6598, Service4=io.github.amayaframework.di.Main$Service4@394e1a0f
Service3=io.github.amayaframework.di.Main$Service3@27a5f880
io.github.amayaframework.di.Main$Service1@1d29cf23
```

### Missing dependency

```Java
import io.github.amayaframework.di.CheckedProviderBuilder;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            var provider = CheckedProviderBuilder
                    .create()
                    .addTransient(App.class)
                    .build();
            System.out.println(provider.instantiate(App.class));
        } catch (ArtifactNotFoundException e) {
            System.out.println(e.getArtifact() + " not found");
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
Artifact{type=interface java.util.List, metadata=[class java.lang.String]} not found
```

### Cyclical dependency

```Java
import io.github.amayaframework.di.CheckedProviderBuilder;

public class Main {
    public static void main(String[] args) {
        try {
            var provider = CheckedProviderBuilder
                    .create()
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

Output:

```
Found cycle: [Artifact{type=class io.github.amayaframework.di.Main$App, metadata=null}, 
Artifact{type=class io.github.amayaframework.di.Main$Service, metadata=null}]
```

## Benchmark

See [jmh benchmark](src/jmh/java/io/github/amayaframework/di/ServiceProviderBenchmark.java).
Running on your machine:

```
gradle jmh
```

Results:

```
# JMH version: 1.36
# VM version: JDK 11.0.22, OpenJDK 64-Bit Server VM, 11.0.22+7-LTS
# VM invoker: ~/.jdks/corretto-11.0.22/bin/java.exe

Benchmark                                      Mode  Cnt   Score   Error  Units
ServiceProviderBenchmark.benchAmayaInjection   avgt   25  17,586 ± 0,240  ns/op
ServiceProviderBenchmark.benchManualInjection  avgt   25  11,586 ± 0,085  ns/op
```

## Structure and possibilities for expansion

The framework actually consists of three replaceable modules: descriptive, intermediate and facade.

### Descriptive module

By a convenient analogy with maven, the framework treats all dependencies as some kind of artifacts containing type
information and additional metadata. Dependent classes are treated as manifest schemas that require assembly.
For example, in the following example

```Java
class Service1 {
    public Service1() {
    }
}

class Service2 {
    public Service2() {
    }
}

class App {
    public App(Service1 s1, Service2 s2) {
    }
}
```

Service1 and Service2 will be both artifacts (like App dependencies) and specific implementations of these artifacts.
Thanks to this approach, it became possible to separate the process of building a "plan" for instantiating a class from
the process of solving dependencies directly.
In amaya-di, the latter is performed using another entity called Repository,
which matches the artifact and its implementation.
Now, having the above set, the framework creates the "schemes" of the injection.
For example, for the example above, it would look like this:

```Java
public class Main {
  public static void main(String[] args) {
    var service1 = Artifact.of(Service1.class);
    var service2 = Artifact.of(Service2.class);
    var app = Artifact.of(App.class);
    var service1Scheme = new ClassScheme(
            Service1.class,
            new ConstructorScheme(Service1.class.getConstructor(), Set.of(), new Artifact[0]),
            Set.of(),
            Set.of()
    );
    var service2Scheme = new ClassScheme(
            Service2.class,
            new ConstructorScheme(Service2.class.getConstructor(), Set.of(), new Artifact[0]),
            Set.of(),
            Set.of()
    );
    var appScheme = new ClassScheme(
            App.class,
            new ConstructorScheme(
                    App.class.getConstructor(Service1.class, Service2.class),
                    Set.of(service1, service2),
                    new Artifact[]{service1, service2}
            ),
            Set.of(),
            Set.of()
    );
  }
}
```

In the framework, the SchemeFactory is responsible for creating the schema,
which is implemented by default using a reflective api.

### Intermediate module

Now, obviously, after building the injection scheme, it is necessary to get implementations of "stubs" that perform
the work of creating an instance of an object of the required class and filling it with all the required dependencies
according to the scheme.
Most frameworks use reflection at this point, but amaya-di, as stated above, although it provides scope for independent
implementation, uses proxy class generation by default.

In the framework, StubFactory is responsible for generating stabs.

### Facade module

Finally, after filling the repository with generated stabs, some important checks (such as the absence of cycles or
lost dependencies), the framework provides the user with a simple interface for building a service provider and
its further use.

## Built With

* [Gradle](https://gradle.org) - Dependency management
* [ASM](http://asm.ow2.io) - Generation of proxy classes
* [jeflect](https://github.com/RomanQed/jeflect) - Defining classes from bytecode, utilities for ASM
* [jfunc](https://github.com/RomanQed/jfunc) - "Lazy" containers, functional interfaces, utilities

## Authors

* **RomanQed** - *Main work* - [RomanQed](https://github.com/RomanQed)

See also the list of [contributors](https://github.com/AmayaFramework/amaya-di/contributors) who participated
in this project.

## License

This project is licensed under the Apache License Version 2.0 - see the [LICENSE](LICENSE) file for details
