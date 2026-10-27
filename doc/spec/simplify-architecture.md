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
  public PumlDiagram of(MyDiagram myDiagram, IDiagramElement vpDiagramElement){}
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

- **步 A3 — `PumlElementExporterFactory` 增加以 VP 形状常量为 key 的分发。（依赖 A2）**
  `PumlElementExporterFactory.java` 当前仅一张 `Map<MyElementType, PumlElementExporter>`，
  经 `@Inject` 注入 9 个 exporter 并按 `MyElementType`（ASSOCIATION/CLASS/PACKAGE/MODEL/RELATIONSHIPS/
  USE_CASE/DEPENDENCY/GENERALIZATION/UNIMPLEMENTED_ELEMENT）注册；`of(MyElementType)` 缺类型抛
  `BugException`。一旦 Exporter 改读 VP 原生模型，分发的输入就从 `MyElement.getElementType()`
  （`MyElementType` 枚举）变成 `IDiagramElement.getShapeType()`（返回 VP 形状类型字符串），
  因此需要第二张以 VP 常量为 key 的 Map 与对应的 `of(String)`。
  改造：
  - 新增 `Map<String, PumlElementExporter>`，key 用 VP `IShapeTypeConstants.SHAPE_TYPE_*`
    （实测常量存在 `SHAPE_TYPE_CLASS/SHAPE_TYPE_ASSOCIATION/SHAPE_TYPE_DEPENDENCY/
    SHAPE_TYPE_GENERALIZATION/SHAPE_TYPE_USE_CASE/SHAPE_TYPE_PACKAGE/SHAPE_TYPE_MODEL`；
    **注意实际接口名是 `IShapeTypeConstants`，而非 `IShapeTypeConsts`**）；
  - 两张 Map 由同一 `register` 喂入：注册时不仅存 `MyElementType → exporter`，同时存
    「exporter 所代表形状类型 → exporter」。为避免两处硬编码漂移，给 `PumlElementExporter`
    增加一个返回自身 VP 形状类型字符串的字段/方法（单一来源），或者在此步显式列出
    `ASSOCIATION↔SHAPE_TYPE_ASSOCIATION` 等一一对应表；
  - 新增 `PumlElementExporter of(String shapeType)`（缺类型抛 `BugException`）；过渡期
    `of(MyElementType)` 保留，并委托到形状类型版，供 MyElement 侧旧调用方继续使用。
  验证：两套分发入口返回同一批 exporter 实例；`PumlElementExporterFactory` 相关契约测试通过；
  `mvn test`。

- **步 A4 — `PumlElementExporter` 别名（alias）改读 VP 原生全限定名。**
  既有的 `protected String getPumlAlias(MyElement)`（`PumlElementExporter.java:24`）
  依赖 `MyElement.getQualifiedName()`。
  改造：新增 `getPumlAlias(IModelElement)`，用 VP 原生 `IModelElement.getQualifiedName()`
  （必要时与 `getParent()` 链归一为与本系统一致的点分/`__` 规格），
  复用以 `.` 分段、非字母数字转 `_`、以 `__` 连接的既有算法；
  `getPumlAlias(MyElement)` 作为过渡委托到原生版本。
  验证：`PumlAliasTest` 等契约测试继续通过，输出别名与本阶段前后一致。

- **步 A5 — `PumlAssociationExporter` 直接读 `IAssociation`。**
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

- **步 A6 — `PumlDependencyExporter` / `PumlGeneralizationExporter` 直接读 VP 关系。**
  - `PumlDependencyExporter.java:21` 强转 `(MyDependency)`，`getFrom()/getTo()`：
    改为 `(IDependency)`，`toFromRelationshipEndArray()[0].getModelElement()`（from）、
    `toToRelationshipEndArray()[0].getModelElement()`（to）。
  - `PumlGeneralizationExporter.java:22` 强转 `(MyGeneralization)`，`getGeneral()/getSpecific()`：
    改为 `(IGeneralization)`（经 `IRelationship`）`getFrom()/getTo()` ——
    VP `getFrom()`=父类(general)、`getTo()`=子类(specific)。
  验证：两张导出器各自契约测试行为等价；整体 `mvn test`。

