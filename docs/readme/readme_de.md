<p align="center">
  <img src="../img/logo.png" alt="logo" style="width: 200px; height: auto;">
</p>

# amaya-di – Ein modernes, leichtgewichtiges und schnelles DI-Framework.

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
- [日本語](readme_jp.md)
- Deutsch (AI translated, Gemini 2.5 Flash)
- [Français](readme_fr.md)

Amaya DI ist eine moderne Interpretation dessen, was ein DI-Framework sein sollte. Es wurde mit dem Ziel entwickelt, Entwicklern einen hochleistungsfähigen, flexiblen und minimalistischen IoC-Container ohne veraltete XML-Konfigurationen, eine Vielzahl von Annotationen und versteckte, reflexionsbasierte Magie zu bieten.

Das Framework unterstützt „out of the box“ die folgenden Funktionen:

* Arbeit mit generischen Typen (z. B. werden `List<String>` und `List<Integer>` als 2 verschiedene Typen erkannt);
* Erstellung unbenannter Scopes;
* Automatische Laufzeitgenerierung von Objektfabriken:
    * Strategien „new-object-per-request“ (transient) und „same-object-per-request“ (Singleton);
    * Scoped-Strategien transient und Singleton;
* Erstellung und Validierung des Abhängigkeitsgraphen:
    * Suche nach fehlenden Typen;
    * Erkennung von Zyklen mit dem iterativen Tarjan-Algorithmus;
* Vollständig deklarativer Prozess zum Aufbau des IoC-Containers mit einer praktischen Fluent API;
* Einfache Integration mit jeder bestehenden Bibliothek, jedem Framework oder Standard.

Die Geschwindigkeit der generierten Objektfabriken beträgt:

* Bei Verwendung der ASM-Implementierung entspricht sie der manuellen Implementierung (13,604 ns vs 13,475 ns);
* Bei Verwendung der Reflect-Implementierung fallen JNI-Overheads an, ist aber immer noch schnell genug (58,055 ns vs 13,402 ns).

