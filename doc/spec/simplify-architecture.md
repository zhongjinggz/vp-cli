# 简化架构

## 目标
直接使用 visual paradigm 的原生模型作为数据源生成plantuml，不再经过转化在 MyElement 这一步，从而简化架构

## 思路
### 问题的提出

这个系统的目的是把visual paradigm 的模型转化为 PlantUML的模型。
目前做法是先把 visual paradigm 模型转化为内部的模型，
也就是 MyElement 及其子类，
然后再以此为数据源生成PlantUML。
但是，现在看 转化成 MyElement 这一步似乎是多余的。
只需直接用 visual paradigm 的模型作为数据源就可以生成 plantuml。
也就是说，完全废弃 MyElement (myuml 包中的内容)。
请思考一下这一方案的可行性，并给出实施思路，和具体改造程序的实施步骤。
最好能够实现渐进的改造。每一步，废弃 myuml 包中的一部分，直至完全废弃。

## 可行性分析

### 结论：可行

把 `MyElement` 从导出路径中移除，改为直接以 VP 原生模型为数据源，是可行的。
依据在于：

- **VP 原生模型已具备输出所需的一切读取能力。** 已用 `javap` 反编译 VP SDK
  `openapi.jar`（`pom.xml` 的 `vp.sdk.dir` 指向
  `/Applications/Visual Paradigm.app/Contents/Resources/app/lib`）逐接口核对：
  - `IModelElement`：`getId()/getModelType()/getName()/getDocumentation()/getParent()/getQualifiedName()`
    （VP 原生自带 `getQualifiedName()`，可直接替代面积最大的 `MyElement.getQualifiedName()` 别名来源）；
  - `IClassifier`：`getOperationByIndex()/getAttributeByIndex()/isAbstract()`；
  - `IAssociation`：`getDirection()`、`toFromRelationshipEndArray()/toToRelationshipEndArray()`；
  - `IRelationshipEnd`：`getModelElement()/getOppositeEnd()`；
  - `IAssociationEnd`：`getAggregationKind()/getMultiplicity()/getNavigable()`；
  - `IDependency`：`toFromRelationshipEndArray()/toToRelationshipEndArray()`；
  - `IGeneralization`（经 `IRelationship`）：`getFrom()/getTo()`；
  - `IOperation`、`IAttribute`（`getVisibility()/getName()/...Iterator()`）。
- **消费方集中且有限。** `myuml` 的引用完全被隔离在
  `application` / `vpmodelvisitor` / `outputmodel` 三层；
  `drivingadapter` / `drivenadapter` 层零引用。改造面可控。
- **存在一批可独立整包删除的闭环死代码**（`writers_old` + `myuml_old`）。
  它不参与任何执行路径、仓库内零外部引用，且其中保留了旧导出逻辑，**对理解映射具有参考价值**，
  因此把它放到最后处理也没有任何风险。

### 现状的两条导出流水线

- **旧流水线（已废弃、闭环）**：
  `application/writers_old/*`（7 个 writer）+ `domain/myuml_old/*`。
  仓库内无任何外部引用（`writers_old` 仅被自身引用；`myuml_old` 仅被 `writers_old`
  与 `myuml/mycomponent/MyComponent.java` 引用）。**可整体退役，且在最后处理。**
- **活跃流水线**：
  `vpmodelvisitor`（`Vi*` 结构 + visitor）→ `outputmodel`（`Puml*Exporter` 族）。
  这条流水线重度依赖 `myuml`，是废弃 `myuml` 的主要工作量所在。

### 关键依赖顺序（必须先删哪些）

`writers_old` **还引用了新 `myuml`**（`MyPackage`、`MyUseCase`），因此：
必须先删 `writers_old` → 再清理 `MyComponent` 的 3 个 `myuml_old` import →
之后 `myuml_old` 与 `writers_old` 才可整包删除。

> 注意：这条「先删顺序」只对「最终整包删除」有意义。由于 `writers_old`/`myuml_old`
> 本身是闭环死代码、不影响程序执行，只要保证不破坏 `mvn clean package` 与契约测试即可，
> 因此把它放到最后一个阶段（阶段 D）收拾。

### 改造策略（思路）

