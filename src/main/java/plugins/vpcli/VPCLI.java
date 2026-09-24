package plugins.vpcli;

import com.vp.plugin.*;
import plugins.vpcli.actions.CLIController;
import plugins.vpcli.export.DiagramExportPipeline;

// DOING
// - 修改主类名
// - 清理 IDEA 警告
// TODO
// - 修改菜单位置和菜单名称
// - 翻译和修改 README
// - 统一处理错误
// - 统一处理日志
// - 整理成DDD架构
// DONE
// - 改为依赖注入
// - 修改包名

public class VPCLI implements VPPlugin, VPPluginCommandLineSupport {
    ExporterFactory exporterFactory = new ExporterFactory();
    WriterFactory writerFactory = new WriterFactory();
    ProjectManagerFactory projectManagerFactory = new ProjectManagerFactory();
    FileFactory fileFactory = new FileFactory();

    DiagramExportPipeline diagramExportPipeline = new DiagramExportPipeline(projectManagerFactory
        , exporterFactory, writerFactory, fileFactory);
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