Für weitere Informationen siehe den Abschnitt [benchmarks](#benchmarks).

# Einführung

Für die Installation benötigen Sie:

* JVM 11+
* Maven/Gradle

Jede JVM-Version unter 11 wird nicht unterstützt.

Die Verwendung anderer Sprachen als Java ist möglich, da das Framework keine Compile-Time-Plugins besitzt und den Java-AST nicht analysiert.

## Installation

Um das Framework zu verwenden, müssen zwei Module installiert werden: das Basismodul (`io.github.amayaframework:amaya-di`) und die Implementierung der Stub-Fabrik (`:amaya-di-asm` oder `:amaya-di-reflect`). Weitere Informationen zur Auswahl finden Sie im Abschnitt [Implementierungsauswahl](#implementierungsauswahl).

### Gradle

```groovy
dependencies {
    implementation group: 'io.github.amayaframework', name: 'amaya-di', version: '3.0.5'
    // ASM stub implementation
    implementation group: 'io.github.amayaframework', name: 'amaya-di-asm', version: '2.0.3'
    // Oder reflect stub implementation
    implementation group: 'io.github.amayaframework', name: 'amaya-di-reflect', version: '2.0.0'
}
```

### Maven

```
<dependencies>
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-di</artifactId>
        <version>3.0.5</version>
    </dependency>
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-di-asm</artifactId>
        <version>2.0.3</version>
    </dependency>
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-di-reflect</artifactId>
        <version>2.0.0</version>
    </dependency>
</dependencies>
```

## Hello, world\!

Der Aufbau des Containers erfolgt mit einem Builder (`ServiceProviderBuilder` oder `ScopedProviderBuilder`). Der Aufbauprozess kann in 3 Schritte unterteilt werden:

* Abrufen und Konfigurieren des Builder-Objekts;
* Deklarieren der erforderlichen Typen;
* Aufrufen der Methode `build()`.

Betrachten wir die grundlegenden Anwendungsfälle.

Zuerst, bei Verwendung von Modulen, geben wir die Abhängigkeiten für `module-info.java` an:

```java
module com.github.romanqed.di.examples {
    requires amayaframework.di; // Hauptmodul
    exports com.github.romanqed.di.examples;
}
```

Jetzt bauen wir den einfachsten Container:

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

Nach dem Start gibt dieser Code `Hello, world!` aus.

Betrachten wir ein komplexeres Szenario. Nehmen wir an, wir haben einen abstrakten Dienst `IGreeter`:

```java
public interface IGreeter { 
    String sayHello(String name);
}
```

Und zwei Implementierungen davon, eine für den globalen Kontext, die andere für den Scoped-Kontext:

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

Fügen wir dem `module-info.java` den reflektiven Objektfabriksgenerator hinzu:

```java
module com.github.romanqed.di.examples {
    requires amayaframework.di; // Hauptmodul
    requires amayaframework.di.reflect; // Verwenden der reflektiven Implementierung
    exports com.github.romanqed.di.examples;
}
```

Wir bauen den Container:

```java
var provider = ProviderBuilders.createScoped(new ReflectStubFactory())
        .addSingleton(IGreeter.class, GlobalGreeter.class)
        .addScoped(String.class)
        .addScopedSingleton(IGreeter.class, ScopedGreeter.class)
        .build();
```

Und erstellen zwei Scopes:

```java
var scope1 = provider.createScoped();
scope1.repository().put("Scope One"); 
var scope2 = provider.createScoped(); 
scope2.repository().put("Scope Two");
```

Und schließlich geben wir die Ergebnisse aus:

```java
System.out.println(provider.get(IGreeter.class).sayHello("Roman"));
System.out.println(scope1.get(IGreeter.class).sayHello("Roman")); 
System.out.println(scope2.get(IGreeter.class).sayHello("Roman"));
```

In der Konsole erscheint:

```
Hello, Roman!
Hello from scope 'Scope One', Roman!
Hello from scope 'Scope Two', Roman!
```

Der vollständige Code sieht wie folgt aus:

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

Alle ausführbaren Beispiele finden Sie [hier](../../examples).

# Grundlegende Konzepte

## Kern

### ObjectFactory und TypeProvider

Der grundlegende Mechanismus des Containers sind die Schnittstellen `ObjectFactory` und `TypeProvider`. Sie sehen wie folgt aus:

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

`ObjectFactory` erstellt eine Instanz eines Typs, dessen Abhängigkeiten von `TypeProvider` bereitgestellt werden. Dieses Design ist aus zwei Gründen vorteilhaft:

1)  Die Objektfabrik hat keine feste Abhängigkeit vom Typanbieter und kann diesen vor der Übergabe an untergeordnete Fabriken umschließen und modifizieren, was die Implementierung beliebiger Szenarien ermöglicht;
2)  Das direkte Abrufen der Fabrik anstelle der erstellten Instanz vermeidet unnötige Lookups und ermöglicht das Caching der Fabrik, ohne andere Vorteile zu verlieren.

Was die auf den ersten Blick nutzlose Methode `TypeProvider#canProvide` betrifft, so wird sie in Situationen benötigt, in denen wir uns **eindeutig** von der Fähigkeit des Anbieters überzeugen wollen, uns eine Objektfabrik zur Verfügung zu stellen. Denn ein Vergleich mit `null` funktioniert nicht in Fällen, in denen die Implementierung des Containers aus bestimmten Gründen Wrapper über Fabriken bereitstellt. Das heißt, angenommen `get(Type.class)` würde eine `ObjectFactory`-Implementierung zurückgeben, die immer `null` zurückgeben würde.

Gerade um solche Situationen zu verhindern, existiert das eindeutig definierbare `canProvide`. Wenn es `true` zurückgegeben hat, bedeutet dies, dass `get` eine **nicht-`null`** Instanz von `ObjectFactory` zurückgibt. Und das mit der Garantie (aus Sicht des Containers\!), dass es sich um die ursprünglich vom Benutzer bereitgestellte Fabrik handelt und nicht um einen Stub/temporären Wrapper.

