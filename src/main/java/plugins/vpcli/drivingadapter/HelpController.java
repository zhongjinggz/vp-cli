package plugins.vpcli.drivingadapter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

// 关于创建图表和模型文件，有不同的策略：
// 1）先遍历VP一遍，创建图表（创建包目录，写图标文件），在此过程中创建必要的模型（只缓存在内存中）；然后再遍历 VP 一遍，补充遗漏的模型并写模型文件（复用之前缓存的内存模型）
// 2）先遍历VP一遍，创建模型（写文件，同时缓存）；然后再补图（不必树形遍历，用API一次性读出所有的图，利用已缓存的模型）
// 第二中策略较通顺，但为了快些看到成效，先用第一种策略，之后再改成第二种。

// DOING
// - export: 重写生成图表的程序 - Class / Package
// - export: 重写生成图表的程序 - Association
// - export: 重写生成图表的程序 - Dependency
// - export: 重写生成图表的程序 - Stereotype/Keyword

// TODO
// - export: fix bug of Class Diagram 包和类同名造成混乱
// - 翻译和修改 README

// - 清理测试
// - 统一处理错误
// - 清理 IDEA 警告
// - 拆分成多个 Controller， 分别依赖注入

// - Tree: 根据参数控制 Tree 的类型和深度
// - export：将图表放入 markdown
// - export: 图表 markdown 中放入交叉引用
// - export: 根据模型内容生成 markdown
// - export：链接交叉引用
// - tree: 补测试
// - tree: 增加一个虚拟的根包 ProjectVirtualPackage 对应于 VP Project
// - 统一处理日志
// - 考虑将 python 改为 java ， 改为多模块项目
// - 改用 NIO
// - 补充建立目录时的各种异常情况

// DONE
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
            "    - refactor ElementType 不区分diagram type 20:25";

        ApplicationManager.instance().getViewManager().showMessageDialog(
            ApplicationManager.instance()
                .getViewManager()
                .getRootFrame()
            , usage + changeLog
        );
    }

    @Override
    public void update(VPAction action) {
    }

}
