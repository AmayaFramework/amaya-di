# 為 amaya-di 做出貢獻

- [English](../../CONTRIBUTING.md)
- [Русский](contributing_ru.md)
- [简体中文](contributing_zh_cn.md)
- 正體中文 (AI translated, GPT-4o)
- [日本語](contributing_jp.md)
- [Deutsch](contributing_de.md)
- [Français](contributing_fr.md)

感謝您對 amaya-di 的關注！本文將說明如何貢獻，包括提交 pull request、創建第三方模組或討論功能。您可以實作自己的注入架構工廠、stub 工廠，或構建類似 `amaya-di` 的模組（參見 [創建第三方模組](#創建第三方模組)），也可以對 [本倉庫](https://github.com/AmayaFramework/amaya-di) 提交 pull request（參見 [創建 pull request](#創建-pull-request)）。

以下將詳細說明每種方式。在此之前，我們強烈建議在開發第三方模組時遵循以下基本原則，對於合併 PR 則是強制要求。

## 基本原則

amaya-di 是一個現代的 DI 框架，重視穩定性、高效能與極簡設計。我們遵循以下原則：

* 在清晰可讀與高效執行之間保持謹慎平衡：
    * 避免不必要的實體與抽象；
    * 若某段程式碼本身不夠直觀，應盡量加以註解；
    * 向使用者暴露的公開 API 應盡可能友好、自我說明且具備靈活性；
    * 放棄清晰可控與可組合的 API，轉而使用隱式魔法、全域容器與註解，是死路一條，此類改動永遠不會被接受。
* 所有變更應解決實際問題，提升穩定性、效能或可讀性。不需要無目的的新功能。
* 對於架構設計或功能的討論與批評非常歡迎，只要它們是建設性的。
* 最低支援 JDK 為 11。將專案移植至更低版本的 PR 會被拒絕。
* 採取依賴最小化策略：
    * 不應為了一個功能而引入整個函式庫；
    * 如引入 `guava`、`apache-commons` 等龐大套件將直接導致 PR 被拒絕（但不包含其裁剪版／模組化版本，僅包含必要功能）；
    * `amaya-di` 本身及其核心模組中不得集成其他 DI API 的實作，應透過獨立模組進行；
    * 若某功能可用 Java SDK 在合理時間與品質內實作，請避免使用第三方依賴。
* 所有對公開 API 的改動應維持其通用性。不得為了某個功能而犧牲整體結構。
* 若只是想修復 bug 或進行小優化，請提交 PR 而非 fork 並以相同名稱重新發佈套件。這有助於維護生態清潔，避免混淆。
* 若您想實作基於位元組碼產生的功能：
    * 避免使用緩慢、智慧型或過於龐大的函式庫（如 `bytebuddy`）；我們推薦使用純粹的 `org.ow2.asm:asm` 或 [Class-File API](https://openjdk.org/jeps/484)；
    * 不要使用 java agents 或已載入 class 的修改機制 — 並非所有 JVM 都支援；
    * 請確保您的位元組碼產生器效能合理；
    * 請確保產生的 bytecode 經過最佳化並能高效執行；
    * 請盡量確保所產生的位元組碼不依賴 JVM 的內部狀態，可於重啟後無須改動地載入：
        * 僅使用穩定資料作為 descriptor 與名稱；
        * 若傳遞順序重要，請使用有穩定順序的集合（如 `TreeMap`）；
        * 若無法達成獨立性，請務必在文件中說明。

## 創建第三方模組

amaya-di 是一個元專案，包含核心模組（core、schema、stub）與整合封裝模組。
所有模組皆作為獨立 JPMS 模組與 Maven 套件發佈。
您可以按需引用所需部分。

開發第三方模組需在獨立專案與倉庫中進行（Java 或其他 JDK 語言皆可）。若您認為該模組提供了核心或通用功能，可提交包含 gradle 子模組的 PR（需在 settings.gradle 加入 `include '<your_module_name>'`，並於專案根目錄建立對應資料夾與 `build.gradle` 和 `src` 目錄）。

建立專案後，需設定依賴。`amaya-di` 採用與 `jakarta` 類似的供應模型：

* 每個邏輯模組是獨立的 JPMS 模組，發佈於 maven central；
* 模組提供公開 API（以 interface 與基礎工具為主）；
* 其實作由框架本身提供，第三方模組應以 `compileOnly` 引入依賴。

您可參考 [asm](../../asm/build.gradle) 與 [reflect](../../reflect/build.gradle) 的實作作為範例。

所需模組取決於您要實作的功能。請明確您需使用框架的哪一部分 API，並引入對應模組。也請注意控制依賴的範圍。

* `amaya-di-core`: `ObjectFactory`, `TypeProvider`, `TypeRepository`, `ServiceProvider` 及其實作（詳見 readme）；
* `amaya-di-schema`: `ClassSchema`（及其子類別）、`SchemaFactory` 與其實作（詳見 readme）；
* `amaya-di-stub`: `CachedObjectFactory`, `CacheMode`, `StubFactory`（包含 core 與 schema 類型）；
* `amaya-di`: 所有三個模組 + builder API。

請實作所需功能，並思考使用者如何透過 API 使用它（即“進入點”）。確保參與容器構建與運行的代碼已經過測試。
若想發佈至 maven central，可參考框架配置或聯繫我協助解決技術問題。

若您已發佈模組，歡迎提交 pull request 或開 issue 提議將其加入官方文件中。

## 創建 pull request

### 如何開始

1. fork 本倉庫。
2. 建立新分支，命名方式如：`feature/<feature-name>` 或 `bugfix/<bugfix-description>`。
3. 依照現有風格與結構進行開發，並遵循上述[原則](#基本原則)。
4. 若無對應測試，請補上。
5. 附上說明變更內容與理由。
6. 提交 Pull Request。

### 程式碼要求

* 程式碼應清晰可讀，符合良好 OOP 實踐。
* 不應引入平台／JVM 特定實作。
* 基本功能需有完整測試（正向與反向）。
* 只允許使用專案中指定的 JDK 版本（目前為 11）。
* 如涉及核心模組修改，僅允許使用 Java。
* 新模組／非 Java 語言的模組可使用任一 JVM 語言（JDK 11+）或原語言。

### 溝通方式

* Pull Request 必須有清晰說明（做了什麼、為什麼、怎麼做）。
* 有問題或建議請透過 [issues](https://github.com/AmayaFramework/amaya-di/issues)。
* 粗魯、攻擊人身或有毒的發言將導致 PR／issue 被關閉，不另行通知。
* 本專案為業餘開發，不保證快速回應。

### 分支與發行版本

* `main` 分支永遠為最新穩定 major 版本。
* `release/<major>.<minor>` 分支為穩定版，包含標記的 minor 發行。
* 功能與修復應使用獨立分支。
* PR 須能成功編譯、通過測試並通過審核。

## 以下行為不被鼓勵

- 違反框架基本原則；
- 無測試的改動；
- 添加未被充分論證的複雜功能；
- 無理由的格式／風格更動；
- 大型重構未經討論。

---

感謝您為 amaya-di 的發展做出貢獻。

---

*本文可能會變更，請持續關注更新。*