Ohne die automatische Generierung von `ObjectFactory` würde das Beispiel `ComplexHelloWorld` wie folgt aussehen (`ManualHelloWorld`):

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
    // IGreeter und seine Impls
}
```

Dabei könnte `addScoped(String.class)`, das in `ComplexHelloWorld` vorhanden war, hier entfernt werden, da die Validierung der vom Benutzer bereitgestellten Fabriken nicht durchgeführt wird. Solche Abhängigkeiten (einschließlich Instanz- und `Function0`-Versionen) werden als Stammtypen betrachtet, da es unmöglich ist, die Menge der abhängigen Typen in endlicher Zeit (d.h. ohne deren Ausführung) zu bestimmen.

### Erweiterung von TypeProvider: TypeRepository

`TypeRepository` erweitert `TypeProvider` und verwandelt es in einen vollwertigen CRUD-Speicher für Typen. Es sieht wie folgt aus:

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

Die Methode `put(type, factory)` fügt einen Eintrag des Typs `type->factory` in das Repository ein. Wenn ein solcher Eintrag für den übergebenen Typ bereits existiert, wird die Fabrik durch die angegebene Instanz überschrieben.

Die Methoden `put(type, provider)`, `put(type, instance)`, `put(instance)` sind bedingt (abhängig von der Implementierung des Repositorys) Äquivalente zu `put(type, p -> provider.invoke())`, `put(type, p -> instance)` und `put(instance.getClass(), p -> instance)`.

Die Methode `remove(type)` löscht den Eintrag für den Typ aus dem Repository und gibt die gespeicherte `ObjectFactory` für diesen Typ zurück, oder `null`, wenn dieser Typ nicht vorhanden war.

Beide Variationen von `putAll(...)` kopieren Einträge des Typs `type->factory` aus der übergebenen Quelle in das Repository.

Die Methode `clear()` löscht den gesamten Inhalt des Repositorys.

Die Methode `forEach(BiConsumer)` wendet die übergebene `BiConsumer`-Instanz auf jeden Eintrag des Typs `type->factory` an, und die Methoden der Schnittstelle `Iterable<Type>` verhalten sich wie bei einer veränderlichen `Set<Type>`.

Auf Basis von `TypeRepository` werden alle Container-Varianten aufgebaut, und über ihn wird der Inhalt des Containers verwaltet. Beachten Sie – **es gibt keine Garantie**, dass die an `ObjectFactory#create()` übergebene `TypeProvider`-Instanz eine `TypeRepository`-Instanz ist. Im Allgemeinen ist der Versuch, innerhalb einer Fabrik auf das Repository zuzugreifen, aus Sicht des Framework-Designs inkorrekt. Eine solche Funktionalität wird niemals implementiert.

### Universalcontainer: ServiceProvider

Die Schnittstelle `ServiceProvider` beschreibt einen abstrakten DI-Container, der sowohl eine Schnittstelle zum Abrufen fertiger Implementierungen der angeforderten Typen als auch zur Verwaltung des Inhalts bietet. Es sieht so aus:

```java
public interface ServiceProvider {
    TypeRepository repository();
    
    ServiceProvider createScoped();
    
    <T> T get(Type type);
    
    <T> T get(Class<T> type);
    
    <T> T get(JType<T> type);
}
```

Die Methoden `get(type)` suchen nach einer `ObjectFactory` für den angeforderten Typ und:

1)  Wenn eine solche existiert, wird eine Instanz für den angeforderten Typ erstellt;
2)  Andernfalls wird `null` zurückgegeben.

Die Methode `repository()` gibt eine veränderliche `TypeRepository`-Instanz zurück, die von diesem Container verwendet wird.

Die Methode `createScoped()` erstellt einen neuen Container, der bei der Typensuche ein Fallthrough zum übergeordneten Container verwendet, aus dem die Methode aufgerufen wurde. Das heißt, wenn ein Typ in diesem Container nicht gefunden wird, wird die Anfrage an den untergeordneten Container weitergeleitet. Scoping kann unendlich oft durchgeführt werden, wodurch eine verknüpfte Kette von Containern entsteht.

### Weitere Kern-Dienstprogramme

Neben den oben beschriebenen Schnittstellen enthält der Kern:

* Eine abstrakte Implementierung von `ServiceProvider` (`AbstractServiceProvider`), die ein `repository`-Feld und alle Methoden außer `createScoped()` enthält;
* Eine `TypeRepository`-Implementierung basierend auf `Map<Type, ObjectFactory>` (`HashTypeRepository`);
* Einen threadsicheren Lazy-Wrapper über `ObjectFactory`, der für Singleton-Strategien verwendet wird (`LazyObjectFactory`);
* Eine universelle Scoped-Implementierung von `TypeRepository`, die jedes Paar von Scoped- und Parent-Repositories miteinander verbindet (`ScopedTypeRepository`).

Für weitere Details siehe Javadoc.

## Schemata

### Konzept des Injektionsschemas

Um eine vollständige Unabhängigkeit der Analyse von Reflexionsinformationen von der Art ihrer Beschaffung zu gewährleisten, führt das Framework das Konzept eines `Injektionsschemas` ein. Es stellt einen Deskriptor dar, der folgende Informationen enthält:

