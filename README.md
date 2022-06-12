# amaya-di [![maven-central](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di?color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di/)

A framework responsible for dependency management and class search.
DI is implemented without the use of reflection in runtime.

## Getting Started

To install it, you will need:

* java 8+
* [classindex](https://github.com/atteo/classindex)
* Maven/Gradle

## Installing

### Gradle dependency

```Groovy
dependencies {
    annotationProcessor group: 'org.atteo.classindex', name: 'classindex', version: '3.11'
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

First you need to initialize DI. If it is intended to be used with auto-transformation, then in the future
an instance of DI (unless additional transformations are planned) will not be needed, and may not be saved.
Otherwise, it is recommended to declare a static container at your discretion and save the DI instance.

The default instance can be created like this:

```Java
private static final DI INJECTOR = DIBuilder.createDefault();
```

Next, we declare a class with a single field of primitive type:

```Java

@Inject
@AutoTransform
class Target {
    @Prototype
    private String value;

    public String getValue() {
        return value;
    }
}
```

And now you can create an instance of the class and check the result:

```Java
public class Main {
    private static final DI INJECTOR = DIBuilder.createDefault();

    public static void main(String[] args) {
        Target target = new Target();
        System.out.println(target.getValue());
    }
}

@Inject
@AutoTransform
class Target {
    @Prototype
    private String value;

    public String getValue() {
        return value;
    }
}
```

However, if auto transformation is not intended to be used, the target class MUST BE PUBLIC.

## Built With

* [Gradle](https://gradle.org) - Dependency management
* [classindex](https://github.com/atteo/classindex) - Annotation scanning
* [jeflect](https://github.com/RomanQed/jeflect) - Method wrapping
* [asm](https://asm.ow2.io) - Modification of classes
* [byte-buddy-agent](https://github.com/raphw/byte-buddy) - Javaagent installation

## Authors

* **RomanQed** - *Main work* - [RomanQed](https://github.com/RomanQed)

See also the list of [contributors](https://github.com/AmayaFramework/amaya-di/contributors) who participated
in this project.

## License

This project is licensed under the Apache License Version 2.0 - see the [LICENSE](LICENSE) file for details