**"新旧并存、先行后删"：** 不为 `myuml` 增加任何新能力（尤其不要把 #12/#13 的属性/操作
继续塞进 `MyClass`），而是逐步让 `outputmodel` 与 `vpmodelvisitor` 直接消费 VP 原生模型；
每替换掉一片 `myuml` 引用，就删除对应的一片 `myuml` 类，直至整包消亡。

**渐进式入口（核心机制）：** 为了让「旧逻辑（MyElement）与新逻辑（VP 原生）并行」，
在 `PumlDiagramFactory.of` 上增加 `IDiagramElement vpDiagramElement` 参数作为突破口：
方法内部据此取得 VP 原生模型，再**逐一把 VP 元素传给各 Exporter**。各 Exporter 增加一个
接收 VP 原生模型的入口（逐步把实现切换为读 VP 模型），而尚未改造的 Exporter 仍然使用
`MyElement` 的子类。当所有 Exporter 都迁移到 VP 模型后，多余的参数与逻辑自然无用，届时安全删除。

## 总体实施步骤

> 每个阶段结束应保证 `mvn test`（仓库现有契约测试）通过、行为等价；
> 每个阶段可独立提交、独立验证。
>
> 阶段序号已按新思路重排：**最核心的 outputmodel 改造放最前（阶段A）**，
> **闭环死代码（writers_old + myuml_old）放到最后（阶段D）**。

- **阶段 A：让 `outputmodel` 直接消费 VP 原生模型（核心）。**
  借助 `of(MyDiagram, IDiagramElement)` 签名扩容作为突破口，把 VP 原生模型逐一下传给
  `Puml*Exporter`，使导出器族逐步从读 `MyElement` 切换为读 `IModelElement`/`IAssociation`/
  `IDependency`/`IGeneralization`/`IClass`。新旧逻辑并行，先保证输出字节级等价。

- **阶段 B：让 `vpmodelvisitor` 直接消费 VP 原生模型。**
  `Vi*` 结构与 visitor 不再构建 `MyElement` 树，直接携带/读取 VP 原生模型，
  从而兑现 `MyElementFactory`/`MyDiagramFactory`/`MyProjectFactory` 及 `MyPackage`/`MyModel`/`MyClass` 的退役。

- **阶段 C：删除剩余 `myuml` 包。**
  迁移其内部契约测试桩后整包删除，收尾活跃流水线。

- **阶段 D：退役闭环死代码（`writers_old` + `myuml_old`）。**
  闭环死代码，不影响执行、保留旧映射参考价值，故放到最后统一收拾。
  删除 `writers_old` → 清理 `MyComponent` 对 `myuml_old` 的 import → 删除整个 `myuml_old`。

---

## 阶段A 详细步骤

**目标：** 导出器族不再触碰 `MyElement` 家族，改读 VP 原生模型；**不动 `vpmodelvisitor`**，
通过给 `PumlDiagramFactory.of` 增加 VP 原生参数这一突破口，让新旧逻辑并行、输出字节级等价。

> **编排说明：** A1 是 A2 的前置（先有 Exporter 的 VP 入口，`of` 才能向下分发），
> 故先定义入口（A1），再改 `of` 签名并分发（A2），二者合为一组原子提交更佳。

- **步 A1 — `PumlElementExporter` 先增加接收 VP 原生参数的过渡入口。**
  `src/.../pumelementexporter/PumlElementExporter.java` 目前抽象方法
  `abstract void to(PumlDiagram, ProperMyDiagramElement)`，子类各自读取
  `myDiagramElement.getModelElement()`。
  改造：增加一个接收「对应 VP 模型元素」的重载入口（或把 VP 元素作为额外入参），
  例如 `void to(PumlDiagram result, ProperMyDiagramElement myDiagramElement, IModelElement vpElement)`。
  该入口**先均衡委托到原读法**（`to(result, myDiagramElement)`），保证此刻行为不变；
  之后每迁移一个子类，就在其内改写为直读 VP 参数。
  **已改造的子类走 VP 分支；未改造的子类沿用原有 `(MyElement)` 读法**，二者并存互不影响。
  验证：编译通过；迁移前后所有 Exporter 输出一致。

