# AGENTS.md

本文件为在此仓库中工作的 AI 编码代理提供指导。它与 CLAUDE.md 互补：CLAUDE.md 侧重于架构与约定，本文件侧重于通用工作流程、目录布局与验证。

## 项目概述

这是一个 Visual Paradigm 插件（Java），用于导入/导出 PlantUML 图表。插件标识为 `plugins.plantUML`，打包为 `.zip` 供 VP 插件安装机制使用。

**注意：** 尽管仓库名为 `vp-cli`，它**不是**独立的 CLI 应用——实际的命令是通过 VP 的 `VPPluginCommandLineSupport` 在插件上下文中提供的。请在文档、注释或 issue 中不要把它当作独立的 CLI 工具来呈现。

**支持的图表类型：** 类图、用例图、时序图、组件图、部署图、状态机图、活动图。

## 快速开始

本仓库没有自动化测试。验证是手动的：在 Visual Paradigm 中安装 zip 并通过 GUI 操作或命令行入口练习导入/导出。

构建插件 zip：
```bash
mvn clean package
```

产物：`target/plugins.plantUML.zip`

## 环境

- **JDK：** release 11（见 `pom.xml` 的 `maven.compiler.release`）
- **Visual Paradigm Plugin SDK（`com.vp.plugin.*`）：** `system` scope，从 `vp.sdk.dir`（默认 `/Applications/Visual Paradigm.app/Contents/Resources/app/lib`）解析，**不会**打进 zip。如果你的 VP 安装目录不同，用 `-Dvp.sdk.dir=<VP lib folder>` 覆盖。SDK 缺失会导致编译失败。
- **PlantUML：** `net.sourceforge.plantuml:plantuml:1.2024.7`（导入时解析 `.puml`）
- **Jackson：** `2.17.2`，用于语义往返格式的 JSON 序列化

## 目录布局

```
src/main/java/plugins/plantUML/
├── PlantUML.java                入口点（VPPlugin + VPPluginCommandLineSupport）
├── actions/                     Swing 操作控制器（plugin.xml 绑定）
├── export/                      "先读取、后写入"导出流水线
│   └── writers/                 PlantUML / JSON 渲染器
├── imports/
│   ├── importers/               PlantUML 源码 → 数据对象
│   └── creators/                数据对象 → VP 模型元素/图形
└── models/                      类型化数据传输对象（PDF/SemanticsData）
src/main/resources/
├── plugin.xml                   插件描述符（绑定 3 个 Swing 操作）
└── icons/
assembly/plugin-zip.xml          打 zip 的 assembly 描述符
testdata/                        手动验证用的示例 .puml
```

不要把源码路径写成 `src/plugins/...`；实际是 `src/main/java/plugins/...`。

## 核心工作流（横切关注点）

### 语义往返（Semantics Round-Trip）

PlantUML 语法无法表达元素描述、URL 或子图表链接。导出时这些被写入 `project_semantics.puml`（`@startjson`/`@endjson` 块）；导入时该文件最先被读取，并通过 `setModelElementSemantics` 重新附加。查找键为 `ownerName|ownerType`。新增元素数据结构时，确保导出端的 `extractSemantics()` 与导入端的 `putInSemanticsMap()` 都得到维护，否则断言语义会丢。

### 两阶段设计

- **导出：** `extract()` 遍历 VP 图表模型填充数据对象，再由 writer `writeToFile()` 渲染。`DiagramExportPipeline` 按图表类型分派，`PlantJSONWriter` 写出语义 JSON。
- **导入：** 先解析后创建。顺序：JSON 语义文件优先 → 非时序图 → 时序图最后（确保生命线的分类器已存在）。

## 约定

- VP 图表类型字符串（`IDiagramUIModel.getType()`）是驼峰式且无空格的：`ClassDiagram`、`UseCaseDiagram`、`InteractionDiagram`（时序图）、`ComponentDiagram`、`DeploymentDiagram`、`StateDiagram`、`ActivityDiagram`。
- 警告按图表收集到 `warnings` 列表，处理完成后通过 `showPopupWarnings()` 展示——**绝不抛出未处理异常**。
- 导入时的命名冲突通过将已有 VP 元素重命名为 `_renamed_on_import_<timestamp>` 后缀解决。
- PlantUML 名称会被清理为仅拉丁/希腊字母和数字（`PlantUMLWriter` 的 `formatName`/`formatAlias`）。

## 验证

- **构建：** `mvn clean package`（编译 + 打包 zip）。这是唯一可靠的确定性检查。
- **运行时：** 在 VP 中手动安装 zip 验证导入/导出往返。
- **不要**宣称运行过不存在的测试套件。本仓库没有测试，验证工作以手动/构建输出为准，并如实报告。
- CLI 入口：`PlantUML.java` 的 `invoke(String[] args)` 解析 `-action import|export`、`-path`、`-target`、`-list`。

## 修改的交集点（改代码前先看这些）

- 改动模型数据：`models/` 中对应 `*Data.java` 及 `SemanticsData`。
- 改动某类图的导入/导出：成对的 `*DiagramExporter` + `*UMLWriter`，与 `*DiagramImporter` + `*DiagramCreator`，保持数据对象对称。
- 新增/修改命令入口：`PlantUML.java` 与 `plugin.xml`。