* Das Objekt oder „Ziel“ der Injektion – ein Klassenmitglied oder die Klasse selbst;
* Die Menge der Typen, von denen das Injektionsziel abhängt;
* Eine Typzuordnung für das aufgerufene Ziel (Methode oder Konstruktor), die der Parameternummer den entsprechenden Typ zuordnet;
* Ein einzelner Typ für ein Feld.

Das Injektionsschema einer Klasse enthält auch das Konstruktorschema, eine Menge von Methodenschemata und eine Menge von Feldschemata. Die Menge der Typen, von denen die Klasse abhängt, enthält dabei die Vereinigung der Mengen der Typen aller Schemata der Klassenmitglieder.

Die Basisschnittstelle, die jedes `...Schema` implementiert, sieht wie folgt aus:

```java
public interface Schema<T> {
    T getTarget();
    
    Set<Type> getTypes();
}
```

Für die Verwendung mit aufrufbaren Zielen erweitert `ExecutableSchema` dies:

```java
public interface ExecutableSchema<T extends Executable> extends Schema<T> {
    Type[] getMapping();
}
```

Insgesamt gibt es 4 Implementierungen:

* `FieldSchema` – Injektionsschema für ein Feld;
* `ConstructorSchema` – Injektionsschema für einen Konstruktor, implementiert die `ExecutableSchema`-Schnittstelle; es ist nur eines pro Klasse erlaubt;
* `MethodSchema` – Injektionsschema für eine Methode, implementiert die `ExecutableSchema`-Schnittstelle;
* `ClassSchema` – Injektionsschema für die gesamte Klasse.

### Schemabibliothek

Für die Generierung von Schemata für eine angegebene Klasse werden Schemabibliotheken verwendet. Jede solche Bibliothek muss die Schnittstelle `SchemaFactory` implementieren:

```java
public interface SchemaFactory {
    ClassSchema create(Class<?> clazz);
}
```

Standardmäßig wird eine Implementierung bereitgestellt, die nach Injektionszielen unter den Klassenmitgliedern sucht (`ReflectSchemaFactory`). Die folgende Logik wird angewendet:

1)  Es werden alle öffentlichen Konstruktoren der Klasse (`Class#getConstructors()`) durchsucht:
    1)  Wenn nur ein solcher Konstruktor vorhanden ist, wird dieser ausgewählt;
    2)  Wenn mehrere vorhanden sind, wird der mit der Marker-Annotation annotierte ausgewählt;
    3)  Andernfalls wird die Klasse als ungültig betrachtet;
2)  Es werden alle öffentlichen virtuellen Felder der Klasse gesucht, die mit der Marker-Annotation annotiert sind;
3)  Es werden alle öffentlichen Methoden der Klasse gesucht, die mit der Marker-Annotation annotiert sind:
    1)  Wenn die Methode virtuell ist, wird sie ausgewählt;
    2)  Wenn die Methode statisch ist und der Typ ihres ersten Arguments gleich oder ein Supertyp der Zielklasse ist, wird sie ausgewählt;
    3)  Andernfalls wird die Methode als ungültig betrachtet.

Dabei kann die Marker-Annotation beliebig sein (im Framework wird standardmäßig `@Inject` verwendet, das mit dem `amaya-di`-Modul geliefert wird) und als Konstruktorparameter angegeben.

`ReflectSchemaFactory` ermöglicht es auch, einen benutzerdefinierten "Typ-Verarbeiter" anzugeben, der die folgende Schnittstelle implementieren muss:

```java
public interface TypeProcessor {
    Type process(Type type, AnnotatedElement element);
}
```

Der standardmäßig verwendete Typ-Verarbeiter (`ReflectTypeProcessor`) parst sowohl „einfache“ Typen (Instanzen von `Class`) als auch generische Typen. Dabei werden bei der Verarbeitung alle Wildcard-Typen auf ihre oberen Grenzen gebracht:

* `List<? extends String>` =\> `List<String>`;
* `List<? super String>` =\> `List<Object>`;
* `List<?>` =\> `List<Object>`.

Typvariablen sind vollständig verboten, da eine eindeutige Konvertierung nicht möglich ist. Wenn man sie beispielsweise auf den ersten Typ in den oberen Grenzen (`<T>` =\> `Object`, `<T extends Number>` =\> `Number`) abbildet, ist es bei einem Zyklus `class A<T extends A<T>>` unmöglich, eindeutig zu bestimmen, wie er aufgelöst werden kann.

## Generierung von ObjectFactory

### Stub-Fabrik

Stubs im Kontext des Frameworks sind Implementierungen von `ObjectFactory`, die automatisch gemäß dem Injektionsschema der Klasse generiert werden. Stubs werden mit `StubFactory` erstellt, deren Basisschnittstelle wie folgt aussieht:

