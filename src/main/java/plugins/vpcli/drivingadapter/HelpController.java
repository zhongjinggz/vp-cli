package plugins.vpcli.drivingadapter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import javax.swing.*;

// DOING
// - export: 使用全路径名作为包和classifier 的别名，以避免同名

// TODO
// - export: 重写生成图表的程序 - Association
// - export: 重写生成图表的程序 - Dependency(various kinds of dependency)
// - export: 重写生成图表的程序 - Stereotype/Keyword
// - export：将图表放入 markdown
// - 翻译和修改 README

// - 清理测试
// - ViElementFactory : use Abstract Factory pattern

// - replace logback with tinylog
// - export: 重写生成图表的程序 - Class 属性和操作
// - export: circle or class style of Interface
// - Tree: 根据参数控制 Tree 的类型和深度
// - export: 根据模型内容生成 markdown
// - export: 图表 markdown 中放入交叉引用
// - 考虑将 python 改为 java
// - 引入 picocli
// - 拆分成多个 Controller， 分别依赖注入?
// - 改用 NIO
// - 补充建立目录时的各种异常情况

// DONE
// - 引入 Dagger 2 for DI
// - 重构依赖注入：通过内部类，将若干工具或工程组织在一起，简化依赖注入(例如 IO)
// - 清理 IDEA 警告
// - remove redundant XxxCreator s
// - 删除 MyElementTypeRepository、MyElementTypes、MyElementType, 用户FactoryFactory + ElementType;
// - export: 重写生成图表的程序 - Package / Model
// - export: 重写生成图表的程序 - 为 Model 增加衍型
// - export: 递归构建父 MyElement
// - 问题：顶层元素的 parentId 可能不是 IProject ID, 这将影响缓存中对象图的构建
// - MyElementFactory: 使用合适的 MyElementFactory 多态的创建上级 MyElement
// - MyElementFactory: 初始化缓存时就注入初始的 MyProject
// - MyElementFactory: 各种 MyElementFactory 成为单例
// - MyElementFactory: 使缓存成为单例模式
// - 重构依赖注入：用方法创建 Bean，而不是直接初始化属性，从而解决顺序上的依赖
// - 重构依赖注入：使用单独的 BeanFactory
// - visitor: 在超类中创建 visitor 树
// - MyElementFactory: 引入Lambda
// - MyElementFactory: 引入抽象工厂方法创建MyElement
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
            " add stereotypes - 22:25";

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
