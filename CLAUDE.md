# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 提供在本仓库中工作时的指导。

## 项目概述

这是一个用于导入/导出 PlantUML 图表的 Visual Paradigm 插件（Java）。插件标识为 `plugins.plantUML`，打包为 `.zip` 格式供 Visual Paradigm 的插件安装机制使用。尽管仓库名为 `vp-cli`，但它是一个 GUI/CLI 插件——而非独立的 CLI 应用程序。

**支持的图表类型：** 类图（Class）、用例图（Use Case）、时序图（Sequence）、组件图（Component）、部署图（Deployment）、状态机图（State Machine）、活动图（Activity）。

## 构建与开发

这是一个 **Maven 项目**（`pom.xml`），编译目标是 Visual Paradigm Plugin SDK，产物为插件 zip。原先的 Eclipse 项目文件（`.classpath`/`.project`）已 gitignore。

**构建命令：**
```bash
mvn clean package          # 编译并打包，生成 target/plugins.plantUML.zip
```

构建完成后，zip 内容为 VP 插件结构：
```
plugins.plantUML/
├── classes/           编译后的 .class
├── plugin.xml         插件描述符
├── icons/             图标
└── lib/               plantuml.jar + jackson jars
```

关键依赖（Maven 管理，`pom.xml` 中声明）：
- `com.vp.plugin.*` — Visual Paradigm Plugin SDK（`system` scope，指向 VP 安装目录 `openapi.jar`，不打进 zip）
- `net.sourceforge.plantuml:plantuml:1.2024.7` — PlantUML 解析器（导入时用于将 `.puml` 源码解析为图表对象）
- `com.fasterxml.jackson.*` — 用于语义往返（round-trip）格式的 JSON 序列化

本仓库**没有自动化测试**。验证方式是手动的：在 Visual Paradigm 中安装 `target/plugins.plantUML.zip`，通过 GUI 操作或 CLI 入口练习导入/导出。

## 架构

### 入口点

`src/plugins/plantUML/PlantUML.java` 实现了 `VPPlugin`（生命周期）和 `VPPluginCommandLineSupport`（CLI）。`invoke(String[] args)` 方法解析 `-action import|export`、`-path`、`-target`、`-list` 参数。`plugin.xml` 声明了插件 id、版本，并将三个 Swing 操作绑定到控制器类。

### 导出流水线 (`export/`)

"先读取、后写入"的两阶段设计：

1. **`DiagramExportPipeline`** — 根据 `IDiagramUIModel.getType()`（如 `"ClassDiagram"`、`"InteractionDiagram"` 即时序图）分派到具体的 `DiagramExporter`。导出所有图表后，通过 `PlantJSONWriter` 写入一个 `project_semantics.puml` JSON 文件。
2. **`DiagramExporter`（抽象类）** — `extract()` 遍历 VP 图表模型并填充类型化的数据对象（如 `ClassData`、`UseCaseData`、`MessageData`）。子类：`ClassDiagramExporter`、`UseCaseDiagramExporter`、`SequenceDiagramExporter`、`ComponentDeploymentDiagramExporter`（同时处理组件图和部署图）、`StateDiagramExporter`、`ActivityDiagramExporter`。
3. **`PlantUMLWriter`（抽象类）** — `writeToFile()` 将数据对象渲染为 PlantUML 语法。子类：`ClassUMLWriter`、`UseCaseWriter`、`SequenceUMLWriter`、`ComponentDeploymentUMLWriter`、`StateUMLWriter`、`ActivityUMLWriter`。`PlantJSONWriter` 负责语义 JSON。

### 导入流水线 (`imports/`)

"先解析、后创建"的两阶段设计，拆分在 `importers/` 和 `creators/` 两个包中：

1. **`DiagramImportPipeline`** — 协调导入顺序：JSON 语义文件优先 → 非时序图 → 时序图最后（确保生命线的分类器已存在）。使用 `plantuml.jar` 的 `SourceStringReader` + `SyntaxChecker` 解析和验证源码，然后按 `UmlDiagramType` 分派到具体的 `DiagramImporter`。
2. **`DiagramImporter`（抽象类）** — `extract()` 将 PlantUML 的 `Entity`/`Member`/`Participant` 对象读取到与导出相同的类型化数据对象中。子类：`ClassDiagramImporter`、`DescriptionDiagramImporter`（组件/部署/用例图，按符号类型自动分类）、`SequenceDiagramImporter`、`ActivityDiagramImporter`、`StateDiagramImporter`。
3. **`DiagramCreator`（抽象类）** — `createDiagram()` 将数据对象持久化为 VP 项目中的模型元素和图表形状。子类与导入器一一对应。

### 模型 (`models/`)

类型化的数据传输对象，将 PlantUML 侧与 VP 侧解耦。有两个基类：
- **`BaseWithSemanticsData`** — 任何携带 `SemanticsData` 的元素的基类。
- **`SemanticsData`** — 承载无法用 PlantUML 语法表示的非图形化元数据（描述、引用、子图表）。通过 `@JsonInclude(NON_EMPTY)` 序列化/反序列化 JSON 以实现往返。

### 语义往返（Semantics Round-Trip）

核心的横切关注点。PlantUML 语法无法表达元素描述、URL 或子图表链接，因此在导出时这些内容被写入一个 `project_semantics.puml` 文件（`@startjson`/`@endjson` 块）。导入时该文件被最先读取，并通过 `setModelElementSemantics` 重新附加到模型元素。查找键为 `ownerName|ownerType`。`DiagramExporter.extractSemantics()` 和 `DiagramCreator.putInSemanticsMap()` 共同向该系统输送数据。

### 操作 (`actions/`)

`plugin.xml` 中绑定的三个 `VPActionController` 实现：
- `PlantUMLImportController` — 通过 Swing 对话框进行单文件或文件夹导入。
- `PlantUMLExportController` — 多图表导出，带有按图表类型分组的复选框对话框。
- `PlantUMLExportActiveController` — 仅导出当前活动图表。

## 约定

- VP 的图表类型字符串（`IDiagramUIModel.getType()`）是驼峰式且无空格的：`ClassDiagram`、`UseCaseDiagram`、`InteractionDiagram`（时序图）、`ComponentDiagram`、`DeploymentDiagram`、`StateDiagram`、`ActivityDiagram`。
- 警告按图表收集到 `warnings` 列表中，处理完成后通过 `showPopupWarnings()` 展示——绝不抛出异常。
- 导入时的命名冲突通过将已有的 VP 元素重命名为 `_renamed_on_import_<timestamp>` 后缀来解决。
- PlantUML 名称会被清理为仅包含拉丁/希腊字母和数字（`PlantUMLWriter` 中的 `formatName`/`formatAlias`）。