```java
@FunctionalInterface
public interface StubFactory {
    ObjectFactory create(ClassSchema schema, CacheMode mode);
    
    default ObjectFactory create(ClassSchema schema) {
        return create(schema, CacheMode.NONE);
    }
}
```

Alle Implementierungen **müssen** strikt dem angegebenen Injektionsschema und Cache-Modus folgen. Wenn es aus irgendeinem Grund unmöglich ist, eine **gebrauchsfertige** Instanz von `ObjectFactory` zu erstellen, oder wenn die Injektion einiger Abhängigkeiten nicht möglich ist (z. B. wenn das Zielfeld irgendwie privat ist), muss die Fabrik eine Ausnahme auslösen. Die Rückgabe von `null` und/oder die Rückgabe einer primitiven Implementierung wie `provider -> null` sind nicht zulässig.

### Caching von ObjectFactory

In den meisten Szenarien wird der erstellte Container nur im Nur-Lese-Modus verwendet. Folglich ist es in solchen Fällen sinnvoll, für die generierten `ObjectFactory` die Implementierungen der Fabriken für die Typen, von denen sie abhängen, direkt zu speichern. Dies minimiert (oder vermeidet ganz) unnötige Aufrufe von `TypeProvider#get(type)`, was den Overhead erheblich reduziert.

Für die explizite Steuerung des Cachings werden folgende Modi eingeführt:

* `CacheMode.FULL` – die Anfrage an `TypeProvider` erfolgt überhaupt nicht, die benötigten Fabriken werden direkt aus dem internen Cache entnommen;
* `CacheMode.PARTIAL` – die Anfrage an `TypeProvider` erfolgt nur, wenn sich die benötigte Fabrik nicht im internen Cache befindet; das Ergebnis der Anfrage wird **nicht gespeichert** (für threadsicheren Zugriff auf den Container);
* `CacheMode.NONE` – die Anfrage an `TypeProvider` erfolgt immer, es existiert kein interner Cache.

Darüber hinaus müssen alle `ObjectFactory` mit einem internen Cache die Schnittstelle `CachedObjectFactory` implementieren, die das Initialisieren oder Aktualisieren des Inhalts ermöglicht:

```java
public interface CachedObjectFactory extends ObjectFactory {
    void set(Type type, ObjectFactory factory);
}
```

# Containerbau

Das Erstellen und Füllen eines Containers kann entweder direkt (durch Implementierung und Instanziierung aller notwendigen Entitäten) oder mithilfe benutzerdefinierter Dienstprogramme erfolgen.

Standardmäßig bietet das Framework einen Build-Mechanismus, der auf dem „Builder“-Muster basiert. Seine Funktionalität umfasst:

