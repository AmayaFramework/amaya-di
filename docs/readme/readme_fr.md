<p align="center">
  <img src="../img/logo.png" alt="logo" style="width: 200px; height: auto;">
</p>

# amaya-di – Un framework DI moderne, léger et rapide.

|      Module      |                                                                                                                              Version                                                                                                                              |
|:----------------:|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|     amaya-di     |                 [![amaya-di](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di?strategy=releaseProperty&style=for-the-badge&label=amaya-di&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di/)                 |
|  amaya-di-core   |       [![amaya-di-core](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-core?strategy=releaseProperty&style=for-the-badge&label=amaya-di-core&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-core/)       |
| amaya-di-schema  |   [![amaya-di-schema](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-schema?strategy=releaseProperty&style=for-the-badge&label=amaya-di-schema&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-schema/)   |
|  amaya-di-stub   |       [![amaya-di-stub](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-stub?strategy=releaseProperty&style=for-the-badge&label=amaya-di-stub&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-stub/)       |
|   amaya-di-asm   |         [![amaya-di-asm](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-asm?strategy=releaseProperty&style=for-the-badge&label=amaya-di-asm&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-asm/)         |
| amaya-di-reflect | [![amaya-di-reflect](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-di-reflect?strategy=releaseProperty&style=for-the-badge&label=amaya-di-reflect&color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-di-reflect/) |

- [English](../../README.md)
- [Русский](readme_ru.md)
- [简体中文](readme_zh_cn.md)
- [正體中文](readme_zh_tw.md)
- [日本語](readme_jp.md)
- [Deutsch](readme_de.md)
- Français (AI translated, Gemini 2.5 Flash)

Amaya DI est une approche moderne de ce que devrait être un framework DI. Il a été créé dans le but de fournir aux développeurs un conteneur IoC performant, flexible et minimaliste, sans configurations XML obsolètes, sans multitude d'annotations et sans magie cachée basée sur la réflexion.

Le framework prend en charge les fonctionnalités suivantes "prêtes à l'emploi" :

* Gestion des types génériques (par exemple, `List<String>` et `List<Integer>` seront reconnus comme 2 types différents);
* Création de scopes non nommés;
* Génération automatique de fabriques d'objets au moment de l'exécution:
    * Stratégies "nouvel objet par requête" (transient) et "même objet par requête" (singleton);
    * Stratégies scoped transient et singleton;
* Construction et validation du graphe de dépendances:
    * Recherche de types manquants;
    * Détection de cycles par l'algorithme itératif de Tarjan;
* Processus de construction du conteneur IoC entièrement déclaratif via une API fluide et conviviale;
* Implémentation simple de l'intégration avec toute bibliothèque, framework ou standard existant.

La vitesse des fabriques d'objets générées est la suivante:

* En utilisant l'implémentation ASM, elle correspond à l'implémentation manuelle (13,604 ns contre 13,475 ns);
* En utilisant l'implémentation reflect, elle inclut la surcharge de JNI, mais reste suffisamment rapide (58,055 ns contre 13,402 ns).

Pour plus d'informations, voir la section [benchmarks](#benchmarks).

# Introduction

Pour l'installation, vous aurez besoin de:

* JVM 11+
* Maven/Gradle

Toute version de JVM inférieure à 11 n'est pas prise en charge.

L'utilisation d'autres langages que Java est possible, car le framework n'a pas de plugins de compilation et n'analyse pas l'AST Java.

## Installation

Pour utiliser le framework, vous devez installer deux modules : le module de base (`io.github.amayaframework:amaya-di`) et une implémentation de fabrique stub (`:amaya-di-asm` ou `:amaya-di-reflect`). Pour plus de détails sur le choix, voir la section [choix de l'implémentation](#choix-de-limplémentation).

### Gradle

```groovy
dependencies {
    implementation group: 'io.github.amayaframework', name: 'amaya-di', version: '3.1.0'
    // ASM stub implementation
    implementation group: 'io.github.amayaframework', name: 'amaya-di-asm', version: '2.1.0'
    // Ou reflect stub implementation
    implementation group: 'io.github.amayaframework', name: 'amaya-di-reflect', version: '2.1.0'
}
```

### Maven

```
<dependencies>
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-di</artifactId>
        <version>3.1.0</version>
    </dependency>
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-di-asm</artifactId>
        <version>2.1.0</version>
    </dependency>
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-di-reflect</artifactId>
        <version>2.1.0</version>
    </dependency>
</dependencies>
```

## Hello, world\!

La construction du conteneur est effectuée à l'aide d'un constructeur (`ServiceProviderBuilder` ou `ScopedProviderBuilder`). Le processus de construction peut être divisé en 3 étapes:

* Obtention et configuration de l'objet constructeur;
* Déclaration des types requis;
* Appel de la méthode `build()`.

Examinons les scénarios d'utilisation de base.

Tout d'abord, si vous utilisez des modules, spécifiez les dépendances pour `module-info.java`:

```java
module com.github.romanqed.di.examples {
    requires amayaframework.di; // Module principal
    exports com.github.romanqed.di.examples;
}
```

Maintenant, construisons le conteneur le plus simple:

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

Après l'exécution, ce code affichera `Hello, world!`.

Examinons un scénario plus complexe. Supposons que nous ayons un service abstrait `IGreeter`:

```java
public interface IGreeter { 
    String sayHello(String name);
}
```

Et deux de ses implémentations, l'une pour le contexte global, l'autre pour le scoped:

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

Ajoutons le générateur de fabriques d'objets réflexifs à `module-info.java`:

```java
module com.github.romanqed.di.examples {
    requires amayaframework.di; // Module principal
    requires amayaframework.di.reflect; // Utilisation de l'implémentation réflexive
    exports com.github.romanqed.di.examples;
}
```

Construisons le conteneur:

```java
var provider = ProviderBuilders.createScoped(new ReflectStubFactory())
        .addSingleton(IGreeter.class, GlobalGreeter.class)
        .addScoped(String.class)
        .addScopedSingleton(IGreeter.class, ScopedGreeter.class)
        .build();
```

Et créons deux scopes:

```java
var scope1 = provider.createScoped();
scope1.repository().put("Scope One"); 
var scope2 = provider.createScoped(); 
scope2.repository().put("Scope Two");
```

Et enfin, affichons les résultats:

```java
System.out.println(provider.get(IGreeter.class).sayHello("Roman"));
System.out.println(scope1.get(IGreeter.class).sayHello("Roman")); 
System.out.println(scope2.get(IGreeter.class).sayHello("Roman"));
```

La console affichera:

```
Hello, Roman!
Hello from scope 'Scope One', Roman!
Hello from scope 'Scope Two', Roman!
```

Le code complet est le suivant:

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

Tous les exemples exécutables peuvent être trouvés [ici](../../examples).

# Concepts de base

## Noyau

### ObjectFactory et TypeProvider

Les mécanismes fondamentaux du conteneur sont les interfaces `ObjectFactory` et `TypeProvider`. Elles se présentent comme suit:

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

`ObjectFactory` effectue la création d'une instance de type, dont les dépendances sont fournies par `TypeProvider`. Cette conception est pratique pour deux raisons:

1)  La fabrique d'objets n'a pas de dépendance forte vis-à-vis du fournisseur de types et peut l'envelopper et le modifier avant de le transmettre aux fabriques sous-jacentes, ce qui permet de réaliser tous les scénarios;
2)  L'obtention de la fabrique directement au lieu de l'instance créée permet d'éviter les recherches inutiles et de mettre en cache la fabrique sans perdre les autres avantages.

Quant à la méthode `TypeProvider#canProvide` qui semble inutile à première vue, elle est nécessaire dans les situations où nous voulons nous **assurer sans équivoque** de la capacité du fournisseur à nous fournir une fabrique d'objets. En effet, la comparaison avec `null` ne fonctionnera pas dans les cas où l'implémentation du conteneur, à des fins quelconques, fournit des enveloppes sur les fabriques. Autrement dit, supposons que `get(Type.class)` renvoie une implémentation de `ObjectFactory` qui renverra toujours `null`.

C'est précisément pour éviter de telles situations qu'il existe un `canProvide` défini de manière univoque. Si elle renvoie `true`, cela signifie que `get` renverra une instance `ObjectFactory` **non `null`**. Et ce, avec la garantie (du point de vue du conteneur \!) qu'il s'agit de la fabrique initialement fournie par l'utilisateur, et non d'un stub/d'une enveloppe temporaire.

Sans génération automatique de `ObjectFactory`, l'exemple `ComplexHelloWorld` ressemblerait à ceci (`ManualHelloWorld`):

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
    // IGreeter et ses implémentations
}
```

De plus, `addScoped(String.class)`, présent dans `ComplexHelloWorld`, peut être supprimé ici, car la validation des fabriques fournies par l'utilisateur n'est pas effectuée. De telles dépendances (y compris les versions d'instance et `Function0`) sont considérées comme des types racines, car il est impossible de déterminer l'ensemble des types dépendants pour elles en un temps fini (c'est-à-dire sans les exécuter).

### Extension de TypeProvider : TypeRepository

`TypeRepository` étend `TypeProvider`, le transformant en un magasin de types CRUD complet. Il se présente comme suit:

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

La méthode `put(type, factory)` insère une entrée de type `type->factory` dans le référentiel. Si une telle entrée existe déjà pour le type donné, la fabrique est écrasée par l'instance spécifiée.

Les méthodes `put(type, provider)`, `put(type, instance)`, `put(instance)` sont conditionnellement (selon l'implémentation du référentiel) des analogues de `put(type, p -> provider.invoke())`, `put(type, p -> instance)` et `put(instance.getClass(), p -> instance)` respectivement.

La méthode `remove(type)` supprime l'entrée de type du référentiel et renvoie l'`ObjectFactory` stockée pour ce type ou `null` si ce type n'existait pas.

Les deux variantes de `putAll(...)` copient les entrées de type `type->factory` de la source donnée vers le référentiel.

La méthode `clear()` vide entièrement le référentiel, supprimant tout son contenu.

La méthode `forEach(BiConsumer)` applique l'instance `BiConsumer` donnée à chaque entrée `type->factory`, et les méthodes de l'interface `Iterable<Type>` se comportent comme s'il s'agissait d'un `Set<Type>` modifiable.

Tous les types de conteneurs sont construits sur la base de `TypeRepository`, et la gestion du contenu du conteneur est effectuée par son intermédiaire. Veuillez noter qu'il n'y a **aucune garantie** que l'instance `TypeProvider` passée à `ObjectFactory#create()` soit une instance de `TypeRepository`. En général, une tentative d'accès au référentiel à l'intérieur d'une fabrique est incorrecte du point de vue de la conception du framework. Une telle fonctionnalité ne sera jamais implémentée.

### Conteneur universel : ServiceProvider

L'interface `ServiceProvider` décrit un conteneur di abstrait, fournissant une interface à la fois pour obtenir des implémentations prêtes à l'emploi des types demandés et pour gérer le contenu. Il se présente comme suit:

```java
public interface ServiceProvider {
    TypeRepository repository();
    
    ServiceProvider createScoped();
    
    <T> T get(Type type);
    
    <T> T get(Class<T> type);
    
    <T> T get(JType<T> type);
}
```

Les méthodes `get(type)` recherchent l'`ObjectFactory` pour le type demandé et:

1)  Si une telle instance existe, créent une instance pour le type demandé;
2)  Sinon, renvoient `null`.

La méthode `repository()` renvoie une instance modifiable de `TypeRepository` utilisée par ce conteneur.

La méthode `createScoped()` crée un nouveau conteneur qui utilise une "descente" (fallthrough) vers le conteneur parent, à partir duquel la méthode a été appelée, lors de la recherche de types. Autrement dit, si le type n'est pas trouvé dans ce conteneur lors de la requête, la requête est transmise au conteneur sous-jacent. Le scoping peut être effectué à l'infini, créant une chaîne de conteneurs liés.

### Autres utilitaires du noyau

En plus des interfaces décrites ci-dessus, le noyau comprend:

* Une implémentation abstraite de `ServiceProvider` (`AbstractServiceProvider`), contenant le champ `repository` et toutes les méthodes sauf `createScoped()`;
* Une implémentation de `TypeRepository` basée sur `Map<Type, ObjectFactory>` (`HashTypeRepository`);
* Un wrapper paresseux thread-safe sur `ObjectFactory`, utilisé pour les politiques singleton (`LazyObjectFactory`);
* Une implémentation `TypeRepository` générique et scoped, connectant n'importe quelle paire de référentiels scoped et parent (`ScopedTypeRepository`).

Pour plus de détails, voir le javadoc.

## Schémas

### Le concept de schéma d'injection

Afin d'assurer une indépendance totale de l'analyse des informations réflexives par rapport à la méthode d'obtention, le framework introduit le concept de `schéma d'injection`. Il s'agit d'un descripteur contenant les informations suivantes:

* L'objet ou la "cible" de l'injection - un membre de classe ou la classe elle-même;
* L'ensemble des types dont dépend la cible d'injection;
* Le mappage des types pour la cible appelée (méthode ou constructeur), faisant correspondre le numéro du paramètre au type correspondant;
* Un type unique pour un champ.

Le schéma d'injection de classe contient également le schéma du constructeur, un ensemble de schémas de méthodes et un ensemble de schémas de champs. L'ensemble des types dont dépend la classe contient l'union des ensembles de types de tous les schémas des membres de la classe.

L'interface de base implémentée par chaque `...Schema` est la suivante:

```java
public interface Schema<T> {
    T getTarget();
    
    Set<Type> getTypes();
}
```

Pour une utilisation avec des cibles appelables, elle est étendue par `ExecutableSchema`:

```java
public interface ExecutableSchema<T extends Executable> extends Schema<T> {
    Type[] getMapping();
}
```

Il existe 4 implémentations au total:

* `FieldSchema` – schéma d'injection pour un champ;
* `ConstructorSchema` – schéma d'injection pour un constructeur, implémente l'interface `ExecutableSchema` ; un seul est autorisé par classe;
* `MethodSchema` – schéma d'injection pour une méthode, implémente l'interface `ExecutableSchema`;
* `ClassSchema` – schéma d'injection pour la classe entière.

### Fabrique de schémas

Les fabriques de schémas sont utilisées pour générer des schémas pour une classe spécifiée. Chaque fabrique doit implémenter l'interface `SchemaFactory`:

```java
public interface SchemaFactory {
    ClassSchema create(Class<?> clazz);
}
```

Par défaut, une implémentation est fournie qui recherche les cibles d'injection parmi les membres de la classe (`ReflectSchemaFactory`). La logique suivante est appliquée:

1)  Tous les constructeurs publics de la classe (`Class#getConstructors()`) sont examinés:
    1)  S'il n'y en a qu'un, il est sélectionné;
    2)  S'il y en a plusieurs, celui annoté avec l'annotation marqueur est sélectionné;
    3)  Sinon, la classe est considérée comme invalide;
