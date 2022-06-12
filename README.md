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


## Built With

* [Gradle](https://gradle.org) - Dependency management
* [classindex](https://github.com/atteo/classindex) - Annotation scanning
* [jeflect](https://github.com/RomanQed/jeflect) - Method wrapping
* [asm](https://asm.ow2.io) - Modification of classes
* [byte-buddy-agent](https://github.com/raphw/byte-buddy) - Installing javaagent

## Authors

* **RomanQed** - *Main work* - [RomanQed](https://github.com/RomanQed)

See also the list of [contributors](https://github.com/AmayaFramework/amaya-di/contributors) who participated
in this project.

## License

This project is licensed under the Apache License Version 2.0 - see the [LICENSE](LICENSE) file for details