- **步 A2 — 给 `PumlDiagramFactory.of` 增加 VP 原生参数作为突破口（依赖 A1）。**
  `src/.../outputmodel/PumlDiagramFactory.java:20` 当前签名为
  `public PumlDiagram of(MyDiagram myDiagram)`。
  改为：
  ```java
  public PumlDiagram of(MyDiagram myDiagram, IDiagramElement vpDiagramElement)
  ```
  方法内：
  - `createPumlDiagram(myDiagram)`（名称/类型）暂不变，过渡期仍从 `MyDiagram` 取；
  - 以 `vpDiagramElement` 为入口取得 VP 原生模型集合（图表子元素所对应的模型元素），
    建立「`MyElement` 模型元素 ↔ VP `IModelElement`」的映射（可按 VP `getId()`
    对应到 MyElement 的 id），供后续分发给各 Exporter；
  - 遍历 `myDiagram.getSubShapes()/getSubEdges()` 时，把**对应的 VP 元素一并**传给
    各 `PumlElementExporter`（走步 A1 增设的 VP 入口）。
  原有调用方在过渡期将其持有的 `IDiagramElement` 传入（缺省时传 null 并验证不空指针）。
  - **API 变化的连锁反应（必须同步）：**
    1. 直接调用方 `ExportDiagramVisitor.visit(ViDiagramAsElement)`
       （`ExportDiagramVisitor.java:65` 的 `buildPlantUML.of(myDiagram)`）必须改为传入
       VP 图表元素。此处 `viDiagram.getVPDiagramUIModel()` 得到的 `vpDiagram`
       （`IDiagramUIModel`）即可作为 `IDiagramElement` 入参来源（`IDiagramUIModel`
       处于 VP `IDiagramElement` 接口谱系内），改造前先确认该继承关系。
    2. 凡调用 `buildPlantUML.of(...)` 的其它位置（含 `drivingadapter` 暴露的
       CLI 入口、契约测试、测试中构造该工厂的 stub/Mock）全部需加参数。
    3. 改造期间为避免改动面扩散到 `vpmodelvisitor`，可在 A2 先让 `ExportDiagramVisitor`
       直接透传 `vpDiagram`；真正的结构层改造留待阶段 B（步 B4）统一处理。
  验证：新增入口编译通过、行为与旧签名等价；`mvn test` 通过。

- **步 A3 — `PumlElementExporter` 别名（alias）改读 VP 原生全限定名。**
  既有的 `protected String getPumlAlias(MyElement)`（`PumlElementExporter.java:24`）
  依赖 `MyElement.getQualifiedName()`。
  改造：新增 `getPumlAlias(IModelElement)`，用 VP 原生 `IModelElement.getQualifiedName()`
  （必要时与 `getParent()` 链归一为与本系统一致的点分/`__` 规格），
  复用以 `.` 分段、非字母数字转 `_`、以 `__` 连接的既有算法；
  `getPumlAlias(MyElement)` 作为过渡委托到原生版本。
  验证：`PumlAliasTest` 等契约测试继续通过，输出别名与本阶段前后一致。

- **步 A4 — `PumlAssociationExporter` 直接读 `IAssociation`。**
  `PumlAssociationExporter.java:20` 目前强转 `(MyAssociation)`，
  借 `MyAssociationEnd` 常量与兼容 getter（`getElement/getMultiplicity/getName/getNavigable/getAggregationKind`）
  与 `MyAssociation.NameDirection`。
  改造：入参模型元素强转为 `IAssociation`；两端经
  `toFromRelationshipEndArray()/toToRelationshipEndArray()` 得 `IRelationshipEnd`，
  再 `getModelElement()` 得端分类器、`(IAssociationEnd)` 取
  `getMultiplicity()/getNavigable()/getAggregationKind()`；
  方向由 `IAssociation.getDirection()` 映射为 `PumlDiagram` 的阅读方向
  （沿用 FROM_TO/TO_FROM/NONE 语义，常量可内聚到前端枚举，不再依赖 myuml）。
  验证：`PumlAssociationExporterTest` 行为等价通过（用 VP 桩替换 myuml 桩）。

- **步 A5 — `PumlDependencyExporter` / `PumlGeneralizationExporter` 直接读 VP 关系。**
  - `PumlDependencyExporter.java:21` 强转 `(MyDependency)`，`getFrom()/getTo()`：
    改为 `(IDependency)`，`toFromRelationshipEndArray()[0].getModelElement()`（from）、
    `toToRelationshipEndArray()[0].getModelElement()`（to）。
  - `PumlGeneralizationExporter.java:22` 强转 `(MyGeneralization)`，`getGeneral()/getSpecific()`：
    改为 `(IGeneralization)`（经 `IRelationship`）`getFrom()/getTo()` ——
    VP `getFrom()`=父类(general)、`getTo()`=子类(specific)。
  验证：两张导出器各自契约测试行为等价；整体 `mvn test`。