* Arbeit mit generischen Typen unter Verwendung von [jtype](https://github.com/RomanQed/jtype);
* Hinzufügen benutzerdefinierter `ObjectFactory`-Implementierungen;
* Hinzufügen von transienten und Singleton-Typen anhand ihrer Klasse;
* Hinzufügen von Scoped-Typen;
* Validierung des Abhängigkeitsgraphen auf Zyklen und fehlende Typen.

## Grundlegende Anwendungsfälle

Die Builder-Varianten werden durch die Schnittstellen `ServiceProviderBuilder` und deren Erweiterung `ScopedProviderBuilder` repräsentiert, die Scoped-Funktionen hinzufügt. Der Zugriff auf ihre Instanzen ist über die Hilfsklasse `ProviderBuilders` vorgesehen. Diese stellt statische Methoden zum Erstellen aller im Framework enthaltenen Implementierungen bereit und ist im Wesentlichen der Einstiegspunkt in die API.

Es gibt folgende Hauptmethoden (Überladungen siehe Javadoc):

```java
// Erstellt einen Builder mit den angegebenen Fabriken und Prüfungen
// Wenn checks == BuilderChecks.NO_CHECKS, wird eine Implementierung ohne Validierungsmechanismen verwendet
public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {...}

// Erstellt einen Builder mit einer Schemabibliothek und einem Standard-Cache-Modus (ProviderBuilders#SCHEMA_FACTORY und #CACHE_MODE)
// Wenn checks == BuilderChecks.NO_CHECKS, wird eine Implementierung ohne Validierungsmechanismen verwendet 
public static ServiceProviderBuilder create(int checks) {...}

// Erstellt einen Builder mit den angegebenen Fabriken, ProviderBuilders#CACHE_MODE und ohne Prüfungen
public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory) {}

// Erstellt einen Builder mit ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE und ohne Prüfungen.
public static ServiceProviderBuilder create() {...}

// Erstellt einen Builder mit ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE und BuilderChecks.VALIDATE_ALL.
public static ServiceProviderBuilder createChecked() {...}

// Erstellt einen Scoped-Builder mit den angegebenen Fabriken und Prüfungen
// Wenn checks == BuilderChecks.NO_CHECKS, wird eine Implementierung ohne Validierungsmechanismen verwendet
public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {...}

// Erstellt einen Scoped-Builder mit einer Schemabibliothek und einem Standard-Cache-Modus (ProviderBuilders#SCHEMA_FACTORY und #CACHE_MODE)
// Wenn checks == BuilderChecks.NO_CHECKS, wird eine Implementierung ohne Validierungsmechanismen verwendet 
public static ScopedProviderBuilder createScoped(int checks) {...}

// Erstellt einen Scoped-Builder mit den angegebenen Fabriken, ProviderBuilders#CACHE_MODE und ohne Prüfungen
public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory) {...}

// Erstellt einen Scoped-Builder mit ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE und ohne Prüfungen.
public static ScopedProviderBuilder createScoped() {...}

// Erstellt einen Scoped-Builder mit ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE und BuilderChecks.VALIDATE_ALL.
public static ScopedProviderBuilder createCheckedScoped() {...}
```

Ein Beispiel, das alle Möglichkeiten der Builder zeigt, finden Sie [hier](../../examples/src/main/java/com/github/romanqed/di/examples/AllMethods.java).

## Generische Typen

Zur Registrierung eines generischen Typs im Container kann sowohl jtype als auch Drittanbieterbibliotheken verwendet werden, die Implementierungen der Schnittstelle `java.lang.reflect.Type` für parametrisierte Typen bereitstellen können. Unten sehen Sie ein Beispiel, das die automatische Typerfassung und deren manuelle Erstellung zeigt:

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
                // B<A1> und B<A2>
                .add(Types.of(B.class, A1.class), tp -> new B<>((A1) tp.get(A1.class).create(tp)))
                .add(new JType<B<A2>>(){}, tp -> new B<>((A2) tp.get(A2.class).create(tp)))
                // Und schließlich C
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

Das Ergebnis wird sein:

```
[1, 2, 3]
[str1, str2, str3]
{1=1, 2=2}
{s2=k2, s1=k1}
```

## Validierung des Abhängigkeitsgraphen

Für die Implementierung des Validierungsmechanismus werden die folgenden Konventionen verwendet:

* Alle vom Benutzer bereitgestellten `ObjectFactory`-Implementierungen werden bedingungslos als korrekt angesehen und als „Root“-Typen betrachtet, die keine eigenen Abhängigkeiten haben;
* Alle benutzerdefinierten `ServiceWrapper` geben korrekte `ObjectFactory` zurück und erzeugen keine Fehler;
* Alle Typen, für die ein Injektionsschema erstellt werden kann, werden als „schwach“ betrachtet und werden überprüft auf:
    * Anforderung fehlender Abhängigkeiten;
    * Erstellung von Zyklen im Abhängigkeitsgraphen.

Für Scoped-Container wird zusätzlich eingeführt:

* Die Registrierung eines promised-Typs **garantiert**, dass beim Erstellen eines Scoped-Containers, BEVOR mit ihm gearbeitet wird, der Typ im Repository bereitgestellt wird;
* Scope-Typen können auf andere Scope-Typen und auf Basistypen verweisen;
* Basistypen können NUR auf andere Basistypen verweisen.

Basierend darauf und den Container als Abhängigkeitsgraph betrachtet, können fünf Klassen möglicher Fehler unterschieden werden.

1.  Im Basiskontainer fehlt ein Typ, von dem ein anderer Basistyp abhängt.

Beispiel:

```
class A -> class String
class String -> fehlt
```

2.  Im Scoped-Container fehlt ein Typ, von dem ein anderer Scoped-Typ abhängt.

Beispiel:

```
class ScopedA -> class String
class String -> fehlt im Scoped- und im Basiskontainer
```

3)  Im Basiskontainer ist eine zirkuläre Abhängigkeit zwischen Basistypen aufgetreten.

Beispiel:

```
class A -> class B
class B -> class C
class C -> class A
```

4)  Im Scoped-Container ist eine zirkuläre Abhängigkeit zwischen Scoped-Typen aufgetreten.

