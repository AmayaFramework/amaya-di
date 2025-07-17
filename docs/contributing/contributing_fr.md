# Contribuer à amaya-di

- [English](../../CONTRIBUTING.md)
- [Русский](contributing_ru.md)
- [简体中文](contributing_zh_cn.md)
- [正體中文](contributing_zh_tw.md)
- [日本語](contributing_jp.md)
- [Deutsch](contributing_de.md)
- Français (AI translated, GPT-4o)

Merci de votre intérêt pour amaya-di ! Ce fichier décrit comment contribuer — que ce soit par pull request,  
création de modules tiers ou discussion des fonctionnalités. Vous pouvez créer votre propre implémentation d’une fabrique de schémas d’injection,  
une fabrique de stubs, construire un équivalent du module `amaya-di` (voir [création de modules tiers](#création-de-modules-tiers))  
ou faire une pull request sur ce [répertoire](https://github.com/AmayaFramework/amaya-di)  
(voir [création de pull request](#création-de-pull-request)).

Chaque option sera détaillée ci-dessous. Mais avant tout, voici les principes fondamentaux dont le respect est  
fortement recommandé pour le développement de modules tiers et obligatoire pour l’intégration d’une pull request.

## Principes fondamentaux

amaya-di est un framework DI moderne, orienté vers la fiabilité, la haute performance et le minimalisme. Nous suivons donc  
les principes suivants.

* Nous valorisons un équilibre prudent entre un code clair et compréhensible et la performance :
    * éviter de multiplier les entités inutiles ;
    * si une partie du code n’est pas évidente en soi, la commenter autant que possible ;
    * l’API publique destinée aux utilisateurs doit être aussi conviviale, auto-documentée et flexible que possible ;
    * renoncer à une API explicitement contrôlée et bien intégrable au profit de magie implicite, de conteneurs globaux et  
      d’annotations est une voie directe vers l’échec, de tels changements ne seront jamais approuvés.
* Toute modification doit résoudre un problème existant, améliorer la stabilité, la performance ou la lisibilité.  
  Pas de fonctionnalités pour les fonctionnalités.
* Les discussions préalables sur les décisions architecturales et/ou les critiques constructives des fonctionnalités sont vivement encouragées.
* Le niveau minimal de JDK est 11. Les PR visant des versions plus anciennes seront rejetées.
* Nous suivons une politique de minimisation et de réduction maximale des dépendances utilisées :
    * ne pas ajouter de bibliothèques pour une seule fonction ;
    * la présence de `guava`, `apache-commons` ou d’autres paquets monstrueux entraînera automatiquement le rejet du PR (cependant, cela ne s’applique pas à leurs versions réduites/modulaires contenant uniquement les fonctionnalités utilisées) ;
    * ne pas intégrer dans `amaya-di` ou ses modules de base des implémentations d’autres API DI, des modules séparés existent pour cela ;
    * si une fonctionnalité requise peut être réalisée avec le SDK Java en temps raisonnable et avec une qualité acceptable, il ne faut pas utiliser de dépendances externes.
* Les modifications apportées à l’API publique doivent en préserver l’universalité. Adapter l’API pour une fonctionnalité spécifique au détriment des autres n’est pas autorisé.
* Si vous souhaitez faire une petite correction ou amélioration, veuillez envoyer un PR plutôt que de créer un fork ou republier le paquet sous le même nom. Cela aide à garder l’écosystème propre et évite la confusion.
* Si vous souhaitez implémenter une fonctionnalité basée sur la génération de bytecode :
    * évitez l’utilisation de bibliothèques lentes, « intelligentes » et/ou volumineuses (`bytebuddy`, etc.) ; notre choix est  
      l’usage pur de `org.ow2.asm:asm` ou de l’[API Class-File](https://openjdk.org/jeps/484) ;
    * ne pas utiliser les java agents ni les mécanismes de modification du bytecode chargé — cette fonctionnalité n’est pas disponible sur toutes les JVM ;
    * assurez-vous que votre algorithme de génération est suffisamment optimisé et s’exécute en temps raisonnable ;
    * assurez-vous que le bytecode généré est optimal et performant — les optimisations sont toujours les bienvenues ;
    * assurez-vous, autant que possible, que votre bytecode ne dépend pas de l’état interne de la JVM et peut être chargé sans modification après redémarrage :
        * utilisez pour les descripteurs et noms uniquement des données stables ;
        * si l’ordre des données transmises est important, assurez-vous que les collections utilisées ont un ordre stable (ex. `TreeMap`) ;
        * si pour une raison quelconque cette indépendance ne peut être obtenue, mentionnez-le impérativement dans la documentation.

## Création de modules tiers

amaya-di est un méta-projet incluant des modules de base (core, schema, stub) et leur enveloppe unificatrice.  
Tous les modules sont publiés en tant que modules JPMS indépendants et artefacts Maven.  
Cela permet de ne charger que les parties nécessaires du framework.

Le développement d’un module tiers se fait dans un projet Java (ou tout autre langage JDK) séparé et dans un dépôt séparé.  
Cependant, si vous pensez que votre module implémente une fonctionnalité basique ou suffisamment générale pour être incluse dans le jeu principal, créez un PR avec un module Gradle (ajoutez dans `settings.gradle`  
`include '<your_module_name>'`, créez un dossier à la racine du projet portant ce nom, ajoutez-y un `build.gradle` et les sous-dossiers `src` correspondants).

Ensuite, après création du projet, il faut configurer les dépendances. `amaya-di` suit un principe de livraison similaire à celui de `jakarta` :

* chaque module logique est un module JPMS indépendant publié sur Maven Central ;
* les modules fournissent une API publique (principalement des interfaces et utilitaires de base) ;
* les implémentations sont fournies par le framework lui-même, les modules tiers qui en dépendent les importent en `compileOnly`.

Vous pouvez prendre pour exemple les implémentations [asm](../../asm/build.gradle) et [reflect](../../reflect/build.gradle).

La sélection des modules dépend de la fonctionnalité que vous souhaitez réaliser. Décidez quelle partie de l’API du framework vous avez besoin et importez les modules correspondants. Veillez également à la portée de vos dépendances.

* `amaya-di-core` : `ObjectFactory`, `TypeProvider`, `TypeRepository`, `ServiceProvider` et leurs implémentations (voir README) ;
* `amaya-di-schema` : `ClassSchema` (et variantes), `SchemaFactory` et son implémentation (voir README) ;
* `amaya-di-stub` : `CachedObjectFactory`, `CacheMode`, `StubFactory` (+ types core et schema) ;
* `amaya-di` : les trois modules plus builder-API.

Implémentez ensuite la fonctionnalité souhaitée et réfléchissez au mécanisme par lequel l’utilisateur final pourra y accéder (le « point d’entrée » dans l’API).  
Couvrez impérativement de tests le code participant à la construction et au fonctionnement du conteneur.  
Si vous le souhaitez, publiez votre bibliothèque sur Maven Central (en cas de difficultés techniques, consultez la configuration du framework ou contactez-moi).

Je serai ravi si, après publication, vous créez un pull request ou ouvrez un issue proposant d’ajouter un lien vers votre implémentation dans la documentation.

## Création d’une pull request

### Comment commencer

1. Forkez le dépôt.
2. Créez une nouvelle branche avec un nom explicite : `feature/<feature-name>` ou `bugfix/<bugfix-description>`.
3. Codez en suivant le style existant, l’architecture et les [principes](#principes-fondamentaux) mentionnés plus haut.
4. Couvrez vos changements par des tests s’ils n’existent pas encore.
5. Joignez une description détaillée des changements en format libre.
6. Créez la pull request.

### Exigences pour le code

* Le code doit être lisible et suivre les bonnes pratiques de la POO.
* Pas de solutions dépendantes de plateformes ou JVM spécifiques.
* Le cœur fonctionnel doit être testé (scénarios positifs et négatifs).
* Seule la version JDK utilisée dans le projet (actuellement 11) est autorisée.
* Pour les modifications des modules principaux existants, seule la Java est autorisée.
* Pour les nouveaux modules ou changements dans des modules dans d’autres langages, tous les langages JVM (11+) ou le langage d’origine du module sont permis.

### Communication

* Les pull requests doivent contenir une description claire : quoi, pourquoi et comment.
* Questions et propositions via les [issues](https://github.com/AmayaFramework/amaya-di/issues).
* Tout comportement négatif (grossièreté, attaques personnelles) ou toxicité entraîne la fermeture sans discussion des PR/issues.
* Pas de garantie de réponse rapide. Le projet est développé sur le temps libre.

### Branches et releases

* La branche `main` contient toujours la dernière version majeure stable.
* Les branches `release/<major>.<minor>` sont stables et contiennent des tags marquant les mises à jour mineures.
* Les branches de features et corrections de bugs sont séparées.
* Pour accepter un PR, le code doit compiler, passer les tests et être revu.

## Ce qui n’est PAS encouragé

- Violation des principes fondamentaux du framework.
- Modifications sans tests.
- Ajout de fonctionnalités complexes sans justification.
- Changements de style sans raison.
- Gros refactorings sans discussion préalable.

---

Merci de contribuer à améliorer amaya-di.

---

*Ce document peut évoluer, veuillez suivre les mises à jour.*