- **步 A7 — `PumlBlockExporter`（抽象基类）改为直读 VP 原生模型。**
  `PumlBlockExporter.java:20-39` 是所有块型 exporter 的**父类**，直接依赖 myuml，须最先改：
  其 `to(...)` 中 `myElement.getName()`、`getPumlAlias(myElement)`、以及遍历
  `myDiagramElement.getSubShapes()` 时 `pumlElementExporterFactory.of(...getElementType())`
  的分发，全部建立在 `ProperMyDiagramElement`（myuml）之上。
  改造：改走步 A1 的 VP 入口，块名/别名/子形状分发改为读 `IModelElement`/VP 图表元素；
  其 `writeBlockBody` 钩子签名同步调整。
  **此步是先于其子类（`PumlClassExporter`/`PumlPackageExporter`/`PumlModelExporter`）的前置，必须最先做**，
  否则子类覆写的 `writeBlockBody` 签名无从对齐。
  验证：编译通过；块型 exporter 输出与迁移前等价；`mvn test`。

- **步 A8 — `PumlClassExporter` 直接读 `IClass`，并承接 #12/#13 的映射结论。**
  - `PumlClassExporter`（`PumlClassExporter.java:22` 现读 `(MyClass)`，继承自已改造的
    `PumlBlockExporter`），用 `IClass.attributeIterator()/operationIterator()`
    直接生成类体 PlantUML 成员行（属性 `+ name: type = init`、操作含 `{static}/{abstract}` 与
    参数列表），可见性符号沿用 #14 映射。
  - **关键澄清（与 #12/#13 的关系）：** `spec-class-diagram-basic.md` 的 #12/#13 曾计划把
    `MyClass` 的成员由 `MyAttribute_old/MyOperation_old` 换成新 `MyAttribute/MyOperation`。
    **在"废弃 myuml"目标下此方向应反转**：不再为 `MyClass` 补充新成员类型，而是让
    `PumlClassExporter` 直接读 VP 的 `IClass` 成员。若 #12/#13 需保留其"读取映射"结论
    （如何从 `IClass` 取可见性/类型/初值等），仅取其映射逻辑到 `PumlClassExporter` 内部，
    不引入新的 `MyAttribute/MyOperation` 消费。
  验证：`PumlClassExporterTest`（#12/#13/#14 相关）行为等价通过；整体 `mvn test`。

- **步 A9 — `PumlUseCaseExporter` 改为直读 VP 原生模型。**
  `PumlUseCaseExporter.java` 直接依赖 myuml：`myElement.getName()` + `getPumlAlias(myElement)`。
  改造：改为读 `IModelElement`（use case 的分类器）的 `getName()` 与 VP 全限定名别名。
  验证：`PumlUseCaseExporter` 相关契约测试行为等价通过；`mvn test`。

- **步 A10 — `PumlUnimplementedExporter` 改为直读 VP 原生模型。**
  `PumlUnimplementedExporter.java` 直接依赖 myuml：`myDiagramElement.getModelElement()
  .getName()/getElementType()`。
  改造：改为读 VP 原生元素（`IModelElement.getName()/getModelType()`），或接收裸参数后仅打日志。
  验证：编译通过；日志输出等价；`mvn test`。

- **步 A11 — `PumlModelRelationshipContainerExporter` 删除空方法。**
  `PumlModelRelationshipContainerExporter.java` 方法体为空，注释已注明
  "will never be called, remove later"；仅方法签名引用 `ProperMyDiagramElement`（myuml）。
  改造：连同空方法一并删除（Null Object 语义），从而去掉最后一个 myuml import。
  验证：`outputmodel` 下 `grep -r "myuml"` 无命中；`mvn test`。

