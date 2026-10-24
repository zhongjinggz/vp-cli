
## 补充需求条目
这个程序的主要功能是将 Visual Paradigm 中的图表转换成 PlantUml 图。
目前已经完成了基本的包、类和关联。在继续完成其他功能之前，需要补齐需求规格和方案设计。
在  “/Users/zhongjing/Projects/mine/vp-cli/doc/spec/spec-use-case-diagram.md” 和 “/Users/zhongjing/Projects/mine/vp-cli/doc/spec/spec-class-diagram.md” 中已经编写了关于用例图和类图的部分内容。这些文件中，每个需求条目（每个二级标题 “##” 说明一个需求条目）下，“### 需求” 下，用 PlantUML 图来表示这一项的需求，“### 方案”下则是具体实施方案，例如修改或增加哪段代码，怎样进行单元测试等。
目前， “/Users/zhongjing/Projects/mine/vp-cli/doc/spec/spec-component-diagram.md”(顺序图)中的内容尚未编写。

你的任务是：根据对UML以及 PlantUML 的理解，参考“spec-class-diagram.md””和“spec-use-case-diagram.md”，补齐 spec-component-diagram.md 中遗漏的需求条目。先不必编写具体需求和方案，只补齐二级标题即可。有任何并明白的请与我讨论。
注意：只改 spec-component-diagram.md 文件，不要修改项目中任何其他文件。


## 补充需求内容
请为“类图”（/Users/zhongjing/Projects/mine/vp-cli/doc/spec/spec-class-diagram.md）的每个需求条目补充未完成的需求。要求：
1）在每个需求条目下的三级标题“### 需求”下补充
2）用 PlantUML 图的形式编写需求
3）不要遗漏 ```plantuml @startuml   @enduml   ```  这些必要的头尾文字，以便正确显示
4）可在图下面补充必要的文字说明
5）有任何不清楚的地方，请和我确认
6）只修改 spec-class-diagram.md，不要修改任何其他文件


我对类图的需求做了一些微调，并更换了部分条目的顺序，请根据现在的情况，重新调整序号。注意：只调整 spec-class-diagram.md 的序号，补修改其他内容，也不要修改其他文件。

## 方案设计
请为 spec-class-diagram.md 补充 “11 泛化 Generalization” 和 “12 接口实现 Implementation (棒棒糖方式)”的设计方案。
要求：
1）方案写在每个需求条目的 “### 方案” 一节中；
2）方案中可包括增加/修改哪部分代码、这么做的理由、怎样做单元测试、进行的分析和研究的结果以及其他你觉得有必要的内容；
3）对于复杂的类设计，方案中可包含 plantuml 图来表示设计；
4）将方案分成若干步骤，每个步骤都可进行测试和验证；
5）有任何不确定的地方，请与我确认；
6）只修改指定的文件和章节，不要修改其他部分
7）只给出方案，不要改代码
8）完成方案后，将状态由“待方案设计”改为“待编码”


## 代码实现
请实现 `/Users/zhongjing/Projects/mine/vp-cli/doc/spec/spec-class-diagram-basic.md` 中 “## 10 依赖 Dependency” 中的方案。要求：
1）只实现指定的需求，不要实现其他需求；
2）如果发现方案中有问题，请先和我确认。
