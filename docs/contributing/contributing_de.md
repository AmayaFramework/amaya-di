# Beitrag zu amaya-di leisten

- [English](../../CONTRIBUTING.md)
- [Русский](contributing_ru.md)
- [简体中文](contributing_zh_cn.md)
- [正體中文](contributing_zh_tw.md)
- [日本語](contributing_jp.md)
- Deutsch (AI translated, GPT-4o)
- [Français](contributing_fr.md)

Vielen Dank für Ihr Interesse an amaya-di! In dieser Datei wird beschrieben, wie Sie beitragen können – sei es durch Pull Requests,  
die Erstellung von Drittanbieter-Modulen oder die Diskussion von Features. Sie können Ihre eigene Implementierung einer Schema-Fabrik,  
einer Stub-Fabrik erstellen, ein Pendant zum Modul `amaya-di` bauen (siehe [Erstellung von Drittanbieter-Modulen](#erstellung-von-drittanbieter-modulen))  
oder einen Pull Request in dieses [Repository](https://github.com/AmayaFramework/amaya-di) einreichen  
(siehe [Erstellung von Pull Requests](#erstellung-von-pull-requests)).

Jede Möglichkeit wird weiter unten detailliert behandelt. Zunächst jedoch listen wir die Grundprinzipien auf, deren Einhaltung  
für die Entwicklung von Drittanbieter-Modulen sehr empfohlen und für das Einbringen von Pull Requests verpflichtend ist.

## Grundprinzipien

amaya-di ist ein moderner DI-Framework, der auf Zuverlässigkeit, hohe Performance und Minimalismus ausgelegt ist. Daher  
halten wir uns an die folgenden Prinzipien:

* Wir schätzen ein vorsichtiges Gleichgewicht zwischen sauberem, verständlichem Code und Geschwindigkeit:
    * Vermeiden Sie unnötige Entitäten;
    * Wenn ein Codeabschnitt nicht selbsterklärend ist, kommentieren Sie ihn möglichst;
    * Das öffentliche API, das den Nutzern zur Verfügung steht, sollte so benutzerfreundlich, selbstdokumentierend und flexibel wie möglich sein;
    * Der Verzicht auf ein klar kontrolliertes und gut erweiterbares API zugunsten von impliziter Magie, globalen Containern und  
      Annotationen ist ein direkter Weg zum Untergang und wird niemals genehmigt.
* Änderungen müssen bestehende Probleme lösen oder die Stabilität, Performance oder Lesbarkeit verbessern. Features um der Features willen sind nicht erwünscht.
* Vorab-Diskussionen zu Architekturentscheidungen und/oder konstruktive Kritik sind sehr willkommen.
* Minimale JDK-Version ist 11. PRs, die auf niedrigere Versionen portieren, werden abgelehnt.
* Wir verfolgen eine Politik der Minimierung und Reduzierung von Abhängigkeiten:
    * Fügen Sie keine Bibliotheken nur für eine einzelne Funktion hinzu;
    * Das Einbinden von `guava`, `apache-commons` oder anderen großen Paketen führt automatisch zur Ablehnung (Ausnahme: reduzierte/modulare Versionen mit nur verwendetem Funktionsumfang);
    * Implementierungen anderer DI-APIs sollten nicht in `amaya-di` oder dessen Basismodule eingebunden werden; dafür gibt es separate Module;
    * Wenn eine Funktionalität mit Java SDK in angemessener Zeit und Qualität umgesetzt werden kann, sollten keine externen Abhängigkeiten verwendet werden.
* Änderungen am öffentlichen API müssen dessen Universalität bewahren. Eine Spezialisierung auf eine Funktion zu Lasten anderer ist nicht zulässig.
* Für kleine Fehlerbehebungen oder Verbesserungen senden Sie bitte PRs, anstatt Forks oder Neuveröffentlichungen unter demselben Namen zu erstellen. Dies hält das Ökosystem sauber und vermeidet Verwirrung.
* Wenn Sie eine Funktion basierend auf Bytecode-Generierung implementieren möchten:
    * Verzichten Sie auf langsame, "intelligente" oder umfangreiche Bibliotheken (`bytebuddy` etc.); wir bevorzugen reines `org.ow2.asm:asm` oder die [Class-File API](https://openjdk.org/jeps/484);
    * Verwenden Sie keine Java Agents oder Mechanismen zur Modifikation bereits geladenen Bytecodes – diese Funktionalität ist nicht auf allen JVMs verfügbar;
    * Stellen Sie sicher, dass Ihr Generierungsalgorithmus ausreichend performant ist und in akzeptabler Zeit arbeitet;
    * Der generierte Bytecode soll optimiert und performant sein – Optimierungen sind hier nie überflüssig;
    * Vermeiden Sie, dass Ihr Bytecode vom internen JVM-Zustand abhängt und stellen Sie sicher, dass er nach einem Neustart unverändert geladen werden kann:
        * Verwenden Sie für Deskriptoren und Namen nur stabile Daten;
        * Wenn bei der Datenübergabe die Reihenfolge wichtig ist, verwenden Sie Collections mit stabiler Ordnung (z.B. `TreeMap`);
        * Falls Unabhängigkeit nicht erreicht werden kann, dokumentieren Sie dies zwingend.

## Erstellung von Drittanbieter-Modulen

amaya-di ist ein Metaprojekt, das Basismodule (core, schema, stub) und eine übergreifende Hülle umfasst.  
Alle Module werden als eigenständige JPMS-Module und Maven-Artefakte veröffentlicht.  
Dadurch können nur benötigte Teile des Frameworks eingebunden werden.

Die Entwicklung eines Drittanbieter-Moduls erfolgt in einem separaten Java- (oder einem anderen JDK-Sprach-) Projekt und einem eigenen Repository.  
Wenn Sie jedoch der Meinung sind, dass Ihr Modul grundlegende oder allgemein genug nutzbare Funktionalität bietet, um in den Hauptsatz aufgenommen zu werden, erstellen Sie einen PR mit einem Gradle-Modul (dazu `include '<your_module_name>'` in `settings.gradle` hinzufügen, im Projektstammverzeichnis ein gleichnamiges Verzeichnis mit `build.gradle` und den passenden `src`-Unterverzeichnissen anlegen).

Anschließend müssen die Abhängigkeiten eingerichtet werden. `amaya-di` verwendet ein Lieferprinzip ähnlich dem von `jakarta`:

* Jedes logische Modul ist ein eigenständiges JPMS-Modul, veröffentlicht im Maven Central;
* Module liefern öffentliche APIs (hauptsächlich Interfaces und grundlegende Utilities);
* Die Implementierung liefern das Framework selbst, Drittanbieter-Module binden diese als `compileOnly` ein.

Als Beispiel können Sie die Implementierungen von [asm](../../asm/build.gradle) und [reflect](../../reflect/build.gradle) verwenden.

Die Auswahl der Module hängt von der zu realisierenden Funktion ab. Entscheiden Sie, welche API-Teile Sie brauchen, und binden Sie die entsprechenden Module ein. Achten Sie zudem auf den Sichtbarkeitsbereich Ihrer Abhängigkeiten.

* `amaya-di-core`: `ObjectFactory`, `TypeProvider`, `TypeRepository`, `ServiceProvider` und deren Implementierungen (siehe README);
* `amaya-di-schema`: `ClassSchema` (und Varianten), `SchemaFactory` und deren Implementierung (siehe README);
* `amaya-di-stub`: `CachedObjectFactory`, `CacheMode`, `StubFactory` (+ core- und schema-Typen);
* `amaya-di`: alle drei Module plus builder-API.

Implementieren Sie dann die gewünschte Funktionalität und überlegen Sie, wie der Endnutzer darauf zugreifen wird (der "Einstiegspunkt" ins API).  
Der Code, der am Containerbau und dessen Laufzeit beteiligt ist, muss unbedingt durch Tests abgesichert sein.  
Wenn Sie wollen, veröffentlichen Sie Ihre Bibliothek im Maven Central (bei technischen Problemen helfen die Framework-Konfiguration oder ich gern weiter).

Ich freue mich, wenn Sie nach der Veröffentlichung einen Pull Request erstellen oder ein Issue schreiben, um Ihre Implementierung in der Dokumentation zu verlinken.

## Erstellung von Pull Requests

### Wie anfangen

1. Forken Sie das Repository.
2. Erstellen Sie einen neuen Branch mit einem aussagekräftigen Namen: `feature/<feature-name>` oder `bugfix/<bugfix-description>`.
3. Schreiben Sie Code, der dem bestehenden Stil, der Architektur und den oben genannten [Prinzipien](#grundprinzipien) folgt.
4. Fügen Sie Tests für Ihre Änderungen hinzu, falls noch nicht vorhanden.
5. Fügen Sie eine ausführliche Beschreibung der Änderungen in freier Form bei.
6. Erstellen Sie den Pull Request.

### Anforderungen an den Code

* Der Code muss lesbar sein und guten OOP-Praktiken folgen.
* Plattform- und JVM-spezifische Lösungen sind zu vermeiden.
* Der Hauptfunktionsumfang muss mit Tests (positiv und negativ) abgedeckt sein.
* Es ist nur die JDK-Version erlaubt, die im Projekt verwendet wird (aktuell 11).
* Änderungen an bestehenden Kernmodulen sind nur in Java erlaubt.
* Für neue Module oder Änderungen in Modulen in anderen Sprachen sind alle JVM-Sprachen (11+) oder die Ursprungssprache des Moduls erlaubt.

### Kommunikation

* Pull Requests müssen eine verständliche Beschreibung enthalten: was, warum und wie.
* Fragen und Vorschläge bitte über [Issues](https://github.com/AmayaFramework/amaya-di/issues).
* Unhöflichkeit, persönliche Angriffe oder Toxizität führen zur sofortigen Schließung von PRs/Issues ohne Diskussion.
* Wir garantieren keine schnelle Rückmeldung. Das Projekt wird in der Freizeit entwickelt.

### Branches und Releases

* Der `main`-Branch steht immer für den aktuell stabilen Major-Release.
* Branches `release/<major>.<minor>` sind stabil und enthalten Tags für kleinere Updates.
* Feature-Branches und Bugfixes werden separat geführt.
* Für die Annahme von PRs muss der Code kompilierbar sein, Tests bestehen und ein Review erfolgreich abgeschlossen sein.

## Was nicht erwünscht ist

- Verletzung der Grundprinzipien des Frameworks.
- Änderungen ohne Tests.
- Hinzufügen komplexer Features ohne Begründung.
- Stiländerungen ohne Grund.
- Große Refactorings ohne vorherige Absprache.

---

Danke, dass Sie helfen, amaya-di besser zu machen.

---

*Dieses Dokument kann sich ändern, bitte verfolgen Sie Updates.*
