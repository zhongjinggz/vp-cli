package plugins.vpcli;

import com.vp.plugin.*;
import plugins.vpcli.drivenadapter.FileFactory;
import plugins.vpcli.drivenadapter.ProjectRepository;
import plugins.vpcli.drivingadapter.CLIController;
import plugins.vpcli.application.DiagramExportPipeline;
import plugins.vpcli.application.TreeService;
import plugins.vpcli.application.exporter.ExporterFactory;
import plugins.vpcli.application.writers.WriterFactory;

// DOING
// - tree: 拆分模型和适配器
// - export：按照模型包结构创建目录结构

// TODO
// - tree: 补测试
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
// - tree: 完成功能
// - 将命令行改为 vp_export
// - 清理 IDEA 警告 - part1
// - 整理成DDD架构
// - 修改菜单位置和菜单名称
// - 修改主类名
// - 改为依赖注入
// - 修改包名

public class VPCLI implements VPPlugin, VPPluginCommandLineSupport {
    private final ExporterFactory exporterFactory = new ExporterFactory();
    private final WriterFactory writerFactory = new WriterFactory();
    private final ProjectRepository projectRepository = new ProjectRepository();
    private final FileFactory fileFactory = new FileFactory();

    private final DiagramExportPipeline diagramExportPipeline = new DiagramExportPipeline(projectRepository
        , exporterFactory, writerFactory, fileFactory);
    private final TreeService treeService = new TreeService(projectRepository);
    private final CLIController cliController = new CLIController(diagramExportPipeline, treeService);

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
