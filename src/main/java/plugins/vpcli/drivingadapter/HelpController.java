package plugins.vpcli.drivingadapter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import javax.swing.*;

// 关于创建图表和模型文件，有不同的策略：
// 1）先遍历VP一遍，创建图表（创建包目录，写图标文件），在此过程中创建必要的模型（只缓存在内存中）；然后再遍历 VP 一遍，补充遗漏的模型并写模型文件（复用之前缓存的内存模型）
// 2）先遍历VP一遍，创建模型（写文件，同时缓存）；然后再补图（不必树形遍历，用API一次性读出所有的图，利用已缓存的模型）
// 第二中策略较通顺，但为了快些看到成效，先用第一种策略，之后再改成第二种。


// 三种模型
// 1）Visual Paradigm 模型
//    简述：Visual Paradigm 的插件机制所公开的模型
//    参考：https://www.visual-paradigm.com/support/documents/pluginjavadoc/index.html
//    类名：即 Visual Paradigm 中的类名。例如 IPoject, IClass, IDiagramUIModel
//    属性/变量名：vpXxx (vp 是 Visual Paradigm 的简称），例如 vpProject, vpClass, vpDiagram
// 2）Visitor 模型
//    简述：为了使用 Visitor 模式遍历 Visual Paradigm 中的模型，对其中的元素进行的简单包装
//    类名：ViXxx （Vi 是 Visitor 的简称），例如 ViProject, ViClass, ViDiagram
//    属性/变量名： viXxx，例如 viProject, viClass, viDiagram
// 3）内部UML元模型
//    简述：本项目的核心元模型。由 Visual Paradigm 模型转换为内部UML模型，再基于此进行导出
//    类名：MyXxx，例如 MyProject, MyClass, MyDiagram
//    变量名：myXxx，例如 myProject, myClass, myDiagram


// DOING
// - MyElementFactory: 引入Lambda
// - MyElementFactory: 引入抽象工厂方法创建MyElement
// - visitor: 在超类中创建 visitor 树
// - export: 递归构建父 MyElement
// - export: 重写生成图表的程序 - Package / Model
// - export: 重写生成图表的程序 - 为 Model 增加衍型

// TODO
// - 重构依赖注入：使用单独的 BeanFactory
// - 重构依赖注入：通过内部类，将若干工具或工程组织在一起，简化依赖注入
// - 重构依赖注入：使用DSL
// - 从 ViXxx 中拿后缀，而不是从 ElementType(考虑删除 ElementType)
// - export: 重写生成图表的程序 - Class
// - export: 重写生成图表的程序 - Association
// - export: 重写生成图表的程序 - Dependency
// - export: 重写生成图表的程序 - Stereotype/Keyword
// - export: fix bug of Class Diagram 包和类同名造成混乱
// - export：将图表放入 markdown
// - 翻译和修改 README


// - 清理测试
// - 统一处理错误
// - 清理 IDEA 警告
// - 拆分成多个 Controller， 分别依赖注入

// - Tree: 根据参数控制 Tree 的类型和深度
// - export: 图表 markdown 中放入交叉引用
// - export: 根据模型内容生成 markdown
// - export：链接交叉引用
// - tree: 补测试
// - tree: 增加一个虚拟的根包 ProjectVirtualPackage 对应于 VP Project
// - 考虑将 python 改为 java ， 改为多模块项目
// - 改用 NIO
// - 补充建立目录时的各种异常情况

// DONE
// - export: 重写生成图表的程序 - 建立 Package / Model 目录结构
// - 增加日志机制 1）记录错误 2）记录内部运作轨迹
// - export：将图表放入目录结构
// - export: 使用Visitor 创建目录
// - Tree：显示 diagram
// - 使用 Visitor 模式浏览IProject, 并基于改模式重构 TreeService
// - export: 重构生成图表的程序(类名和包结构）
// - tree: 使用IModelElement[] toChildArray(java.lang.String[] modelTypes)
// - 修改命令行 -action list
// - export：按照模型包结构创建目录结构
// - 将依赖注入作为方法
// - tree: 拆分模型和适配器
// - tree: 完成功能
// - 将命令行改为 vp_export
// - 清理 IDEA 警告 - part1
// - 整理成DDD架构
// - 修改菜单位置和菜单名称
// - 修改主类名
// - 改为依赖注入
// - 修改包名
public class HelpController implements VPActionController {

    @Override
    public void performAction(VPAction action) {
        String usage = "Usage:\n" +
            "    vp-tree -project <project name.vpp>\n";

        String changeLog = "Change Log:\n" +
            "    - rewrite MyElementFactory - abstract factory pattern - 13:19";

        ApplicationManager.instance().getViewManager().showMessageDialog(
            ApplicationManager.instance()
                .getViewManager()
                .getRootFrame() ,
            usage + changeLog,
            "VP CLI Help",
            JOptionPane.INFORMATION_MESSAGE

        );
    }

    @Override
    public void update(VPAction action) {
    }

}
