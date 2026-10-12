# Visual Paradigm CLI
A Visual Paradigm Plugin to provide CLI for AI

（origin: https://github.com/nbourdi/PlantUML-VP-Plugin)

## 用户指南


### 路线图 


#### TODO
- 中英文README
- vp-export 2 - 导出每个包下的图 Markdown
- vp-export 3 - 导出每个包下的模型 Markdown
- vp-mount - 把模型文件绑定到目录，不用每次都提供模型文件参数
- vp-cd
- vp-cat
- vp-pwd

#### DOING
- vp-export 1 - 导出每个包下的图 plantuml

#### DONE
- vp-tree - 显示模型的树形结构




## 开发指南

### 设计思路


- 关于创建图表和模型文件，有不同的策略：
  - 1）先遍历VP一遍，创建图表（创建包目录，写图标文件），在此过程中创建必要的模型（只缓存在内存中）；然后再遍历 VP 一遍，补充遗漏的模型并写模型文件（复用之前缓存的内存模型）
  - 2）先遍历VP一遍，创建模型（写文件，同时缓存）；然后再补图（不必树形遍历，用API一次性读出所有的图，利用已缓存的模型）
  - 第二中策略较通顺，但为了快些看到成效，先用第一种策略，之后再改成第二种。


- 三种模型
  
  - 1）Visual Paradigm 模型
    - 简述：Visual Paradigm 的插件机制所公开的模型
    - 参考：https://www.visual-paradigm.com/support/documents/pluginjavadoc/index.html
    - 类名：即 Visual Paradigm 中的类名。例如 IPoject, IClass, IDiagramUIModel
    - 属性/变量名：vpXxx (vp 是 Visual Paradigm 的简称），例如 vpProject, vpClass, vpDiagram
  
  - 2）Visitor 模型
    - 简述：为了使用 Visitor 模式遍历 Visual Paradigm 中的模型，对其中的元素进行的简单包装
    - 类名：ViXxx （Vi 是 Visitor 的简称），例如 ViProject, ViClass, ViDiagram
    - 属性/变量名： viXxx，例如 viProject, viClass, viDiagram
    
  - 3）内部UML元模型
    - 简述：本项目的核心元模型。由 Visual Paradigm 模型转换为内部UML模型，再基于此实现导入导出等逻辑
    - 类名：MyXxx，例如 MyProject, MyClass, MyDiagram
    - 变量名：myXxx，例如 myProject, myClass, myDiagram
    
    
- Factory vs Creator
  - Factory 有逻辑，没有 new
  - Creator 单纯的 new，没有逻辑

   


## **Supported Diagram Types**

The plugin currently supports conversion to and from:

- Class Diagram
- Use Case Diagram
- Sequence Diagram
- Component Diagram
- Deployment Diagram
- State (Machine) Diagram
- Activity Diagram (new syntax)

---

## **Installation Guide**


### **Automatic Installation**
1. **Download the `.zip` release** of the plugin.
2. **Open Visual Paradigm** and navigate to the **Help** tab.
3. Select **Install Plugin**.
4. Choose **Install from a zip of plugin** and click **Next**.
5. Browse to the downloaded `.zip` file and select it.
6. **Restart Visual Paradigm** to complete the installation.

### **Manual Installation**
1. Extract the plugin folder from the `.zip` file.
2. Copy the folder into the `/plugins` directory specified by the **Installation Plugin** dialog.
3. **Restart Visual Paradigm**.

---

## **PlantUML Plugin CLI**


The CLI is packaged with the plugin and does not require a separate installation.

### To allow usage:
1. Locate the script file (`Plugin.bat` / `Plugin.sh`) in:
    ```
    VP_installation_dir/scripts
    ```
2. Copy the file to the binaries' folder:
    ```
    VP_installation_dir/bin
    ```

### **Example 1: Exporting diagrams**
Export all diagrams under project:
```bash
Plugin.bat -project "C:/Demo/demo_project.vpp" -pluginid "plugins.vpcli" -pluginargs -action "export" -path "C:/Demo/output" -target "all"
```

List all diagrams and ids and export specific target:
```bash
Plugin.bat -project "C:/Demo/demo_project.vpp" -pluginid "plugins.vpcli" -pluginargs -action "export" -list
```

```bash
Plugin.bat -project "C:/Demo/demo_project.vpp" -pluginid "plugins.vpcli" -pluginargs -action "export" -path "C:/Demo/output" -target "lAJWSCmGAqACKRNG"
```

### **Example 2: Importing a folder of diagrams**
```bash
Plugin.bat -project "C:/Demo/demo_project.vpp" -pluginid "plugins.vpcli" -pluginargs -action "import" -path "C:/Demo/plant_diagrams"
```
