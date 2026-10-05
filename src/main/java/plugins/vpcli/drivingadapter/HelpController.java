package plugins.vpcli.drivingadapter;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

// DOING
// - 使用 Visitor 模式浏览IProject, 并基于改模式重构 TreeService
// - export：将图表放入目录结构

// TODO
// - export: 重构/重写生成图表的程序
// - export: fix bug of Class Diagram 包和类同名造成混乱
// - 翻译和修改 README

// - 清理测试
// - 统一处理错误
// - 清理 IDEA 警告
// - 拆分成多个 Controller， 分别依赖注入

// - TreeConverter: 考虑区分仅用于 Tree 的 TreeItem 和实际的元素（例如Package）
// - TreeConverter: 考虑根据参数控制 TreeItem 的类型和深度
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

        ApplicationManager.instance().getViewManager().showMessageDialog(
            ApplicationManager.instance()
                .getViewManager()
                .getRootFrame()
            , "simplify visitor pattern for TreeService -  10:23"
        );
    }

    @Override
    public void update(VPAction action) {
    }

}