- **步 A12 — `PumlPackageExporter` / `PumlModelExporter` 复核。**
  二者继承 `PumlBlockExporter`，自身只覆写 `getBlockType()`/`getBlockSuffix()`，无 MyElement 代码，
  随步 A7 基类改造自动完成。此步仅作复核：确认其依赖图不再向下触碰 myuml。
  验证：`outputmodel` 下 `grep -r "myuml"` 无命中；`mvn test`。

- **步 A13 — 清场：当全部 Exporter 迁移完成后删除过渡代码。**
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
最终 `vpmodelvisitor` 与 `application` 对 `myuml` 零依赖。

> **关键耦合（本方案拆分依据）：**
> ① `ViElement`/`ViProject` 用 `MyElementType` 表达类型，而 `ExportDiagramVisitor.makeDirOf` 需要
> `getType().getSuffix()`、`ViProject`/`ViModelElement` 需要 `isModelElement()/getVPModelType()` 过滤，
> 因此类型载体必须先迁移到一个 vpmodelvisitor 自有（且不依赖 myuml）的枚举上，后缀映射才能跟着走。
> ② `ViElementFactory`/`ViPackageFactory` 携带的 `MyElementFactory` 只用于 `toMyElementUnder`，而
> `toMyElementUnder` 的唯一生产消费点是 `ExportDiagramVisitor.buildElementModel`。因此**必须先改
> `ExportDiagramVisitor` 使其不再调用 `toMyElementUnder`，之后才能删除 `toMyElementUnder` 与
> `MyElementFactory` 参数** —— 顺序不可颠倒。
> ③ `ViProject.setElementTypes` 的形参类型改变会立即要求 `TreeService`/`ExportService` 的静态 import
> 同步调整，故 application 清引用随类型迁移一起做，而不是放到最后。
>
> 每步以「编译通过 + `mvn test`（现有 111 个契约测试）通过」为验收。myuml 各类（含契约测试桩）
> 仅从 vpmodelvisitor/application 退役对它们的引用，类本身留到阶段 C 再删。

- **步 B1 — 新增 vpmodelvisitor 自有的 `ViElementType` 枚举（替代对 `MyElementType` 的依赖）。**
  `src/.../vpmodelvisitor/` 下新增 `ViElementType`，把 `MyElementType` 的"VP modelType ↔ 后缀"纯映射
  复制过来（含 `of(String)/getVPModelType()/getSuffix()/isModelElement()` 与 PACKAGE/MODEL/CLASS/USE_CASE/
  DIAGRAM 等常量），**不 import 任何 myuml 类**（DIAGRAM/PROJECT 的 `diagram_has_no_model_type`、
  UNIMPLEMENTED 的文本 modelType 用等价字符串字面量或 VP 常量）。
  行为与 `MyElementType` 完全同构，仅属不同的包。
  验证：编译通过；`ViElementType` 不引用 `myuml`；`mvn test`。