2)  Tous les champs virtuels publics de la classe annotés avec l'annotation marqueur sont recherchés;
3)  Toutes les méthodes publiques de la classe annotées avec l'annotation marqueur sont recherchées:
    1)  Si la méthode est virtuelle, elle est sélectionnée;
    2)  Si la méthode est statique et que le type de son premier argument est égal ou est un super-type de la classe cible, elle est sélectionnée;
    3)  Sinon, la méthode est considérée comme invalide.

L'annotation marqueur peut être n'importe quelle annotation (le framework utilise par défaut `@Inject`, fournie avec le module `amaya-di`) et est spécifiée comme paramètre du constructeur.

De plus, `ReflectSchemaFactory` permet de spécifier un "processeur de types" personnalisé, qui doit implémenter l'interface suivante:

```java
public interface TypeProcessor {
    Type process(Type type, AnnotatedElement element);
}
```

Le processeur de types par défaut (`ReflectTypeProcessor`) analyse à la fois les types "simples" (instances de `Class`) et les types génériques. De plus, lors du traitement, tous les types wildcard sont convertis en bornes supérieures:

* `List<? extends String>` =\> `List<String>`;
* `List<? super String>` =\> `List<Object>`;
* `List<?>` =\> `List<Object>`.

Les variables de type sont totalement interdites, car il est impossible de les convertir de manière univoque. Par exemple, si on les convertissait au premier type dans les bornes supérieures (`<T>` =\> `Object`, `<T extends Number>` =\> `Number`), dans le cas d'un cycle `class A<T extends A<T>>`, il serait impossible de déterminer de manière univoque comment le rompre.

