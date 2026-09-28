package plugins.vpcli;

import com.vp.plugin.*;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.ProjectManagerFactory;
import plugins.vpcli.drivingadapter.CLIController;
import plugins.vpcli.application.DiagramExportPipeline;
import plugins.vpcli.application.exporter.ExporterFactory;
import plugins.vpcli.application.writers.WriterFactory;

// DOING
// - 将命令行改为 vp_export
// TODO
// - export：按照模型包结构创建目录结构
// - export：将图表放入目录结构
// - export: 重构生成图表的程序
// - export：将图表放入 markdown
// - export: 图表 markdown 中放入交叉引用
// - export: 根据模型内容生成 markdown
// - export：链接交叉引用
// - 翻译和修改 README
// - 统一处理错误
// - 统一处理日志
// - 清理 IDEA 警告
// - 考虑将 python 改为 java ， 改为多模块项目
// DONE
// - 清理 IDEA 警告 - part1
// - 整理成DDD架构
// - 修改菜单位置和菜单名称
// - 修改主类名
// - 改为依赖注入
// - 修改包名

public class VPCLI implements VPPlugin, VPPluginCommandLineSupport {
    private final ExporterFactory exporterFactory = new ExporterFactory();
    private final WriterFactory writerFactory = new WriterFactory();
    private final ProjectManagerFactory projectManagerFactory = new ProjectManagerFactory();
    private final FileFactory fileFactory = new FileFactory();

    private final DiagramExportPipeline diagramExportPipeline = new DiagramExportPipeline(projectManagerFactory
        , exporterFactory, writerFactory, fileFactory);
    private final CLIController cliController = new CLIController(diagramExportPipeline);

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