- **步 B2 — `ViProject` 持有的类型切到 `ViElementType`，连带 `getChildren` 签名链（团）,（application 同步清静态 import）。**
  这是 B1 之后第一个闭合单元：只要 `ViProject.myElementTypes` 换成 `List<ViElementType>`，就必然连带
  `ViElement.getChildren`、`ViModelElement.getChildren`、`ViDiagramAsElement.getChildren` 三个签名同步换
  （`ViProject.elementsAccept` 在 ViProject.java:71 把持有的 list 直接传给 `anElement.getChildren(...)`，
  形参类型被实参类型钳制，无法拆开）。因此把「持有类型 + `getChildren` 签名链」作为一个原子步收在 B2；
  **`getType()/typeIs()` 与 list 传递无关，留到 B3 再切**。
  - `ViProject.myElementTypes`：`List<MyElementType>` → `List<ViElementType>`；
    `setElementTypes(List<ViElementType>)`；`getVpModelTypeArray()` 的
    `filter(MyElementType::isModelElement).map(MyElementType::getVPModelType)` 改为
    `filter(ViElementType::isModelElement).map(ViElementType::getVPModelType)`。
  - 连带 `getChildren` 签名链：
    - `ViElement`：`getChildren(List<MyElementType>)` → `getChildren(List<ViElementType>)`；
    - `ViModelElement`：`getChildren(List<ViElementType>)`，方法体内的 `filter/map` 用 `ViElementType`；
    - `ViDiagramAsElement`：`getChildren(List<ViElementType>)`。
    - 注意：这三处只改 `getChildren` 的形参类型与内部 `MyElementType` 引用，**不动 `getType()`**。
  - `TreeService.java`、`ExportService.java`：`import static ...MyElementType.*` 改为
    `import static ...ViElementType.*`，`List.of(PACKAGE, MODEL, DIAGRAM /*, CLASS */)` 改用新常量。
  至此 `application` 对 `myuml` 的引用清零（阶段 B 中首次达成）。
  验证：编译通过；`grep -r "myuml" application` 无命中；`mvn test`。

- **步 B3 — `ViElement` 族类型载体切到 `ViElementType`（去 `MyElementType` import）。**
  - `ViElement`：`getType()` 返回 `ViElementType`；`typeIs(ViElementType)`；`getChildren(List<ViElementType>)`。
  - `ViModelElement`：`getType()` → `ViElementType.of(vpElement.getModelType())`；
    `getChildren(List<ViElementType>)` 内 `filter/map` 用 `ViElementType`。
  - `ViDiagramAsElement`：`getType()` → `ViElementType.DIAGRAM`。
  - 移除以上文件对 `MyElementType` 的 import。
  - `ExportDiagramVisitor.makeDirOf` 的 `getType().getSuffix()` 沿用（`ViElementType.getSuffix()` 已具备）。
  验证：编译通过；`vpmodelvisitor/element` 下无 `MyElementType` import；`mvn test`。

- **步 B4 — `ExportDiagramVisitor`/`RootExportDiagramVisitor`/`ExportDiagramVisitorFactory` 不再构建 `MyElement`/`MyProject`（先于删 toMyElementUnder）。**
  - `PumlDiagramFactory.of(MyDiagram, IDiagramUIModel)` → `of(IDiagramUIModel)`：方法体直接以 `vpDiagramElement`
    为数据源（`createPumlDiagram(IDiagramUIModel)` 从 `getDocumentation()/getName()/getType()` 取，
    迁移自 `MyDiagramFactory.creatMyDiagram` 的 name/type 逻辑）。阶段 A 已把导出器切到 VP 原生读法，此处只是把
    顶层 `MyDiagram` 载体彻底去掉。
  - `ExportDiagramVisitor`：删除 `buildMyDiagram` 字段、`myElement` 字段；`visit(ViDiagramAsElement)` 改为
    `buildPlantUML.of(vpDiagram)`；删除 `buildElementModel(ViPackage)`；`visit(ViPackage)/visit(ViModel)` 只保留
    `makeDirOf`。Root 专用构造去掉 `MyProject` 参数。
  - `RootExportDiagramVisitor`：构造签名去掉 `MyProject`。
  - `ExportDiagramVisitorFactory`：移除 `@Inject MyProjectFactory` 依赖与字段；`createRoot()` 改为
    `new RootExportDiagramVisitor(this.rootDir)`。
  此刻 `toMyElementUnder` 已无生产消费点，但**本步暂不删除它**（见 B5），保持改动面最小。
  验证：编译通过；`mvn test`（myuml 的 `MyDiagramFactoryTest` 等契约测试仍在，因类未删）。