5)  Eine zirkuläre Abhängigkeit zwischen Containern ist durch Überschreiben eines Typs durch einen Scoped-Container aufgetreten.

Beispiel:

```
class A -> class B
class B -> interface IC
interface IC: C
interface IC: ScopedC -> A
```

Der integrierte Validierungsmechanismus unterstützt die Verarbeitung all dieser 5 Fehlerarten. Ein Beispiel finden Sie [hier](../../examples/src/main/java/com/github/romanqed/di/examples/AllChecks.java).

# Varianten von ObjectFactory-Generatoren

Das Framework bietet zwei Implementierungen von `StubFactory`, die in den Modulen `amaya-di-asm` und `amaya-di-reflect` bereitgestellt werden.

## ASM

Die ASM-Implementierung verwendet die On-the-fly-Bytecode-Generierung zur Erstellung von `ObjectFactory`. Die generierte Klasse erhält einen Namen, der aus dem Namen der Klasse, die das Injektionsziel ist, und einem Alias des verwendeten Cache-Modus besteht. Entsprechend erfolgt bei wiederholter Generierungsanforderung keine erneute Generierung, sondern es wird die bereits geladene Klasse verwendet, die über ihren Namen abgerufen wird.

Eine Besonderheit der ASM-Implementierung ist die relativ lange Vorbereitungszeit und die maximal mögliche Arbeitsgeschwindigkeit, die praktisch der Geschwindigkeit von normalem Code entspricht.

Auch das Caching von Bytecode ist implementiert, was die Wiederverwendung einer einmal generierten Klasse ermöglicht. Dies ist äußerst nützlich, wenn die finale (und, was wichtig ist, zukünftig unveränderliche) Jar-Datei erstellt wurde und auf der Produktion veröffentlicht werden soll:

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

Dieser Code erstellt nach dem ersten Start ein Verzeichnis „cache“, in dem der generierte Bytecode gespeichert wird. Bei den nächsten Starts wird er diesen sofort laden, anstatt ihn neu zu generieren. Beim Löschen des Caches erfolgt die Generierung erneut.

## Reflect

Die Reflect-Implementierung verwendet reflektive Methodenaufrufe und Feldzugriffe zur Erstellung von `ObjectFactory`. Die angeforderten Klassenmitglieder werden als ohne Modifikatorprüfung zugänglich (`setAccessible(true)`) markiert und in eine vorbereitete Fabrikimplementierung eingeschlossen.

Eine Besonderheit der Reflect-Implementierung ist die relativ schnelle Vorbereitungszeit und die langsame Arbeitsgeschwindigkeit, die erhebliche Overheads durch reflektive Zugriffsmechanismen beinhaltet.

## Implementierungsauswahl

Die Wahl zwischen ASM- und Reflect-Implementierungen sollte auf den Besonderheiten des Containerszenarios basieren. Wenn in Ihrer Anwendung der Container:

* Häufig neu aufgebaut wird;
* Einmalig während des Starts verwendet wird;
* Nicht in „heißem“ Code verwendet wird (oder hohe Overheads zulässig sind);

dann sollte die Reflect-Implementierung verwendet werden.

Wenn der Container jedoch:

* Einmal aufgebaut wird;
* Ständig in „heißem“ Code verwendet wird;
* Abhängigkeiten in minimaler Zeit liefern muss;

dann sollte die ASM-Implementierung verwendet werden.

## Benchmarks

Die Benchmarks wurden auf Amazon Corretto 11 (`openjdk version "11.0.27" 2025-04-15 LTS`), auf einem PC (nicht Server) mit Intel i5-9400F, 40 GB RAM und Windows 10 22H2 durchgeführt. Das JMH-Framework Version 1.37 wurde verwendet.

Der Abhängigkeitssatz besteht aus:

* Service1;
* Service2;
* Service3, abhängig von Service1 (Konstruktor);
* App, abhängig von allen dreien (Service1 – Konstruktor, Service2 – Feld, Service3 – Methode).

Der Container wird vor dem Benchmark vollständig vorbereitet, es werden keine Interaktionen mit ihm durchgeführt.

Um die Referenzzeit zu erhalten, mit der die anderen Ergebnisse verglichen werden können, wurde ein einfacher Code geschrieben, der die „manuelle“ Erstellung von App und die Injektion von Abhängigkeiten implementiert:

```java
var app = new App(new Service1());
app.s2 = new Service2();
app.setS3(new Service3(new Service1()));
```

