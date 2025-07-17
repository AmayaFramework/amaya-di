# 参与贡献 amaya-di

- [English](../../CONTRIBUTING.md)
- [Русский](contributing_ru.md)
- 简体中文 (AI translated, GPT-4o)
- [正體中文](contributing_zh_tw.md)
- [日本語](contributing_jp.md)
- [Deutsch](contributing_de.md)
- [Français](contributing_fr.md)

感谢你对 amaya-di 感兴趣！本文档说明了如何参与贡献——无论是通过 pull request、开发第三方模块，还是参与功能讨论。你可以实现自定义的注入架构工厂、stub 工厂，或者构建 `amaya-di` 模块的替代实现（详见 [创建第三方模块](#创建第三方模块)），也可以向 [本仓库](https://github.com/AmayaFramework/amaya-di) 提交 pull request（详见 [创建 pull request](#创建-pull-request)）。

下面将详细介绍每种方式。在此之前，我们先列出一些核心原则，强烈建议第三方模块遵守，并且对 pull request 属于强制要求。

## 核心原则

amaya-di 是一个现代的依赖注入框架，追求高可靠性、高性能和极简设计。因此，我们坚持以下原则：

* 在可读性和性能之间保持谨慎的平衡：
    * 避免创建不必要的实体；
    * 对于不够直观的代码，应尽量添加注释；
    * 向用户公开的 API 应尽量简洁、自文档化并具备灵活性；
    * 拒绝“隐式魔法”、全局容器和注解代替明确、可组合的 API ——这类设计不会被接受。
* 所有更改应解决实际问题，提升稳定性、性能或可读性。我们不接受“为做功能而做功能”的更改。
* 欢迎就架构方案或功能设计提出建议或批评，前提是交流保持建设性。
* 最低 JDK 要求为 11。向低版本移植的 PR 将被拒绝。
* 坚持最小化依赖：
    * 不要因单一功能引入整套库；
    * 引入 `guava`、`apache-commons` 等庞大库将直接导致 PR 被拒（裁剪版或模块化版除外）；
    * 不要在 `amaya-di` 或其核心模块中引入其它 DI 框架的实现——这应在单独模块中处理；
    * 如果某功能可以在合理时间内通过 Java SDK 实现，禁止引入额外依赖。
* 对公共 API 的修改必须保持其通用性。不得为了某个具体场景破坏整体一致性。
* 对于小幅修复或改进，请提交 PR，而不是创建同名 fork 或重新发布。这有助于保持生态整洁。
* 如需实现基于字节码生成的功能：
    * 请勿使用笨重、缓慢或智能化的库（如 `bytebuddy`）；我们推荐使用 `org.ow2.asm:asm` 或 [Class-File API](https://openjdk.org/jeps/484)；
    * 不要使用 java agents 或修改已加载字节码的方法——某些 JVM 不支持此功能；
    * 确保你的字节码生成算法足够高效；
    * 确保生成的字节码足够优化——性能优化总是值得；
    * 尽可能使字节码与 JVM 状态无关，保证其在 JVM 重启后仍可加载：
        * 使用稳定数据作为描述符和命名；
        * 若数据顺序重要，使用有序集合（如 `TreeMap`）；
        * 若无法做到无状态，请务必在文档中说明。

## 创建第三方模块

amaya-di 是一个元项目，包含核心模块（core、schema、stub）及其组合封装。所有模块作为独立 JPMS 模块和 Maven 工件发布。这使你能按需接入所需部分。

第三方模块应在独立的 Java（或其他 JDK 语言）项目和仓库中开发。但若你的模块实现了基础能力或通用功能，欢迎提交包含 gradle 子模块的 PR（需在 `settings.gradle` 中添加 `include '<your_module_name>'`，于根目录新建对应文件夹，添加 `build.gradle` 和 `src` 子目录）。

项目创建后，需添加依赖。`amaya-di` 采用类似 `jakarta` 的模块交付方式：

* 每个逻辑模块作为独立 JPMS 模块发布；
* 模块提供公共 API（主要为接口和基础工具类）；
* 模块实现由框架提供，依赖方以 `compileOnly` 方式接入。

可参考 [asm](../../asm/build.gradle) 和 [reflect](../../reflect/build.gradle) 两个实现。

具体应接入哪些模块，取决于你希望实现的功能。请明确你的功能目标，并仅接入必要模块。同时请注意依赖的作用域。

* `amaya-di-core`: `ObjectFactory`、`TypeProvider`、`TypeRepository`、`ServiceProvider` 等（详见 readme）；
* `amaya-di-schema`: `ClassSchema` 及其变体、`SchemaFactory` 及其实现（详见 readme）；
* `amaya-di-stub`: `CachedObjectFactory`、`CacheMode`、`StubFactory`（依赖 core 与 schema）；
* `amaya-di`: 以上三者及 builder-API。

之后实现目标功能，并设计用户使用的“入口”方式。参与容器构建或运行的代码需配备测试。

如希望发布到 maven central，建议参考框架自身的构建配置，或联系我协助发布。

我们也欢迎你发布后通过 PR 或 issue 提出，将链接加入文档中。

## 创建 pull request

### 开始步骤

1. Fork 此仓库；
2. 创建具备描述性的分支名：`feature/<feature-name>` 或 `bugfix/<bugfix-description>`；
3. 遵循现有代码风格和架构，并遵守[核心原则](#核心原则)；
4. 为更改添加必要测试；
5. 提供详细的更改说明；
6. 提交 Pull Request。

### 代码要求

* 保持代码可读，遵循良好 OOP 实践；
* 不要添加特定平台或 JVM 的依赖方案；
* 主体功能需包含正向与负向测试；
* JDK 版本须与项目一致（当前为 11）；
* 修改核心模块仅限 Java；
* 对于新模块/非 Java 模块，支持任何 JVM 语言（11+）或原生语言。

### 沟通说明

* Pull Request 应包含清晰的说明（内容、目的、实现方式）；
* 问题与建议请通过 [issues](https://github.com/AmayaFramework/amaya-di/issues) 提交；
* 粗鲁、攻击性言论或毒性交流将直接导致 PR/issue 被关闭，且不予讨论；
* 项目由业余时间维护，反馈可能不会立即响应。

### 分支与版本

* `main` 分支始终保持最新稳定主版本；
* `release/<major>.<minor>` 为稳定分支，包含小版本更新 tag；
* 功能和修复应位于独立分支中；
* PR 合并前需通过编译、测试及代码审查。

## 不被接受的行为

- 违反核心设计原则；
- 缺乏测试的变更；
- 未有充分理由的复杂功能；
- 无必要的风格调整；
- 未经讨论的大型重构。

---

感谢你为 amaya-di 的贡献！

---

*本文档可能会更新，请关注变更。*
