package plugins.plantUML;

import com.vp.plugin.*;
import plugins.plantUML.actions.CLIController;
import plugins.plantUML.export.DiagramExportPipeline;

//TODO 改为依赖注入 2 - 封装对 ApplicationManager 和 ProjectManager 的创建
//TODO 修改主类名
//TODO 修改包名
//TODO 翻译和修改 README

//DONE 改为依赖注入 1 - 注入 pipeline
public class PlantUML implements VPPlugin, VPPluginCommandLineSupport {

    DiagramExportPipeline diagramExportPipeline = new DiagramExportPipeline();
    CLIController cliController = new CLIController(diagramExportPipeline);

    @Override
    public void loaded(VPPluginInfo pluginInfo) {
    }

    @Override
    public void unloaded() {
    }

    // CLI 入口：解析参数并分发到导入/导出逻辑
    @Override
    public void invoke(String[] args) {

        cliController.invoke(args);
    }
}