- **步 A6 — `PumlClassExporter` 直接读 `IClass`，并承接 #12/#13 的映射结论。**
  - `PumlClassExporter`（`PumlClassExporter.java:22` 现读 `(MyClass)`）与 `PumlBlockExporter`
    目前渲染属性/操作。
    改造：`PumlClassExporter` 读 `(IClass)`：`IClass.attributeIterator()/operationIterator()`
    直接生成类体 PlantUML 成员行（属性 `+ name: type = init`、操作含 `{static}/{abstract}` 与
    参数列表），可见性符号沿用 #14 映射。
  - **关键澄清（与 #12/#13 的关系）：** `spec-class-diagram-basic.md` 的 #12/#13 曾计划把
    `MyClass` 的成员由 `MyAttribute_old/MyOperation_old` 换成新 `MyAttribute/MyOperation`。
    **在"废弃 myuml"目标下此方向应反转**：不再为 `MyClass` 补充新成员类型，而是让
    `PumlClassExporter` 直接读 VP 的 `IClass` 成员。若 #12/#13 需保留其"读取映射"结论
    （如何从 `IClass` 取可见性/类型/初值等），仅取其映射逻辑到 `PumlClassExporter` 内部，
    不引入新的 `MyAttribute/MyOperation` 消费。
  验证：`PumlClassExporterTest`（#12/#13/#14 相关）行为等价通过；整体 `mvn test`。

- **步 A7 — 清场：当全部 Exporter 迁移完成后删除过渡代码。**
  确认 `outputmodel` 全部 `Puml*Exporter` 均已直读 VP 模型后：
  - 删除 `of(MyDiagram)` 旧签名，`PumlDiagramFactory.of` 只保留接收 VP 参数的版本；
  - 删除 `PumlElementExporter.getPumlAlias(MyElement)` 过渡装饰，只留
    `getPumlAlias(IModelElement)`；
  - 删除步 A2 中新增的 VP 过渡重载的冗余参数（此时已无 MyElement 分支）。
  验证：`outputmodel` 无任何 `import plugins.vpcli.domain.myuml` 残留；`mvn test`。

**阶段 A 产出：** `outputmodel` 包（含 `Puml*Exporter` 族）不再依赖 `myuml`；
`myuml` 的消费者只剩 `vpmodelvisitor`（及 `application` 对 `MyElementType` 的静态引用）。

---

## 阶段B 详细步骤

**目标：** `Vi*` 结构与 visitor 不再构建 `MyElement` 树，直接携带/读取 VP 原生模型，
从而兑现 `MyElementFactory`、`MyDiagramFactory`、`MyProjectFactory`、`MyPackage`/`MyModel`/`MyClass` 的退役。

- **步 B1 — `MyElementType` 降级为纯映射（去掉对 `MyElement`/工厂的依赖）。**
  `MyElementType.java` 目前是枚举，仅在 `Vi` 层作 `of(String)/getVPModelType/isModelElement/getSuffix` 映射用。
  保留该枚举作"VP 类型字符串 ↔ 后缀"纯常量即可；确认其不引用 `MyElement`/`UnimplementedElement` 之外的领域类。
  验证：编译通过；`MyElementType` 无 `myuml` 模型依赖。

- **步 B2 — `ViElementFactory` 不再经 `MyElementFactoryFactory`。**
  `src/.../vpmodelvisitor/element/ViElementFactory.java:23` 目前 `myElementFactoryFactory.of(modelType)`。
  改造：`ViElementFactory` 直接按 `element.getModelType()` 分发到 `ViPackage/ViModel/ViClass/ViUseCase`，
  构造时只传 `IModelElement` 与 `ViElementFactory`（去掉 `MyElementFactory` 参数）；
  移除对 `@Inject MyElementFactoryFactory` 的依赖。
  验证：`ViElementFactory` 相关测试通过。