## Génération d'ObjectFactory

### Fabrique de stubs

Dans le contexte du framework, les stubs sont des implémentations de `ObjectFactory`, générées automatiquement conformément au schéma d'injection de la classe. Les stubs sont créés à l'aide de `StubFactory`, dont l'interface de base est la suivante:

```java
@FunctionalInterface
public interface StubFactory {
    ObjectFactory create(ClassSchema schema, CacheMode mode);
    
    default ObjectFactory create(ClassSchema schema) {
        return create(schema, CacheMode.NONE);
    }
}
```

Toutes les implémentations **doivent** suivre strictement le schéma d'injection et le mode de cache spécifiés. Si, pour une raison quelconque, il est impossible de créer une instance `ObjectFactory` **prête à l'emploi**, ou si une partie des dépendances ne peut pas être injectée (par exemple, un champ cible s'est avéré privé), la fabrique doit lever une exception. Le renvoi de `null` et/ou le renvoi d'une implémentation primitive comme `provider -> null` ne sont pas autorisés.

### Mise en cache d'ObjectFactory

Dans la plupart des scénarios, le conteneur construit est utilisé uniquement en mode lecture seule. Par conséquent, pour les `ObjectFactory` générées dans de tels cas, il est logique de stocker directement les implémentations des fabriques pour les types dont elles dépendent. Cela minimisera (ou évitera complètement) les appels inutiles à `TypeProvider#get(type)`, ce qui réduit considérablement la surcharge.

Les modes suivants sont introduits pour une gestion explicite du cache:

* `CacheMode.FULL` – la requête vers `TypeProvider` n'est pas effectuée du tout, les fabriques nécessaires sont récupérées directement du cache interne;
* `CacheMode.PARTIAL` – la requête vers `TypeProvider` est effectuée si la fabrique requise n'est pas dans le cache interne ; le résultat de la requête **n'est pas enregistré** (pour un accès thread-safe au conteneur);
* `CacheMode.NONE` – la requête vers `TypeProvider` est toujours effectuée, le cache interne n'existe pas.

De plus, toutes les `ObjectFactory` avec un cache interne doivent implémenter l'interface `CachedObjectFactory`, permettant d'initialiser ou de mettre à jour le contenu:

```java
public interface CachedObjectFactory extends ObjectFactory {
    void set(Type type, ObjectFactory factory);
}
```

# Construction du conteneur

La création et le remplissage du conteneur peuvent être effectués soit directement (par l'implémentation et l'instanciation de toutes les entités nécessaires), soit à l'aide d'utilitaires personnalisés.

Par défaut, le framework fournit un mécanisme de construction basé sur le modèle "builder". Ses fonctionnalités comprennent:

* La gestion des types génériques à l'aide de [jtype](https://github.com/RomanQed/jtype);
* L'ajout d'implémentations `ObjectFactory` personnalisées;
* L'ajout de types transient et singleton par leur classe;
* L'ajout de types scoped;
* La validation du graphe de dépendances pour détecter les cycles et les types manquants.

## Scénarios d'utilisation de base

Les variantes de constructeurs sont représentées par les interfaces `ServiceProviderBuilder` et son extension `ScopedProviderBuilder`, qui ajoute des fonctionnalités scoped. L'obtention de leurs instances est prévue à l'aide de la classe utilitaire `ProviderBuilders`. Elle fournit des méthodes statiques pour créer toutes les implémentations incluses dans le framework et est, en substance, le point d'entrée de l'API.

Il existe les principales méthodes suivantes (voir le javadoc pour les surcharges):

```java
// Crée un constructeur avec les fabriques et les vérifications spécifiées
// Si checks == BuilderChecks.NO_CHECKS, une implémentation sans mécanismes de validation est utilisée
public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {...}

// Crée un constructeur avec la fabrique de schémas d'injection et le mode de cache par défaut (ProviderBuilders#SCHEMA_FACTORY et #CACHE_MODE)
// Si checks == BuilderChecks.NO_CHECKS, une implémentation sans mécanismes de validation est utilisée
public static ServiceProviderBuilder create(int checks) {...}

// Crée un constructeur avec les fabriques spécifiées, ProviderBuilders#CACHE_MODE et sans vérifications
public static ServiceProviderBuilder create(SchemaFactory schemaFactory, StubFactory stubFactory) {}

// Crée un constructeur avec ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE et sans vérifications.
public static ServiceProviderBuilder create() {...}

// Crée un constructeur avec ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE et BuilderChecks.VALIDATE_ALL.
public static ServiceProviderBuilder createChecked() {...}

// Crée un constructeur scoped avec les fabriques et les vérifications spécifiées
// Si checks == BuilderChecks.NO_CHECKS, une implémentation sans mécanismes de validation est utilisée
public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory, int checks) {...}

// Crée un constructeur scoped avec la fabrique de schémas d'injection et le mode de cache par défaut (ProviderBuilders#SCHEMA_FACTORY et #CACHE_MODE)
// Si checks == BuilderChecks.NO_CHECKS, une implémentation sans mécanismes de validation est utilisée
public static ScopedProviderBuilder createScoped(int checks) {...}

// Crée un constructeur scoped avec les fabriques spécifiées, ProviderBuilders#CACHE_MODE et sans vérifications
public static ScopedProviderBuilder createScoped(SchemaFactory schemaFactory, StubFactory stubFactory) {...}

// Crée un constructeur scoped avec ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE et sans vérifications.
public static ScopedProviderBuilder createScoped() {...}

// Crée un constructeur scoped avec ProviderBuilders#SCHEMA_FACTORY, #CACHE_MODE et BuilderChecks.VALIDATE_ALL.
public static ScopedProviderBuilder createCheckedScoped() {...}
```

Pour un exemple montrant toutes les capacités des constructeurs, voir [ici](../../examples/src/main/java/com/github/romanqed/di/examples/AllMethods.java).

## Types génériques

Pour enregistrer un type générique dans le conteneur, vous pouvez utiliser jtype ou des bibliothèques tierces capables de fournir des implémentations de l'interface `java.lang.reflect.Type` pour les types paramétrés. Ci-dessous un exemple montrant la capture automatique du type et sa création manuelle:

```java
public final class GenericTypes {
    public static void main(String[] args) {
        var provider = ProviderBuilders.create(new ReflectStubFactory(), BuilderChecks.VALIDATE_MISSING_TYPES)
                .addInstance(new JType<>(){}, List.of("str1", "str2", "str3")) // auto-catch
                .addInstance(Types.of(List.class, Integer.class), List.of(1, 2, 3)) // manual
                // pour "new JType<>(){}" le type sera Map<String, String>
                .addInstance(new JType<Map<String, Object>>(){}, Map.of("s1", "k1", "s2", "k2")) // et pour la map
                .addInstance(Types.of(Map.class, Integer.class, Object.class), Map.of(1, 1, 2, 2))
                .addTransient(A1.class)
                .addTransient(A2.class)
                // B<A1> et B<A2>
                .add(Types.of(B.class, A1.class), tp -> new B<>((A1) tp.get(A1.class).create(tp)))
                .add(new JType<B<A2>>(){}, tp -> new B<>((A2) tp.get(A2.class).create(tp)))
                // Et, enfin, C
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

Le résultat sera:

```
[1, 2, 3]
[str1, str2, str3]
{1=1, 2=2}
{s2=k2, s1=k1}
```

## Validation du graphe de dépendances

Pour implémenter le mécanisme de validation, les conventions suivantes sont utilisées:

* Toutes les implémentations de `ObjectFactory` fournies par l'utilisateur sont inconditionnellement considérées comme correctes et sont traitées comme des types "racines" n'ayant pas de dépendances propres;
* Tous les `ServiceWrapper` personnalisés renvoient des `ObjectFactory` correctes et ne créent pas d'erreurs;
* Tous les types pour lesquels un schéma d'injection peut être construit sont considérés comme "faibles" et seront vérifiés pour:
    * La requête de dépendances manquantes;
    * La création de cycles dans le graphe de dépendances.

Pour les conteneurs scoped, les éléments suivants sont introduits en complément:

* L'enregistrement d'un type promis **garantit** que lors de la création d'un conteneur scoped, AVANT de commencer à l'utiliser, le type sera fourni dans le référentiel;
* Les types de portée peuvent faire référence à d'autres types de portée et à des types de base;
* Les types de base peuvent faire référence UNIQUEMENT à d'autres types de base.

En se basant sur cela et en considérant le conteneur comme un graphe de dépendances, cinq classes d'erreurs possibles peuvent être identifiées.

1.  Un type dont dépend un autre type de base est manquant dans le conteneur de base.

    Exemple:

    ```
    class A -> class String
    class String -> manquant
    ```

2.  Un type dont dépend un autre type scoped est manquant dans le conteneur scoped.

    Exemple:

    ```
    class ScopedA -> class String
    class String -> manquant dans le conteneur scoped et le conteneur de base
    ```

<!-- end list -->

3)  Une dépendance circulaire entre les types de base s'est produite dans le conteneur de base.

    Exemple:

    ```
    class A -> class B
    class B -> class C
    class C -> class A
    ```

4)  Une dépendance circulaire entre les types scoped s'est produite dans le conteneur scoped.

5)  Une dépendance circulaire entre les conteneurs s'est produite en raison de l'écrasement d'un type par un conteneur scoped.

    Exemple:

    ```
    class A -> class B
    class B -> interface IC
    interface IC: C
    interface IC: ScopedC -> A
    ```

Le mécanisme de validation intégré prend en charge le traitement de ces 5 types d'erreurs. Pour un exemple, voir [ici](../../examples/src/main/java/com/github/romanqed/di/examples/AllChecks.java).

# Variantes des générateurs ObjectFactory

Le framework fournit deux implémentations de `StubFactory`, fournies dans les modules `amaya-di-asm` et `amaya-di-reflect`.

## ASM

L'implémentation ASM utilise la génération de bytecode à la volée pour créer `ObjectFactory`. La classe générée reçoit un nom composé du nom de la classe cible de l'injection et de l'alias du mode de cache utilisé. Par conséquent, lors d'une nouvelle demande de génération, elle ne se produit pas, mais la classe déjà chargée, obtenue par son nom, est utilisée.

La particularité de l'implémentation ASM est une vitesse de préparation relativement longue et une vitesse d'exécution maximale possible, pratiquement égale à celle du code normal.

La mise en cache du bytecode est également implémentée, ce qui permet de réutiliser une classe générée une fois. Ceci est extrêmement utile lorsque le fichier jar final (et, ce qui est important, inchangé à l'avenir) est construit et sera publié en production:

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

Ce code, après la première exécution, créera un répertoire cache où il stockera le bytecode généré. Lors des exécutions suivantes, au lieu de générer, il le chargera directement. Si le cache est supprimé, la génération aura lieu à nouveau.

## Reflect

L'implémentation Reflect utilise des appels de méthode réflexifs et l'accès aux champs pour créer `ObjectFactory`. Les membres de classe demandés sont marqués comme accessibles sans vérification des modificateurs (`setAccessible(true)`) et sont enveloppés dans une implémentation de fabrique préparée.

La particularité de l'implémentation Reflect est une vitesse de préparation relativement rapide et une vitesse d'exécution lente, incluant des surcharges importantes pour les mécanismes d'accès réflexif.

## Choix de l'implémentation

Le choix entre les implémentations ASM et Reflect doit être fait en fonction des particularités du scénario d'utilisation du conteneur. Si dans votre application le conteneur:

* Est souvent reconstruit;
* Est utilisé une seule fois au démarrage;
* N'est pas utilisé dans le code "chaud" (ou si des surcharges élevées sont acceptables);

alors il convient d'utiliser l'implémentation Reflect.

Si, par contre, le conteneur:

* Est construit une seule fois;
* Est constamment utilisé dans le code "chaud";
* Doit fournir des dépendances en un minimum de temps;

alors il convient d'utiliser l'implémentation ASM.

## Benchmarks

Les benchmarks ont été réalisés sur Amazon Corretto 11 (`openjdk version "11.0.27" 2025-04-15 LTS`), sur un PC (pas un serveur) avec Intel i5-9400F, 40 Go de RAM et Windows 10 22H2. Le framework jmh version 1.37 a été utilisé.

L'ensemble des dépendances se compose de:

* Service1;
* Service2;
* Service3, dépendant de Service1 (constructeur);
* App, dépendant des trois (Service1 – constructeur, Service2 – champ, Service3 – méthode).

Le conteneur est entièrement préparé avant le benchmark, aucune interaction n'est effectuée avec lui.

Pour obtenir un temps de référence avec lequel les autres résultats peuvent être comparés, un code simple a été écrit, réalisant la création "manuelle" de l'application et l'injection des dépendances:

```java
var app = new App(new Service1());
app.s2 = new Service2();
app.setS3(new Service3(new Service1()));
```

De plus, les benchmarks pour obtenir des données sur la surcharge du référentiel lui-même (pour la recherche de type) ont été réalisés en deux versions:

* Le temps "pur" d'exécution de la création manuelle de l'application a été mesuré;
* La création de l'application a été enveloppée dans `ObjectFactory` et placée dans le conteneur, le temps d'appel de recherche dans le conteneur et l'appel de `create()` ont été mesurés.

Le temps pour chaque CacheMode a également été mesuré séparément, et de nouveau pour CacheMode.FULL (mode par défaut).

Les résultats suivants ont été obtenus:

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

L'exécution des benchmarks se fait comme suit:

```
>./gradlew asm:jmh
>./gradlew reflect:jmh
>./gradlew :jmh
```

# Différences par rapport à la version 2.x

Lors de la transition de 2.x à 3.x, l'API de base du framework a perdu toute compatibilité. L'API des constructeurs, à l'exception des nouvelles méthodes, est restée la même. Ceux qui utilisaient amaya-di 2.x et souhaitent passer à 3.x doivent tenir compte des changements clés suivants:

* `ObjectFactory` est désormais utilisé comme fabrique d'objets, et non `Function0`;
* L'interface `ServiceRepository` a été renommée `TypeRepository`;
* L'interface `TypeProvider`, qui était un mécanisme local pour les besoins du module stub, a été entièrement supprimée;
* L'interface `ManualProviderBuilder` a été entièrement supprimée;
* L'API de `StubFactory` a été modifiée : `create(schema, typeProvider)` =\> `create(schema, cacheMode)`;
* Désormais, `StubFactory` ne remplit plus le cache de la fabrique lors de la création, cela doit être fait manuellement;
* Le module `jgraph` n'est plus utilisé et a été exclu des dépendances;
* La classe abstraite `AbstractProviderBuilder` a été remplacée par `AbstractServiceProviderBuilder`, l'API protégée a été entièrement modifiée;
* L'interface `ServiceWrapper` n'hérite plus de l'interface `Function1`, la signature de sa méthode fonctionnelle est désormais `ObjectFactory wrap(ObjectFactory factory)`;
* La classe `LazyProvider` a été entièrement supprimée;
* Une nouvelle exception `CyclesFoundException` est apparue, qui peut être levée si plusieurs cycles sont détectés;
* La division en modules a été entièrement modifiée:
    * `ServiceProvider` et les types associés ont été déplacés dans un module séparé `amaya-di-core`;
    * `ClassSchema` et les types associés ont été déplacés dans un module séparé `amaya-di-schema`;
    * `StubFactory` et les types associés ont été déplacés dans un module séparé `amaya-di-stub`;
    * Le module `amaya-di` inclut désormais ces trois modules, le module `amaya-di-core` est spécifié comme une dépendance transitive;
* Un nouveau mécanisme de scopes est apparu.

# Contribuer

Je serais très heureux si vous souhaitez proposer vos corrections, améliorations et extensions de fonctionnalités à amaya-di. Un guide détaillé sur la contribution se trouve [ici](../contributing/contributing_fr.md).

# Créé avec

* [Gradle](https://gradle.org) - Gestion des dépendances
* [ASM](https://asm.ow2.io) - Génération d'`ObjectFactory`
* [jeflect-loader](https://github.com/RomanQed/jeflect) - Chargement de bytecode en runtime
* [jfunc](https://github.com/RomanQed/jfunc) - Interfaces fonctionnelles, utilitaires
* [jtype](https://github.com/RomanQed/jtype) - Utilitaires pour la gestion des génériques

# Auteurs

* [RomanQed](https://github.com/RomanQed) - *Travail principal*

Consultez également la liste des [contributeurs](https://github.com/AmayaFramework/amaya-di/contributors) qui ont contribué à ce projet.

# Licence

Ce projet est sous licence Apache License Version 2.0 - voir le fichier [LICENSE](../../LICENSE) pour plus de détails.