- **步 B5 — 删除 `toMyElementUnder` 与 `MyElementFactory` 参数，`ViElementFactory`/`ViPackageFactory` 去除 `MyElementFactoryFactory`。**
  - `ViModelElement`：删除 `myElementFactory` 字段、构造第 3 参、抽象方法 `toMyElementUnder`。
  - `ViPackage/ViModel/ViClass/ViUseCase`：构造去掉 `MyElementFactory` 第 3 参，删除 `toMyElementUnder`
    覆写（含"恒返回 null"），移除对 `MyPackage/MyModel/MyElement/MyElementFactory` 的 import。
  - `ViElementFactory`：删除 `myElementFactoryFactory` 字段、`@Inject MyElementFactoryFactory` 构造依赖、
    `getMyElementFactory`；`from(IModelElement)` 内 `new ViPackage(element, this)` 等（去掉第 3 参）。
  - `ViPackageFactory`：调用 `super()`（无参），`from` 内 `new ViPackage(vpElement, this)`。
  验证：编译通过；`vpmodelvisitor` 下 `grep -r "myuml"` 无命中；`mvn test`。

- **步 B6 — 阶段 B 收尾核对。**
  `grep -r "domain.myuml[^_]" src/main/java/plugins/vpcli/domain/vpmodelvisitor src/main/java/plugins/vpcli/application`
  确认零命中；`mvn test` 通过（111 个）。至此 vpmodelvisitor/application 对 myuml 零依赖，为阶段 C 整删铺路。

**阶段 B 产出：** `vpmodelvisitor` 与 `application` 不再依赖 `myuml`；
`myuml` 已是零消费者（只剩自我内部引用与契约测试）。

---

## 阶段C 详细步骤

**目标：** 整包删除 `myuml`，收尾活跃流水线。

- **步 C1 — 前置整理：确认 #12/#13 不再引入新成员。（已完成：确认结论——暂不可删）**
  若此前按步 A8 将属性/操作读法内聚进 `PumlClassExporter`，则 `MyClass/MyAttribute/MyOperation/MyOperation.Parameter`
  不再被消费，可删除。
  > **执行确认（2024 步 C1）：** `MyClass` **现在还不能删**。核实依据——① `PumlClassExporter` 2 参过渡版
  > `writeBlockBody(PumlDiagram, ProperMyDiagramElement)` 仍 `instanceof MyClass` 并遍历
  > `myClass.getAttributes()/getOperations()`（`PumlClassExporter.java:34-45`，含 `MyClass/MyAttribute/MyOperation` import）；
  > ② 构造链仍在：`vpmodelvisitor`（阶段 B 未动）→ `MyElementFactoryFactory`（注册 `MyClassFactory`，`MyElementFactoryFactory.java:47`）
  > → `MyClassFactory.reallyCreate` 实例化 `MyClass` 并经 `IClass.attributeIterator()/operationIterator()` 填充成员；③ `MyPackage.getClasses()`
  > 依赖 `MyClass` 类型（`MyPackage.java:89-93`）。
  > 但 A8 内聚目标（VP 版 `writeBlockBody` 直读 `IClass`，不再为 `MyClass` 补新成员读法）已达成——
  > **`MyClass/MyAttribute/MyOperation` 无需新增成员，只是删除时机未到**。
  > **可删时机 = 阶段 B 退役 `MyElementFactoryFactory`/`MyClassFactory` 构造链之后**，届时随 C2 整包删除一并处理。
  > 本步无文件改动。

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
  需在步 A4 用现有 `PumlAliasTest` 用例锁定输出；若 VP 原生全限定名与现 `MyElement.getQualifiedName()`
  （跳过 PROJECT 根节点）有差异，需在 `PumlElementExporter` 内做等价归一后再迁移测试桩。
- **主要风险二：`of` 签名扩容期间的"元素对应"。** 在步 A1 需稳定建立
  MyElement 模型元素 ↔ VP 原生元素的对应（建议按 VP 元素 `getId()` 建索引），
  避免分发错配导致输出漂移；该对应在阶段 A 末尾（步 A13）随旧逻辑一并移除。
- **注意：** `src/main/python/__pycache__/` 为未跟踪杂物，不属本次改造范围，勿纳入提交。