- **步 B3 — `ViModelElement` 族改为 VP 原生读取。**
  `src/.../element/ViModelElement.java` 目前持 `MyElementFactory myElementFactory` 字段、
  `getType()` 用 `MyElementType.of(vpElement.getModelType())`、`getChildren(...)` 用
  `MyElementType::isModelElement/getVPModelType` 过滤、并有 `abstract MyPackage toMyElementUnder(...)`。
  改造：
  - 移除 `myElementFactory` 字段与 `toMyElementUnder`（及 `ViPackage/ViModel/ViClass/ViUseCase` 中
    对应"恒返回 null"的实现）。
  - `getType()` 改为裸露 VP 类型（或继续借 `MyElementType.of` 映射——届时尚且保留该枚举，可先不动）。
  - `getChildren(...)` 不再需要 `List<MyElementType>` 形参，改由自身 `toSubDiagramArray()/toChildArray(vpModelTypes)`
    直接遍历。
  验证：`ViModelElement`/`ViPackage`/`ViModel`/`ViClass`/`ViUseCase` 相关测试通过。

- **步 B4 — 导出 visitor 改读 VP 原生图表。**
  `ExportDiagramVisitor.java:41` 目前经 `MyProjectFactory.fromGlobal()` 构造、
  `buildMyDiagram.of(vpDiagram, myElement)` 构建 `MyDiagram` 再交给 `PumlDiagramFactory.of(MyDiagram)`。
  改造：`RootExportDiagramVisitor`/`ExportDiagramVisitorFactory` 直接持有当前包名的 VP 上下文
  （`IProject.toDiagramArray` 或 `IElement.toSubDiagramArray`），把图表 `IDiagramUIModel` 交给
  `PumlDiagramFactory.of(..., IDiagramElement)`（阶段 A 已把导出器切到 VP 原生读法），
  不再构建任何 `MyDiagram`/`MyProject`。
  验证：`ExportDiagramVisitor`/`RootExportDiagramVisitor`/`ExportDiagramVisitorFactory` 相关测试通过；整体 `mvn test`。

- **步 B5 — `application` 层清引用。**
  `ExportService.java`、`TreeService.java` 静态 `import MyElementType.*`（用 PACKAGE/MODEL/DIAGRAM/CLASS 常量）。
  随步 B3/B4 移除了 `List<MyElementType>` 入参后，改用 VP 常量
  （`IModelElementFactory.MODEL_TYPE_PACKAGE` 等）或已降级的枚举，去除对 `myuml.myelement.MyElementType` 的依赖。
  验证：编译通过；`application` 层不再 import `myuml`。

**阶段 B 产出：** `vpmodelvisitor` 与 `application` 不再依赖 `myuml`；
`myuml` 已是零消费者（只剩自我内部引用与契约测试）。

---

## 阶段C 详细步骤

**目标：** 整包删除 `myuml`，收尾活跃流水线。

- **步 C1 — 前置整理：确认 #12/#13 不再引入新成员。**
  若此前按步 A6 将属性/操作读法内聚进 `PumlClassExporter`，则 `MyClass/MyAttribute/MyOperation/MyOperation.Parameter`
  不再被消费，可删除。

- **步 C2 — 删除 `domain/myuml` 全部子包与顶层类。**
  如 `myassociation`、`myclassifier`、`mycomponent`、`mydependency`、`mydiagram`、`myelement`、
  `mygeneralization`、`mymodel`、`mymodelrelationshipcontainer`、`mynary`、`mypackage`、`myproject`、
  `mystereotype`、`myunknownelement`、`myusecase` 及其顶层 `MyElement`/`MyElementType`/`MyProject` 等。

- **步 C3 — 迁移/删除 `myuml` 包内契约测试。**
  将 `src/test` 中那些仍依赖 `myuml` 的测试改为对 VP 原生模型桩
  （`IAssociation`/`IClass`/`IDependency`/`IGeneralization`）的等价断言；
  若某测试已无可测对象则删除。

- **步 C4 — 全局清理并验收。**
  `grep -r "domain.myuml[^_]"`（排除已无意义的 `myuml_old`）确认零命中；
  `mvn clean package` 产出 `target/plugins.vpcli.zip`；在 VP 中手动安装并分别对每种支持的图表
  （类/用例/时序/组件/部署/状态机/活动）做导入导出冒烟，确认 `.puml` 可被 PlantUML 解析渲染、
  命名冲突与语义往返（`project_semantics.puml`）行为不变。