Dabei wurden Benchmarks zur Erfassung des Overheads des Repositorys selbst (für die Typensuche) in zwei Versionen durchgeführt:

* Die „reine“ Ausführungszeit der manuellen Erstellung der App wurde gemessen;
* Die Erstellung der App wurde in `ObjectFactory` gewickelt und in den Container platziert, die Zeit für den Aufruf der Suche im Container und den Aufruf von `create()` wurde gemessen.

Auch die Zeit für jeden CacheMode wurde separat gemessen, und erneut für CacheMode.FULL (Standardmodus).

Folgende Ergebnisse wurden erzielt:

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

Die Ausführung der Benchmarks erfolgt wie folgt:

```
>./gradlew asm:jmh
>./gradlew reflect:jmh
>./gradlew :jmh
```

# Unterschiede zu 2.x

Beim Übergang von 2.x auf 3.x hat die grundlegende API des Frameworks die Kompatibilität vollständig verloren. Die Builder-API blieb mit Ausnahme neuer Methoden unverändert. Diejenigen, die zuvor amaya-di 2.x verwendet haben und auf 3.x umsteigen möchten, sollten die folgenden wichtigen Änderungen beachten:

* Als Objektfabrik wird nun `ObjectFactory` anstelle von `Function0` verwendet;
* Die Schnittstelle `ServiceRepository` wurde in `TypeRepository` umbenannt;
* Die Schnittstelle `TypeProvider`, die ein lokaler Mechanismus für die Stub-Modul-Anforderungen war, wurde vollständig entfernt;
* Die Schnittstelle `ManualProviderBuilder` wurde vollständig entfernt;
* Die `StubFactory`-API wurde geändert: `create(schema, typeProvider)` =\> `create(schema, cacheMode)`;
* `StubFactory` füllt den Fabrik-Cache beim Erstellen nicht mehr, dies muss selbst gemacht werden;
* Das Modul `jgraph` wird nicht mehr verwendet und wurde aus den Abhängigkeiten entfernt;
* Die abstrakte Klasse `AbstractProviderBuilder` wurde durch `AbstractServiceProviderBuilder` ersetzt, die Protected-API hat sich vollständig geändert;
* Die Schnittstelle `ServiceWrapper` erbt nicht mehr die Schnittstelle `Function1`, die Signatur ihrer funktionalen Methode lautet nun `ObjectFactory wrap(ObjectFactory factory)`;
* Die Klasse `LazyProvider` wurde vollständig entfernt;
* Es wurde eine neue Ausnahme `CyclesFoundException` eingeführt, die ausgelöst werden kann, wenn mehrere Zyklen gefunden werden;
* Die Aufteilung in Module hat sich vollständig geändert:
    * `ServiceProvider` und verwandte Typen wurden in ein separates Modul `amaya-di-core` verschoben;
    * `ClassSchema` und verwandte Typen wurden in ein separates Modul `amaya-di-schema` verschoben;
    * `StubFactory` und verwandte Typen wurden in ein separates Modul `amaya-di-stub` verschoben;
    * Das Modul `amaya-di` enthält nun diese drei Module, das Modul `amaya-di-core` ist als transitive Abhängigkeit aufgeführt;
* Ein neuer Scoping-Mechanismus wurde eingeführt.

# Mitwirken

Ich würde mich sehr freuen, wenn Sie Korrekturen, Verbesserungen und Funktionserweiterungen für amaya-di vorschlagen möchten. Eine detaillierte Anleitung zum Mitwirken finden Sie [hier](../contributing/contributing_de.md).

# Erstellt mit

* [Gradle](https://gradle.org) - Abhängigkeitsverwaltung
* [ASM](https://asm.ow2.io) - Generierung von `ObjectFactory`
* [jeflect-loader](https://github.com/RomanQed/jeflect) - Laufzeit-Bytecode-Lader
* [jfunc](https://github.com/RomanQed/jfunc) - Funktionale Schnittstellen, Dienstprogramme
* [jtype](https://github.com/RomanQed/jtype) - Dienstprogramme für die Arbeit mit Generics

# Autoren

* [RomanQed](https://github.com/RomanQed) - *Hauptarbeit*

Schauen Sie sich auch die Liste der [Mitwirkenden](https://github.com/AmayaFramework/amaya-di/contributors) an, die zu diesem Projekt beigetragen haben.

# Lizenz

Dieses Projekt ist unter der Apache License Version 2.0 lizenziert – siehe die Datei [LICENSE](../../LICENSE) für Details.