**阶段 C 产出：** `myuml`（含全部 `MyElement` 家族）消失，活跃流水线收尾。

---

## 阶段D 详细步骤

**目标：** 退役闭环死代码 `writers_old` + `myuml_old`。它不影响程序执行、保留旧映射参考价值，
故放在**最后**统一收拾。

- **步 D1 — 删除 `application/writers_old/*`。**
  7 个文件：`PlantUMLWriter.java`、`ClassDiagramWriter.java`、`UseCaseWriter.java`、
  `SequenceUMLWriter.java`、`ComponentDeploymentUMLWriter.java`、`StateUMLWriter.java`、
  `ActivityUMLWriter.java`。
  理由：`writers_old` 仅被自身引用；它是 `myuml_old` 的最大消费者，且还引用了新 `myuml`，
  必须最先删，才能让后续 `myuml_old` 变成无引用。
  验证：`rg`/`grep` 确认 `src` 下无 `import plugins.vpcli.application.writers_old` 残留；`mvn test`。

- **步 D2 — 清理 `MyComponent` 的 3 个 `myuml_old` import。**
  `src/main/java/plugins/vpcli/domain/myuml/mycomponent/MyComponent.java` 第 4-6 行引用了
  `MyAttribute_old`、`MyOperation_old`、`MyArtifact_old`（已在 myuml_old 包内）。
  需看其字段实际类型：若这些字段本就意在持 VP 侧模型的新类型，则改为 `myuml` 对应新类型
  （`MyAttribute`/`MyOperation`/`MyArtifact`）。
  > 若此阶段之前 `myuml` 已整包删除（阶段 C），则该文件也不再被活跃流水线消费，可一并评估删除。
  验证：编译通过，无 `myuml_old` import 残留。

- **步 D3 — 删除整个 `domain/myuml_old` 包。**
  含 5 个子包下的全部旧模型：`myactivity_old`、`myassociation_old`、`myclass_old`、
  `mycomponent_old`、`mydeployment_old`、`mydiagram_old`、`myinteraction_old`、`mymodel_old`、
  `mynary_old`、`mystatemachine_old`、`myusecase_old`、`mycommon_old` 等，以及顶层
  `MyElement`/`MyElementType`/`MyProject`/... 的 `_old` 版本。
  验证：`grep -r "myuml_old"` 无命中；`mvn test`。

- **步 D4 — 全局验收。**
  `grep -r "myuml_old"` 与 `grep -r "writers_old"` 均无命中；
  `mvn clean package` 产出 `target/plugins.vpcli.zip`；在 VP 中手动安装并冒烟，
  确认导出路径 = `vpmodelvisitor(VP 原生) → outputmodel(VP 原生) → PlantUML`。

**阶段 D 产出：** `writers_old`/`myuml_old` 一并消失，仓库内不存在任何旧导出路径，架构得以简化。

---

## 里程碑与风险

- **里程碑：** 阶段 A（输出层改造，核心、行为冻结点）→ 阶段 B（结构层改造）→ 阶段 C（删除 `myuml`）→ 阶段 D（退役死代码、收尾）。
- **行为基准：** 各阶段以 `src/test` 中 `outputmodel`/`pumelementexporter` 测试及 `myuml` 契约测试为回归基准，
  逐步把桩从 `MyElement` 家族切换为 VP 原生模型接口。
- **主要风险一：VP 原生 `getQualifiedName()` 的段序/分隔归一。** 别名是导出一致性的关键，
  需在步 A3 用现有 `PumlAliasTest` 用例锁定输出；若 VP 原生全限定名与现 `MyElement.getQualifiedName()`
  （跳过 PROJECT 根节点）有差异，需在 `PumlElementExporter` 内做等价归一后再迁移测试桩。
- **主要风险二：`of` 签名扩容期间的"元素对应"。** 在步 A1 需稳定建立
  MyElement 模型元素 ↔ VP 原生元素的对应（建议按 VP 元素 `getId()` 建索引），
  避免分发错配导致输出漂移；该对应在阶段 A 末尾（步 A7）随旧逻辑一并移除。
- **注意：** `src/main/python/__pycache__/` 为未跟踪杂物，不属本次改造范围，勿纳入提交。
